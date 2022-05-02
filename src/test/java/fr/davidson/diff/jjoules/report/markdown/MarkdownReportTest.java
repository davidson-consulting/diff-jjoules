package fr.davidson.diff.jjoules.report.markdown;

import fr.davidson.diff.jjoules.AbstractDiffJJoulesStepTest;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.Computation;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.report.ReportStep;
import fr.davidson.tlpc.sensor.IndicatorPerLabel;
import fr.davidson.tlpc.sensor.Report;
import fr.davidson.tlpc.sensor.TLPCSensor;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import static fr.davidson.diff.jjoules.util.Constants.NEW_LINE;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 26/11/2021
 */
public class MarkdownReportTest extends AbstractDiffJJoulesStepTest {

    @Test
    void test() throws Exception{
        final String testName2 = "fr.davidson.diff_jjoules_demo.InternalListTest#testCount2";
        final String testName1 = "fr.davidson.diff_jjoules_demo.InternalListTest#testCount";
        final Configuration configuration = this.getConfiguration();
        final Deltas deltas = new Deltas();
        final Data dataV1 = new Data(10, 10, 10, 10, 10, 10, 10, 10);
        final Data dataV2 = new Data(100, 100, 100, 100, 100, 100, 100, 100);
        final Delta delta = new Delta(dataV1, dataV2);

        final Data dataV1Unconsidered1 = new Data(10, 10, 10, 10, 10, 10, 10, 10);
        final Data dataV1Unconsidered2 = new Data(75, 75, 75, 75, 75, 75, 75, 75);
        final Data dataV2Unconsidered1 = new Data(25, 25, 25, 25, 25, 25, 25, 25);
        final Data dataV2Unconsidered2 = new Data(100, 100, 100, 100, 100, 100, 100, 100);
        final Datas datasV1 = new Datas();

        datasV1.put(testName1, Collections.singletonList(dataV1));
        final List<Data> datasV1Unconsidered = new ArrayList<>();
        datasV1Unconsidered.add(dataV1Unconsidered1);
        datasV1Unconsidered.add(dataV1Unconsidered2);

        datasV1.put(testName2, datasV1Unconsidered);
        configuration.setDataV1(datasV1);

        final Datas datasV2 = new Datas();
        datasV2.put(testName1, Collections.singletonList(dataV2));
        final List<Data> datasV2Unconsidered = new ArrayList<>();
        datasV2Unconsidered.add(dataV2Unconsidered1);
        datasV2Unconsidered.add(dataV2Unconsidered2);
        datasV2.put(testName2, datasV2Unconsidered);
        configuration.setDataV2(datasV2);

        final Delta deltaUnconsidered = new Delta(
                Computation.computeMedian(datasV1).get(testName2),
                Computation.computeMedian(datasV2).get(testName2)
        );
        deltas.put(testName1, delta);
        deltas.put(testName2, deltaUnconsidered);
        configuration.setDeltas(deltas);
        final Data deltaOmega = new Data(0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
        configuration.setDeltaOmega(deltaOmega);

        final IndicatorPerLabel report = new IndicatorPerLabel();
        report.put(IndicatorPerLabel.KEY_ENERGY_CONSUMPTION, 100000L);
        report.put(IndicatorPerLabel.KEY_INSTRUCTIONS, 100000L);
        report.put(IndicatorPerLabel.KEY_DURATION, 100000L);
        TLPCSensor.getIndicatorsPerIdentifier().put("ownConsumptionReport", report);
        final Map<String, Double> scorePerLineV1 = new HashMap<>();
        scorePerLineV1.put("fr.davidson.Example#exampleMethod#5", 0.5);
        scorePerLineV1.put("fr.davidson.Example#exampleMethod#10", 1.0);
        configuration.setScorePerLineV1(scorePerLineV1);
        configuration.setScorePerLineV2(scorePerLineV1);
        new ReportStep().run(configuration);
        try(final BufferedReader reader = new BufferedReader(new FileReader("target/report.md"))) {
            assertEquals(EXPECTED_REPORT, reader.lines().collect(Collectors.joining(NEW_LINE)));
        }
    }

    private static final String EXPECTED_REPORT = "| Label | Consumption V1 | Consumption V2 | &Delta;(t) |" + NEW_LINE +
            "| --- | --- | --- | --- |" + NEW_LINE +
            "| fr.davidson.diff_jjoules_demo.InternalListTest |" + NEW_LINE +
            "| testCount |" + NEW_LINE +
            "| Energy(&mu;J) | 10.0 | 100.0 | 90.0 |" + NEW_LINE +
            "| Instruction | 10.0 | 100.0 | 90.0 |" + NEW_LINE +
            "| Duration(ms) | 10.0 | 100.0 | 90.0 |" + NEW_LINE +
            "| Cycles | 10.0 | 100.0 | 90.0 |" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "| Unit | &Delta;(&Omega;) | Raw&Delta; | Decision |" + NEW_LINE +
            "| --- | --- | --- | --- |" + NEW_LINE +
            "| Energy(&mu;J) | 0.5 | 90.0 | :x: |" + NEW_LINE +
            "| Instructions | 0.5 | 90.0 | :x: |" + NEW_LINE +
            "| Durations(ms) | 0.5 | 90.0 | :x: |" + NEW_LINE +
            "| Cycles | 0.5 | 90.0 | :x: |" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "### Unconsidered Test Methods" + NEW_LINE +
            "" + NEW_LINE +
            "| Test | Delta | V1 | V2 |" + NEW_LINE +
            "| --- | --- | --- | --- |" + NEW_LINE +
            "| fr.davidson.diff_jjoules_demo.InternalListTest#testCount2 | 20.0 | 10.0,75.0 | 25.0,100.0 |" + NEW_LINE +
            "## Suspicious Lines" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "### V1 and Deletions" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "| Line | Score |" + NEW_LINE +
            "| --- | --- |" + NEW_LINE +
            "| fr.davidson.Example#exampleMethod#10 | 1.0 |" + NEW_LINE +
            "| fr.davidson.Example#exampleMethod#5 | 0.5 |" + NEW_LINE +
            "### V2 and Additions" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "| Line | Score |" + NEW_LINE +
            "| --- | --- |" + NEW_LINE +
            "| fr.davidson.Example#exampleMethod#10 | 1.0 |" + NEW_LINE +
            "| fr.davidson.Example#exampleMethod#5 | 0.5 |" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "## Diff-JJoules Consumption" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "| Step | Energy(&mu;J) | Instruction | Durations(ms) |" + NEW_LINE +
            "| --- | --- | --- | --- |" + NEW_LINE +
            "| ownConsumptionReport | 100000 | 100000 | 100000 |" + NEW_LINE +
            "| Diff-Jjoules | 100000 | 100000 | 100000 |";

}
