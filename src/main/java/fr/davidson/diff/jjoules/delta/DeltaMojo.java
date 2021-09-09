package fr.davidson.diff.jjoules.delta;

import eu.stamp_project.testrunner.EntryPoint;
import eu.stamp_project.testrunner.listener.TestResult;
import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.CSVReader;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import fr.davidson.diff.jjoules.util.maven.MavenRunner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 23/06/2021
 */
@Mojo(name = "delta")
public class DeltaMojo extends DiffJJoulesMojo {


    @Override
    public void run(Configuration configuration) {
        getLog().info("Run Delta - " + configuration.toString());
        final Map<String, List<String>> testsList = configuration.getTestsList();
        final Datas dataV1 = new Datas();
        final Datas dataV2 = new Datas();
        MeasureEnergyConsumption.measureEnergyConsumptionForBothVersion(
                configuration,
                dataV1,
                dataV2,
                testsList
        );
        final Map<String, Data> mediansV1 = Computation.computeMedian(dataV1);
        final Map<String, Data> mediansV2 = Computation.computeMedian(dataV2);
        final Deltas deltas = Computation.computeDelta(mediansV1, mediansV2);
        JSONUtils.write(configuration.pathToJSONDataV1, dataV1);
        configuration.setDataV1(dataV1);
        JSONUtils.write(configuration.pathToJSONDataV2, dataV2);
        configuration.setDataV2(dataV2);
        JSONUtils.write(configuration.pathToJSONDelta, deltas);
        configuration.setDeltas(deltas);
        filterTestMethods(configuration, dataV1, dataV2, deltas);
    }

    private void filterTestMethods(Configuration configuration, Datas dataV1, Datas dataV2, Deltas deltaPerTestMethodName) {
        final Map<String, Boolean> emptyIntersectionPerTestMethodName = dataV1.isEmptyIntersectionPerTestMethodName(dataV2);
        final Map<String, List<String>> consideredTestsNames = new HashMap<>();
        for (String key : deltaPerTestMethodName.keySet()) {
            if (emptyIntersectionPerTestMethodName.get(key)) {
                final String[] split = key.split("#");
                if (!consideredTestsNames.containsKey(split[0])) {
                    consideredTestsNames.put(split[0], new ArrayList<>());
                }
                consideredTestsNames.get(split[0]).add(split[1]);
            }
        }
        JSONUtils.write(
                configuration.pathToJSONConsideredTestMethodNames,
                consideredTestsNames
        );
        configuration.setConsideredTestsNames(consideredTestsNames);
    }
}
