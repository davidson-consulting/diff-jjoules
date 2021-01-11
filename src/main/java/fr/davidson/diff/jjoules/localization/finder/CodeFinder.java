package fr.davidson.diff.jjoules.localization.finder;

import eu.stamp_project.diff_test_selection.diff.ModifiedLinesTool;
import eu.stamp_project.diff_test_selection.utils.DiffTestSelectionChecker;
import fr.davidson.diff.jjoules.localization.sorter.OccurrenceSorter;
import fr.davidson.diff.jjoules.localization.sorter.Sorter;

import java.util.*;
import java.util.stream.Collectors;

public class CodeFinder {

    private final String pathToFirstVersion;
    private final String pathToSecondVersion;
    private final String diff;
    private final Map<String, Map<String, Map<String, List<Integer>>>> coverageV1;
    private final Map<String, Map<String, Map<String, List<Integer>>>> coverageV2;
    private final Sorter sorter = new OccurrenceSorter();

    public CodeFinder(String pathToFirstVersion,
                      String pathToSecondVersion,
                      String diff,
                      Map<String, Map<String, Map<String, List<Integer>>>> coverageV1,
                      Map<String, Map<String, Map<String, List<Integer>>>> coverageV2) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.diff = diff;
        this.coverageV1 = coverageV1;
        this.coverageV2 = coverageV2;
    }

    public Map<String, List<Integer>> findFaultyLines() {
        final String[] lines = this.diff.split(System.getProperty("line.separator"));
        final Map<String, List<Integer>> suspectLines = new HashMap<>();
        for (int i = 0; i < lines.length; i++) {
            final String currentLine = lines[i];
            if (DiffTestSelectionChecker.checkIfDiffLineIsAJavaFileModification(currentLine)) {
                final ModifiedLinesTool modifiedLinesTool = new ModifiedLinesTool(this.pathToFirstVersion, this.pathToSecondVersion);
                modifiedLinesTool.compute(currentLine, lines[++i]);
                if (modifiedLinesTool.hasResult()) {
                    this.findFaultyLinesForCoverage(modifiedLinesTool.getAdditionPerQualifiedName(), this.coverageV2, suspectLines);
                    this.findFaultyLinesForCoverage(modifiedLinesTool.getDeletionPerQualifiedName(), this.coverageV1, suspectLines);
                }
            }
        }
        return this.sorter.sortFaultyLines(suspectLines);
    }

    private void findFaultyLinesForCoverage(Map<String, List<Integer>> modifiedLinesPerClassName,
                                            Map<String, Map<String, Map<String, List<Integer>>>> coverage,
                                            Map<String, List<Integer>> suspectLines) {
        for (String modifiedClassName : modifiedLinesPerClassName.keySet()) {
            final List<Integer> modifiedLines = modifiedLinesPerClassName.get(modifiedClassName);
            for (String testClassName : coverage.keySet()) {
                for (String testMethodName : coverage.get(testClassName).keySet()) {
                    final Map<String, List<Integer>> coverageOfTestMethod = coverage.get(testClassName).get(testMethodName);
                    if (coverageOfTestMethod.containsKey(modifiedClassName)) {
                        if (modifiedLines.stream().anyMatch(line -> coverageOfTestMethod.get(modifiedClassName).contains(line))) {
                            final List<Integer> matchingLines = coverageOfTestMethod.get(modifiedClassName)
                                    .stream()
                                    .filter(modifiedLines::contains)
                                    .collect(Collectors.toList());
                            if (!suspectLines.containsKey(modifiedClassName)) {
                                suspectLines.put(modifiedClassName, new ArrayList<>());
                            }
                            suspectLines.get(modifiedClassName).addAll(matchingLines);
                        }
                    }
                }
            }
        }
    }

}
