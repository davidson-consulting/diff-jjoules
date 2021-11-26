package fr.davidson.diff.jjoules.mutation.mojo;

import fr.davidson.diff.jjoules.mutation.Main;
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
 * on 26/05/2021
 */
@Mojo(name = "mutate")
public class UntareJjoulesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * [Mandatory] Specify the path to a CSV file that contains the list of methods names per full qualified names to be mutated.
     * example : fr.davidson.UntareJjoulesMojo;execute
     */
    @Parameter(property = "method-names-per-full-qualified-names")
    private String methodNamesPerFullQualifiedNames;

    /**
     * [Optional] Specify the path to a file that contains the full classpath of the project.
     * We advise use to use the following goal right before this one :
     * dependency:build-classpath -Dmdep.outputFile=classpath
     */
    @Parameter(property = "classpath", defaultValue = "classpath")
    private String classpathPath;

    /**
     * [Optional] Specify the amount of energy to be consumed by the mutation.
     */
    @Parameter(property = "energy-to-consume", defaultValue = "10000")
    private long energyToConsume;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final String classpath;
        final String classpathV2;
        try {
            getLog().info("Running on:");
            getLog().info(this.project.getBasedir().getAbsolutePath());
            classpath = this.readClasspathFile(this.project.getBasedir().getAbsolutePath() + "/" + this.classpathPath);
            Main.main(
                    new String[]{
                            "--root-path-dir", this.project.getBasedir().getAbsolutePath(),
                            "--method-names-per-full-qualified-names", this.methodNamesPerFullQualifiedNames,
                            "--classpath", classpath,
                            "--energy-to-consume", this.energyToConsume + ""
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readClasspathFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.lines().collect(Collectors.joining(":"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
