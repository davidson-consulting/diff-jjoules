package fr.davidson.diff.jjoules.failer.processor;

import fr.davidson.diff.jjoules.instrumentation.InstrumentationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/06/2021
 */
public class MakeTestFailingProcessor extends InstrumentationProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MakeTestFailingProcessor.class);

    public MakeTestFailingProcessor(Map<String, Set<String>> testsToBeInstrumented, String rootPathFolder, String testFolderPath) {
        super(testsToBeInstrumented, rootPathFolder, testFolderPath);
    }

    @Override
    public void process(CtMethod<?> ctMethod) {
        final Factory factory = ctMethod.getFactory();
        // target
        // TODO WARNING, we use here JUnit 3 it might be a source of issues
        final CtTypeReference<?> assertReference = factory.Type().createReference("junit.framework.Assert");
        final CtTypeAccess<?> typeAccess = factory.createTypeAccess(assertReference);

        // method to call
        final CtExecutableReference<?> fail = factory.createExecutableReference();
        fail.setDeclaringType(assertReference);
        fail.setStatic(true);
        fail.setSimpleName("fail");
        // Invocations
        final CtInvocation<?> failInvocation = factory.createInvocation(typeAccess, fail);
        ctMethod.getBody().insertEnd(failInvocation);
        this.instrumentedTypes.add(ctMethod.getDeclaringType());
    }

    @Override
    public void processingDone() {
        this.instrumentedTypes.forEach(this::printCtType);
        LOGGER.info("{} instrumented test classes have been printed!", this.instrumentedTypes.size());
    }

}
