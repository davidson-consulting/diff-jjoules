package fr.davidson.diff.jjoules.mutation;

import fr.davidson.diff.jjoules.mutation.process.UntareJjoulesProcessor;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/09/2021
 */
public class UntareJjoulesProcessorTest extends AbstractProcessorTest {

    @Override
    protected void oracle(CtMethod<?> testMethod) {
        assertEquals(TEST_CLASS_NAME + ".consumeEnergy(10000L)", testMethod.getBody().getStatement(0).toString());
        final CtType<?> testType = testMethod.getDeclaringType();
        final CtMethod<?> consumeEnergyMethod = testType.getMethodsByName("consumeEnergy").get(0);
        final String expectedConsumeEnergyMethodString = "private static void consumeEnergy(final long energyToConsume) {\n" +
                "    final org.powerapi.jjoules.EnergySample energySample = org.powerapi.jjoules.rapl.RaplDevice.RAPL.recordEnergy();\n" +
                "    long random = 0L;\n" +
                "    while (energySample.getEnergyReport().get(\"package|uJ\") < energyToConsume) {\n" +
                "        random += new java.util.Random(random).nextLong();\n" +
                "    } \n" +
                "    energySample.stop();\n" +
                "}";
        assertEquals(expectedConsumeEnergyMethodString, consumeEnergyMethod.toString());
    }

    @Override
    protected AbstractProcessor<?> getProcessor() {
        return new UntareJjoulesProcessor(
                this.getTestToBeProcessed(),
                10000,
                FULL_OUTPUT_DIR_PATH
        );
    }
}
