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

    protected String getReportPathname() {
        return "mark";
    }

    @Override
    public void run(Configuration configuration) {
        this.configuration = configuration;
        LOGGER.info("Run Mark - {}", configuration.toString());
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
        JSONUtils.write(configuration.output + "/coverage_first.json", coverageFirstVersion);
        JSONUtils.write(configuration.output + "/coverage_second.json", coverageSecondVersion);
        // Exec(l,t)
        final List<ExecsLines> execLineTestMaps = Exec.computeExecLT(
                configuration.pathToFirstVersion,
                configuration.pathToSecondVersion,
                coverageFirstVersion,
                coverageSecondVersion,
                configuration.diff
        );
        JSONUtils.write(configuration.output + "/exec_deletions.json", execLineTestMaps.get(0));
        configuration.setExecLinesAdditions(execLineTestMaps.get(0));
        JSONUtils.write(configuration.output + "/exec_additions.json", execLineTestMaps.get(1));
        configuration.setExecLinesDeletions(execLineTestMaps.get(1));

        // 2 Compute line values
        final Map<String, Integer> thetaL = Line.computeThetaL(execLineTestMaps);
        JSONUtils.write(configuration.output + "/theta.json", thetaL);
        final Map<String, Double> phiL = Line.computePhiL(Line.computeTheta(thetaL), thetaL);
        // 3
        final Map<String, Double> omegaT = Test.computeOmegaT(execLineTestMaps, phiL);
        JSONUtils.write(configuration.output + "/omega.json", omegaT);
        final Map<String, Data> omegaUpperT = Test.computeOmegaUpperT(omegaT, consideredDeltas);
        JSONUtils.write(configuration.output + "/Omega.json", omegaUpperT);
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
        JSONUtils.write(configuration.output + "/deltaOmega.json", deltaOmega);
        configuration.setDeltaOmega(deltaOmega);
        LOGGER.info("DeltaOmega {}", deltaOmega);
    }

}
