package fr.davidson.diff.jjoules.mutation.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 18/02/2022
 */
public class DiffJJoulesMutationProcessor extends AbstractProcessor<CtMethod<?>> {

    private static final String FULL_QUALIFIED_NAME_CONSUME_METHOD_NAME = "fr.davidson.tlpc.sensor.TLPCSensor.consume";

    private static final String FULL_QUALIFIED_NAME_KEY_ENERGY_CONSUMPTION = "fr.davidson.tlpc.sensor.IndicatorPerLabel.KEY_CYCLES";

    private long consumption;

    private Map<String, Set<String>> methodsToMutate;

    public DiffJJoulesMutationProcessor(Map<String, Set<String>> methodsToMutate, long consumption) {
        this.methodsToMutate = methodsToMutate;
        this.consumption = consumption;
    }

    @Override
    public boolean isToBeProcessed(CtMethod<?> candidate) {
        final CtType<?> declaringType = candidate.getDeclaringType();
        if (declaringType == null) {
            return false;
        }
        return this.methodsToMutate.isEmpty() || (
                this.methodsToMutate.containsKey(declaringType.getQualifiedName()) &&
                        this.methodsToMutate
                                .get(declaringType.getQualifiedName())
                                .contains(candidate.getSimpleName()));
    }

    @Override
    public void process(CtMethod<?> ctMethod) {
        final Factory factory = ctMethod.getFactory();
        final CtCodeSnippetStatement consumeStatement = factory.createCodeSnippetStatement(
                FULL_QUALIFIED_NAME_CONSUME_METHOD_NAME + "(" + this.consumption + ", " + FULL_QUALIFIED_NAME_KEY_ENERGY_CONSUMPTION + ")"
        );
        ctMethod.getBody().insertBegin(consumeStatement);
    }
}
