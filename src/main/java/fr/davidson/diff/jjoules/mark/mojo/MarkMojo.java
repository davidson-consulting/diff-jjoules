package fr.davidson.diff.jjoules.mark.mojo;

import fr.davidson.diff.jjoules.mark.Main;
import fr.davidson.diff.jjoules.mark.configuration.Configuration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
@Mojo(name = "mark")
public class MarkMojo extends AbstractMojo {

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
     * [Mandatory] Specify the path to a CSV file that contains the list of tests to be instrumented.
     */
    @Parameter(property = "tests-list")
    private String testsList;

    /**
     * [Optional] Specify the path of a diff file. If it is not specified, it will be computed using diff command line.
     */
    @Parameter(defaultValue = "", property = "path-to-diff")
    private String pathToDiff;

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
                            this.testsList
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
