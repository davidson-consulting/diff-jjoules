package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.report.ReportEnum;
import fr.davidson.diff.jjoules.util.Utils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

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
     * Specify the path of a diff file. If it is not specified, it will be computed using diff command line.
     */
    @Parameter(defaultValue = "", property = "path-to-diff")
    private String pathToDiff;

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
     * Enable or disable the mark (and the suspect and the failer) goals when running diff-jjoules
     */
    @Parameter(property = "mark", defaultValue = "true")
    private boolean shouldMark;

    /**
     * Specify the type of report to generate
     */
    @Parameter(property = "report", defaultValue = "MARKDOWN")
    private String reportType;

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
            Configuration configuration = new Configuration(
                    this.project.getBasedir().getAbsolutePath(),
                    this.pathDirSecondVersion == null || this.pathDirSecondVersion.isEmpty() ? "" : this.pathDirSecondVersion,
                    this.classpathPath,
                    this.classpathPathV2,
                    classpath.split(":"),
                    classpathV2.split(":"),
                    junit4,
                    this.iterations,
                    this.outputPath,
                    this.pathToDiff,
                    this.pathToRepositoryV1,
                    this.pathToRepositoryV2,
                    this.pathToReport,
                    this.shouldSuspect,
                    this.shouldMark,
                    ReportEnum.valueOf(this.reportType)
            );
            final DiffJJoulesStep diffJJoulesStep = this.getStep();
            diffJJoulesStep.run(configuration);
            diffJJoulesStep.report();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected DiffJJoulesStep getStep() {
        return new DiffJJoulesStep();
    }
}
