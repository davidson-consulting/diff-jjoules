package fr.davidson.diff.jjoules.localization;

import java.util.List;
import java.util.Map;

public interface Sorter {

    Map<String, List<Integer>> sortFaultyLines(final Map<String, List<Integer>> suspectLines);

}
