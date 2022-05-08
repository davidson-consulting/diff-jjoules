package fr.davidson.diff.jjoules.selection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public class NewCoverage extends HashMap<String, Map<String, Map<String, List<Integer>>>> {

    public int getNbLineCovered() {
        int nbLineCovered = 0;
        for (String testFileName : this.keySet()) {
            for (String testName : this.get(testFileName).keySet()) {
                for (String sourceFileName : this.get(testFileName).get(testName).keySet()) {
                    nbLineCovered += this.get(testFileName).get(testName).get(sourceFileName).size();
                }
            }
        }
        return nbLineCovered;
    }

}