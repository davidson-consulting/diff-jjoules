package fr.davidson.diff.jjoules.markdown;

import fr.davidson.diff.jjoules.delta.Data;
import fr.davidson.diff.jjoules.delta.Delta;
import fr.davidson.diff.jjoules.delta.Deltas;
import fr.davidson.diff.jjoules.markdown.configuration.Configuration;
import fr.davidson.diff.jjoules.markdown.configuration.Options;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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
        final Deltas deltas = JSONUtils.read(configuration.pathToDeltaJSON, Deltas.class);
        final Data deltaOmega = JSONUtils.read(configuration.pathToDeltaOmega, Data.class);
        final StringBuilder report = new StringBuilder();
        final Map<String, Map<String, String>> reportPerTestClassPerTestMethod = new HashMap<>();
        Double rawDeltaEnergy = 0.0D;
        Double rawDeltaInstructions = 0.0D;
        Double rawDeltaDurations = 0.0D;
        for (String testMethodName : deltas.keySet()) {
            final Delta delta = deltas.get(testMethodName);
            final String[] split = testMethodName.split("#");
            if (!reportPerTestClassPerTestMethod.containsKey(split[0])) {
                reportPerTestClassPerTestMethod.put(split[0], new HashMap<>());
            }
            reportPerTestClassPerTestMethod.get(split[0]).put(
                    split[1],
                    makeAMarkdownRow("Consumption V1", delta.dataV1.energy + "", delta.dataV1.instructions + "", delta.dataV1.durations + "") +
                    makeAMarkdownRow("Consumption  V2", delta.dataV2.energy + "", delta.dataV2.instructions + "", delta.dataV2.durations + "") +
                    makeAMarkdownRow(
                            "Delta Consumption",
                            makeDeltaWithEmoji(delta.energy),
                            makeDeltaWithEmoji(delta.instructions),
                            makeDeltaWithEmoji(delta.durations)
                    )
            );
            rawDeltaEnergy += delta.energy;
            rawDeltaInstructions += delta.instructions;
            rawDeltaDurations += delta.durations;
        }
        for (String testClassName : reportPerTestClassPerTestMethod.keySet()) {
            report.append(makeAMarkdownRow(testClassName));
            for (String testMethodName : reportPerTestClassPerTestMethod.get(testClassName).keySet()) {
                report.append(makeAMarkdownRow(testMethodName));
                report.append(reportPerTestClassPerTestMethod.get(testClassName).get(testMethodName));
            }
        }
        LOGGER.info("{}", report.toString());
        try (FileWriter writer = new FileWriter(".github/workflows/template.md")) {
            writer.write(makeAMarkdownRow("Test", "Energy", "Instructions", "Durations"));
            writer.write(makeAMarkdownRow("---", "---", "---", "---"));
            writer.write(report.toString());
            writer.write(
                    makeAMarkdownRow(
                            "RawDeltaCommit",
                            makeDeltaWithEmoji(rawDeltaEnergy),
                            makeDeltaWithEmoji(rawDeltaInstructions),
                            makeDeltaWithEmoji(rawDeltaDurations)
                    )
            );
            writer.write(
                    makeAMarkdownRow(
                            "DeltaOmega",
                            makeDeltaWithEmoji(deltaOmega.energy),
                            makeDeltaWithEmoji(deltaOmega.instructions),
                            makeDeltaWithEmoji(deltaOmega.durations)
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static StringBuilder appendRow(
            StringBuilder report,
            String testMethodName,
            String suffixLabel,
            String energy,
            String instructions,
            String durations
    ) {
        return report.append(makeAMarkdownRow(testMethodName + suffixLabel, energy, instructions, durations));
    }

    public static String makeDeltaWithEmoji(Double delta) {
        return delta + (delta > 0 ? ":x:" : ":heavy_check_mark:");
    }

    public static String makeAMarkdownRow(String... strings) {
        return "| " + String.join(" | ", strings) + " |\n";
    }
}
