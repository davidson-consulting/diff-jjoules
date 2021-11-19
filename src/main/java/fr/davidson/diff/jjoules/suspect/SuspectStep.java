package fr.davidson.diff.jjoules.suspect;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.mark.computation.ExecLineTestMap;
import fr.davidson.diff.jjoules.mark.computation.ExecsLines;
import fr.davidson.diff.jjoules.suspect.fl.FlacocoRunner;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.spoonlabs.flacoco.api.result.Location;
import fr.spoonlabs.flacoco.api.result.Suspiciousness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class SuspectStep extends DiffJJoulesStep {

    public static final String PATH_TO_JSON_SUSPICIOUS_V1 = "suspicious_v1.json";

    public static final String PATH_TO_JSON_SUSPICIOUS_V2 = "suspicious_v2.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(SuspectStep.class);

    protected String getReportPathname() {
        return "suspect";
    }

    @Override
    protected void _run(Configuration configuration) {
        this.configuration = configuration;
        LOGGER.info("Run Suspect");
        final Set<String> testsList = FullQualifiedName.toSetFullQualifiedNames(configuration.getTestsList());
        if (testsList.isEmpty()) {
            throw new RuntimeException();
        }
        // execute flacoco
        final Map<Location, Suspiciousness> runV1 = FlacocoRunner.run(
                configuration.junit4,
                configuration.getClasspathV1AsString(),
                configuration.pathToFirstVersion,
                testsList
        );
        final Map<Location, Suspiciousness> runV2 = FlacocoRunner.run(
                configuration.junit4,
                configuration.getClasspathV2AsString(),
                configuration.pathToSecondVersion,
                testsList
        );
        final ExecsLines execLinesAdditions = configuration.getExecLinesAdditions();
        final ExecsLines execLinesDeletions = configuration.getExecLinesDeletions();
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
        JSONUtils.write(configuration.output + "/" + PATH_TO_JSON_SUSPICIOUS_V1, suspiciousLinesV1);
        configuration.setScorePerLineV1(suspiciousLinesV1);
        JSONUtils.write(configuration.output + "/" + PATH_TO_JSON_SUSPICIOUS_V2, suspiciousLinesV2);
        configuration.setScorePerLineV2(suspiciousLinesV2);
    }

    private static Map<String, Double> getSuspiciousLinesFromDiff(Map<Location, Suspiciousness> flacocoMap, ExecsLines execLines) {
        final Map<String, Double> scorePerSuspiciousLine = new HashMap<>();
        for (ExecLineTestMap execLinesDeletion : execLines) {
            for (String line : execLinesDeletion.getExecLt().keySet()) {
                final String[] split = line.split("#");
                final String className = split[0];
                final String lineNumber = split[1];
                final Location key = new Location(className, Integer.parseInt(lineNumber));
                if (flacocoMap.containsKey(key)) {
                    scorePerSuspiciousLine.put(line, flacocoMap.get(key).getScore());
                }
            }
        }
        return scorePerSuspiciousLine;
    }


}
