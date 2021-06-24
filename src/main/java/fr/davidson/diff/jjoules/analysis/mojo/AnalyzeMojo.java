package fr.davidson.diff.jjoules.analysis.mojo;

import fr.davidson.diff.jjoules.analysis.Main;
import fr.davidson.diff.jjoules.analysis.configuration.Configuration;
import fr.davidson.diff.jjoules.analysis.output.ReportEnum;
import fr.davidson.diff.jjoules.localization.select.SelectorEnum;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Deprecated
@Mojo(name = "analyze")
public class AnalyzeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * [Mandatory] Specify the path to root directory of the project in the second version.
     */
    @Parameter(property = "path-dir-second-version")
    private String pathDirSecondVersion;

    /**
     *
     */
    @Parameter(property = "path-data-json-first-version")
    private String pathJSONDataFirstVersion;

    /**
     *
     */
    @Parameter(property = "path-data-json-second-version")
    private String pathJSONDataSecondVersion;

    /**
     * [Optional] Specify the path of a diff file. If it is not specified, it will be computed using diff command line.
     */
    @Parameter(defaultValue = "", property = "path-to-diff")
    private String pathToDiff;

    /**
     *
     */
    @Parameter(defaultValue = "JSON", property = "report")
    private String reportType;

    /**
     *
     */
    @Parameter(defaultValue = "", property = "output-path")
    private String outputPath;

    private static final String defaultOutputPath = "target/diff-jjoules";

    /**
     *
     */
    @Parameter(defaultValue = "LargestImpact", property = "selector")
    private String selectorType;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final String module = this.project.getBasedir().getAbsolutePath().substring(this.pathDirSecondVersion.length());
            getLog().info("Running on:");
            getLog().info(this.project.getBasedir().getAbsolutePath());
            getLog().info(this.pathDirSecondVersion + "/" + module);
            Main.run(
                    new Configuration(
                            this.project.getBasedir().getAbsolutePath(),
                            this.pathDirSecondVersion + "/" + module,
                            this.pathJSONDataFirstVersion,
                            this.pathJSONDataSecondVersion,
                            this.pathToDiff,
                            ReportEnum.fromReportEnumValue(
                                    this.reportType,
                                    this.outputPath.isEmpty() ?
                                            this.project.getBasedir().getAbsolutePath() + "/" + defaultOutputPath : this.outputPath)
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
