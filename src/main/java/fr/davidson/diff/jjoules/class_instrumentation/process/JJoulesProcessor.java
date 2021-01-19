package fr.davidson.diff.jjoules.class_instrumentation.process;

import fr.davidson.diff.jjoules.util.Checker;
import fr.davidson.diff.jjoules.util.NodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.PrettyPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 01/12/2020
 */
public class JJoulesProcessor extends AbstractProcessor<CtMethod<?>> {

    private final int NB_DUPLICATION_TEST_METHOD = 9;

    private static final Logger LOGGER = LoggerFactory.getLogger(JJoulesProcessor.class);

    private static final String TEST_FOLDER_PATH = "src/test/java/";

    protected final Set<CtType<?>> instrumentedTypes;

    protected final Map<String, List<String>> testsToBeInstrumented;

    protected String rootPathFolder;

    private JUnitVersion jUnitVersion;

    public JJoulesProcessor(final Map<String, List<String>> testsList, String rootPathFolder) {
        this.instrumentedTypes = new HashSet<>();
        this.testsToBeInstrumented = testsList;
        this.rootPathFolder = rootPathFolder;
    }

    @Override
    public boolean isToBeProcessed(CtMethod<?> candidate) {
        if (this.testsToBeInstrumented.values()
                .stream()
                .noneMatch(tests -> tests.contains(candidate.getSimpleName()))) {
            return false;
        }
        CtType<?> declaringType = candidate.getDeclaringType();
        if (declaringType == null) {
            return false;
        }
        return Checker.mustInstrument(this.testsToBeInstrumented, declaringType.getQualifiedName(), candidate.getSimpleName()) ||
                Checker.checkInheritance(this.testsToBeInstrumented, candidate);
    }

    @Override
    public void processingDone() {
        LOGGER.info("Processing Done...");
        this.instrumentedTypes.forEach(this::printCtType);
    }

    private void printCtType(CtType<?> type) {
        final File directory = new File(this.rootPathFolder + "/" + TEST_FOLDER_PATH);
        type.getFactory().getEnvironment().setSourceOutputDirectory(directory);
        final PrettyPrinter prettyPrinter = type.getFactory().getEnvironment().createPrettyPrinter();
        final String fileName = this.rootPathFolder + "/" +
                TEST_FOLDER_PATH + "/" +
                type.getQualifiedName().replaceAll("\\.", "/") + ".java";
        LOGGER.info("Printing {} to {}", type.getQualifiedName(), fileName);
        try (final FileWriter write = new FileWriter(fileName)) {
            write.write(prettyPrinter.printTypes(type));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRootPathFolder(String rootPathFolder) {
        this.rootPathFolder = rootPathFolder;
    }

    @Override
    public void process(CtMethod<?> ctMethod) {
        System.out.println("Processing " + ctMethod.getDeclaringType().getQualifiedName() + "#" + ctMethod.getSimpleName());
        final CtType<?> originalTestClass = ctMethod.getParent(CtType.class);
        this.jUnitVersion = getJUnitVersion(ctMethod);
        final AbstractInternalJJoulesProcessor internalProcessor = this.jUnitVersion.getInternalProcessor();
        final CtMethod<?> clone = ctMethod.clone();
        final CtType<?> testClass = originalTestClass.clone();
        originalTestClass.getPackage().addType(testClass);
        testClass.setSimpleName(testClass.getSimpleName() + "_" + ctMethod.getSimpleName());
        NodeManager.replaceAllReferences(originalTestClass, clone, testClass);
        NodeManager.replaceAllReferences(originalTestClass, testClass, testClass);
        NodeManager.removeOtherMethods(ctMethod, testClass, internalProcessor.getPredicateIsTest());
        duplicateTestMethodToMeasure(clone, testClass);
        testClass.removeMethod(ctMethod);
        internalProcessor.processSetupAndTearDown(ctMethod, testClass);
        this.instrumentedTypes.add(testClass);
    }

    private JUnitVersion getJUnitVersion(CtMethod<?> ctMethod) {
        for (JUnitVersion junitVersion : JUnitVersion.values()) {
            if (junitVersion.getInternalProcessor().isTestOfThisVersion(ctMethod)) {
                return junitVersion;
            }
        }
        return JUnitVersion.JUNIT4;
    }

    private void duplicateTestMethodToMeasure(CtMethod<?> ctMethod, CtType<?> testClass) {
        for (int i = 0; i < NB_DUPLICATION_TEST_METHOD; i++) {
            final CtMethod<?> clone = ctMethod.clone();
            clone.setSimpleName(clone.getSimpleName() + "_" + i);
            testClass.addMethod(clone);
        }
    }

}
