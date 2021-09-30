package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.delta.DeltaMojo;
import fr.davidson.diff.jjoules.failer.FailerMojo;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationMojo;
import fr.davidson.diff.jjoules.mark.MarkMojo;
import fr.davidson.diff.jjoules.report.ReportEnum;
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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

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
     * Specify the path to root directory of the project in the second version.
     */
    @Parameter(property = "path-dir-second-version")
    protected String pathDirSecondVersion;

    /**
     * Specify the path to a CSV file that contains the list of tests to be instrumented.
     */
    @Parameter(property = "tests-list", defaultValue = "testsThatExecuteTheChange.csv")
    protected String testsList;

    /**
     * Specify the path to a file that contains the full classpath of the project for the version before the code changes.
     * We advise use to use the following goal to generate it :
     * dependency:build-classpath -Dmdep.outputFile=classpath
     */
    @Parameter(property = "classpath-path-v1", defaultValue = "classpath")
    protected String classpathPath;

    /**
     * Specify the path to a file that contains the full classpath of the project for the version after the code changes.
     * We advise use to use the following goal to generate it :
     * dependency:build-classpath -Dmdep.outputFile=classpath
     */
    @Parameter(property = "classpath-path-v2", defaultValue = "classpath")
    protected String classpathPathV2;

    /**
     * Number of execution to do to measure the energy consumption of tests.
     */
    @Parameter(property = "iterations", defaultValue = "5")
    protected int iterations;

    /**
     *  Specify the path to output the files that produces this plugin
     */
    @Parameter(property = "output-path", defaultValue = "diff-jjoules")
    protected String outputPath;

    private static final String DEFAULT_OUTPUT_PATH = "diff-jjoules";

    /**
     *  Specify the path to a json file that contains the deltas per test
     */
    @Parameter(property = "path-json-delta", defaultValue = "deltas.json")
    private String pathToJSONDelta;

    /**
     *  Specify the path to a json file that contains the measure of the energy consumption of tests for the version
     *  before the code changes.
     */
    @Parameter(property = "path-json-data-first-version", defaultValue = "data_v1.json")
    private String pathToJSONDataV1;

    /**
     *  Specify the path to a json file that contains the measure of the energy consumption of tests for the version
     *  after the code changes.
     */
    @Parameter(property = "path-json-data-second-version", defaultValue = "data_v2.json")
    private String pathToJSONDataV2;

    /**
     * Specify the path of a diff file. If it is not specified, it will be computed using diff command line.
     */
    @Parameter(defaultValue = "", property = "path-to-diff")
    private String pathToDiff;

    /**
     *  Specify the path to a json file that contains the delta omega to decide to pass or fail the build
     */
    @Parameter(property = "path-json-delta-omega", defaultValue = "deltaOmega.json")
    private String pathToJSONDeltaOmega;

    /**
     *  Specify the path to the root directory of the project before applying the commit.
     *  This is useful when it is used on multi-modules project.
     */
    @Parameter(property = "path-repo-v1")
    private String pathToRepositoryV1;

    /**
     *  Specify the path to the root directory of the project after applying the commit.
     *  This is useful when it is used on multi-modules project.
     */
    @Parameter(property = "path-repo-v2")
    private String pathToRepositoryV2;

    /**
     *  Specify the path to a json file that contains the list of test methods that is considered to compute the delta omega.
     */
    @Parameter(property = "path-considered-test-method-names", defaultValue = "consideredTestMethods.json")
    private String pathToJSONConsideredTestMethodNames;

    /**
     *  Specify the path to a json file that contains the list of test methods that are executing the additions of the commit.
     */
    @Parameter(property = "path-exec-lines-additions", defaultValue = "exec_additions.json")
    private String pathToExecLinesAdditions;

    /**
     *  Specify the path to a json file that contains the list of test methods that are executing the deletions of the commit.
     */
    @Parameter(property = "path-exec-lines-deletions", defaultValue = "exec_deletions.json")
    private String pathToExecLinesDeletions;

    /**
     *  Specify the path to a json file that contains the list of test methods that are suspicious regarding the version before the commit.
     */
    @Parameter(property = "path-json-suspicious-v2", defaultValue = "suspicious_v1.json")
    private String pathToJSONSuspiciousV1;

    /**
     *  Specify the path to a json file that contains the list of test methods that are suspicious regarding the version after the commit.
     */
    @Parameter(property = "path-json-suspicious-v2", defaultValue = "suspicious_v2.json")
    private String pathToJSONSuspiciousV2;

    // TODO should depend on the report we want to output
    // For now I set by default the path to the template.md for MarkdownMojo
    /**
     * Specify the path to output the report
     */
    @Parameter(property = "path-to-report", defaultValue = ".github/workflows/template.md")
    private String pathToReport;

    /**
     * Enable or disable the suspect (and failer) goals when running diff-jjoules
     */
    @Parameter(property = "suspect", defaultValue = "true")
    private boolean shouldSuspect;

    /**
     * Specify the type of report to generate
     */
    @Parameter(property = "report", defaultValue = "MARKDOWN")
    private String reportType;

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
                    this.pathToReport,
                    ReportEnum.valueOf(this.reportType)
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
        if (this.energySample != null) {
            final Map<String, Long> report = this.energySample.stop();
            JSONUtils.write(configuration.output + "/" + reportPathName + ".json", report);
            configuration.addReport(reportPathName, report);
            this.energySample = null;
        }
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
        if (this.shouldSuspect) {
            this.testFailingInstrumentation();
            this.testSuspicious();
        }
    }

    private void runDiffJJoulesStep(DiffJJoulesMojo step, String messageInCaseOfFailure) {
        try {
            step.run(this.configuration);
        } catch (Exception e) {
            this.end(messageInCaseOfFailure, e);
        }
    }

    private void testSelection() {
        final Properties properties = new Properties();
        properties.setProperty("path-dir-second-version", this.configuration.pathToSecondVersion);
        startMonitoring();
        MavenRunner.runGoals(
                this.project.getBasedir().getAbsolutePath(),
                properties,
                "clean", "eu.stamp-project:dspot-diff-test-selection:3.1.1-SNAPSHOT:list"
        );
        stopMonitoring(this.configuration, "selection");
        final Map<String, List<String>> testsList = CSVReader.readFile(this.configuration.pathToTestListAsCSV);
        if (testsList.isEmpty()) {
            this.end("No test could be selected");
        }
        MavenRunner.runCleanAndCompile(this.configuration.pathToFirstVersion);
        MavenRunner.runCleanAndCompile(this.configuration.pathToSecondVersion);
        this.configuration.setTestsList(testsList);
    }

    private void testInstrumentation() {
        this.runDiffJJoulesStep(new InstrumentationMojo(),"Something went wrong during test instrumentation.");
        MavenRunner.runCleanAndCompile(this.configuration.pathToSecondVersion);
        MavenRunner.runCleanAndCompile(this.pathToPom);
        this.configuration.setClasspathV1(Utils.readClasspathFile(this.project.getBasedir().getAbsolutePath() + "/" + this.classpathPath).split(":"));
        this.configuration.setClasspathV2(Utils.readClasspathFile(this.pathDirSecondVersion + "/" + this.classpathPathV2).split(":"));
    }

    private void deltaComputation() {
        this.runDiffJJoulesStep(new DeltaMojo(), "Something went wrong during delta.");
        this.resetAndCleanBothVersion();
    }

    private void commitMarking() {
        this.runDiffJJoulesStep(new MarkMojo(), "Something went wrong during marking.");
        if (this.configuration.getConsideredTestsNames().isEmpty()) {
            this.end("The energy consumption are too unstable, no method could be considered.");
        }
        MavenRunner.runCleanAndCompile(this.configuration.pathToFirstVersion);
        MavenRunner.runCleanAndCompile(this.configuration.pathToSecondVersion);
    }

    private void testFailingInstrumentation() {
        this.runDiffJJoulesStep(new FailerMojo(), "Something went wrong during failing instrumentation.");
        MavenRunner.runCleanAndCompile(this.pathDirSecondVersion);
        MavenRunner.runCleanAndCompile(this.pathToPom);
    }

    private void testSuspicious() {
        this.runDiffJJoulesStep(new SuspectMojo(), "Something went wrong during suspect");
    }

    private void report() {
        this.configuration.getReportEnum().get().run(configuration);
    }

    private void resetAndCleanBothVersion() {
        this.gitResetHard(this.configuration.pathToRepositoryV1);
        this.gitResetHard(this.configuration.pathToRepositoryV2);
        MavenRunner.runCleanAndCompile(this.configuration.pathToFirstVersion);
        MavenRunner.runCleanAndCompile(this.configuration.pathToSecondVersion);
        this.configuration.setClasspathV1(Utils.readClasspathFile(this.project.getBasedir().getAbsolutePath() + "/" + this.classpathPath).split(":"));
        this.configuration.setClasspathV2(Utils.readClasspathFile(this.pathDirSecondVersion + "/" + this.classpathPathV2).split(":"));
    }

    private void gitResetHard(String pathToFolder) {
        try {
            Git.open(new File(pathToFolder))
                    .reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .call();
            // must delete module-info.java
            try (Stream<Path> walk = Files.walk(Paths.get(pathToFolder))) {
                walk.filter(path -> path.endsWith("module-info.java"))
                        .forEach(path -> path.toFile().delete());
            }
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void end(String reason) {
        this.end(reason, null);
    }

    private void end(String reason, Exception exception) {
        this.stopMonitoring(this.configuration);
        try (final FileWriter writer = new FileWriter(
                this.configuration.output + "/end.txt", false)) {
            writer.write(reason + "\n");
            if (exception != null) {
                for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                    writer.write(stackTraceElement.toString() + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(1);
    }



}
