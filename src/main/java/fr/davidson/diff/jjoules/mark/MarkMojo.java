package fr.davidson.diff.jjoules.mark;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.computation.*;
import fr.davidson.diff.jjoules.util.CSVReader;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
@Mojo(name = "mark")
public class MarkMojo extends DiffJJoulesMojo {

    @Override
    public void run(Configuration configuration) {
        getLog().info("Run Mark - " + configuration.toString());
        final Map<String, List<String>> testsListName = CSVReader.readFile(configuration.pathToTestListAsCSV);
        final Deltas data = JSONUtils.read(configuration.pathToJSONDelta, Deltas.class);

        final Datas dataV1 = JSONUtils.read(configuration.pathToJSONDataV1, Datas.class);
        final Datas dataV2 = JSONUtils.read(configuration.pathToJSONDataV2, Datas.class);
        final Map<String, Boolean> emptyIntersectionPerTestMethodName = dataV1.isEmptyIntersectionPerTestMethodName(dataV2);

        final Deltas consideredDeltas = new Deltas();
        for (String key : data.keySet()) {
            final Delta delta = data.get(key);
            if (emptyIntersectionPerTestMethodName.get(key)) {
                consideredDeltas.put(key, delta);
            }
        }

        final File file = new File("diff-jjoules");
        if (file.exists()) {
            file.delete();
        }
        file.mkdir();

        // 1 Compute coverage
        final Map<String, Coverage> coveragePerTestMethodNameFirstVersion =
                CoverageComputation.computeCoverageForGivenVersionOfTests(testsListName, configuration.pathToFirstVersion);
        final Map<String, Coverage> coveragePerTestMethodNameSecondVersion =
                CoverageComputation.computeCoverageForGivenVersionOfTests(testsListName, configuration.pathToSecondVersion);
        JSONUtils.write(file.getAbsolutePath() + "/coverage_first.json", coveragePerTestMethodNameFirstVersion);
        JSONUtils.write(file.getAbsolutePath() + "/coverage_second.json", coveragePerTestMethodNameSecondVersion);

        // Exec(l,t)
        final List<ExecsLines> execLineTestMaps = Exec.computeExecLT(
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
        final Map<String, Data> omegaUpperT = Test.computeOmegaUpperT(omegaT, consideredDeltas);
        JSONUtils.write(file.getAbsolutePath() + "/Omega.json", omegaUpperT);

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
        JSONUtils.write(file.getAbsolutePath() + "/deltaOmega.json", deltaOmega);
        getLog().info("DeltaOmega " + deltaOmega);
    }
}
