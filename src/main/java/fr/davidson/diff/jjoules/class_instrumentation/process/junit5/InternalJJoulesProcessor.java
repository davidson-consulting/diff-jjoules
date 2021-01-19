package fr.davidson.diff.jjoules.class_instrumentation.process.junit5;

import fr.davidson.diff.jjoules.class_instrumentation.process.AbstractInternalJJoulesProcessor;
import fr.davidson.diff.jjoules.class_instrumentation.process.JUnitVersion;
import fr.davidson.diff.jjoules.util.NodeManager;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class InternalJJoulesProcessor extends AbstractInternalJJoulesProcessor {

    public static final Predicate<CtMethod<?>> IS_TEST_JUNIT_4_5 = (m) -> m.getAnnotations()
            .stream()
            .anyMatch(ctAnnotation ->
                    ctAnnotation.getType().getQualifiedName().endsWith("Test")
            );

    public InternalJJoulesProcessor(JUnitVersion jUnitVersion) {
        super(jUnitVersion);
    }

    @Override
    public void processSetupAndTearDown(CtMethod<?> ctMethod, CtType<?> testClass) {
        final Factory factory = ctMethod.getFactory();

        final CtMethod<?> init = this.getOrCreateMethod(testClass, this.jUnitVersion.getInitClassFullQualifiedName(), "init");
        final CtMethod<?> cleanUp = this.getOrCreateMethod(testClass, this.jUnitVersion.getTearDownClassFullQualifiedName(), "cleanUp");
        // target
        final CtTypeReference<?> energyTestReference = factory.Type().createReference("org.powerapi.jjoules.junit4.EnergyTest");
        final CtTypeAccess<?> typeAccess = factory.createTypeAccess(energyTestReference);

        // method to call
        final CtExecutableReference<?> beforeTestReference = NodeManager.createExecutableReference(factory, energyTestReference, "beforeTest");
        final CtExecutableReference<?> afterTestReference = NodeManager.createExecutableReference(factory, energyTestReference, "afterTest");

        // parameters
        final CtLiteral<String> classNameLiteral = factory.createLiteral(ctMethod.getDeclaringType().getQualifiedName());
        final CtLiteral<String> testMethodNameLiteral = factory.createLiteral(ctMethod.getSimpleName());

        // Invocations
        final CtInvocation<?> beforeTestInvocation = factory.createInvocation(typeAccess, beforeTestReference, Arrays.asList(classNameLiteral, testMethodNameLiteral));
        final CtInvocation<?> afterTestInvocation = factory.createInvocation(typeAccess, afterTestReference);
        NodeManager.insertOrSetIfNull(init, beforeTestInvocation, false);
        NodeManager.insertOrSetIfNull(cleanUp, afterTestInvocation, true);
    }


    @Override
    public Predicate<CtMethod<?>> getPredicateIsTest() {
        return IS_TEST_JUNIT_4_5;
    }
}
