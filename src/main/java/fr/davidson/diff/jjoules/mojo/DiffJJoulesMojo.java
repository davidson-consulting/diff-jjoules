package fr.davidson.diff.jjoules.mojo;

import fr.davidson.diff.jjoules.Main;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 26/11/2020
 */
@Mojo(name = "instrument")
public class DiffJJoulesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     *	[Mandatory] Specify the path to root directory of the project in the second version.
     */
    @Parameter(property = "path-dir-second-version")
    private String pathDirSecondVersion;

    /**
     *	[Mandatory] Specify the path to a CSV file that contains the list of tests to be instrumented.
     */
    @Parameter(property = "tests-list")
    private String testsList;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Main.main(
            new String[] {
                    "--path-dir-first-version", this.project.getBasedir().getAbsolutePath(),
                    "--path-dir-second-version", this.pathDirSecondVersion,
                    "--tests-list", this.testsList
            }
        );
    }
}
