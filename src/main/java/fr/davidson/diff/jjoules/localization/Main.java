package fr.davidson.diff.jjoules.localization;


import eu.stamp_project.diff_test_selection.clover.CloverExecutor;
import eu.stamp_project.diff_test_selection.clover.CloverReader;
import fr.davidson.diff.jjoules.localization.configuration.Configuration;
import fr.davidson.diff.jjoules.localization.configuration.Options;
import fr.davidson.diff.jjoules.localization.finder.CodeFinder;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        run(Options.parse(args));
    }

    public static void run(Configuration configuration) {
        System.out.println(configuration.toString());
        final Map<String, List<String>> testsList =
                configuration.selector.select(configuration.pathToJSONDataFirstVersion, configuration.pathToJSONDataSecondVersion);
        configuration.report.outputSelectedTests(testsList, configuration.selector.getTestRecordPerTestClass());
        System.out.println(testsList);
        final Map<String, Map<String, Map<String, List<Integer>>>> coverageV1 = getCoverage(configuration.pathToFirstVersion, testsList);
        final Map<String, Map<String, Map<String, List<Integer>>>> coverageV2 = getCoverage(configuration.pathToSecondVersion, testsList);
        final CodeFinder finder = new CodeFinder(
                configuration.pathToFirstVersion,
                configuration.pathToSecondVersion,
                configuration.diff,
                coverageV1,
                coverageV2
        );
        final Map<String, List<Integer>> suspectLines = finder.findFaultyLines();
        configuration.report.outputSuspectLines(suspectLines);
    }

    private static Map<String, Map<String, Map<String, List<Integer>>>> getCoverage(final String pathToFirstVersion, Map<String, List<String>> tests) {
        System.out.println("Computing coverage for " + pathToFirstVersion);
        new CloverExecutor().instrumentAndRunGivenTest(pathToFirstVersion, tests);
        return Collections.emptyMap();
//        return new CloverReader().read(pathToFirstVersion);
    }

}
