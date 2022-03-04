package fr.davidson.diff.jjoules.delta;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationProcessor;
import fr.davidson.diff.jjoules.report.ReportEnum;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;
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
        new File(ROOT_PATH_V1 + InstrumentationProcessor.FOLDER_MEASURES_PATH).mkdir();
        Files.copy(
                Paths.get(Constants.joinFiles(TEST_RESOURCES_JSON_PATH, "v1", InstrumentationProcessor.FOLDER_MEASURES_PATH, InstrumentationProcessor.OUTPUT_FILE_NAME)),
                Paths.get(Constants.joinFiles(ROOT_PATH_V1, InstrumentationProcessor.FOLDER_MEASURES_PATH, InstrumentationProcessor.OUTPUT_FILE_NAME)),
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
                new MethodNamesPerClassNames() {
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
