package fr.davidson.diff.jjoules.suspect;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.mark.computation.ExecLineTestMap;
import fr.davidson.diff.jjoules.mark.computation.ExecsLines;
import fr.davidson.diff.jjoules.suspect.fl.FlacocoRunner;
import fr.davidson.diff.jjoules.util.CSVReader;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.spoonlabs.flacoco.api.Suspiciousness;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/06/2021
 */
@Mojo(name = "suspect")
public class SuspectMojo extends DiffJJoulesMojo {

    @Override
    public void run(Configuration configuration) {
        getLog().info("Run Suspect - " + configuration.toString());
        final Set<String> testsList = FullQualifiedName.toSetFullQualifiedNames(CSVReader.readFile(configuration.pathToTestListAsCSV));
        if (testsList.isEmpty()) {
            throw new RuntimeException();
        }
        getLog().info(String.join("\n", testsList));
        // execute flacoco
        final Map<String, Suspiciousness> runV1 = FlacocoRunner.run(
                configuration.getClasspathV1AsString(),
                configuration.pathToFirstVersion,
                testsList
        );
        final Map<String, Suspiciousness> runV2 = FlacocoRunner.run(
                configuration.getClasspathV2AsString(),
                configuration.pathToSecondVersion,
                testsList
        );

        final ExecsLines execLinesAdditions = configuration.getExecLinesAdditions();
        final ExecsLines execLinesDeletions = configuration.getExecLinesDeletions();
        final Map<String, Double> suspiciousLinesV1 = getSuspiciousLinesFromDiff(runV1, execLinesDeletions);
        final Map<String, Double> suspiciousLinesV2 = getSuspiciousLinesFromDiff(runV2, execLinesAdditions);

        getLog().info("Suspect Lines in V1");
        suspiciousLinesV1.keySet()
                .stream()
                .sorted((key1, key2) -> -(int)((suspiciousLinesV1.get(key1)*100.0D) - (suspiciousLinesV1.get(key2)*100.0D)))
                .forEach(key -> getLog().info(key + ": " + suspiciousLinesV1.get(key)));
        getLog().info("Suspect Lines in V2");
        suspiciousLinesV2.keySet()
                .stream()
                .sorted((key1, key2) -> -(int)((suspiciousLinesV2.get(key1)*100.0D) - (suspiciousLinesV2.get(key2)*100.0D)))
                .forEach(key -> getLog().info(key + ": " + suspiciousLinesV2.get(key)));
        JSONUtils.write(configuration.pathToJSONSuspiciousV1, suspiciousLinesV1);
        configuration.setScorePerLineV1(suspiciousLinesV1);
        JSONUtils.write(configuration.pathToJSONSuspiciousV2, suspiciousLinesV2);
        configuration.setScorePerLineV2(suspiciousLinesV2);

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
