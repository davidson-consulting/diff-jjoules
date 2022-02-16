package fr.davidson.diff.jjoules.instrumentation;

import fr.davidson.diff.jjoules.AbstractProcessorTest;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 20/01/2022
 */
public class InstrumentationProcessorTest extends AbstractProcessorTest {

    @Override
    protected void oracle(CtMethod<?> testMethod) {
        final String expectedIdentifier = new FullQualifiedName(TEST_CLASS_NAME, TEST_METHOD_NAME).toString();
        assertEquals(
                "fr.davidson.tlpc.sensor.TLPCSensor.start(\"" + expectedIdentifier + "\")",
                testMethod.getBody().getStatement(0).toString()
        );
        assertEquals(
                "fr.davidson.tlpc.sensor.TLPCSensor.stop(\"" + expectedIdentifier + "\")",
                testMethod.getBody().getLastStatement().toString()
        );
        final CtClass<?> ctClass = testMethod.getParent(CtClass.class);
        final String expectedStaticBlock =
                "static {" + Constants.NEW_LINE +
                        "        java.lang.Runtime.getRuntime().addShutdownHook(new java.lang.Thread() {" + Constants.NEW_LINE +
                        "            @java.lang.Override" + Constants.NEW_LINE +
                        "            public void run() {" + Constants.NEW_LINE +
                        "                fr.davidson.tlpc.sensor.TLPCSensor.report(\"target/trash//diff-jjoules-measurements/measurements.json/\");" + Constants.NEW_LINE +
                        "            }" + Constants.NEW_LINE +
                        "        });" + Constants.NEW_LINE +
                        "    }";
        assertTrue(ctClass.toString().contains(expectedStaticBlock), ctClass.toString());
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
