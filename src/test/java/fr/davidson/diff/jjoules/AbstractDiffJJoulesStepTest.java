package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.maven.MavenRunner;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public abstract class AbstractDiffJJoulesStepTest {

    protected Configuration getConfiguration() {
        final Configuration configuration = new Configuration(
                new File("src/test/resources/diff-jjoules-demo/").getAbsolutePath(),
                new File("src/test/resources/diff-jjoules-demo-v2/").getAbsolutePath(),
                "",
                "src/test/resources/diff-jjoules-demo/classpath",
                "src/test/resources/diff-jjoules-demo/classpath",
                Utils.readClasspathFile("src/test/resources/diff-jjoules-demo/classpath").split(":"),
                Utils.readClasspathFile("src/test/resources/diff-jjoules-demo/classpath").split(":"),
                false,
                5
        );
        configuration.setTestsList(
                new HashMap<String, List<String>>() {
                    {
                        put("fr.davidson.diff_jjoules_demo.InternalListTest", new ArrayList<>());
                        get("fr.davidson.diff_jjoules_demo.InternalListTest").addAll(Collections.singletonList("testCount"));
                    }
                }
        );
        return configuration;
    }

    @BeforeEach
    void setUp() throws IOException {
        // compile
        MavenRunner.runCleanAndCompile("src/test/resources/diff-jjoules-demo");
        MavenRunner.runCleanAndCompile("src/test/resources/diff-jjoules-demo-v2");
        new File("src/test/resources/diff-jjoules-demo/diff-jjoules").delete();
        new File("src/test/resources/diff-jjoules-demo/target/jjoules-reports/").mkdirs();
        new File("src/test/resources/diff-jjoules-demo-v2/target/jjoules-reports/").mkdirs();
        Files.copy(
                Paths.get("src/test/resources/diff-jjoules-demo/" + "fr.davidson.diff_jjoules_demo.InternalListTest#testCount_v1.json"),
                Paths.get("src/test/resources/diff-jjoules-demo/target/jjoules-reports/" + "fr.davidson.diff_jjoules_demo.InternalListTest#testCount.json"),
                StandardCopyOption.REPLACE_EXISTING
        );
        Files.copy(
                Paths.get("src/test/resources/diff-jjoules-demo/" + "fr.davidson.diff_jjoules_demo.InternalListTest#testCount_v2.json"),
                Paths.get("src/test/resources/diff-jjoules-demo-v2/target/jjoules-reports/" + "fr.davidson.diff_jjoules_demo.InternalListTest#testCount.json"),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

}
