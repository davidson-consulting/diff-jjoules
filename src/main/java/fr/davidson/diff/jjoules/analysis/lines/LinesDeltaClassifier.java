package fr.davidson.diff.jjoules.analysis.lines;

import eu.stamp_project.diff_test_selection.diff.ModifiedLinesTool;
import eu.stamp_project.diff_test_selection.utils.DiffTestSelectionChecker;
import fr.davidson.diff.jjoules.analysis.tests.DeltasComputation;
import fr.davidson.diff.jjoules.analysis.tests.TestDeltaClassifier;
import fr.davidson.diff.jjoules.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LinesDeltaClassifier {

    private final String pathToFirstVersion;
    private final String pathToSecondVersion;
    private final String diff;

    private final Map<String, Set<Integer>> positiveLines;
    private final Map<String, Set<Integer>> negativeLines;
    private final Map<String, Set<Integer>> unknownLines;

    private final Map<String, Double> positiveTests;
    private final Map<String, Double> negativeTests;
    private final Map<String, Double> neutralTests;

    private final double globalDelta;
    private final double negativeDelta;
    private final double positiveDelta;

    public LinesDeltaClassifier(DeltasComputation deltasComputation,
                                TestDeltaClassifier testDeltaClassifier,
                                String pathToFirstVersion,
                                String pathToSecondVersion,
                                String diff) {
        this(
                testDeltaClassifier.getPositiveTests(),
                testDeltaClassifier.getNegativeTests(),
                testDeltaClassifier.getNeutralTests(),
                pathToFirstVersion,
                pathToSecondVersion,
                diff,
                deltasComputation.getGlobalDelta(),
                deltasComputation.getNegativeDelta(),
                deltasComputation.getPositiveDelta()
        );
    }

    public LinesDeltaClassifier(Map<String, Double> positiveTests,
                                Map<String, Double> negativeTests,
                                Map<String, Double> neutralTests,
                                String pathToFirstVersion,
                                String pathToSecondVersion,
                                String diff,
                                double globalDelta,
                                double negativeDelta,
                                double positiveDelta
    ) {
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.diff = diff;
        this.positiveLines = new HashMap<>();
        this.negativeLines = new HashMap<>();
        this.unknownLines = new HashMap<>();
        this.positiveTests = positiveTests;
        this.negativeTests = negativeTests;
        this.neutralTests = neutralTests;
        this.globalDelta = globalDelta;
        this.negativeDelta = negativeDelta;
        this.positiveDelta = positiveDelta;
    }

    public void classify(Map<String, Map<String, Map<String, List<Integer>>>> coverageV1,
                         Map<String, Map<String, Map<String, List<Integer>>>> coverageV2) {
        final String[] lines = this.diff.split(System.getProperty("line.separator"));
        for (int i = 0; i < lines.length; i++) {
            final String currentLine = lines[i];
            if (DiffTestSelectionChecker.checkIfDiffLineIsAJavaFileModification(currentLine)) {
                final ModifiedLinesTool modifiedLinesTool = new ModifiedLinesTool(this.pathToFirstVersion, this.pathToSecondVersion);
                modifiedLinesTool.compute(currentLine, lines[++i]);
                if (modifiedLinesTool.hasResult()) {
                    this._classify(modifiedLinesTool.getAdditionPerQualifiedName(), coverageV2);
                    this._classify(modifiedLinesTool.getDeletionPerQualifiedName(), coverageV1);
                }
            }
        }
    }

    private void _classify(Map<String, List<Integer>> modifiedLinesPerClassName,
                           Map<String, Map<String, Map<String, List<Integer>>>> coverage) {
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
                            final String key = testClassName + '-' + testMethodName;
                            System.out.println(key);
                            if (this.positiveTests.containsKey(key)) {
                                addToMapIfNotInTheOtherOne(
                                        this.positiveTests.get(key),
                                        this.negativeDelta,
                                        this.positiveLines,
                                        this.negativeLines,
                                        modifiedClassName,
                                        matchingLines
                                );
                            } else if (this.negativeTests.containsKey(key)) {
                                addToMapIfNotInTheOtherOne(
                                        this.negativeTests.get(key),
                                        this.positiveDelta,
                                        this.negativeLines,
                                        this.positiveLines,
                                        modifiedClassName,
                                        matchingLines
                                );
                            } else {
                                Utils.addToGivenMapSet(modifiedClassName, matchingLines, this.unknownLines);
                            }
                        }
                    }
                }
            }
        }
    }

    private void addToMapIfNotInTheOtherOne(double deltaTest,
                                            double deltaOtherCategory,
                                            final Map<String, Set<Integer>> map,
                                            final Map<String, Set<Integer>> otherMap,
                                            String modifiedClassName,
                                            List<Integer> matchingLines) {
        if (Math.abs(deltaTest) > Math.abs(deltaOtherCategory)) {
            if (otherMap.containsKey(modifiedClassName) &&
                    otherMap.get(modifiedClassName).containsAll(matchingLines)) {
                otherMap.get(modifiedClassName).removeAll(matchingLines);
                if (otherMap.get(modifiedClassName).isEmpty()) {
                    otherMap.remove(modifiedClassName);
                }
                Utils.addToGivenMapSet(modifiedClassName, matchingLines, this.unknownLines);
            } else {
                Utils.addToGivenMapSet(modifiedClassName, matchingLines, map);
            }
        }
    }

    public Map<String, Set<Integer>> getPositiveLines() {
        return positiveLines;
    }

    public Map<String, Set<Integer>> getNegativeLines() {
        return negativeLines;
    }

    public Map<String, Set<Integer>> getUnknownLines() {
        return unknownLines;
    }
}
