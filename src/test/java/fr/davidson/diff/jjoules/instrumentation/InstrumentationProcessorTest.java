package fr.davidson.diff.jjoules.instrumentation;

import fr.davidson.diff.jjoules.AbstractProcessorTest;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 20/01/2022
 */
public class InstrumentationProcessorTest extends AbstractProcessorTest {

    @Override
    protected void oracle(CtMethod<?> testMethod) {
        assertEquals(
                "new fr.davidson.tlpc.sensor.TLPCSensor().start()",
                testMethod.getBody().getStatement(0).toString()
        );
        assertEquals(
                "new fr.davidson.tlpc.sensor.TLPCSensor().stop(\"" + TEST_METHOD_NAME + "\")",
                testMethod.getBody().getLastStatement().toString()
        );
    }

    @Override
    protected AbstractProcessor<?> getProcessor() {
        return new InstrumentationProcessor(
                this.getTestToBeProcessed(),
                ROOT_OUTPUT_DIR_PATH,
                TEST_PATH
        );
    }
}
