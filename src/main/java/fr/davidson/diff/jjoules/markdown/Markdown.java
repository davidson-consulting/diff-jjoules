package fr.davidson.diff.jjoules.markdown;

import fr.davidson.diff.jjoules.delta.data.Data;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 05/07/2021
 */
public class Markdown {

    public static String makeAMarkdownRow(String... strings) {
        return "| " + String.join(" | ", strings) + " |\n";
    }

    public static String toMarkdownRow(Data data, String label, boolean withEmoji) {
        return makeAMarkdownRow(
                label,
                suffixedValue(data.energy, withEmoji),
                suffixedValue(data.instructions, withEmoji),
                suffixedValue(data.durations, withEmoji)
        );
    }

    private static String suffixedValue(double value, boolean withEmoji) {
        return value +
                (withEmoji ? value > 0 ? ":x:" : ":heavy_check_mark:" : "");
    }

    public static String toMarkdownRow(Data data, String label) {
        return toMarkdownRow(data, label, false);
    }

}
