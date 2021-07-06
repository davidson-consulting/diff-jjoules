package fr.davidson.diff.jjoules.markdown;

import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.markdown.configuration.Configuration;
import fr.davidson.diff.jjoules.markdown.configuration.Options;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/06/2021
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        run(Options.parse(args));
    }

    public static void run(Configuration configuration) {
        final Deltas deltas = JSONUtils.read(configuration.pathToJSONDelta, Deltas.class);
        final Data deltaOmega = JSONUtils.read(configuration.pathToJSONDeltaOmega, Data.class);

        final Datas dataV1 = JSONUtils.read(configuration.pathToJSONDataV1, Datas.class);
        final Datas dataV2 = JSONUtils.read(configuration.pathToJSONDataV2, Datas.class);

        final Map<String, Boolean> emptyIntersectionPerTestMethodName = dataV1.isEmptyIntersectionPerTestMethodName(dataV2);

        final StringBuilder report = new StringBuilder();
        final Map<String, Map<String, String>> reportPerTestClassPerTestMethod = new HashMap<>();
        double rawDeltaEnergy = 0.0D;
        double rawDeltaInstructions = 0.0D;
        double rawDeltaDurations = 0.0D;
        final Deltas consideredDelta = new Deltas();
        final Deltas unconsideredDelta = new Deltas();

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
                        Markdown.toMarkdownRow(delta.dataV1, "Consumption V1") +
                                Markdown.toMarkdownRow(delta.dataV2, "Consumption V1") +
                                Markdown.toMarkdownRow(delta, "Delta Consumption", true)
                );
                rawDeltaEnergy += delta.energy;
                rawDeltaInstructions += delta.instructions;
                rawDeltaDurations += delta.durations;
            } else {
                unconsideredDelta.put(testMethodName, deltas.get(testMethodName));
            }
        }
        for (String testClassName : reportPerTestClassPerTestMethod.keySet()) {
            report.append(Markdown.makeAMarkdownRow(testClassName));
            for (String testMethodName : reportPerTestClassPerTestMethod.get(testClassName).keySet()) {
                report.append(Markdown.makeAMarkdownRow(testMethodName));
                report.append(reportPerTestClassPerTestMethod.get(testClassName).get(testMethodName));
            }
        }
        final Data rawDeltaData = new Data(rawDeltaEnergy, rawDeltaInstructions, rawDeltaDurations);
        LOGGER.info("{}", report.toString());
        try (FileWriter writer = new FileWriter(".github/workflows/template.md")) {
            writer.write(Markdown.makeAMarkdownRow("Test", "Energy", "Instructions", "Durations"));
            writer.write(Markdown.makeAMarkdownRow("---", "---", "---", "---"));
            writer.write(report.toString());
            writer.write(Markdown.toMarkdownRow(rawDeltaData, "RawDeltaCommit", true));
            writer.write(Markdown.toMarkdownRow(deltaOmega, "DeltaOmega", true));
            writer.write("\n\n");
            writer.write("### Unconsidered Test Methods\n\n");
            writer.write(Markdown.makeAMarkdownRow("Test", "Delta", "V1", "V2"));
            writer.write(Markdown.makeAMarkdownRow("---", "---", "---", "---"));
            for (String testMethodName : unconsideredDelta.keySet()) {
                final Delta delta = deltas.get(testMethodName);
                final List<Data> datasV1 = dataV1.get(testMethodName);
                final List<Data> datasV2 = dataV2.get(testMethodName);
                writer.write(Markdown.makeAMarkdownRow(testMethodName, delta.instructions + "",
                        datasV1.stream().map(d -> d.instructions).sorted().map(Object::toString).collect(Collectors.joining(",")),
                        datasV2.stream().map(d -> d.instructions).sorted().map(Object::toString).collect(Collectors.joining(","))
                        )
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
