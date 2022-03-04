package fr.davidson.diff.jjoules.mutation.mojo;

import fr.davidson.diff.jjoules.mutation.Configuration;
import fr.davidson.diff.jjoules.mutation.Main;
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
 * on 28/02/2022
 */
@Mojo(name = "mutate")
public class DiffJJoulesMutationMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    @Parameter(property = "method-list-path", required = true)
    private String methodListFilePath;

    @Parameter(property = "consumption", defaultValue = "10000")
    private long consumption;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Main.run(
                new Configuration(
                    this.project.getBasedir().getAbsolutePath(),
                    this.methodListFilePath,
                    this.consumption,
                    WrapperEnum.MAVEN
            )
        );
    }
}
