package fr.davidson.diff.jjoules.util;

import eu.stamp_project.diff_test_selection.utils.DiffTestSelectionChecker;
import eu.stamp_project.diff_test_selection.utils.DiffTestSelectionUtils;
import fr.davidson.diff.jjoules.util.wrapper.Wrapper;

import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 05/04/2022
 */
public class ModuleFinder {

    public static String findConcernedModule(
            String pathRootFolder,
            String diff,
            Wrapper wrapper
    ) {
        final String[] lines = diff.split(System.getProperty("line.separator"));
        final Map<String, Integer> modules = new HashMap<>();
        for (int i = 0; i < lines.length; i++) {
            final String currentLine = lines[i];
            if (DiffTestSelectionChecker.checkIfDiffLineIsAJavaFileModification(currentLine)) {
                final String javaFile = DiffTestSelectionUtils.getJavaFile(currentLine);
                if (javaFile.contains(wrapper.getPathToTestFolder())) {
                    continue;
                }
                final String module = javaFile.split(wrapper.getPathToSrcFolder())[0].split(pathRootFolder)[0];
                if (!modules.containsKey(module)) {
                    modules.put(module, 0);
                }
                modules.put(module, modules.get(module) + 1);
            }
        }
        return modules.keySet().stream().max(Comparator.comparingInt(modules::get)).orElse("");
    }

}
