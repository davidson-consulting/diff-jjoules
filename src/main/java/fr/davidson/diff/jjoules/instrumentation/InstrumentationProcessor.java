package fr.davidson.diff.jjoules.instrumentation;

import fr.davidson.diff.jjoules.instrumentation.process.AbstractJJoulesProcessor;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentationProcessor.class);

    protected final Set<CtType<?>> instrumentedTypes;

    protected final Map<String, Set<String>> testsToBeInstrumented;

    protected String rootPathFolder;

    protected String testFolderPath;

    public InstrumentationProcessor(final Map<String, Set<String>> testsList, String rootPathFolder, String testFolderPath) {
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
        this.instrumentedTypes.forEach(this::processingDone);
        LOGGER.info("{} instrumented test classes have been printed!", this.instrumentedTypes.size());
    }

    private void processingDone(CtType<?> type) {
        final Factory factory = type.getFactory();
        final CtAnonymousExecutable anonymousExecutable = factory.createAnonymousExecutable();
        anonymousExecutable.setBody(factory.createCodeSnippetStatement(
                "Runtime.getRuntime().addShutdownHook(new Thread(() ->" +
                        "new fr.davidson.tlpc.sensor.TLPCSensor().report(\"" +
                        this.rootPathFolder + Constants.FILE_SEPARATOR + type.getQualifiedName() + ".json\"" +
                        ")" +
                    ")" +
                ")"
            )
        );
        anonymousExecutable.setModifiers(Collections.singleton(ModifierKind.STATIC));
        type.addTypeMember(anonymousExecutable);
        this.printCtType(type);
    }

    private void printCtType(CtType<?> type) {
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
        final String fullQualifiedTestMethodName = new FullQualifiedName(ctMethod.getDeclaringType().getQualifiedName(), ctMethod.getSimpleName()).toString();
        ctMethod.getBody().insertBegin(
                factory.createCodeSnippetStatement("new fr.davidson.tlpc.sensor.TLPCSensor().start()")
        );
        ctMethod.getBody().insertEnd(
                factory.createCodeSnippetStatement("new fr.davidson.tlpc.sensor.TLPCSensor().stop(\"" + fullQualifiedTestMethodName + "\")")
        );
        this.instrumentedTypes.add(ctMethod.getDeclaringType());
    }
}
