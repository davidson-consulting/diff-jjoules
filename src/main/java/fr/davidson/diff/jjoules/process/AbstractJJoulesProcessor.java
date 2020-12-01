package fr.davidson.diff.jjoules.process;

import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.support.JavaOutputProcessor;

import java.io.File;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 01/12/2020
 */
public abstract class AbstractJJoulesProcessor extends AbstractProcessor<CtMethod<?>> {

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
        return candidate.getDeclaringType() != null &&
                this.testsToBeInstrumented.containsKey(candidate.getDeclaringType().getQualifiedName()) &&
                this.testsToBeInstrumented.get(
                        candidate.getDeclaringType().getQualifiedName()
                ).contains(candidate.getSimpleName());
    }

    @Override
    public void processingDone() {
        this.instrumentedTypes.forEach(this::printCtType);
    }

    private void printCtType(CtType<?> type) {
        final File directory = new File(this.rootPathFolder + "/" + TEST_FOLDER_PATH);
        final Environment env = type.getFactory().getEnvironment();
        try {
            env.setAutoImports(true);
            env.setNoClasspath(false);
            env.setCommentEnabled(true);
            JavaOutputProcessor processor = new JavaOutputProcessor(env.createPrettyPrinterAutoImport());
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
