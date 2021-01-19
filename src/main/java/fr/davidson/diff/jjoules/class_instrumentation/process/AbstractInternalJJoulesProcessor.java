package fr.davidson.diff.jjoules.class_instrumentation.process;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractInternalJJoulesProcessor {

    protected JUnitVersion jUnitVersion;

    public AbstractInternalJJoulesProcessor(JUnitVersion jUnitVersion) {
        this.jUnitVersion = jUnitVersion;
    }

    public abstract Predicate<CtMethod<?>> getPredicateIsTest();

    public abstract void processSetupAndTearDown(CtMethod<?> ctMethod, CtType<?> testClass);

    protected CtMethod<?> getOrCreateMethod(CtType<?> testClass, String fullQualifiedName, String simpleName) {
        return testClass.getMethods()
                .stream()
                .filter(method ->
                        this.isMatchingMethod(method, fullQualifiedName)
                ).findFirst()
                .orElseGet(() ->
                        this.createMethodInitOrTearDown(testClass, simpleName, fullQualifiedName)
                );
    }

    public boolean isTestOfThisVersion(CtMethod<?> testMethod) {
        return this.getPredicateIsTest().test(testMethod);
    }

    public CtMethod<?> createMethodInitOrTearDown(CtType<?> testClass, String simpleName, String fullQualifiedName) {
        final Factory factory = testClass.getFactory();
        final CtMethod<?> method = factory.createMethod(
                testClass,
                getModifiers(),
                factory.Type().VOID_PRIMITIVE,
                simpleName,
                Collections.emptyList(),
                Collections.emptySet()
        );
        final CtTypeReference<? extends Annotation> reference =
                factory.Type().createReference(fullQualifiedName);
        method.addAnnotation(factory.createAnnotation(reference));
        return method;
    }

    @NotNull
    protected Set<ModifierKind> getModifiers() {
        return new HashSet<>(Arrays.asList(ModifierKind.PUBLIC, ModifierKind.STATIC));
    }

    public boolean isMatchingMethod(CtMethod<?> testMethod, String fullQualifiedName) {
        return testMethod.getAnnotations().stream()
                .filter(Objects::nonNull)
                .filter(annotation -> annotation.getType() != null)
                .anyMatch(annotation -> annotation.getType().getQualifiedName().equals(fullQualifiedName));
    }

}
