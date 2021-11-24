package fr.davidson.diff.jjoules.instrumentation.junit5;

import fr.davidson.diff.jjoules.AbstractProcessorTest;
import fr.davidson.diff.jjoules.instrumentation.process.junit5.JJoulesProcessor;
import fr.davidson.diff.jjoules.util.Constants;
import org.junit.jupiter.api.Test;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
                ROOT_OUTPUT_DIR_PATH,
                BIN_PATH + Constants.PATH_SEPARATOR + BIN_TEST_PATH
        );
    }

    @Test
    void testWhenTestToBeInstrumentedNoMatch() {
        final CtClass<?> aClass = this.getJUnit5TestClass();
        aClass.getFactory().getModel().processWith(
                new JJoulesProcessor(
                        new HashMap<String, Set<String>>() {
                            {
                                put(TEST_CLASS_NAME + "2", new HashSet<>());
                                get(TEST_CLASS_NAME + "2").addAll(Arrays.asList(TEST_METHOD_NAME));
                            }
                        },
                        ROOT_OUTPUT_DIR_PATH,
                        BIN_PATH + Constants.PATH_SEPARATOR + BIN_TEST_PATH
                )
        );
        assertFalse(new File(FULL_OUTPUT_DIR_PATH + TEST_CLASS_NAME + JAVA_EXTENSION).exists());
    }
}
