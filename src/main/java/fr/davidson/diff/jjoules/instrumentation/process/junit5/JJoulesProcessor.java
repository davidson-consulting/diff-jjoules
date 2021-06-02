package fr.davidson.diff.jjoules.instrumentation.process.junit5;

import fr.davidson.diff.jjoules.instrumentation.process.AbstractJJoulesProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
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
public class JJoulesProcessor extends AbstractJJoulesProcessor {

    public JJoulesProcessor(Map<String, List<String>> testsList, String rootPathFolder) {
        super(testsList, rootPathFolder);
    }

    @Override
    public boolean isToBeProcessed(CtMethod<?> candidate) {
        return super.isToBeProcessed(candidate) && candidate.getAnnotations()
                .stream()
                .anyMatch(ctAnnotation -> ctAnnotation.getType().getQualifiedName().endsWith("Test"));
    }

    @Override
    public void process(CtMethod<?> ctMethod) {
        final CtType<?> declaringType = ctMethod.getDeclaringType();
        System.out.println("Processing " + declaringType.getQualifiedName() + "#" + ctMethod.getSimpleName());
        final Factory factory = ctMethod.getFactory();
        final CtTypeReference<? extends Annotation> reference = factory.Type().createReference("org.powerapi.jjoules.junit5.EnergyTest");
        final CtAnnotation<? extends Annotation> testAnnotation =
                ctMethod.getAnnotations()
                        .stream()
                        .filter(ctAnnotation -> ctAnnotation.getType().getQualifiedName().endsWith("Test"))
                        .findAny()
                        .get();
        //ctMethod.removeAnnotation(testAnnotation);
        //ctMethod.addAnnotation(factory.createAnnotation(reference));
        testAnnotation.replace(factory.createAnnotation(reference));
//        this.duplicateMethodForWarmup(declaringType, ctMethod);
        super.instrumentedTypes.add(declaringType);
    }
}
