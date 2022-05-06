package fr.davidson.diff.jjoules.mark.strategies.original;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.filters.TestFilter;
import fr.davidson.diff.jjoules.mark.strategies.MarkStrategy;
import fr.davidson.diff.jjoules.mark.strategies.original.computation.Exec;
import fr.davidson.diff.jjoules.mark.strategies.original.computation.ExecsLines;
import fr.davidson.diff.jjoules.mark.strategies.original.computation.Line;
import fr.davidson.diff.jjoules.mark.strategies.original.computation.Test;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.coverage.CoverageComputation;

import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 27/04/2022
 */
public class OriginalStrategy implements MarkStrategy {

    public static final String PATH_TO_JSON_COVERAGE_FIRST = "coverage_first.json";

    public static final String PATH_TO_JSON_COVERAGE_SECOND = "coverage_second.json";

    public static final String PATH_TO_JSON_EXEC_DELETION = "exec_deletions.json";

    public static final String PATH_TO_JSON_EXEC_ADDITIONS = "exec_additions.json";

    public static final String PATH_TO_JSON_THETA = "theta.json";

    public static final String PATH_TO_JSON_OMEGA = "omega.json";

    public static final String PATH_TO_JSON_OMEGA_UPPER = "Omega.json";

    public static final String PATH_TO_JSON_DELTA_OMEGA = "deltaOmega.json";

    private void filterTestMethods(Configuration configuration, Datas dataV1, Datas dataV2, Deltas deltaPerTestMethodName) {
        final Map<String, Boolean> emptyIntersectionPerTestMethodName = dataV1.isEmptyIntersectionPerTestMethodName(dataV2);
        final MethodNamesPerClassNames consideredTestsNames = new MethodNamesPerClassNames();
        for (String key : deltaPerTestMethodName.keySet()) {
            if (emptyIntersectionPerTestMethodName.get(key)) {
                final String[] split = key.split("#");
                if (!consideredTestsNames.containsKey(split[0])) {
                    consideredTestsNames.put(split[0], new HashSet<>());
                }
                consideredTestsNames.get(split[0]).add(split[1]);
            }
        }
        JSONUtils.write(
                configuration.getOutput() + Constants.FILE_SEPARATOR + TestFilter.PATH_TO_JSON_CONSIDERED_TEST_METHOD_NAME,
                consideredTestsNames
        );
        configuration.setConsideredTestsNames(consideredTestsNames);
    }

    @Override
    public void applyStrategy(Configuration configuration, Datas dataV1, Datas dataV2, Deltas deltas, MethodNamesPerClassNames consideredTest) {
        filterTestMethods(configuration, dataV1, dataV2, deltas);
        final MethodNamesPerClassNames consideredTestsNames = configuration.getConsideredTestsNames();
        final Map<String, Delta> consideredDeltas = new HashMap<>();
        for (String key : deltas.keySet()) {
            final String[] splittedKey = key.split("#");
            if (consideredTestsNames.containsKey(splittedKey[0]) && consideredTestsNames.get(splittedKey[0]).contains(splittedKey[1])) {
                consideredDeltas.put(Utils.toFullQualifiedName(splittedKey[0], splittedKey[1]), deltas.get(key));
            }
        }
        // 1 Compute coverage
        final List<String> fullQualifiedNameTestClasses = new ArrayList<>(consideredTestsNames.keySet());
        final List<String> testMethodNames = new ArrayList<>();
        for (String fullQualifiedNameTestClass : fullQualifiedNameTestClasses) {
            final Set<String> consideredTestMethodNames = consideredTestsNames.get(fullQualifiedNameTestClass);
            testMethodNames.addAll(consideredTestMethodNames);
        }
        final Coverage coverageFirstVersion = CoverageComputation.convert(
                CoverageComputation.getCoverage(
                        configuration.getPathToFirstVersion(),
                        configuration.getClasspathV1AsString(),
                        configuration.isJunit4(),
                        fullQualifiedNameTestClasses,
                        testMethodNames,
                        configuration.getWrapper().getBinaries()
                )
        );
        final Coverage coverageSecondVersion = CoverageComputation.convert(
                CoverageComputation.getCoverage(
                        configuration.getPathToSecondVersion(),
                        configuration.getClasspathV2AsString(),
                        configuration.isJunit4(),
                        fullQualifiedNameTestClasses,
                        testMethodNames,
                        configuration.getWrapper().getBinaries()
                )
        );
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_COVERAGE_FIRST, coverageFirstVersion);
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_COVERAGE_SECOND, coverageSecondVersion);
        // Exec(l,t)
        final List<ExecsLines> execLineTestMaps = Exec.computeExecLT(
                configuration.getPathToFirstVersion(),
                configuration.getPathToSecondVersion(),
                coverageFirstVersion,
                coverageSecondVersion,
                configuration.getDiff()
        );
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_EXEC_DELETION, execLineTestMaps.get(0));
        configuration.setExecLinesDeletions(execLineTestMaps.get(0));
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_EXEC_ADDITIONS, execLineTestMaps.get(1));
        configuration.setExecLinesAdditions(execLineTestMaps.get(1));

        // 2 Compute line values
        final Map<String, Integer> thetaL = Line.computeThetaL(execLineTestMaps);
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_THETA, thetaL);
        final Map<String, Double> phiL = Line.computePhiL(Line.computeTheta(thetaL), thetaL);
        // 3
        final Map<String, Double> omegaT = Test.computeOmegaT(execLineTestMaps, phiL);
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_OMEGA, omegaT);
        final Map<String, Data> omegaUpperT = Test.computeOmegaUpperT(omegaT, consideredDeltas);
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_OMEGA_UPPER, omegaUpperT);
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
        JSONUtils.write(configuration.getOutput() + Constants.FILE_SEPARATOR + PATH_TO_JSON_DELTA_OMEGA, deltaOmega);
        configuration.setDeltaOmega(deltaOmega);
    }
}
