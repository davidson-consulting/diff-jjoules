package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.maven.MavenRunner;
import fr.davidson.diff.jjoules.util.wrapper.Wrapper;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
import fr.davidson.diff.jjoules.util.wrapper.maven.MavenWrapper;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public abstract class AbstractDiffJJoulesStepTest {

    public static final String ROOT_PATH_V1 = "src/test/resources/diff-jjoules-demo/";
    public static final String TARGET_FOLDER_PATH_V1 = ROOT_PATH_V1 + "/target/";
    public static final String JJOULES_REPORT_PATH_V1 = TARGET_FOLDER_PATH_V1 + "/jjoules-reports/";
    public static final String CLASSPATH_PATH_V1 = ROOT_PATH_V1 + "/classpath";

    public static final String ROOT_PATH_V2 = "src/test/resources/diff-jjoules-demo-v2/";
    public static final String CLASSPATH_PATH_V2 = ROOT_PATH_V2 + "/classpath";
    public static final String TARGET_FOLDER_PATH_V2 = ROOT_PATH_V2 + "/target/";
    public static final String JJOULES_REPORT_PATH_V2 = TARGET_FOLDER_PATH_V2 + "/jjoules-reports/";

    public static final String SRC_PATH = "src/main/java/";

    public static final String TEST_PATH = "src/test/java/";

    public static final String BIN_PATH = "target/classes/";

    public static final String BIN_TEST_PATH = "target/test-classes/";

    public static final String DIFF_JJOULES_FOLDER_PATH = ROOT_PATH_V1 + "/diff-jjoules";
    public static final String JAVA_EXTENSION = ".java";
    public static final String PACKAGE_NAME = "fr.davidson.diff_jjoules_demo";
    public static final String PACKAGE_PATH = "/fr/davidson/diff_jjoules_demo/";

    public static final String SIMPLE_NAME_CLASS = "InternalList";
    public static final String FULL_QUALIFIED_NAME_CLASS = PACKAGE_NAME + "." + SIMPLE_NAME_CLASS;
    public static final String CLASS_PATH = PACKAGE_PATH + SIMPLE_NAME_CLASS + JAVA_EXTENSION;

    public static final String SIMPLE_NAME_TEST_CLASS = "InternalListTest";
    public static final String FULL_QUALIFIED_NAME_TEST_CLASS = PACKAGE_NAME + "." + SIMPLE_NAME_TEST_CLASS;
    public static final String TEST_CLASS_PATH = PACKAGE_PATH + SIMPLE_NAME_TEST_CLASS + JAVA_EXTENSION;
    public static final String TEST_COUNT = "testCount";

    protected Configuration getConfiguration() {
        final Configuration configuration = new Configuration(
                new File(ROOT_PATH_V1).getAbsolutePath(),
                new File(ROOT_PATH_V2).getAbsolutePath(),
                5,
                true
        );
        configuration.setTestsList(
                new HashMap<String, java.util.Set<String>>() {
                    {
                        put(FULL_QUALIFIED_NAME_TEST_CLASS, new HashSet<>());
                        get(FULL_QUALIFIED_NAME_TEST_CLASS).addAll(Collections.singletonList(TEST_COUNT));
                    }
                }
        );
        return configuration;
    }

    @BeforeEach
    protected void setUp() throws IOException {
        // compile
        WrapperEnum.MAVEN.getWrapper().cleanAndCompile(ROOT_PATH_V1);
        WrapperEnum.MAVEN.getWrapper().cleanAndCompile(ROOT_PATH_V2);
        final File diffJJoulesFolderFd = new File(DIFF_JJOULES_FOLDER_PATH);
        if (diffJJoulesFolderFd.exists()) {
            Files.walk(Paths.get(DIFF_JJOULES_FOLDER_PATH))
                    .map(Path::toFile)
                    .forEach(File::delete);
            diffJJoulesFolderFd.delete();
        }
        diffJJoulesFolderFd.mkdir();
        new File(JJOULES_REPORT_PATH_V1).mkdirs();
        new File(JJOULES_REPORT_PATH_V2).mkdirs();
        Files.copy(
                Paths.get(ROOT_PATH_V1 + "fr.davidson.diff_jjoules_demo.InternalListTest#testCount_v1.json"),
                Paths.get(JJOULES_REPORT_PATH_V1 + "fr.davidson.diff_jjoules_demo.InternalListTest#testCount.json"),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get(ROOT_PATH_V1 + "fr.davidson.diff_jjoules_demo.InternalListTest#testCount_v2.json"),
                Paths.get(JJOULES_REPORT_PATH_V2 + "fr.davidson.diff_jjoules_demo.InternalListTest#testCount.json"),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

}
