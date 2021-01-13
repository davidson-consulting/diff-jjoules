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
        final Map<String, Map<String, Map<String, List<Integer>>>> coverageV1 = getCoverage(configuration.pathToFirstVersion, configuration.testsList);
        final Map<String, Map<String, Map<String, List<Integer>>>> coverageV2 = getCoverage(configuration.pathToSecondVersion, configuration.testsList);
        final CodeFinder finder = new CodeFinder(
                configuration.pathToFirstVersion,
                configuration.pathToSecondVersion,
                configuration.diff,
                coverageV1,
                coverageV2
        );
        final Map<String, List<Integer>> faultyLines = finder.findFaultyLines();
        configuration.report.output(faultyLines);
    }

    private static Map<String, Map<String, Map<String, List<Integer>>>> getCoverage(final String pathToFirstVersion, Map<String, List<String>> tests) {
        System.out.println("Computing coverage for " + pathToFirstVersion);
        new CloverExecutor().instrumentAndRunGivenTest(pathToFirstVersion, tests);
        return new CloverReader().read(pathToFirstVersion);
    }

}
