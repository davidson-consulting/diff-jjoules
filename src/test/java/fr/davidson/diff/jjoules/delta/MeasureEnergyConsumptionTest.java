package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.report.ReportEnum;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/09/2021
 */
public class MeasureEnergyConsumptionTest {

    @BeforeEach
    void setUp() throws IOException {
        // compile
        WrapperEnum.MAVEN.getWrapper().cleanAndCompile("src/test/resources/v1/");
        new File(ROOT_PATH_V1 + PATH_DIFF_JJOULES_MEASUREMENTS).mkdir();
        Files.copy(
                Paths.get(TEST_RESOURCES_JSON_PATH + "v1/" + PATH_DIFF_JJOULES_MEASUREMENTS + "/fr.davidson.diff_jjoules_demo.InternalListTest.json"),
                Paths.get(ROOT_PATH_V1 + PATH_DIFF_JJOULES_MEASUREMENTS + "/fr.davidson.diff_jjoules_demo.InternalListTest.json"),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @Test
    void test() {
        final Datas dataV1 = new Datas();
        final Datas dataV2 = new Datas();
        new MeasureEnergyConsumption().measureEnergyConsumptionForBothVersion(
                new Configuration(
                        new File("src/test/resources/v1/").getAbsolutePath(),
                        new File("src/test/resources/v1/").getAbsolutePath(),
                        5,
                        "target/diff-jjoules",
                        "",
                        "",
                        "",
                        true, true, ReportEnum.NONE, WrapperEnum.MAVEN,
                        false
                ),
                dataV1,
                dataV2,
                new HashMap<String, Set<String>>() {
                    {
                        put("fr.davidson.diff_jjoules_demo.InternalListTest", new HashSet<>());
                        get("fr.davidson.diff_jjoules_demo.InternalListTest").addAll(Arrays.asList("testCount"));
                    }
                }
        );
        assertFalse(dataV1.isEmpty());
        assertFalse(dataV2.isEmpty());
    }
}
