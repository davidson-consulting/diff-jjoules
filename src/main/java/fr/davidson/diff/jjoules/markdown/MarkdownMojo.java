package fr.davidson.diff.jjoules.markdown;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/06/2021
 */
@Mojo(name = "markdown")
public class MarkdownMojo extends DiffJJoulesMojo {

    @Override
    public void run(Configuration configuration) {
        getLog().info("Run Markdown - " + configuration.toString());
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
        for (String testClassName : reportPerTestClassPerTestMethod.keySet()) {
            report.append(Markdown.makeAMarkdownRow(testClassName));
            for (String testMethodName : reportPerTestClassPerTestMethod.get(testClassName).keySet()) {
                report.append(Markdown.makeAMarkdownRow(testMethodName));
                report.append(reportPerTestClassPerTestMethod.get(testClassName).get(testMethodName));
            }
        }
        getLog().info(report.toString());
        writeReport(report, rawDeltaData, deltaOmega, consideredDelta, unconsideredDelta, deltas, dataV1, dataV2);
        suspiciousLines(configuration);
    }

    private void suspiciousLines(Configuration configuration) {
        final Map<String, Double> scorePerLineV1 = configuration.getScorePerLineV1();
        final Map<String, Double> scorePerLineV2 = configuration.getScorePerLineV2();
        try (FileWriter writer = new FileWriter(".github/workflows/template.md", true)) {
            writer.write("## Suspicious Lines\n\n\n");
            if (!scorePerLineV1.isEmpty()) {
                writer.write("### V1 and Deletions\n\n\n");
                writer.write(Markdown.makeAMarkdownRow("Line", "Score"));
                writer.write(Markdown.makeAMarkdownRow("---", "---"));
                for (String key : scorePerLineV1.keySet()) {
                    writer.write(Markdown.makeAMarkdownRow(key, scorePerLineV1.get(key) + ""));
                }
            }
            if (!scorePerLineV2.isEmpty()) {
                writer.write("### V2 and Additions\n\n\n");
                writer.write(Markdown.makeAMarkdownRow("Line", "Score"));
                writer.write(Markdown.makeAMarkdownRow("---", "---"));
                for (String key : scorePerLineV2.keySet()) {
                    writer.write(Markdown.makeAMarkdownRow(key, scorePerLineV2.get(key) + ""));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeReport(
            final StringBuilder report,
            final Data rawDeltaData,
            final Data deltaOmega,
            final Deltas consideredDelta,
            final Deltas unconsideredDelta,
            final Deltas deltas,
            final Datas dataV1,
            final Datas dataV2
    ) {
        try (FileWriter writer = new FileWriter(".github/workflows/template.md")) {
            writer.write(Markdown.makeAMarkdownRow("Label", "Consumption V1", "Consumption V2", "&Delta;(t)"));
            writer.write(Markdown.makeAMarkdownRow("---", "---", "---", "---"));
            writer.write(report.toString());
            writer.write("\n\n");

            writer.write(Markdown.makeAMarkdownRow("Unit", "&Delta;(&Omega;)", "Raw&Delta;", "Decision"));
            writer.write(Markdown.makeAMarkdownRow("---", "---", "---", "---"));
            writer.write(Markdown.makeAMarkdownRow("Energy(&mu;J)", "" + deltaOmega.energy, "" + rawDeltaData.energy, Markdown.emojiDecision(deltaOmega.energy)));
            writer.write(Markdown.makeAMarkdownRow("Instructions", "" + deltaOmega.instructions, "" + rawDeltaData.instructions, Markdown.emojiDecision(deltaOmega.instructions)));
            writer.write(Markdown.makeAMarkdownRow("Durations(ms)", "" + deltaOmega.durations, "" + rawDeltaData.durations, Markdown.emojiDecision(deltaOmega.durations)));
            writer.write(Markdown.makeAMarkdownRow("Cycles", "" + deltaOmega.cycles, "" + rawDeltaData.cycles, Markdown.emojiDecision(deltaOmega.cycles)));
            writer.write("\n\n");
            if (!unconsideredDelta.isEmpty()) {
                writer.write("### Unconsidered Test Methods\n\n");
                if (unconsideredDelta.size() > 10) {
                    writer.write(unconsideredDelta.size() + "/" + (consideredDelta.size() + unconsideredDelta.size()) + " have been discarded because of too much variation in the measures.\n");
                } else {
                    writer.write(Markdown.makeAMarkdownRow("Test", "Delta", "V1", "V2"));
                    writer.write(Markdown.makeAMarkdownRow("---", "---", "---", "---"));
                    for (String testMethodName : unconsideredDelta.keySet()) {
                        final Delta delta = deltas.get(testMethodName);
                        final List<Data> datasV1 = dataV1.get(testMethodName);
                        final List<Data> datasV2 = dataV2.get(testMethodName);
                        writer.write(Markdown.makeAMarkdownRow(
                                testMethodName,
                                delta.instructions + "",
                                datasV1.stream().map(d -> d.instructions).sorted().map(Object::toString).collect(Collectors.joining(",")),
                                datasV2.stream().map(d -> d.instructions).sorted().map(Object::toString).collect(Collectors.joining(","))
                                )
                        );
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
