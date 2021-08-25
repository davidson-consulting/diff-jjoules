package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.delta.DeltaMojo;
import fr.davidson.diff.jjoules.failer.FailerMojo;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationMojo;
import fr.davidson.diff.jjoules.mark.MarkMojo;
import fr.davidson.diff.jjoules.markdown.MarkdownMojo;
import fr.davidson.diff.jjoules.suspect.SuspectMojo;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/08/2021
 */
@Mojo(name = "diff-jjoules")
public class DiffJJoulesMojo extends AbstractMojo {

    protected static final String TEST_FOLDER_PATH = "src/test/java/";

    @Parameter(defaultValue = "${basedir}/pom.xml")
    private String pathToPom;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    /**
     * [Mandatory] Specify the path to root directory of the project in the second version.
     */
    @Parameter(property = "path-dir-second-version")
    protected String pathDirSecondVersion;

    /**
     * [Mandatory] Specify the path to a CSV file that contains the list of tests to be instrumented.
     */
    @Parameter(property = "tests-list", defaultValue = "testsThatExecuteTheChange.csv")
    protected String testsList;

    /**
     * [Optional] Specify the path to a file that contains the full classpath of the project.
     * We advise use to use the following goal right before this one :
     * dependency:build-classpath -Dmdep.outputFile=classpath
     */
    @Parameter(property = "classpath-path-v1", defaultValue = "classpath")
    protected String classpathPath;

    /**
     * [Optional] Specify the path to a file that contains the full classpath of the project.
     * We advise use to use the following goal right before this one :
     * dependency:build-classpath -Dmdep.outputFile=classpath
     */
    @Parameter(property = "classpath-path-v2", defaultValue = "classpath")
    protected String classpathPathV2;

    @Parameter(property = "iterations", defaultValue = "5")
    protected int iterations;

    /**
     *
     */
    @Parameter(property = "output-path", defaultValue = "diff-jjoules")
    protected String outputPath;

    private static final String DEFAULT_OUTPUT_PATH = "diff-jjoules";

    /**
     *
     */
    @Parameter(property = "path-json-delta", defaultValue = "diff-jjoules/delta.json")
    private String pathToJSONDelta;

    /**
     *
     */
    @Parameter(property = "path-json-data-first-version", defaultValue = "diff-jjoules/data_v1.json")
    private String pathToJSONDataV1;

    /**
     *
     */
    @Parameter(property = "path-json-data-second-version", defaultValue = "diff-jjoules/data_v2.json")
    private String pathToJSONDataV2;

    /**
     * [Optional] Specify the path of a diff file. If it is not specified, it will be computed using diff command line.
     */
    @Parameter(defaultValue = "", property = "path-to-diff")
    private String pathToDiff;

    /**
     *
     */
    @Parameter(property = "path-delta-json", defaultValue = "diff-jjoules/delta.json")
    private String pathDeltaJSON;

    /**
     *
     */
    @Parameter(property = "path-json-delta-omega", defaultValue = "diff-jjoules/deltaOmega.json")
    private String pathToJSONDeltaOmega;

    protected Configuration configuration;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final String classpath;
        final String classpathV2;
        try {
            //final String module = this.pathDirSecondVersion == null || this.pathDirSecondVersion.isEmpty() ? "" : this.project.getBasedir().getAbsolutePath().substring(this.pathDirSecondVersion.length());
            getLog().info("Running on:");
            getLog().info(this.project.getBasedir().getAbsolutePath());
            getLog().info(this.pathDirSecondVersion);
            classpath = this.readClasspathFile(this.project.getBasedir().getAbsolutePath() + "/" + this.classpathPath);
            classpathV2 = this.pathDirSecondVersion == null || this.pathDirSecondVersion.isEmpty() ? "" : this.readClasspathFile(this.pathDirSecondVersion + "/" + this.classpathPathV2);
            final boolean junit4 = !classpath.contains("junit-jupiter-engine-5") && (classpath.contains("junit-4") || classpath.contains("junit-3"));
            if (junit4) {
                getLog().info("Enable JUnit4 mode");
            }
            this.configuration = new Configuration(
                    this.project.getBasedir().getAbsolutePath(),
                    this.pathDirSecondVersion == null || this.pathDirSecondVersion.isEmpty() ? "" : this.pathDirSecondVersion,
                    this.testsList,
                    classpath.split(":"),
                    classpathV2.split(":"),
                    junit4,
                    this.iterations,
                    this.outputPath,
                    this.pathToJSONDelta,
                    this.pathToJSONDataV1,
                    this.pathToJSONDataV2,
                    this.pathToDiff,
                    this.pathDeltaJSON,
                    this.pathToJSONDeltaOmega
            );
            this.run(configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(Configuration configuration) {
        getLog().info("Run DiffJJoules - " + configuration.toString());
        // diff-test-selection
        final Properties properties = new Properties();
        properties.setProperty("path-dir-second-version", this.configuration.pathToSecondVersion);
        this.runGoals(
                this.pathToPom,
                properties,
                "clean", "eu.stamp-project:dspot-diff-test-selection:3.1.1-SNAPSHOT:list"
        );
        // clean V2
        this.runCleanAndCompile(this.configuration.pathToSecondVersion + "/pom.xml");
        // instrumentation
        new InstrumentationMojo().run(this.configuration);
        // compile V1 and V2
        this.runCleanAndCompile(this.configuration.pathToSecondVersion + "/pom.xml");
        this.runCleanAndCompile(this.pathToPom);
        // delta
        new DeltaMojo().run(this.configuration);
        // checkout !
        this.gitCheckout(this.configuration.pathToFirstVersion);
        this.gitCheckout(this.configuration.pathToSecondVersion);
        // mark
        new MarkMojo().run(this.configuration);
        // failer
        new FailerMojo().run(this.configuration);
        // compile
        this.runCleanAndCompile(this.pathDirSecondVersion + "/pom.xml");
        this.runCleanAndCompile(this.pathToPom);
        // suspect
        new SuspectMojo().run(this.configuration);
        // markdown TODO must be optional
        new MarkdownMojo().run(this.configuration);
    }

    private void gitCheckout(String pathToFolder) {
        try {
            Git.open(new File(pathToFolder))
                    .checkout()
                    .addPath(".")
                    .call();
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runCleanAndCompile(String pathToPom) {
        final Properties properties = new Properties();
        properties.setProperty("mdep.outputFile", "classpath");
        this.runGoals(pathToPom, properties, "clean", "test", "-DskipTests", "dependency:build-classpath");
    }

    private void runGoals(String pathToPom, String... goals) {
        this.runGoals(pathToPom, new Properties(), goals);
    }

    private void runGoals(String pathToPom, Properties properties, String... goals) {
        final InvocationRequest invocationRequest = new DefaultInvocationRequest();
        invocationRequest.setPomFile(new File(pathToPom));
        invocationRequest.setGoals(Arrays.asList(goals));
        invocationRequest.setProperties(properties);
        final Invoker invoker = new DefaultInvoker();
        try {
            final InvocationResult invocationResult = invoker.execute(invocationRequest);
        } catch (MavenInvocationException e) {
            throw new RuntimeException(e);
        }
    }

    private String readClasspathFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.lines().collect(Collectors.joining(":"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
