package fr.davidson.diff.jjoules.instrumentation.junit5;

import fr.davidson.diff.jjoules.AbstractProcessorTest;
import fr.davidson.diff.jjoules.instrumentation.process.junit5.JJoulesProcessor;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class JJoulesProcessorTest extends AbstractProcessorTest {

    @Override
    protected void oracle(CtMethod<?> testMethod) {
        assertEquals(
                "@org.powerapi.jjoules.junit5.EnergyTest",
                testMethod.getAnnotations().get(0).toString()
        );
    }

    @Override
    protected AbstractProcessor<?> getProcessor() {
        return new JJoulesProcessor(
                this.getTestToBeProcessed(),
                ROOT_OUTPUT_DIR_PATH
        );
    }
}
