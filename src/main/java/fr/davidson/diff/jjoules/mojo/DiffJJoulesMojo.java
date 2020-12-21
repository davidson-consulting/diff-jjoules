package fr.davidson.diff.jjoules.mojo;

import fr.davidson.diff.jjoules.Main;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

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
     * [Mandatory] Specify the path to root directory of the project in the second version.
     */
    @Parameter(property = "path-dir-second-version")
    private String pathDirSecondVersion;

    /**
     * [Mandatory] Specify the path to a CSV file that contains the list of tests to be instrumented.
     */
    @Parameter(property = "tests-list")
    private String testsList;

    /**
     * [Optional] Specify the path to a file that contains the full classpath of the project.
     * We advise use to use the following goal right before this one :
     * dependency:build-classpath -Dmdep.outputFile=classpath
     */
    @Parameter(property = "classpath-path", defaultValue = "classpath")
    private String classpathPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final String classpath;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.project.getBasedir().getAbsolutePath() + "/" + this.classpathPath))) {
            final String module = this.project.getBasedir().getAbsolutePath().substring(this.pathDirSecondVersion.length());
            getLog().info("Running on:");
            getLog().info(this.project.getBasedir().getAbsolutePath());
            getLog().info(this.pathDirSecondVersion + "/" + module);
            classpath = reader.lines().collect(Collectors.joining(":"));
            final boolean junit4 = classpath.contains("junit-4");
            if (junit4) {
                getLog().info("Enable JUnit4 mode");
            }
            Main.main(
                    new String[]{
                            "--path-dir-first-version", this.project.getBasedir().getAbsolutePath(),
                            "--path-dir-second-version", this.pathDirSecondVersion + "/" + module,
                            "--tests-list", this.testsList,
                            "--classpath", classpath,
                            junit4 ? "--junit4" : ""
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
