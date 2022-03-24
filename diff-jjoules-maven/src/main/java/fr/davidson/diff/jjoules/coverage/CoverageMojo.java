package fr.davidson.diff.jjoules.coverage;

import eu.stamp_project.diff_test_selection.clover.CloverExecutor;
import eu.stamp_project.diff_test_selection.clover.CloverReader;
import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.testrunner.listener.CoveredTestResultPerTestMethod;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.coverage.CoverageComputation;
import fr.davidson.diff.jjoules.util.coverage.detection.TestDetector;
import fr.davidson.diff.jjoules.util.wrapper.Wrapper;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 04/03/2022
 */
@Mojo(name = "coverage")
public class CoverageMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "classpath", property = "classpath-path")
    private String classpathPathFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final String classpath;
        if (new File(classpathPathFile).isAbsolute()) {
            classpath = Utils.readClasspathFile(classpathPathFile);
        } else {
            classpath = Utils.readClasspathFile(Constants.joinFiles(this.project.getBasedir().getAbsolutePath(), this.classpathPathFile));
        }
        getLog().info("Classpath:" + classpath);
        final String rootAbsolutePath = this.project.getBasedir().getAbsolutePath();
        final Wrapper wrapper = WrapperEnum.MAVEN.getWrapper();
        final List<String> allFullQualifiedNameTestClasses =
                new TestDetector(Constants.joinFiles(rootAbsolutePath, wrapper.getPathToTestFolder()))
                        .getAllFullQualifiedNameTestClasses();
        System.out.println(allFullQualifiedNameTestClasses);
        final CoveredTestResultPerTestMethod coverage = CoverageComputation.getCoverage(
                rootAbsolutePath,
                classpath,
                classpath.contains("junit-4"),
                allFullQualifiedNameTestClasses,
                wrapper.getBinaries()
        );
        new CloverExecutor().instrumentAndRunTest(rootAbsolutePath);
        final Coverage cloverCoverage = new CloverReader().read(rootAbsolutePath);
        JSONUtils.write(project.getBasedir() + "/clover_coverage.json", cloverCoverage);
        JSONUtils.write(project.getBasedir() + "/coverage.json", coverage);
        JSONUtils.write(project.getBasedir() + "/converted_coverage.json", CoverageComputation.convert(coverage));
    }

}
