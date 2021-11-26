package fr.davidson.diff.jjoules.mutation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 26/05/2021
 */
public class Utils {

    public static Map<String, Set<String>> readFile(String path) {
        final HashMap<String, Set<String>> result = new HashMap<>();
        if (path == null || path.isEmpty()) {
            return result;
        }
        try (final BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.lines().forEach(line -> {
                final String[] split = line.split(";");
                if (!split[0].toLowerCase(Locale.ROOT).contains("concurrency")) {
                    result.put(split[0], new HashSet<>(Arrays.asList(split).subList(1, split.length)));
                }
            });
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


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

    public static Node findNodeWithSpecificChild(Document document,
                                                 Node parent,
                                                 String nodeNameChild,
                                                 String nodeChildValue,
                                                 String groupId,
                                                 String artifactId,
                                                 String version) {
        Node current = parent.getFirstChild();
        while (current != null) {
            final Node artifactIdNode = Utils.findSpecificNodeFromGivenRoot(current, nodeNameChild);
            if (artifactIdNode != null && nodeChildValue.equals(artifactIdNode.getNodeValue())) {
                return current;
            }
            current = current.getNextSibling();
        }
        final Element plugin = createPlugin(document, groupId, artifactId, version);
        parent.appendChild(plugin);
        return plugin;
    }

    public static Node findSpecificNodeFromGivenRoot(Node startingPoint, String nodeName) {
        Node currentChild = startingPoint;
        while (currentChild != null && !nodeName.equals(currentChild.getNodeName())) {
            currentChild = currentChild.getNextSibling();
        }
        return currentChild;
    }

    public static Node findOrCreateGivenNode(Document document, Node root, String nodeToFind) {
        final Node existingProfiles = findSpecificNodeFromGivenRoot(root.getFirstChild(), nodeToFind);
        if (existingProfiles != null) {
            return existingProfiles;
        } else {
            final Element profiles = document.createElement(nodeToFind);
            root.appendChild(profiles);
            return profiles;
        }
    }

    public static Element createElement(Document document,
                                        String elementNodeName,
                                        String groupIdValue,
                                        String artifactIdValue,
                                        String versionValue) {
        final Element element = document.createElement(elementNodeName);

        final Element groupId = document.createElement(GROUP_ID);
        groupId.setTextContent(groupIdValue);
        element.appendChild(groupId);

        final Element artifactId = document.createElement(ARTIFACT_ID);
        artifactId.setTextContent(artifactIdValue);
        element.appendChild(artifactId);

        if (!versionValue.isEmpty()) {
            final Element version = document.createElement(VERSION);
            version.setTextContent(versionValue);
            element.appendChild(version);
        }
        return element;
    }

    public static Element createPlugin(Document document,
                                       String groupIdValue,
                                       String artifactIdValue,
                                       String versionValue) {
        return Utils.createElement(document, PLUGIN, groupIdValue, artifactIdValue, versionValue);
    }

    public static Element createDependency(Document document,
                                           String groupIdValue,
                                           String artifactIdValue,
                                           String versionValue) {
        return Utils.createElement(document, DEPENDENCY, groupIdValue, artifactIdValue, versionValue);
    }

    public static final String PROJECT = "project";

    public static final String PLUGIN = "plugin";

    public static final String GROUP_ID = "groupId";

    public static final String ARTIFACT_ID = "artifactId";

    public static final String VERSION = "version";

    public static final String DEPENDENCIES = "dependencies";

    public static final String DEPENDENCY = "dependency";

}
