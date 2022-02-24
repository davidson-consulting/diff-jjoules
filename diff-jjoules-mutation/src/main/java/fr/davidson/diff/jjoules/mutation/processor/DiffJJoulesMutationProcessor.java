package fr.davidson.diff.jjoules.mutation.processor;

import fr.davidson.diff.jjoules.instrumentation.InstrumentationProcessor;
import fr.davidson.tlpc.sensor.IndicatorPerLabel;
import fr.davidson.tlpc.sensor.TLPCSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 18/02/2022
 */
public class DiffJJoulesMutationProcessor extends InstrumentationProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentationProcessor.class);

    private long consumption;

    public DiffJJoulesMutationProcessor(
            Map<String, Set<String>> testsList,
            String rootPathFolder,
            String testFolderPath,
            long consumption
    ) {
        super(testsList, rootPathFolder, testFolderPath);
        this.consumption = consumption;
    }

    @Override
    public void processingDone() {
        this.instrumentedTypes.forEach(this::processingDone);
        LOGGER.info("{} mutated classes have been printed!", this.instrumentedTypes.size());
        this.instrumentedTypes.clear();
    }

    @Override
    public void process(CtMethod<?> ctMethod) {
        final Factory factory = ctMethod.getFactory();
        final CtClass<?> parentClass = ctMethod.getParent(CtClass.class);
        if (!this.instrumentedTypes.contains(parentClass)) {
            this.addConsumeMethodDeclaration(parentClass);
        }
        ctMethod.getBody().insertBegin(factory.createCodeSnippetStatement("consume(" + this.consumption + ")"));
        this.instrumentedTypes.add(ctMethod.getDeclaringType());
    }

    private void addConsumeMethodDeclaration(CtClass<?> parentClass) {
        final Factory factory = parentClass.getFactory();
        final CtMethod method = factory.createMethod();
        method.setType(factory.Type().VOID_PRIMITIVE);
        method.setSimpleName("consume");
        method.setParameters(Collections.singletonList(
                factory.createParameter(method, factory.Type().LONG_PRIMITIVE, "consumption")
        ));
        method.setModifiers(Collections.singleton(ModifierKind.STATIC));
        method.setBody(factory.createCodeSnippetStatement("TLPCSensor.reset(identifier)"));
        method.getBody().insertBegin(
                factory.createCodeSnippetStatement("TLPCSensor.stop(identifier)")
        );
        method.getBody().insertBegin(
                factory.createCodeSnippetStatement("while (TLPCSensor.read(identifier).get(IndicatorPerLabel.KEY_ENERGY_CONSUMPTION) < consumption);")
        );
        method.getBody().insertBegin(
                factory.createCodeSnippetStatement("TLPCSensor.start(identifier)")
        );
        method.getBody().insertBegin(
                factory.createCodeSnippetStatement("final String identifier = \"diff-jjoules-mutation\"")
        );
        parentClass.addMethod(method);
    }

    /*static void consume(final long consumption) {
        final String identifier = "diff-jjoules-mutation";
        TLPCSensor.start(identifier);
        while (TLPCSensor.read(identifier).get(IndicatorPerLabel.KEY_ENERGY_CONSUMPTION) < consumption);
        TLPCSensor.stop(identifier);
        TLPCSensor.reset(identifier);
    }*/

}
