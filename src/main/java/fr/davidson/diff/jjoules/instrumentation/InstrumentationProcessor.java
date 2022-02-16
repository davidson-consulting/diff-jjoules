package fr.davidson.diff.jjoules.instrumentation;

import eu.stamp_project.testrunner.test_framework.TestFramework;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.PrettyPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 18/01/2022
 */
public class InstrumentationProcessor extends AbstractProcessor<CtMethod<?>> {

    public static final String FOLDER_MEASURES_PATH = "diff-jjoules-measurements";

    public static final String OUTPUT_FILE_NAME = "measurements.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentationProcessor.class);

    protected final Set<CtType<?>> instrumentedTypes;

    protected final Map<String, Set<String>> testsToBeInstrumented;

    protected String rootPathFolder;

    protected String testFolderPath;

    public InstrumentationProcessor(
            final Map<String, Set<String>> testsList,
            String rootPathFolder,
            String testFolderPath) {
        this.instrumentedTypes = new HashSet<>();
        this.testsToBeInstrumented = testsList;
        this.rootPathFolder = rootPathFolder;
        this.testFolderPath = testFolderPath;
    }

    @Override
    public boolean isToBeProcessed(CtMethod<?> candidate) {
        if ((!this.testsToBeInstrumented.isEmpty()) && this.testsToBeInstrumented.values()
                .stream()
                .noneMatch(tests -> tests.contains(candidate.getSimpleName()))) {
            return false;
        }
        CtType<?> declaringType = candidate.getDeclaringType();
        if (declaringType == null) {
            return false;
        }
        TestFramework.init(candidate.getFactory());
        return (TestFramework.isJUnit4(candidate) || TestFramework.isJUnit5(candidate)) &&
                (
                        this.mustInstrument(declaringType.getQualifiedName(), candidate.getSimpleName()) ||
                        this.checkInheritance(candidate)
                );
    }

    private boolean mustInstrument(String testClassQualifiedName, String testMethodName) {
        return this.testsToBeInstrumented.isEmpty() || (
                this.testsToBeInstrumented.containsKey(testClassQualifiedName) &&
                        this.testsToBeInstrumented
                                .get(testClassQualifiedName)
                                .contains(testMethodName));
    }

    private boolean checkInheritance(CtMethod<?> candidate) {
        final CtType<?> declaringType = candidate.getDeclaringType();
        return candidate.getFactory().Type().getAll()
                .stream()
                .filter(type -> type.getSuperclass() != null)
                .filter(type -> type.getSuperclass().getDeclaration() != null)
                .filter(type -> type.getSuperclass().getTypeDeclaration().equals(declaringType))
                .anyMatch(ctType -> this.mustInstrument(ctType.getQualifiedName(), candidate.getSimpleName()));
    }

    @Override
    public void processingDone() {
        this.addShutdownHookToReport(this.instrumentedTypes.stream().findFirst().get());
        this.instrumentedTypes.forEach(this::processingDone);
        LOGGER.info("{} instrumented test classes have been printed!", this.instrumentedTypes.size());
    }

    private void processingDone(CtType<?> type) {
        this.printCtType(type);
        final File outputMeasureFd = new File(this.rootPathFolder + Constants.FILE_SEPARATOR + FOLDER_MEASURES_PATH + Constants.FILE_SEPARATOR);
        if (outputMeasureFd.exists()) {
            try {
                FileUtils.deleteDirectory(outputMeasureFd);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        outputMeasureFd.mkdir();
    }

    private void addShutdownHookToReport(CtType<?> type) {
        LOGGER.info("addShutdownHookToReport {}", type.getQualifiedName());
        final Factory factory = type.getFactory();
        final CtAnonymousExecutable anonymousExecutable = factory.createAnonymousExecutable();
        final String outputPathname = Constants.joinFiles(this.rootPathFolder, FOLDER_MEASURES_PATH, OUTPUT_FILE_NAME);
        anonymousExecutable.setBody(factory.createCodeSnippetStatement(
                        "   java.lang.Runtime.getRuntime().addShutdownHook(" +
                        "       new java.lang.Thread() {" +
                        "           @java.lang.Override" +
                        "           public void run() {" +
                        "               fr.davidson.tlpc.sensor.TLPCSensor.report(" +
                        "                   \"" + outputPathname + "\"" +
                        "               );" +
                        "           }" +
                        "       }" +
                        "   )"
        ));
        anonymousExecutable.setModifiers(Collections.singleton(ModifierKind.STATIC));
        type.addTypeMember(anonymousExecutable);
    }

    protected void printCtType(CtType<?> type) {
        final File directory = new File(this.rootPathFolder + Constants.FILE_SEPARATOR + this.testFolderPath);
        type.getFactory().getEnvironment().setSourceOutputDirectory(directory);
        final PrettyPrinter prettyPrinter = type.getFactory().getEnvironment().createPrettyPrinter();
        final String fileName = this.rootPathFolder +  Constants.FILE_SEPARATOR  +
                testFolderPath +  Constants.FILE_SEPARATOR  +
                type.getQualifiedName().replaceAll("\\.",  Constants.FILE_SEPARATOR ) + ".java";
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
        final Factory factory = ctMethod.getFactory();
        final CtClass<?> parentClass = ctMethod.getParent(CtClass.class);
        final String identifier = new FullQualifiedName(parentClass.getQualifiedName(), ctMethod.getSimpleName()).toString();
        ctMethod.getBody().insertBegin(
                factory.createCodeSnippetStatement("fr.davidson.tlpc.sensor.TLPCSensor.start(\"" + identifier + "\")")
        );
        ctMethod.getBody().insertEnd(
                factory.createCodeSnippetStatement("fr.davidson.tlpc.sensor.TLPCSensor.stop(\"" + identifier + "\")")
        );
        this.instrumentedTypes.add(ctMethod.getDeclaringType());
    }
}
