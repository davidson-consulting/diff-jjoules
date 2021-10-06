package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.maven.MavenRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class DeltaStepTest {

    private Configuration getConfiguration() {
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

    @Test
    void test() {
        /*
            The DeltaStep generates 4 new files and init 4 fields in the Configuration :
                - Data from V1
                - Data from V2
                - Deltas computed from the Data from V1 and the Data from V2
                - The list of methods to be considered
         */
        final Configuration configuration = this.getConfiguration();
        assertTrue(configuration.getDataV1().isEmpty());
        assertTrue(configuration.getDataV2().isEmpty());
        assertTrue(configuration.getDeltas().isEmpty());
        assertTrue(configuration.getConsideredTestsNames().isEmpty());
        new DeltaStep().run(configuration);
        assertFalse(configuration.getDataV1().isEmpty());
        assertFalse(configuration.getDataV2().isEmpty());
        assertFalse(configuration.getDeltas().isEmpty());
        assertFalse(configuration.getConsideredTestsNames().isEmpty());
    }
}
