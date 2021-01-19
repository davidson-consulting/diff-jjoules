package fr.davidson.diff.jjoules.util;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.util.List;
import java.util.Map;

public class Checker {

    public static boolean checkInheritance(Map<String, List<String>> testsToBeInstrumented, CtMethod<?> candidate) {
        final CtType<?> declaringType = candidate.getDeclaringType();
        return candidate.getFactory().Type().getAll()
                .stream()
                .filter(type -> type.getSuperclass() != null)
                .filter(type -> type.getSuperclass().getDeclaration() != null)
                .filter(type -> type.getSuperclass().getTypeDeclaration().equals(declaringType))
                .anyMatch(ctType -> mustInstrument(testsToBeInstrumented, ctType.getQualifiedName(), candidate.getSimpleName()));
    }

    public static boolean mustInstrument(Map<String, List<String>> testsToBeInstrumented, String testClassQualifiedName, String testMethodName) {
        return testsToBeInstrumented.containsKey(testClassQualifiedName) &&
                testsToBeInstrumented
                        .get(testClassQualifiedName)
                        .contains(testMethodName);
    }


}
