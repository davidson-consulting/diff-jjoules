package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.report.ReportEnum;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.maven.MavenRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/09/2021
 */
public class DiffJJoulesMojoTest {

    private class MockConfiguration extends Configuration {
        public MockConfiguration() {
            super(
                    new File("src/test/resources/diff-jjoules-demo/").getAbsolutePath(),
                    new File("src/test/resources/diff-jjoules-demo-v2/").getAbsolutePath(),
                    "src/test/resources/diff-jjoules-demo/classpath",
                    "src/test/resources/diff-jjoules-demo/classpath",
                    Utils.readClasspathFile("src/test/resources/diff-jjoules-demo/classpath").split(":"),
                    Utils.readClasspathFile("src/test/resources/diff-jjoules-demo/classpath").split(":"),
                    false,
                    5,
                    "",
                    "src/test/java/fr/davidson/diff/jjoules/delta/MeasureEnergyConsumptionTest.java",
                    "", "", "", true, true, ReportEnum.NONE
            );
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        // compile
        MavenRunner.runCleanAndCompile("src/test/resources/diff-jjoules-demo");
        new File("src/test/resources/diff-jjoules-demo/target/jjoules-reports/").mkdirs();
        Path dst = Paths.get("src/test/resources/diff-jjoules-demo/target/jjoules-reports/com.google.gson.CommentsTest-testParseComments.json");
        Path src = Paths.get("src/test/resources/json/v1/com.google.gson.CommentsTest-testParseComments.json");
        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void test() {
        final DiffJJoulesMojo diffJJoulesMojo = new DiffJJoulesMojo();
    }
}
