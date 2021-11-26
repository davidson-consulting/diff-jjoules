package fr.davidson.diff.jjoules;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.io.File;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/09/2021
 */
public abstract class AbstractProcessorTest {

    public static final String ABSTRACT_TEST_CLASS_NAME = "AbstractTestClassName";

    public static final String TEST_CLASS_NAME = "TestClassName";

    public static final String TEST_METHOD_NAME = "testMethodName";

    public static final String JAVA_EXTENSION = ".java";

    public static final String ROOT_OUTPUT_DIR_PATH = "target/trash/";

    public static final String SRC_PATH = "src/main/java/";

    public static final String ROOT_RESOURCE_PROJECT_PATH = "src/test/resources/diff-jjoules-demo/";

    public static final String TEST_PATH = "src/test/java/";

    public static final String FULL_OUTPUT_DIR_PATH = ROOT_OUTPUT_DIR_PATH + TEST_PATH;

    public static final String BIN_PATH = "target/classes/";

    public static final String BIN_TEST_PATH = "target/test-classes/";

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    private void cleanUp() {
        final File outputDir = new File(FULL_OUTPUT_DIR_PATH);
        new File(FULL_OUTPUT_DIR_PATH + TEST_CLASS_NAME + JAVA_EXTENSION).delete();
        if (outputDir.exists()) {
            outputDir.delete();
        }
        outputDir.mkdirs();
    }

    protected void test(final CtClass<?> aClass) {
        aClass.getFactory().getModel().processWith(this.getProcessor());
        Launcher launcher = new Launcher();
        launcher.addInputResource(FULL_OUTPUT_DIR_PATH + TEST_CLASS_NAME + JAVA_EXTENSION);
        launcher.buildModel();
        this.oracle(
                launcher
                        .getFactory()
                        .Class()
                        .get(TEST_CLASS_NAME)
                        .getMethodsByName(TEST_METHOD_NAME)
                        .get(0)
        );
    }

    @Test
    void testJUnit4() {
        this.test(this.getJUnit4TestClass());
    }

    @Test
    void testJUnit5() {
        this.test(this.getJUnit5TestClass());
    }

    protected abstract void oracle(CtMethod<?> testMethod);

    protected abstract AbstractProcessor<?> getProcessor();

    protected Map<String, Set<String>> getTestToBeProcessed() {
        return new HashMap<String, Set<String>>() {
            {
                put(TEST_CLASS_NAME, new HashSet<>());
                get(TEST_CLASS_NAME).addAll(Collections.singletonList(TEST_METHOD_NAME));
            }
        };
    }

    protected CtClass<?> getJUnit4TestClass() {
        return Launcher.parseClass(
                "public class " + TEST_CLASS_NAME + " { @org.junit.Test\npublic void "+ TEST_METHOD_NAME +"() {} }"
        );
    }

    protected CtClass<?> getJUnit5TestClass() {
        return Launcher.parseClass(
                "public class " + TEST_CLASS_NAME + " { @org.junit.jupiter.api.Test\npublic void "+ TEST_METHOD_NAME +"() {} }"
        );
    }

    @AfterEach
    void tearDown() {
        this.cleanUp();
    }
}
