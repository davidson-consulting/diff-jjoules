package fr.davidson.diff.jjoules.failer.processor;

import fr.davidson.diff.jjoules.instrumentation.process.AbstractJJoulesProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/06/2021
 */
public class MakeTestFailingProcessor extends AbstractJJoulesProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MakeTestFailingProcessor.class);

    public MakeTestFailingProcessor(Map<String, List<String>> testsToBeInstrumented, String rootPathFolder) {
        super(testsToBeInstrumented, rootPathFolder);
    }

    @Override
    public void process(CtMethod<?> ctMethod) {
        System.out.println("Processing " + ctMethod.getDeclaringType().getQualifiedName() + "#" + ctMethod.getSimpleName());
        final Factory factory = ctMethod.getFactory();
        // target
        final CtTypeReference<?> assertReference = factory.Type().createReference("org.junit.Assert");
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


}
