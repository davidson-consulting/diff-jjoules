package fr.davidson.diff.jjoules.mutation;

import spoon.reflect.code.*;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 26/05/2021
 */
public class Utils {

    public static CtMethod<Void> generateMethodConsumeEnergy(final Factory factory) {
        final CtMethod<Void> methodConsumeEnergy = initMethodConsumeEnergy(factory);

        final CtParameter<Long> parameterEnergyToConsume = factory.createParameter();
        parameterEnergyToConsume.setType(factory.Type().LONG_PRIMITIVE);
        parameterEnergyToConsume.setSimpleName("energyToConsume");
        parameterEnergyToConsume.setModifiers(Collections.singleton(ModifierKind.FINAL));
        methodConsumeEnergy.setParameters(Collections.singletonList(parameterEnergyToConsume));

        final CtLocalVariable energySample = getEnergySample(factory);
        methodConsumeEnergy.setBody(energySample);

        final CtLocalVariable random = factory.createLocalVariable(
                factory.Type().LONG_PRIMITIVE,
                "random",
                factory.createLiteral(0L)
        );
        methodConsumeEnergy.getBody().addStatement(random);

        final CtWhile aWhile = generateWhileEnergyConsumed(factory, parameterEnergyToConsume, energySample, random);

        methodConsumeEnergy.getBody().addStatement(aWhile);

        final CtTypeReference<?> raplDevice = factory.Type().createReference("org.powerapi.jjoules.rapl.RaplDevice");
        final CtExecutableReference<?> stopReference = factory.createExecutableReference();
        stopReference.setDeclaringType(raplDevice);
        stopReference.setStatic(false);
        stopReference.setSimpleName("stop");
        final CtVariableRead energySampleVariableRead = factory.createVariableRead();
        energySampleVariableRead.setVariable(energySample.getReference());

        final CtInvocation stopInvocation = factory.createInvocation(energySampleVariableRead, stopReference);
        methodConsumeEnergy.getBody().insertEnd(stopInvocation);

        return methodConsumeEnergy;
    }

    private static CtWhile generateWhileEnergyConsumed(Factory factory, CtParameter<Long> parameterEnergyToConsume, CtLocalVariable energySample, CtLocalVariable random) {
        final CtWhile aWhile = factory.Core().createWhile();

        final CtTypeReference<?> raplDevice = factory.Type().createReference("org.powerapi.jjoules.rapl.RaplDevice");
        final CtExecutableReference<?> getEnergyReportReference = factory.createExecutableReference();
        getEnergyReportReference.setDeclaringType(raplDevice);
        getEnergyReportReference.setStatic(false);
        getEnergyReportReference.setSimpleName("getEnergyReport");
        final CtVariableRead energySampleVariableRead = factory.createVariableRead();
        energySampleVariableRead.setVariable(energySample.getReference());

        final CtInvocation getEnergyReportInvocation = factory.createInvocation(energySampleVariableRead, getEnergyReportReference);

        final CtExecutableReference<?> getMapReference = factory.createExecutableReference();
        getMapReference.setDeclaringType(factory.createCtTypeReference(Map.class));
        getMapReference.setStatic(false);
        getMapReference.setSimpleName("get");
        getMapReference.setParameters(Collections.singletonList(factory.Type().STRING));

        final CtInvocation getMapInvocation = factory.createInvocation(getEnergyReportInvocation, getMapReference, factory.createLiteral("package|uJ"));

        final CtVariableRead energyToConsumeVariableRead = factory.createVariableRead();
        energyToConsumeVariableRead.setVariable(parameterEnergyToConsume.getReference());

        aWhile.setLoopingExpression(
                factory.createBinaryOperator(
                        getMapInvocation,
                        energyToConsumeVariableRead,
                        BinaryOperatorKind.LT
                )
        );

        final CtVariableRead randomVariableRead = factory.createVariableRead();
        randomVariableRead.setVariable(random.getReference());

        final CtConstructorCall<Random> newRandom = factory.createConstructorCall(
                factory.Type().createReference(Random.class),
                randomVariableRead
        );

        final CtExecutableReference<?> nextLongReference = factory.createExecutableReference();
        nextLongReference.setDeclaringType(factory.createCtTypeReference(Random.class));
        nextLongReference.setStatic(false);
        nextLongReference.setSimpleName("nextLong");

        final CtInvocation nextLongInvocation = factory.createInvocation(newRandom, nextLongReference);

        final CtOperatorAssignment operatorAssignment = factory.createOperatorAssignment();
        operatorAssignment.setAssigned(factory.createVariableRead(random.getReference(), false));
        operatorAssignment.setAssignment(nextLongInvocation);
        operatorAssignment.setKind(BinaryOperatorKind.PLUS);
        aWhile.setBody(operatorAssignment);
        return aWhile;
    }

    private static CtLocalVariable getEnergySample(Factory factory) {
        final CtTypeReference<?> raplDevice = factory.Type().createReference("org.powerapi.jjoules.rapl.RaplDevice");
        final CtTypeAccess<?> raplTypeAccess = factory.createTypeAccess(raplDevice);
        final CtFieldRead RAPLFieldRead = factory.createFieldRead();
        RAPLFieldRead.setTarget(raplTypeAccess);
        RAPLFieldRead.setVariable(raplDevice.getDeclaredField("RAPL"));

        final CtExecutableReference<?> recordEnergyReference = factory.createExecutableReference();
        recordEnergyReference.setDeclaringType(raplDevice);
        recordEnergyReference.setStatic(false);
        recordEnergyReference.setSimpleName("recordEnergy");

        final CtInvocation recordEnergyInvocation = factory.createInvocation(RAPLFieldRead, recordEnergyReference);

        final CtLocalVariable energySample = factory.createLocalVariable(
                factory.Type().createReference("org.powerapi.jjoules.EnergySample"),
                "energySample",
                recordEnergyInvocation
        );
        energySample.setModifiers(Collections.singleton(ModifierKind.FINAL));
        return energySample;
    }

    private static CtMethod<Void> initMethodConsumeEnergy(Factory factory) {
        final CtMethod<Void> methodConsumeEnergy = factory.createMethod();
        methodConsumeEnergy.setType(factory.Type().voidPrimitiveType());
        methodConsumeEnergy.setSimpleName("consumeEnergy");
        methodConsumeEnergy.setModifiers(new HashSet<>(Arrays.asList(ModifierKind.PRIVATE, ModifierKind.STATIC)));
        return methodConsumeEnergy;
    }

}
