package fr.davidson.diff.jjoules.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.support.JavaOutputProcessor;

import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
        if (this.testsToBeInstrumented.values()
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
        return this.testsToBeInstrumented.containsKey(testClassQualifiedName) &&
                this.testsToBeInstrumented
                        .get(testClassQualifiedName)
                        .contains(testMethodName);
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
        this.instrumentedTypes.forEach(this::printCtType);
    }

    private void printCtType(CtType<?> type) {
        final File directory = new File(this.rootPathFolder + "/" + TEST_FOLDER_PATH);
        final Environment env = type.getFactory().getEnvironment();
        LOGGER.info("Printing {}", type.getQualifiedName());
        try {
            env.setAutoImports(true);
            env.setNoClasspath(false);
            env.setCommentEnabled(true);
            JavaOutputProcessor processor = new JavaOutputProcessor(env.createPrettyPrinter());
            processor.setFactory(type.getFactory());
            processor.getEnvironment().setSourceOutputDirectory(directory);
            processor.createJavaFile(type);
            env.setAutoImports(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setRootPathFolder(String rootPathFolder) {
        this.rootPathFolder = rootPathFolder;
    }
}
