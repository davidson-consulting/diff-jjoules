package fr.davidson.diff.jjoules.mark;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.computation.*;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class MarkStep extends DiffJJoulesStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkStep.class);

    public static final String PATH_TO_JSON_COVERAGE_FIRST = "coverage_first.json";

    public static final String PATH_TO_JSON_COVERAGE_SECOND = "coverage_second.json";

    public static final String PATH_TO_JSON_EXEC_DELETION = "exec_deletions.json";

    public static final String PATH_TO_JSON_EXEC_ADDITIONS = "exec_additions.json";

    public static final String PATH_TO_JSON_THETA = "theta.json";

    public static final String PATH_TO_JSON_OMEGA = "omega.json";

    public static final String PATH_TO_JSON_OMEGA_UPPER = "Omega.json";

    public static final String PATH_TO_JSON_DELTA_OMEGA = "deltaOmega.json";

    protected String getReportPathname() {
        return "mark";
    }

    @Override
    protected void _run(Configuration configuration) {
        this.configuration = configuration;
        LOGGER.info("Run Mark");
        final Map<String, List<String>> consideredTestsNames = configuration.getConsideredTestsNames();
        final Deltas deltas = configuration.getDeltas();
        final Map<String, Delta> consideredDeltas = new HashMap<>();
        for (String key : deltas.keySet()) {
            final String[] splittedKey = key.split("#");
            if (consideredTestsNames.containsKey(splittedKey[0]) && consideredTestsNames.get(splittedKey[0]).contains(splittedKey[1])) {
                consideredDeltas.put(Utils.toFullQualifiedName(splittedKey[0], splittedKey[1]), deltas.get(key));
            }
        }
        // 1 Compute coverage
        final Coverage coverageFirstVersion =
                CoverageComputation.computeCoverageForGivenVersionOfTests(
                        consideredTestsNames,
                        configuration.pathToFirstVersion
                );
        final Coverage coverageSecondVersion =
                CoverageComputation.computeCoverageForGivenVersionOfTests(
                        consideredTestsNames,
                        configuration.pathToSecondVersion
                );
        JSONUtils.write(configuration.output + "/" + PATH_TO_JSON_COVERAGE_FIRST, coverageFirstVersion);
        JSONUtils.write(configuration.output + "/" + PATH_TO_JSON_COVERAGE_SECOND, coverageSecondVersion);
        // Exec(l,t)
        final List<ExecsLines> execLineTestMaps = Exec.computeExecLT(
                configuration.pathToFirstVersion,
                configuration.pathToSecondVersion,
                coverageFirstVersion,
                coverageSecondVersion,
                configuration.diff
        );
        JSONUtils.write(configuration.output + "/" + PATH_TO_JSON_EXEC_DELETION, execLineTestMaps.get(0));
        configuration.setExecLinesAdditions(execLineTestMaps.get(0));
        JSONUtils.write(configuration.output + "/" + PATH_TO_JSON_EXEC_ADDITIONS, execLineTestMaps.get(1));
        configuration.setExecLinesDeletions(execLineTestMaps.get(1));

        // 2 Compute line values
        final Map<String, Integer> thetaL = Line.computeThetaL(execLineTestMaps);
        JSONUtils.write(configuration.output + "/" + PATH_TO_JSON_THETA, thetaL);
        final Map<String, Double> phiL = Line.computePhiL(Line.computeTheta(thetaL), thetaL);
        // 3
        final Map<String, Double> omegaT = Test.computeOmegaT(execLineTestMaps, phiL);
        JSONUtils.write(configuration.output + "/" + PATH_TO_JSON_OMEGA, omegaT);
        final Map<String, Data> omegaUpperT = Test.computeOmegaUpperT(omegaT, consideredDeltas);
        JSONUtils.write(configuration.output + "/" + PATH_TO_JSON_OMEGA_UPPER, omegaUpperT);
        final Data deltaOmega = new Data(
                omegaUpperT.values().stream().map(d -> d.energy).reduce(Double::sum).orElse(0.0D),
                omegaUpperT.values().stream().map(d -> d.instructions).reduce(Double::sum).orElse(0.0D),
                omegaUpperT.values().stream().map(d -> d.durations).reduce(Double::sum).orElse(0.0D),
                omegaUpperT.values().stream().map(d -> d.cycles).reduce(Double::sum).orElse(0.0D),
                omegaUpperT.values().stream().map(d -> d.caches).reduce(Double::sum).orElse(0.0D),
                omegaUpperT.values().stream().map(d -> d.cacheMisses).reduce(Double::sum).orElse(0.0D),
                omegaUpperT.values().stream().map(d -> d.branches).reduce(Double::sum).orElse(0.0D),
                omegaUpperT.values().stream().map(d -> d.branchMisses).reduce(Double::sum).orElse(0.0D)
        );
        JSONUtils.write(configuration.output + "/" + PATH_TO_JSON_DELTA_OMEGA, deltaOmega);
        configuration.setDeltaOmega(deltaOmega);
    }

}
