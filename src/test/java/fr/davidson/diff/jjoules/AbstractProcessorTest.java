package fr.davidson.diff.jjoules;

import org.junit.jupiter.api.BeforeEach;

import java.io.File;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/09/2021
 */
public abstract class AbstractProcessorTest {

    public static final String TEST_CLASS_NAME = "TestClassName";

    public static final String TEST_METHOD_NAME = "testMethodName";

    public static final String JAVA_EXTENSION = ".java";

    public static final String ROOT_OUTPUT_DIR_PATH = "target/trash/";

    public static final String FULL_OUTPUT_DIR_PATH = ROOT_OUTPUT_DIR_PATH + "src/test/java/";

    @BeforeEach
    void setUp() {
        final File outputDir = new File(FULL_OUTPUT_DIR_PATH);
        if (outputDir.exists()) {
            outputDir.delete();
        }
        outputDir.mkdirs();
    }
}
