package fr.davidson.diff.jjoules.mark;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import fr.davidson.diff.jjoules.mark.computation.*;
import fr.davidson.diff.jjoules.mark.configuration.Configuration;
import fr.davidson.diff.jjoules.mark.configuration.Options;
import fr.davidson.diff.jjoules.util.CSVReader;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 10/06/2021
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        run(Options.parse(args));
    }

    public static void run(Configuration configuration) {
        System.out.println(configuration.toString());
        final Map<String, List<String>> testsListName = CSVReader.readFile(configuration.pathToTestListAsCSV);
        final Map<String, Map> dataJsonV1 = JSONUtils.read(configuration.pathToJSONDataFirstVersion, Map.class);
        final Map<String, Map> dataJsonV2 = JSONUtils.read(configuration.pathToJSONDataSecondVersion, Map.class);
        final Map<String, Double> deltaT = Delta.computeDelta(dataJsonV1, dataJsonV2);

        final File file = new File("target/diff-jjoules-mark");
        if (file.exists()) {
            file.delete();
        }
        file.mkdir();
        JSONUtils.write(file.getAbsolutePath() + "/deltas.json", deltaT);

        // 1 Compute coverage
        final Map<String, Coverage> coveragePerTestMethodNameFirstVersion =
                CoverageComputation.computeCoverageForGivenVersionOfTests(testsListName, configuration.pathToFirstVersion);
        final Map<String, Coverage> coveragePerTestMethodNameSecondVersion =
                CoverageComputation.computeCoverageForGivenVersionOfTests(testsListName, configuration.pathToSecondVersion);
        JSONUtils.write(file.getAbsolutePath() + "/coverage_first.json", coveragePerTestMethodNameFirstVersion);
        JSONUtils.write(file.getAbsolutePath() + "/coverage_second.json", coveragePerTestMethodNameSecondVersion);

        // Exec(l,t)
        final List<List<ExecLineTestMap>> execLineTestMaps = Exec.computeExecLT(
                configuration.pathToFirstVersion,
                configuration.pathToSecondVersion,
                coveragePerTestMethodNameFirstVersion,
                coveragePerTestMethodNameSecondVersion,
                configuration.diff
        );

        JSONUtils.write(file.getAbsolutePath() + "/exec_deletions.json", execLineTestMaps.get(0));
        JSONUtils.write(file.getAbsolutePath() + "/exec_additions.json", execLineTestMaps.get(1));

        // 2 Compute line values
        final Map<String, Integer> thetaL = Line.computeThetaL(execLineTestMaps);
        JSONUtils.write(file.getAbsolutePath() + "/theta.json", thetaL);
        final Map<String, Double> phiL = Line.computePhiL(Line.computeTheta(thetaL), thetaL);

        // 3
        final Map<String, Double> omegaT = Test.computeOmegaT(execLineTestMaps, phiL);
        JSONUtils.write(file.getAbsolutePath() + "/omega.json", omegaT);
        final Map<String, Double> omegaUpperT = Test.computeOmegaUpperT(omegaT, deltaT);
        JSONUtils.write(file.getAbsolutePath() + "/Omega.json", omegaUpperT);
        final Double deltaOmega = omegaUpperT.values().stream().reduce(Double::sum).orElse(0.0D);

        LOGGER.info("DeltaOmega {}", deltaOmega);
    }

}
