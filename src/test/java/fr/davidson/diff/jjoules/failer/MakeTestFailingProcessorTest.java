package fr.davidson.diff.jjoules.failer;

import fr.davidson.diff.jjoules.AbstractProcessorTest;
import fr.davidson.diff.jjoules.failer.processor.MakeTestFailingProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/09/2021
 */
public class MakeTestFailingProcessorTest extends AbstractProcessorTest {

    @Test
    void test() {
        final CtClass<?> aClass = Launcher.parseClass(
                "public class TestClassName { public void testMethodName() {} }"
        );
        aClass.getFactory().getModel().processWith(
                new MakeTestFailingProcessor(
                        new HashMap<String, List<String>>() {
                            {
                                put(TEST_CLASS_NAME, new ArrayList<>());
                                get(TEST_CLASS_NAME).addAll(Arrays.asList(TEST_METHOD_NAME));
                            }
                        },
                        ROOT_OUTPUT_DIR_PATH
                )
        );
        Launcher launcher = new Launcher();
        launcher.addInputResource(FULL_OUTPUT_DIR_PATH + TEST_CLASS_NAME + JAVA_EXTENSION);
        launcher.buildModel();
        final String lastStatementAsString = launcher
                .getFactory()
                .Class()
                .get(TEST_CLASS_NAME)
                .getMethodsByName(TEST_METHOD_NAME)
                .get(0)
                .getBody()
                .getLastStatement()
                .toString();
        Assertions.assertEquals("junit.framework.Assert.fail()", lastStatementAsString);
    }
}
