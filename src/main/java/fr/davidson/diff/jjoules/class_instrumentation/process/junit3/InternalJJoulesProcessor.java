package fr.davidson.diff.jjoules.class_instrumentation.process.junit3;

import fr.davidson.diff.jjoules.class_instrumentation.process.AbstractInternalJJoulesProcessor;
import fr.davidson.diff.jjoules.class_instrumentation.process.JUnitVersion;
import fr.davidson.diff.jjoules.util.NodeManager;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class InternalJJoulesProcessor extends AbstractInternalJJoulesProcessor {

    public InternalJJoulesProcessor(JUnitVersion jUnitVersion) {
        super(jUnitVersion);
    }

    @Override
    public Predicate<CtMethod<?>> getPredicateIsTest() {
        return (testMethod) -> {
            final CtType<?> testClass = testMethod.getParent(CtType.class);
            return testMethod.getSimpleName().startsWith("test") &&
                    testClass.getSuperclass() != null &&
                    testClass.getSuperclass().getSimpleName().equals("TestCase");
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processSetupAndTearDown(CtMethod<?> ctMethod, CtType<?> testClass) {
        testClass.toString(); // TODO fix this, it seems that when calling this someting is refresh within Spoon model
        final Factory factory = testClass.getFactory();
        final CtMethod<?> suite = testClass.getMethods()
                .stream()
                .filter(method -> method.getModifiers().contains(ModifierKind.STATIC))
                .filter(method -> method.getModifiers().contains(ModifierKind.PUBLIC))
                .filter(method -> method.getType().getQualifiedName().endsWith("Test"))
                .filter(method -> method.getParameters().isEmpty())
                .findFirst()
                .orElseGet(() -> createSuiteMethod(testClass));
        final CtNewClass<?> newClassSuite = createNewClassSuite(testClass, factory);
        final CtTypeReference testSetupTypeReference = factory.Type().createReference("junit.extensions.TestSetup");
        final CtNewClass newClassSetup = createNewClassSetup(ctMethod, testClass, factory, newClassSuite, testSetupTypeReference);
        final CtLocalVariable<?> setupLocalVariable = factory.createLocalVariable(
                testSetupTypeReference,
                "setup",
                newClassSetup
        );
        suite.setBody(setupLocalVariable);
        final CtReturn aReturn = factory.createReturn();
        aReturn.setReturnedExpression(factory.createVariableRead(factory.createLocalVariableReference(setupLocalVariable), false));
        suite.getBody().insertEnd(aReturn);
    }

    @NotNull
    private CtNewClass createNewClassSetup(CtMethod<?> ctMethod, CtType<?> testClass, Factory factory, CtNewClass<?> newClassSuite, CtTypeReference testSetupTypeReference) {
        final CtNewClass newClassSetup = factory.createNewClass();
        newClassSetup.setType(testSetupTypeReference);
        newClassSetup.addArgument(newClassSuite);

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

        final CtClass<?> anonymousClassSuite = createAnonymousClassSuite(factory, testClass, beforeTestInvocation, afterTestInvocation);
        newClassSetup.setAnonymousClass(anonymousClassSuite);
        return newClassSetup;
    }

    private CtNewClass<?> createNewClassSuite(CtType<?> testClass, Factory factory) {
        final CtTypeReference testSuiteTypeReference = factory.Type().createReference("junit.framework.TestSuite");
        final CtFieldRead<?> testClassFieldReadClass = factory.createFieldRead();
        testClassFieldReadClass.setTarget(factory.createTypeAccess(testClass.getReference()));
        final CtFieldReference fieldReference = factory.createFieldReference();
        fieldReference.setDeclaringType(testClass.getReference());
        fieldReference.setType(factory.createCtTypeReference(Class.class));
        fieldReference.setSimpleName("class");
        testClassFieldReadClass.setVariable(fieldReference);
        final CtNewClass<?> newClassSuite = factory.createNewClass(
                testSuiteTypeReference.getTypeDeclaration(),
                testClassFieldReadClass
        );
        return newClassSuite;
    }

    @NotNull
    private CtClass<?> createAnonymousClassSuite(Factory factory, CtType<?> testClass, CtInvocation<?> beforeTestInvocation, CtInvocation<?> afterTestInvocation) {
        final CtClass<?> anonymousClassSuite = factory.createClass();
        createAndAddSetupMethod(factory, anonymousClassSuite, beforeTestInvocation, "setUp");
        createAndAddSetupMethod(factory, anonymousClassSuite, afterTestInvocation, "tearDown");
        return anonymousClassSuite;
    }

    private void createAndAddSetupMethod(Factory factory,
                                         CtType<?> testClass,
                                         CtInvocation<?> invocation,
                                         String simpleName) {
        final CtMethod<?> setUp = factory.createMethod(
                testClass,
                Collections.singleton(ModifierKind.PROTECTED),
                factory.Type().VOID_PRIMITIVE,
                simpleName,
                Collections.emptyList(),
                Collections.emptySet()
        );
        setUp.setBody(invocation);
        testClass.addMethod(setUp);
    }

    private CtMethod<?> createSuiteMethod(final CtType<?> testClass) {
        final Factory factory = testClass.getFactory();
        return (CtMethod<?>) factory.createMethod(
                testClass,
                new HashSet<>(Arrays.asList(ModifierKind.STATIC, ModifierKind.PUBLIC)),
                factory.Type().createReference("junit.framework.Test"),
                "suite",
                Collections.emptyList(),
                Collections.emptySet()
        );
    }

    @NotNull
    @Override
    protected Set<ModifierKind> getModifiers() {
        return Collections.singleton(ModifierKind.PROTECTED);
    }

    @Override
    public boolean isMatchingMethod(CtMethod<?> testMethod, String fullQualifiedName) {
        return super.isMatchingMethod(testMethod, fullQualifiedName);
    }
}
