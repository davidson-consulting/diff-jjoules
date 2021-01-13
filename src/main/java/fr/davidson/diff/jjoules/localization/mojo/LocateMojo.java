package fr.davidson.diff.jjoules.localization.mojo;

import fr.davidson.diff.jjoules.localization.Main;
import fr.davidson.diff.jjoules.localization.configuration.Configuration;
import fr.davidson.diff.jjoules.localization.output.ReportEnum;
import fr.davidson.diff.jjoules.localization.select.SelectorEnum;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "locate")
public class LocateMojo extends AbstractMojo {

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
    @Parameter(defaultValue = "", property = "path-data-json-first-version")
    private String pathJSONDataFirstVersion;

    /**
     *
     */
    @Parameter(defaultValue = "", property = "path-data-json-second-version")
    private String pathJSONDataSecondVersion;

    /**
     * [Mandatory] Specify the test used to locate the potential faulty lines
     * The format must be the following:
     * my.package.MyTestClass#myTestMethod1+myTestMethod2,my.package.MyTestClass2#myTestMethod3+myTestMethod4
     * That is to say, the list of test classes separated by a comma ',', and for each test class, the list of test method
     * separated with a plus '+'. The test class and the list of test method is separated by a sharp '#'
     */
    @Parameter(defaultValue = "", property = "test")
    private String test;

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
                            this.test,
                            this.pathToDiff,
                            ReportEnum.fromReportEnumValue(
                                    this.reportType,
                                    this.outputPath.isEmpty() ?
                                            this.project.getBasedir().getAbsolutePath() + "/" + defaultOutputPath : this.outputPath),
                            SelectorEnum.fromSelectorEnumValue(this.selectorType)
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
