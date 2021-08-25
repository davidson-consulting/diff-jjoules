package fr.davidson.diff.jjoules;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/08/2021
 */
public abstract class DiffJJoulesMojo extends AbstractMojo {

    protected static final String TEST_FOLDER_PATH = "src/test/java/";

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
    @Parameter(defaultValue = "diff-jjoules", property = "output-path")
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
            this.run(
                    new Configuration(
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
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void run(Configuration configuration);

    private String readClasspathFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.lines().collect(Collectors.joining(":"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
