package fr.davidson.diff.jjoules.instrumentation.junit4;

import fr.davidson.diff.jjoules.AbstractProcessorTest;
import fr.davidson.diff.jjoules.instrumentation.process.junit4.JJoulesProcessor;
import fr.davidson.diff.jjoules.util.Constants;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 27/11/2020
 */
public class JJoulesProcessorTest  extends AbstractProcessorTest {

    @Override
    protected void oracle(CtMethod<?> testMethod) {
        assertEquals(
                "org.powerapi.jjoules.junit4.EnergyTest.beforeTest(\"" + TEST_CLASS_NAME + "\", \"" + TEST_METHOD_NAME + "\")",
                testMethod.getBody().getStatement(0).toString()
        );
        assertEquals("org.powerapi.jjoules.junit4.EnergyTest.afterTest()", testMethod.getBody().getLastStatement().toString());
    }

    @Override
    protected AbstractProcessor<?> getProcessor() {
        return new JJoulesProcessor(
                this.getTestToBeProcessed(),
                ROOT_OUTPUT_DIR_PATH,
                BIN_PATH + Constants.PATH_SEPARATOR + BIN_TEST_PATH
        );
    }
}
