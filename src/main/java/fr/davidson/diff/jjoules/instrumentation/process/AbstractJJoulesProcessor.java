package fr.davidson.diff.jjoules.instrumentation.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
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
public abstract class AbstractJJoulesProcessor extends AbstractProcessor<CtMethod<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJJoulesProcessor.class);

    protected final Set<CtType<?>> instrumentedTypes;

    protected final Map<String, List<String>> testsToBeInstrumented;

    protected String rootPathFolder;

    private static final String TEST_FOLDER_PATH = "src/test/java/";

    public AbstractJJoulesProcessor(final Map<String, List<String>> testsList, String rootPathFolder) {
        this.instrumentedTypes = new HashSet<>();
        this.testsToBeInstrumented = testsList;
        this.rootPathFolder = rootPathFolder;
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
        return this.mustInstrument(declaringType.getQualifiedName(), candidate.getSimpleName()) ||
                this.checkInheritance(candidate);
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
       /* for (CtType<?> instrumentedType : this.instrumentedTypes) {
            this.addWarmupMethods(instrumentedType);
        }*/
        this.instrumentedTypes.forEach(this::printCtType);
    }

    protected void duplicateMethodForWarmup(CtType<?> testClass, CtMethod<?> method) {
        for (int i = 0; i < 5; i++) {
            final CtMethod<?> clone = method.clone();
            clone.setSimpleName("aaa_" + i + "_" + clone.getSimpleName());
            testClass.addMethod(clone);
        }
    }

    private void addWarmupMethods(CtType<?> instrumentedType) {
        final Factory factory = instrumentedType.getFactory();
        final CtMethod warmup = factory.createMethod();

        warmup.setModifiers(Collections.singleton(ModifierKind.PUBLIC));
        warmup.addAnnotation(factory.createAnnotation(factory.createReference("org.junit.jupiter.api.Test")));
        warmup.setType(factory.Type().VOID_PRIMITIVE);
        for (int i = 0; i < 10; i++) {
            final CtMethod<?> clone = warmup.clone();
            clone.setBody(factory.createCodeSnippetStatement("System.out.println(\"warmup" + i + "\")"));
            clone.setSimpleName("aaaaa_warmup" + i);
            instrumentedType.addMethod(clone);
        }
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
}
