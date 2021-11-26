package fr.davidson.diff.jjoules.report.markdown;

import fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 26/11/2021
 */
public class MarkdownStepTest extends AbstractDiffJJoulesStepTest {

    @Test
    void test() throws Exception{
        final Configuration configuration = this.getConfiguration();
        final Deltas deltas = new Deltas();
        final Data dataV1 = new Data(10, 10, 10, 10, 10, 10, 10, 10);
        final Data dataV2 = new Data(100, 100, 100, 100, 100, 100, 100, 100);
        final Delta delta = new Delta(dataV1, dataV2);
        deltas.put("fr.davidson.diff_jjoules_demo.InternalListTest#testCount", delta);
        configuration.setDeltas(deltas);
        final Data deltaOmega = new Data(0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
        configuration.setDeltaOmega(deltaOmega);
        final Datas datasV1 = new Datas();
        datasV1.put("fr.davidson.diff_jjoules_demo.InternalListTest#testCount", Collections.singletonList(dataV1));
        final Datas datasV2 = new Datas();
        datasV2.put("fr.davidson.diff_jjoules_demo.InternalListTest#testCount", Collections.singletonList(dataV2));
        configuration.setDataV1(datasV1);
        configuration.setDataV2(datasV2);
        new MarkdownStep()._run(configuration);
        try(final BufferedReader reader = new BufferedReader(new FileReader("target/report.md"))) {
            assertEquals(EXPECTED_REPORT, reader.lines().collect(Collectors.joining(Constants.NEW_LINE)));
        }
    }

    private static final String EXPECTED_REPORT = "| Label | Consumption V1 | Consumption V2 | &Delta;(t) |\n" +
            "| --- | --- | --- | --- |\n" +
            "| fr.davidson.diff_jjoules_demo.InternalListTest |\n" +
            "| testCount |\n" +
            "| Energy(&mu;J) | 10.0 | 100.0 | 90.0 |\n" +
            "| Instruction | 10.0 | 100.0 | 90.0 |\n" +
            "| Duration(ms) | 10.0 | 100.0 | 90.0 |\n" +
            "| Cycles | 10.0 | 100.0 | 90.0 |\n" +
            "\n" +
            "\n" +
            "| Unit | &Delta;(&Omega;) | Raw&Delta; | Decision |\n" +
            "| --- | --- | --- | --- |\n" +
            "| Energy(&mu;J) | 0.5 | 90.0 | :x: |\n" +
            "| Instructions | 0.5 | 90.0 | :x: |\n" +
            "| Durations(ms) | 0.5 | 90.0 | :x: |\n" +
            "| Cycles | 0.5 | 90.0 | :x: |\n" +
            "\n" +
            "\n" +
            "## Suspicious Lines\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "## Diff-JJoules Consumption\n" +
            "\n" +
            "\n" +
            "| Step | Energy(&mu;J) | Instruction | Durations(ms) |\n" +
            "| --- | --- | --- | --- |\n" +
            "| Diff-Jjoules | 0 | 0 | 0 |";

}
