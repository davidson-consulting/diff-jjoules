package fr.davidson.diff.jjoules.markdown.mojo;

import fr.davidson.diff.jjoules.markdown.Main;
import fr.davidson.diff.jjoules.markdown.configuration.Configuration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/06/2021
 */
@Mojo(name = "markdown")
public class MarkdownMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;


    /**
     *
     */
    @Parameter(property = "path-delta-omega")
    private String pathToDeltaOmega;

    /**
     *
     */
    @Parameter(property = "path-delta-json")
    private String pathDeltaJSON;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            getLog().info("Running on:");
            getLog().info(this.project.getBasedir().getAbsolutePath());
            Main.run(
                    new Configuration(
                            this.pathToDeltaOmega,
                            this.pathDeltaJSON
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
