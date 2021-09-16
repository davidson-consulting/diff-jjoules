package fr.davidson.diff.jjoules;

import eu.stamp_project.testrunner.EntryPoint;
import fr.davidson.diff.jjoules.delta.DeltaMojo;
import fr.davidson.diff.jjoules.failer.FailerMojo;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationMojo;
import fr.davidson.diff.jjoules.mark.MarkMojo;
import fr.davidson.diff.jjoules.report.markdown.MarkdownMojo;
import fr.davidson.diff.jjoules.suspect.SuspectMojo;
import fr.davidson.diff.jjoules.util.CSVReader;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.maven.MavenRunner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.powerapi.jjoules.EnergySample;
import org.powerapi.jjoules.rapl.RaplDevice;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

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
    @Parameter(property = "path-json-delta", defaultValue = "deltas.json")
    private String pathToJSONDelta;

    /**
     *
     */
    @Parameter(property = "path-json-data-first-version", defaultValue = "data_v1.json")
    private String pathToJSONDataV1;

    /**
     *
     */
    @Parameter(property = "path-json-data-second-version", defaultValue = "data_v2.json")
    private String pathToJSONDataV2;

    /**
     * [Optional] Specify the path of a diff file. If it is not specified, it will be computed using diff command line.
     */
    @Parameter(defaultValue = "", property = "path-to-diff")
    private String pathToDiff;

    /**
     *
     */
    @Parameter(property = "path-json-delta-omega", defaultValue = "deltaOmega.json")
    private String pathToJSONDeltaOmega;

    /**
     *
     */
    @Parameter(property = "path-repo-v1")
    private String pathToRepositoryV1;

    /**
     *
     */
    @Parameter(property = "path-repo-v2")
    private String pathToRepositoryV2;

    /**
     *
     */
    @Parameter(property = "path-considered-test-method-names", defaultValue = "consideredTestMethods.json")
    private String pathToJSONConsideredTestMethodNames;

    /**
     *
     */
    @Parameter(property = "path-exec-lines-additions", defaultValue = "exec_additions.json")
    private String pathToExecLinesAdditions;

    /**
     *
     */
    @Parameter(property = "path-exec-lines-deletions", defaultValue = "exec_deletions.json")
    private String pathToExecLinesDeletions;

    /**
     *
     */
    @Parameter(property = "path-json-suspicious-v2", defaultValue = "suspicious_v1.json")
    private String pathToJSONSuspiciousV1;

    /**
     *
     */
    @Parameter(property = "path-json-suspicious-v2", defaultValue = "suspicious_v2.json")
    private String pathToJSONSuspiciousV2;

    // TODO should depend on the report we want to output
    // For now I set by default the path to the template.md for MarkdownMojo
    @Parameter(property = "path-to-report", defaultValue = ".github/workflows/template.md")
    private String pathToReport;

    private Configuration configuration;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final String classpath;
        final String classpathV2;
        try {
            getLog().info("Running on:");
            getLog().info(this.project.getBasedir().getAbsolutePath());
            getLog().info(this.pathDirSecondVersion);
            if (this.pathToRepositoryV1 == null || this.pathToRepositoryV1.isEmpty()) {
                this.pathToRepositoryV1 = this.project.getBasedir().getAbsolutePath();
            }
            if (this.pathToRepositoryV2 == null || this.pathToRepositoryV2.isEmpty()) {
                this.pathToRepositoryV2 = this.pathDirSecondVersion;
            }
            classpath = Utils.readClasspathFile(this.project.getBasedir().getAbsolutePath() + "/" + this.classpathPath);
            classpathV2 = this.pathDirSecondVersion == null || this.pathDirSecondVersion.isEmpty() ? "" : Utils.readClasspathFile(this.pathDirSecondVersion + "/" + this.classpathPathV2);
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
                    this.pathToJSONDeltaOmega,
                    this.pathToRepositoryV1,
                    this.pathToRepositoryV2,
                    this.pathToJSONConsideredTestMethodNames,
                    this.pathToExecLinesAdditions,
                    this.pathToExecLinesDeletions,
                    this.pathToJSONSuspiciousV1,
                    this.pathToJSONSuspiciousV2,
                    this.pathToReport
            );
            this._run(configuration);
            this.report();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getReportPathname() {
        return "diff_jjoules";
    }

    private EnergySample energySample;

    private void startMonitoring() {
        this.energySample = RaplDevice.RAPL.recordEnergy();
    }

    private void stopMonitoring(Configuration configuration) {
        this.stopMonitoring(configuration, this.getReportPathname());
    }

    private void stopMonitoring(Configuration configuration, String reportPathName) {
        final Map<String, Long> report = this.energySample.stop();
        JSONUtils.write(configuration.output + "/" + reportPathName + ".json", report);
        configuration.addReport(reportPathName, report);
    }

    public void run(Configuration configuration) {
        startMonitoring();
        _run(configuration);
        stopMonitoring(configuration);
    }

    protected void _run(Configuration configuration) {
        getLog().info("Run DiffJJoules - " + configuration.toString());
        this.resetAndCleanBothVersion();
        this.testSelection();
        this.testInstrumentation();
        this.deltaComputation();
        this.commitMarking();
        this.testFailingInstrumentation();
        this.testSuspicious();
    }

    private void testSelection() {
        final Properties properties = new Properties();
        properties.setProperty("path-dir-second-version", this.configuration.pathToSecondVersion);
        startMonitoring();
        MavenRunner.runGoals(
                this.pathToPom,
                properties,
                "clean", "eu.stamp-project:dspot-diff-test-selection:3.1.1-SNAPSHOT:list"
        );
        stopMonitoring(this.configuration, "selection");
        MavenRunner.runCleanAndCompile(this.configuration.pathToFirstVersion + "/pom.xml");
        MavenRunner.runCleanAndCompile(this.configuration.pathToSecondVersion + "/pom.xml");
        this.configuration.setTestsList(CSVReader.readFile(this.configuration.pathToTestListAsCSV));
    }


    private void testInstrumentation() {
        new InstrumentationMojo().run(this.configuration);
        MavenRunner.runCleanAndCompile(this.configuration.pathToSecondVersion + "/pom.xml");
        MavenRunner.runCleanAndCompile(this.pathToPom);
        this.configuration.setClasspathV1(Utils.readClasspathFile(this.project.getBasedir().getAbsolutePath() + "/" + this.classpathPath).split(":"));
        this.configuration.setClasspathV2(Utils.readClasspathFile(this.pathDirSecondVersion + "/" + this.classpathPathV2).split(":"));
    }

    private void deltaComputation() {
        new DeltaMojo().run(this.configuration);
        this.resetAndCleanBothVersion();
    }

    private void commitMarking() {
        new MarkMojo().run(this.configuration);
        MavenRunner.runCleanAndCompile(this.configuration.pathToFirstVersion + "/pom.xml");
        MavenRunner.runCleanAndCompile(this.configuration.pathToSecondVersion + "/pom.xml");
    }

    private void testFailingInstrumentation() {
        new FailerMojo().run(this.configuration);
        MavenRunner.runCleanAndCompile(this.pathDirSecondVersion + "/pom.xml");
        MavenRunner.runCleanAndCompile(this.pathToPom);
    }

    private void testSuspicious() {
        new SuspectMojo().run(this.configuration);
    }

    private void report() {
        // markdown TODO must be optional
        new MarkdownMojo().run(this.configuration);
    }

    private void resetAndCleanBothVersion() {
        this.gitResetHard(this.configuration.pathToRepositoryV1);
        this.gitResetHard(this.configuration.pathToRepositoryV2);
        MavenRunner.runCleanAndCompile(this.configuration.pathToFirstVersion + "/pom.xml");
        MavenRunner.runCleanAndCompile(this.configuration.pathToSecondVersion + "/pom.xml");
        this.configuration.setClasspathV1(Utils.readClasspathFile(this.project.getBasedir().getAbsolutePath() + "/" + this.classpathPath).split(":"));
        this.configuration.setClasspathV2(Utils.readClasspathFile(this.pathDirSecondVersion + "/" + this.classpathPathV2).split(":"));
    }

    private void gitResetHard(String pathToFolder) {
        try {
            Git.open(new File(pathToFolder))
                    .reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .call();
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }



}
