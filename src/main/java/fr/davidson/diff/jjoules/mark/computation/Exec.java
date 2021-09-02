package fr.davidson.diff.jjoules.mark.computation;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import eu.stamp_project.diff_test_selection.diff.ModifiedLinesTool;
import eu.stamp_project.diff_test_selection.utils.DiffTestSelectionChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
public class Exec {

    private final Map<String, Coverage> coveragePerTestMethodName;

    public Exec(Map<String, Coverage> coveragePerTestMethodName) {
        this.coveragePerTestMethodName = coveragePerTestMethodName;
    }

    public List<ExecLineTestMap> compute(
            Map<String, List<Integer>> modifiedLinesPerClassName
    ) {
        System.out.println(modifiedLinesPerClassName);
        final List<ExecLineTestMap> execLineTestMapList = new ArrayList<>();
        for(String className: modifiedLinesPerClassName.keySet()) {
            final ExecLineTestMap execLineTestMap = new ExecLineTestMap();
            final List<Integer> modifiedLines = modifiedLinesPerClassName.get(className);
            for (String key : coveragePerTestMethodName.keySet()) {
                for (Integer modifiedLine : modifiedLines) {
                    execLineTestMap.addExecutionPerLine(
                            className, modifiedLine,
                            coveragePerTestMethodName.get(key).
                                    getHitCountFromClassNameForLineForAll(className, modifiedLine)
                    );
                }
            }
            execLineTestMapList.add(execLineTestMap);
        }
        return execLineTestMapList;
    }

    public static List<ExecsLines> computeExecLT(
            String pathToFirstVersion,
            String pathToSecondVersion,
            Map<String, Coverage> coveragePerTestMethodNameFirstVersion,
            Map<String, Coverage> coveragePerTestMethodNameSecondVersion,
            String diff
    ) {
        final ExecsLines execLineTestMapsFirst = new ExecsLines();
        final ExecsLines execLineTestMapsSecond = new ExecsLines();
        final Exec execLtFirstVersion = new Exec(coveragePerTestMethodNameFirstVersion);
        final Exec execLtSecondVersion = new Exec(coveragePerTestMethodNameSecondVersion);
        final String[] lines = diff.split(System.getProperty("line.separator"));
        for (int i = 0; i < lines.length; i++) {
            final String currentLine = lines[i];
            if (DiffTestSelectionChecker.checkIfDiffLineIsAJavaFileModification(currentLine)) {
                final ModifiedLinesTool modifiedLinesTool = new ModifiedLinesTool(pathToFirstVersion, pathToSecondVersion);
                modifiedLinesTool.compute(currentLine, lines[++i]);
                if (modifiedLinesTool.getDeletionPerQualifiedName() == null || modifiedLinesTool.getAdditionPerQualifiedName() == null) {
                    continue;
                }
                execLineTestMapsFirst.addAll(execLtFirstVersion.compute(modifiedLinesTool.getDeletionPerQualifiedName()));
                execLineTestMapsSecond.addAll(execLtSecondVersion.compute(modifiedLinesTool.getAdditionPerQualifiedName()));
            }
        }
        return Arrays.asList(
                execLineTestMapsFirst,
                execLineTestMapsSecond
        );
    }

}
