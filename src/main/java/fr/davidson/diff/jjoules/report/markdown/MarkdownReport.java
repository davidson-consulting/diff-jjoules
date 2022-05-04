package fr.davidson.diff.jjoules.report.markdown;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationStep;
import fr.davidson.diff.jjoules.report.Report;
import fr.davidson.tlpc.sensor.IndicatorPerLabel;
import fr.davidson.tlpc.sensor.IndicatorsPerIdentifier;
import fr.davidson.tlpc.sensor.TLPCSensor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class MarkdownReport implements Report {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentationStep.class);

    @Override
    public void report(Configuration configuration) {
        LOGGER.info("Run Markdown Report - {}", configuration.toString());
        final Deltas deltas = configuration.getDeltas();
        final Data deltaOmega = configuration.getDeltaOmega();
        final Datas dataV1 = configuration.getDataV1();
        final Datas dataV2 = configuration.getDataV2();
        final Map<String, Boolean> emptyIntersectionPerTestMethodName = dataV1.isEmptyIntersectionPerTestMethodName(dataV2);
        final StringBuilder report = new StringBuilder();
        final Map<String, Map<String, String>> reportPerTestClassPerTestMethod = new HashMap<>();
        final Deltas consideredDelta = new Deltas();
        final Deltas unconsideredDelta = new Deltas();
        final Data rawDeltaData = splitDataAndBuildReport(deltas, emptyIntersectionPerTestMethodName, reportPerTestClassPerTestMethod, consideredDelta, unconsideredDelta);
        addDeltaPerTest(report, reportPerTestClassPerTestMethod);
        addDeltaOmega(report, rawDeltaData, deltaOmega, consideredDelta, unconsideredDelta, deltas, dataV1, dataV2);
        if (configuration.isShouldSuspect()) {
            suspiciousLines(configuration, report);
        }
        if (configuration.isMeasureEnergyConsumption()) {
            ownConsumption(configuration, report);
        }
        LOGGER.info("{}", report);
        writeReport(report, configuration);
    }

    private void writeReport(StringBuilder report, Configuration configuration) {
        try (FileWriter writer = new FileWriter(configuration.getPathToReport(), false)) {
            writer.write(report.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addDeltaPerTest(StringBuilder report, Map<String, Map<String, String>> reportPerTestClassPerTestMethod) {
        report.append(Markdown.makeAMarkdownRow("Label", "Consumption V1", "Consumption V2", "&Delta;(t)"));
        report.append(Markdown.makeAMarkdownRow("---", "---", "---", "---"));
        for (String testClassName : reportPerTestClassPerTestMethod.keySet()) {
            report.append(Markdown.makeAMarkdownRow(testClassName));
            for (String testMethodName : reportPerTestClassPerTestMethod.get(testClassName).keySet()) {
                report.append(Markdown.makeAMarkdownRow(testMethodName));
                report.append(reportPerTestClassPerTestMethod.get(testClassName).get(testMethodName));
            }
        }
    }

    private void ownConsumption(Configuration configuration, StringBuilder report) {
        report.append("\n\n## Diff-JJoules Consumption\n\n\n");
        report.append(Markdown.makeAMarkdownRow("Step", "Energy(&mu;J)", "Instruction", "Durations(ms)"));
        report.append(Markdown.makeAMarkdownRow("---", "---", "---", "---"));
        final IndicatorsPerIdentifier indicatorsPerIdentifier = TLPCSensor.getIndicatorsPerIdentifier();
        long totalEnergyConsumption = 0L;
        long totalInstructions = 0L;
        long totalDurations = 0L;
        for (String key : indicatorsPerIdentifier.keySet()) {
            final Map<String, Long> consumptionReport = indicatorsPerIdentifier.get(key);
            final Long durations = consumptionReport.get(IndicatorPerLabel.KEY_DURATION);
            final Long instructions = consumptionReport.get(IndicatorPerLabel.KEY_INSTRUCTIONS);
            final Long energyConsumption = consumptionReport.get(IndicatorPerLabel.KEY_ENERGY_CONSUMPTION);
            report.append(Markdown.makeAMarkdownRow(key, "" + energyConsumption, "" + instructions, "" + durations));
            totalDurations += durations;
            totalEnergyConsumption += energyConsumption;
            totalInstructions += instructions;
        }
        report.append(Markdown.makeAMarkdownRow("Diff-Jjoules", "" + totalEnergyConsumption, "" + totalInstructions, "" + totalDurations));
    }

    private void suspiciousLines(Configuration configuration, StringBuilder report) {
        if (configuration.getScorePerLineV1() == null) {
            return;
        }
        final Map<String, Double> scorePerLineV1 = configuration.getScorePerLineV1();
        final Map<String, Double> scorePerLineV2 = configuration.getScorePerLineV2();
        final List<String> keysV1 = scorePerLineV1.keySet()
                .stream()
                .sorted((o1, o2) -> (int) (scorePerLineV1.get(o1) - scorePerLineV1.get(o2)))
                .collect(Collectors.toList());
        report.append("## Suspicious Lines\n\n\n");
        if (!scorePerLineV1.isEmpty()) {
            report.append("### V1 and Deletions\n\n\n");
            report.append(Markdown.makeAMarkdownRow("Line", "Score"));
            report.append(Markdown.makeAMarkdownRow("---", "---"));
            for (String key : keysV1) {
                report.append(Markdown.makeAMarkdownRow(key, scorePerLineV1.get(key) + ""));
            }
        }
        if (!scorePerLineV2.isEmpty()) {
            final List<String> keysV2 = scorePerLineV2.keySet()
                    .stream()
                    .sorted((o1, o2) -> (int) (scorePerLineV2.get(o1) - scorePerLineV2.get(o2)))
                    .collect(Collectors.toList());
            report.append("### V2 and Additions\n\n\n");
            report.append(Markdown.makeAMarkdownRow("Line", "Score"));
            report.append(Markdown.makeAMarkdownRow("---", "---"));
            for (String key : keysV2) {
                report.append(Markdown.makeAMarkdownRow(key, scorePerLineV2.get(key) + ""));
            }
        }
    }

    private void addDeltaOmega(
            final StringBuilder report,
            final Data rawDeltaData,
            final Data deltaOmega,
            final Deltas consideredDelta,
            final Deltas unconsideredDelta,
            final Deltas deltas,
            final Datas dataV1,
            final Datas dataV2
    ) {
        report.append("\n\n");
        report.append(Markdown.makeAMarkdownRow("Unit", "&Delta;(&Omega;)", "Raw&Delta;", "Decision"));
        report.append(Markdown.makeAMarkdownRow("---", "---", "---", "---"));
        report.append(Markdown.makeAMarkdownRow("Energy(&mu;J)", "" + deltaOmega.energy, "" + rawDeltaData.energy, Markdown.emojiDecision(deltaOmega.energy)));
        report.append(Markdown.makeAMarkdownRow("Instructions", "" + deltaOmega.instructions, "" + rawDeltaData.instructions, Markdown.emojiDecision(deltaOmega.instructions)));
        report.append(Markdown.makeAMarkdownRow("Durations(ms)", "" + deltaOmega.durations, "" + rawDeltaData.durations, Markdown.emojiDecision(deltaOmega.durations)));
        report.append(Markdown.makeAMarkdownRow("Cycles", "" + deltaOmega.cycles, "" + rawDeltaData.cycles, Markdown.emojiDecision(deltaOmega.cycles)));
        report.append("\n\n");
        if (!unconsideredDelta.isEmpty()) {
            report.append("### Unconsidered Test Methods\n\n");
            if (unconsideredDelta.size() > 10) {
                report.append(unconsideredDelta.size() + "/" + (consideredDelta.size() + unconsideredDelta.size()) + " have been discarded because of too much variation in the measures.\n");
            } else {
                report.append(Markdown.makeAMarkdownRow("Test", "Delta", "V1", "V2"));
                report.append(Markdown.makeAMarkdownRow("---", "---", "---", "---"));
                for (String testMethodName : unconsideredDelta.keySet()) {
                    final Delta delta = deltas.get(testMethodName);
                    final List<Data> datasV1 = dataV1.get(testMethodName);
                    final List<Data> datasV2 = dataV2.get(testMethodName);
                    report.append(Markdown.makeAMarkdownRow(
                            testMethodName,
                            delta.instructions + "",
                            datasV1.stream().map(d -> d.instructions).sorted().map(Object::toString).collect(Collectors.joining(",")),
                            datasV2.stream().map(d -> d.instructions).sorted().map(Object::toString).collect(Collectors.joining(","))
                            )
                    );
                }
            }
        }
    }

    @NotNull
    private Data splitDataAndBuildReport(Deltas deltas,
                                         Map<String, Boolean> emptyIntersectionPerTestMethodName,
                                         Map<String, Map<String, String>> reportPerTestClassPerTestMethod,
                                         Deltas consideredDelta,
                                         Deltas unconsideredDelta) {
        double rawDeltaEnergy = 0.0D;
        double rawDeltaInstructions = 0.0D;
        double rawDeltaDurations = 0.0D;
        double rawDeltaCycles = 0.0D;
        double rawDeltaCaches = 0.0D;
        double rawDeltaCacheMisses = 0.0D;
        double rawDeltaBranches = 0.0D;
        double rawDeltaBranchMisses = 0.0D;
        for (String testMethodName : deltas.keySet()) {
            final Delta delta = deltas.get(testMethodName);
            if (emptyIntersectionPerTestMethodName.get(testMethodName)) {
                final String[] split = testMethodName.split("#");
                if (!reportPerTestClassPerTestMethod.containsKey(split[0])) {
                    reportPerTestClassPerTestMethod.put(split[0], new HashMap<>());
                }
                consideredDelta.put(testMethodName, deltas.get(testMethodName));
                reportPerTestClassPerTestMethod.get(split[0]).put(
                        split[1],
                        Markdown.makeAMarkdownRow(
                                "Energy(&mu;J)", delta.dataV1.energy + "", delta.dataV2.energy + "", delta.energy + ""
                        ) + Markdown.makeAMarkdownRow(
                                "Instruction", delta.dataV1.instructions + "", delta.dataV2.instructions + "", delta.instructions + ""
                        ) + Markdown.makeAMarkdownRow(
                                "Duration(ms)", delta.dataV1.durations + "", delta.dataV2.durations + "", delta.durations + ""
                        ) + Markdown.makeAMarkdownRow(
                                "Cycles", delta.dataV1.cycles + "", delta.dataV2.cycles + "", delta.cycles + ""
                        )
                );
                rawDeltaEnergy += delta.energy;
                rawDeltaInstructions += delta.instructions;
                rawDeltaDurations += delta.durations;
                rawDeltaCycles += delta.cycles;
                rawDeltaCaches += delta.caches;
                rawDeltaCacheMisses += delta.cacheMisses;
                rawDeltaBranches += delta.branches;
                rawDeltaBranchMisses += delta.branchMisses;
            } else {
                unconsideredDelta.put(testMethodName, deltas.get(testMethodName));
            }
        }
        return new Data(
                rawDeltaEnergy,
                rawDeltaInstructions,
                rawDeltaDurations,
                rawDeltaCycles,
                rawDeltaCaches,
                rawDeltaCacheMisses,
                rawDeltaBranches,
                rawDeltaBranchMisses
        );
    }
}
