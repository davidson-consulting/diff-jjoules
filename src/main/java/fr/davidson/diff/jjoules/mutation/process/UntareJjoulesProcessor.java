package fr.davidson.diff.jjoules.mutation.process;

import fr.davidson.diff.jjoules.mutation.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.PrettyPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 26/05/2021
 */
public class UntareJjoulesProcessor extends AbstractProcessor<CtMethod<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UntareJjoulesProcessor.class);

    private Map<String, List<String>> methodNamePerClassFullQualifiedName;
    private long energyToConsume;
    private Set<CtType<?>> mutatedTypes;

    private CtMethod<?> consumeEnergyMethod;
    private String outputPathFolder;

    public UntareJjoulesProcessor(
            Map<String, List<String>> methodNamePerClassFullQualifiedName,
            long energyToConsume,
            String outputPathFolder) {
        this.methodNamePerClassFullQualifiedName = methodNamePerClassFullQualifiedName;
        this.energyToConsume = energyToConsume;
        this.outputPathFolder = outputPathFolder;
        this.mutatedTypes = new HashSet<>();
        this.consumeEnergyMethod = null;
    }

    @Override
    public boolean isToBeProcessed(CtMethod<?> candidate) {
        return candidate.getDeclaringType() != null &&
                this.methodNamePerClassFullQualifiedName.containsKey(candidate.getDeclaringType().getQualifiedName()) &&
                this.methodNamePerClassFullQualifiedName.get(candidate.getDeclaringType().getQualifiedName()).contains(candidate.getSimpleName());
    }

    @Override
    public void process(CtMethod<?> ctMethod) {
        final Factory factory = ctMethod.getFactory();

        final CtTypeReference<?> declaringType = ctMethod.getDeclaringType().getReference();
        final CtTypeAccess<?> typeAccess = factory.createTypeAccess(declaringType);

        final CtExecutableReference<?> consumeEnergyReference = factory.createExecutableReference();
        consumeEnergyReference.setDeclaringType(declaringType);
        consumeEnergyReference.setStatic(true);
        consumeEnergyReference.setSimpleName("consumeEnergy");
        consumeEnergyReference.setParameters(Collections.singletonList(factory.Type().LONG_PRIMITIVE));

        final CtInvocation<?> consumeEnergyInvocation =
                factory.createInvocation(typeAccess, consumeEnergyReference, factory.createLiteral(this.energyToConsume));
        ctMethod.getBody().insertBegin(consumeEnergyInvocation);
        this.mutatedTypes.add(declaringType.getDeclaration());
        if (this.consumeEnergyMethod == null) {
            this.consumeEnergyMethod = Utils.generateMethodConsumeEnergy(ctMethod.getFactory());
        }
    }

    @Override
    public void processingDone() {
        this.mutatedTypes.forEach(type -> type.addMethod(this.consumeEnergyMethod));
        this.mutatedTypes.forEach(this::printCtType);
    }

    private void printCtType(CtType<?> type) {
        final File directory = new File(this.outputPathFolder);
        type.getFactory().getEnvironment().setSourceOutputDirectory(directory);
        final PrettyPrinter prettyPrinter = type.getFactory().getEnvironment().createPrettyPrinter();
        final String fileName = this.outputPathFolder  + "/" +
                type.getQualifiedName().replaceAll("\\.", "/") + ".java";
        LOGGER.info("Printing {} to {}", type.getQualifiedName(), fileName);
        try (final FileWriter write = new FileWriter(fileName)) {
            write.write(prettyPrinter.printTypes(type));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
