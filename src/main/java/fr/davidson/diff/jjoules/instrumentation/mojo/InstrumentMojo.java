package fr.davidson.diff.jjoules.instrumentation.mojo;

import fr.davidson.diff.jjoules.instrumentation.Main;
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
public class InstrumentMojo extends AbstractMojo {

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
    @Parameter(property = "classpath-path-v1", defaultValue = "classpath")
    private String classpathPath;

    /**
     * [Optional] Specify the path to a file that contains the full classpath of the project.
     * We advise use to use the following goal right before this one :
     * dependency:build-classpath -Dmdep.outputFile=classpath
     */
    @Parameter(property = "classpath-path-v2", defaultValue = "classpath")
    private String classpathPathV2;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final String classpath;
        final String classpathV2;
        try {
            final String module = this.pathDirSecondVersion == null || this.pathDirSecondVersion.isEmpty() ? "" : this.project.getBasedir().getAbsolutePath().substring(this.pathDirSecondVersion.length());
            getLog().info("Running on:");
            getLog().info(this.project.getBasedir().getAbsolutePath());
            getLog().info(this.pathDirSecondVersion + "/" + module);
            classpath = this.readClasspathFile(this.project.getBasedir().getAbsolutePath() + "/" + this.classpathPath);
            classpathV2 = this.pathDirSecondVersion == null || this.pathDirSecondVersion.isEmpty() ? "" : this.readClasspathFile(this.pathDirSecondVersion + "/" + module + "/" + this.classpathPathV2);
            final boolean junit4 = !classpath.contains("junit-jupiter-engine-5") && (classpath.contains("junit-4") || classpath.contains("junit-3"));
            if (junit4) {
                getLog().info("Enable JUnit4 mode");
            }
            Main.main(
                    new String[]{
                            "--path-dir-first-version", this.project.getBasedir().getAbsolutePath(),
                            "--path-dir-second-version", this.pathDirSecondVersion == null || this.pathDirSecondVersion.isEmpty() ? "" : this.pathDirSecondVersion + "/" + module,
                            "--tests-list", this.testsList,
                            "--classpath-v1", classpath,
                            "--classpath-v2", classpathV2,
                            junit4 ? "--junit4" : ""
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