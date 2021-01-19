package fr.davidson.diff.jjoules.util;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Predicate;

public class NodeManager {

    public static CtMethod<?> createMethodInitOrTearDown(CtType<?> testClass, String simpleName, String fullQualifiedName) {
        final Factory factory = testClass.getFactory();
        final CtMethod<?> method = factory.createMethod(
                testClass,
                new HashSet<>(Arrays.asList(ModifierKind.PUBLIC, ModifierKind.STATIC)),
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

    public static boolean isMatchingMethod(CtMethod<?> testMethod, String fullQualifiedName) {
        return testMethod.getAnnotations().stream()
                .filter(Objects::nonNull)
                .filter(annotation -> annotation.getType() != null)
                .anyMatch(annotation -> annotation.getType().getQualifiedName().equals(fullQualifiedName));
    }

    public static void removeOtherMethods(CtMethod<?> ctMethod, CtType<?> testClass, Predicate<CtMethod<?>> filter) {
        testClass.getMethods()
                .stream()
                .filter(filter)
                .filter(method -> !method.getSimpleName().equals(ctMethod.getSimpleName()))
                .forEach(testClass::removeMethod);
    }

    @NotNull
    public static CtExecutableReference<?> createExecutableReference(Factory factory, CtTypeReference<?> energyTestReference, String simpleName) {
        final CtExecutableReference<?> beforeTestReference = factory.createExecutableReference();
        beforeTestReference.setDeclaringType(energyTestReference);
        beforeTestReference.setStatic(true);
        beforeTestReference.setSimpleName(simpleName);
        return beforeTestReference;
    }

    public static void insertOrSetIfNull(CtMethod<?> method, CtInvocation<?> invocation, boolean insertEnd) {
        if (method.getBody() == null) {
            method.setBody(invocation);
        } else {
            if (insertEnd) {
                method.getBody().insertEnd(invocation);
            } else {
                method.getBody().insertBegin(invocation);
            }
        }
    }

    public static void replaceAllReferences(CtType<?> originalTestClass, CtElement element, CtType<?> testClass) {
        element.getElements(new TypeFilter<>(CtTypeReference.class))
                .stream()
                .filter(type ->
                        type.getQualifiedName().equals(originalTestClass.getQualifiedName()))
                .forEach(reference ->
                        reference.replace(testClass.getReference())
                );
    }
}
