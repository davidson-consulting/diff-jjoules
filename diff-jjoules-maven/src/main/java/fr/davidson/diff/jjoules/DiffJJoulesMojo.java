package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.report.ReportEnum;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
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

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    /**
     * Specify the path to root directory of the project in the second version.
     */
    @Parameter(property = "path-dir-second-version", required = true)
    protected String pathDirSecondVersion;

    /**
     * Number of execution to do to measure the energy consumption of tests.
     */
    @Parameter(property = "iterations", defaultValue = "5")
    protected int iterations;

    /**
     *  Specify the path to the root directory of the project before applying the commit.
     *  This is useful when it is used on multi-modules project.
     */
    @Parameter(property = "path-repo-v1", defaultValue = "")
    private String pathToRepositoryV1;

    /**
     *  Specify the path to the root directory of the project after applying the commit.
     *  This is useful when it is used on multi-modules project.
     */
    @Parameter(property = "path-repo-v2", defaultValue = "")
    private String pathToRepositoryV2;

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
     *  Specify the path to output the files that produces this plugin
     */
    @Parameter(property = "output-path", defaultValue = "diff-jjoules")
    protected String outputPath;

    /**
     * Specify the type of report to generate
     */
    @Parameter(property = "report", defaultValue = "MARKDOWN")
    private String reportType;

    // TODO should depend on the report we want to output
    // For now I set by default the path to the template.md for MarkdownMojo
    /**
     * Specify the path to output the report
     */
    @Parameter(property = "path-to-report", defaultValue = ".github/workflows/template.md")
    private String pathToReport;

    @Parameter(property = "should-report", defaultValue = "false")
    private boolean shouldReport;

    @Parameter(property = "measure", defaultValue = "false")
    private boolean measureEnergyConsumption;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
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
            final Configuration configuration = this.getConfiguration();
            final DiffJJoulesStep diffJJoulesStep = this.getStep();
            diffJJoulesStep.run(configuration);
            if (this.shouldReport) {
                diffJJoulesStep.report();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Configuration getConfiguration() {
        return new Configuration(
                this.project.getBasedir().getAbsolutePath() + "/",
                this.pathDirSecondVersion == null || this.pathDirSecondVersion.isEmpty() ? "" : this.pathDirSecondVersion,
                this.iterations,
                this.outputPath,
                this.pathToRepositoryV1,
                this.pathToRepositoryV2,
                this.pathToReport,
                this.shouldSuspect,
                this.shouldMark,
                ReportEnum.valueOf(this.reportType),
                WrapperEnum.MAVEN,
                measureEnergyConsumption
        );
    }

    protected DiffJJoulesStep getStep() {
        return new DiffJJoulesStep();
    }
}
