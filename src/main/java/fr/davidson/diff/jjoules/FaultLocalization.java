package fr.davidson.diff.jjoules;


import eu.stamp_project.diff_test_selection.clover.CloverExecutor;
import eu.stamp_project.diff_test_selection.clover.CloverReader;
import eu.stamp_project.diff_test_selection.configuration.Configuration;
import fr.davidson.diff.jjoules.localization.CodeFinder;

import java.util.*;

public class FaultLocalization {

    public static void main(String[] args) {
        run(new Configuration(
                "/tmp/v1/gson",
                "/tmp/v2/gson",
                "./",
                "CSV",
                "",
                false
        ));
    }

    public static void run(Configuration configuration) {
        System.out.println(configuration.toString());
        final Map<String, List<String>> tests = new HashMap<>();
        tests.put("com.google.gson.DefaultDateTypeAdapterTest", new ArrayList<>());
        tests.get("com.google.gson.DefaultDateTypeAdapterTest").add("testDatePattern");
        final Map<String, Map<String, Map<String, List<Integer>>>> coverageV1 = getCoverage(configuration.pathToFirstVersion, tests);
        final Map<String, Map<String, Map<String, List<Integer>>>> coverageV2 = getCoverage(configuration.pathToSecondVersion, tests);
        final CodeFinder finder = new CodeFinder(
                configuration.pathToFirstVersion,
                configuration.pathToSecondVersion,
                configuration.diff,
                coverageV1,
                coverageV2
        );
        final Map<String, List<Integer>> faultyLines = finder.findFaultyLines();
        System.out.println(faultyLines);
    }

    private static Map<String, Map<String, Map<String, List<Integer>>>> getCoverage(final String pathToFirstVersion, Map<String, List<String>> tests) {
        System.out.println("Computing coverage for " + pathToFirstVersion);
        new CloverExecutor().instrumentAndRunGivenTest(pathToFirstVersion, tests);
        return new CloverReader().read(pathToFirstVersion);
    }

}
