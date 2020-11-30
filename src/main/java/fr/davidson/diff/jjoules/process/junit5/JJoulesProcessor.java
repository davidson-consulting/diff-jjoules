package fr.davidson.diff.jjoules.process.junit5;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class JJoulesProcessor extends AbstractProcessor<CtMethod<?>> {

    private final Map<String, List<String>> testsToBeInstrumented;

    public JJoulesProcessor(final Map<String, List<String>> testsList) {
        this.testsToBeInstrumented = testsList;
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
    public void process(CtMethod<?> ctMethod) {
        System.out.println("Processing " + ctMethod.getDeclaringType().getQualifiedName() + "#" + ctMethod.getSimpleName());
        final Factory factory = ctMethod.getFactory();
        final CtTypeReference<? extends Annotation> reference = factory.Type().createReference("org.powerapi.jjoules.junit5.EnergyTest");
        final CtAnnotation<? extends Annotation> testAnnotation = ctMethod.getAnnotations().stream().filter(ctAnnotation -> ctAnnotation.getType().getQualifiedName().endsWith("Test")).findAny().get();
        ctMethod.removeAnnotation(testAnnotation);
        ctMethod.addAnnotation(factory.createAnnotation(reference));
    }
}
