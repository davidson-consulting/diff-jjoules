package fr.davidson.diff.jjoules.markdown;

import fr.davidson.diff.jjoules.delta.Data;
import fr.davidson.diff.jjoules.delta.Delta;
import fr.davidson.diff.jjoules.delta.Deltas;
import fr.davidson.diff.jjoules.markdown.configuration.Configuration;
import fr.davidson.diff.jjoules.markdown.configuration.Options;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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
        for (String testMethodName : deltas.keySet()) {
            final Delta delta = deltas.get(testMethodName);
            report.append(makeAMarkdownRow(
                    testMethodName,
                    delta.energy + "",
                    delta.instructions + "",
                    delta.durations + ""
            ));
        }
        LOGGER.info("{}", report.toString());
        try (FileWriter writer = new FileWriter(".github/workflows/template.md")) {
            writer.write(makeAMarkdownRow("Test", "Energy", "Instructions", "Durations"));
            writer.write(makeAMarkdownRow("---", "---", "---", "---"));
            writer.write(report.toString());
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

    public static String makeDeltaWithEmoji(Double delta) {
        return delta + (delta > 0 ? ":x:" : ":heavy_check_mark:");
    }

    public static String makeAMarkdownRow(String... strings) {
        return "| " + String.join(" | ", strings) + " |\n";
    }
}
