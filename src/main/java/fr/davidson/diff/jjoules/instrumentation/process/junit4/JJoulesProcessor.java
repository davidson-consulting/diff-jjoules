package fr.davidson.diff.jjoules.instrumentation.process.junit4;

import fr.davidson.diff.jjoules.instrumentation.process.AbstractJJoulesProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class JJoulesProcessor extends AbstractJJoulesProcessor {

    public JJoulesProcessor(final Map<String, Set<String>> testsList, String rootPathFolder, String testFolderPath) {
        super(testsList, rootPathFolder, testFolderPath);
    }

    @Override
    public void process(CtMethod<?> ctMethod) {
        final Factory factory = ctMethod.getFactory();

        // target
        final CtTypeReference<?> energyTestReference = factory.Type().createReference("org.powerapi.jjoules.junit4.EnergyTest");
        final CtTypeAccess<?> typeAccess = factory.createTypeAccess(energyTestReference);

        // method to call
        final CtExecutableReference<?> beforeTestReference = factory.createExecutableReference();
        beforeTestReference.setDeclaringType(energyTestReference);
        beforeTestReference.setStatic(true);
        beforeTestReference.setSimpleName("beforeTest");
        final CtExecutableReference<?> afterTestReference = factory.createExecutableReference();
        afterTestReference.setStatic(true);
        afterTestReference.setDeclaringType(energyTestReference);
        afterTestReference.setSimpleName("afterTest");

        // parameters
        final CtLiteral<String> classNameLiteral = factory.createLiteral(ctMethod.getDeclaringType().getQualifiedName());
        final CtLiteral<String> testMethodNameLiteral = factory.createLiteral(ctMethod.getSimpleName());

        // Invocations
        final CtInvocation<?> beforeTestInvocation = factory.createInvocation(typeAccess, beforeTestReference, Arrays.asList(classNameLiteral, testMethodNameLiteral));
        final CtInvocation<?> afterTestInvocation = factory.createInvocation(typeAccess, afterTestReference);

        ctMethod.getBody().insertBegin(beforeTestInvocation);
        ctMethod.getBody().insertEnd(afterTestInvocation);
        this.instrumentedTypes.add(ctMethod.getDeclaringType());
    }
}
