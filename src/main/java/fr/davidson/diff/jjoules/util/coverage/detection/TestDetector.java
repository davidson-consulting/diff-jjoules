package fr.davidson.diff.jjoules.util.coverage.detection;

import eu.stamp_project.testrunner.test_framework.TestFramework;
import spoon.Launcher;
import spoon.OutputType;
import spoon.reflect.declaration.CtType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 22/11/2021
 */
public class TestDetector {

    private Launcher launcher;

    public TestDetector(String pathToTestFolder) {
        this.launcher = new Launcher();
        this.launcher.addInputResource(pathToTestFolder);
        this.launcher.getEnvironment().setNoClasspath(true);
        this.launcher.getEnvironment().setShouldCompile(false);
        this.launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
        this.launcher.buildModel();
        TestFramework.init(launcher.getFactory());
    }

    public List<CtType<?>> getAllTestClasses() {
        return TestFramework.getAllTestClasses();
    }

    public List<String> getAllFullQualifiedNameTestClasses() {
        return this.getAllTestClasses().stream().map(CtType::getQualifiedName).collect(Collectors.toList());
    }

    public List<String> getAllFullQualifiedNameTestClassesJUnit4() {
        return this.getAllTestClasses()
                .stream()
                .filter(ctType -> ctType.getMethods().stream().anyMatch(TestFramework::isJUnit4))
                .map(CtType::getQualifiedName)
                .collect(Collectors.toList());
    }

    public List<String> getAllFullQualifiedNameTestClassesJUnit5() {
        return this.getAllTestClasses()
                .stream()
                .filter(ctType -> ctType.getMethods().stream().anyMatch(TestFramework::isJUnit5))
                .map(CtType::getQualifiedName)
                .collect(Collectors.toList());
    }

}
