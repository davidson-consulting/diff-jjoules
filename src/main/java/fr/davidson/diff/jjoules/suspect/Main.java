package fr.davidson.diff.jjoules.suspect;

import fr.davidson.diff.jjoules.mark.computation.ExecLineTestMap;
import fr.davidson.diff.jjoules.mark.computation.ExecsLines;
import fr.davidson.diff.jjoules.suspect.configuration.Configuration;
import fr.davidson.diff.jjoules.suspect.configuration.Options;
import fr.davidson.diff.jjoules.util.CSVReader;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.spoonlabs.flacoco.api.Suspiciousness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/06/2021
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        run(Options.parse(args));
    }

    public static void run(Configuration configuration) {
        LOGGER.info("{}", configuration.toString());
        final Set<String> testsList = FullQualifiedName.toSetFullQualifiedNames(CSVReader.readFile(configuration.pathToTestListAsCSV));
        if (testsList.isEmpty()) {
            throw new RuntimeException();
        }
        LOGGER.info("{}", String.join("\n", testsList));
        // execute flacoco
        final Map<String, Suspiciousness> runV1 = FlacocoRunner.run(
                String.join(":", configuration.classpathV1),
                configuration.pathToFirstVersion,
                testsList
        );
        final Map<String, Suspiciousness> runV2 = FlacocoRunner.run(
                String.join(":", configuration.classpathV2),
                configuration.pathToSecondVersion,
                testsList
        );

        final ExecsLines execLinesAdditions = JSONUtils.read("diff-jjoules/exec_additions.json", ExecsLines.class);
        final ExecsLines execLinesDeletions = JSONUtils.read("diff-jjoules/exec_deletions.json", ExecsLines.class);
        final Map<String, Double> suspiciousLinesV1 = getSuspiciousLinesFromDiff(runV1, execLinesDeletions);
        final Map<String, Double> suspiciousLinesV2 = getSuspiciousLinesFromDiff(runV2, execLinesAdditions);

        LOGGER.info("Suspect Lines in V1");
        suspiciousLinesV1.keySet()
                .stream()
                .sorted((key1, key2) -> -(int)((suspiciousLinesV1.get(key1)*100.0D) - (suspiciousLinesV1.get(key2)*100.0D)))
                .forEach(key -> LOGGER.info("{}: {}", key, suspiciousLinesV1.get(key)));
        LOGGER.info("Suspect Lines in V2");
        suspiciousLinesV2.keySet()
                .stream()
                .sorted((key1, key2) -> -(int)((suspiciousLinesV2.get(key1)*100.0D) - (suspiciousLinesV2.get(key2)*100.0D)))
                .forEach(key -> LOGGER.info("{}: {}", key, suspiciousLinesV2.get(key)));
        JSONUtils.write("suspicious_v1.json", suspiciousLinesV1);
        JSONUtils.write("suspicious_v2.json", suspiciousLinesV2);
    }

    private static Map<String, Double> getSuspiciousLinesFromDiff(Map<String, Suspiciousness> flacocoMap, ExecsLines execLines) {
        final Map<String, Double> scorePerSuspiciousLine = new HashMap<>();
        for (ExecLineTestMap execLinesDeletion : execLines) {
            for (String line : execLinesDeletion.getExecLt().keySet()) {
                final String[] split = line.split("#");
                final String className = split[0];
                final String lineNumber = split[1];
                final String key = className.replaceAll("\\.", "/") + "@-@" + lineNumber;
                if (flacocoMap.containsKey(key)) {
                    scorePerSuspiciousLine.put(line, flacocoMap.get(key).getScore());
                }
            }
        }
        return scorePerSuspiciousLine;
    }
}
