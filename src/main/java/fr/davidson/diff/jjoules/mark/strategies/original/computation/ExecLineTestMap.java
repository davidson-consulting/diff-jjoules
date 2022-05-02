package fr.davidson.diff.jjoules.mark.strategies.original.computation;

import fr.davidson.diff.jjoules.util.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/06/2021
 */
public class ExecLineTestMap {

    private final Map<String, Map<String, Integer>> execLt;

    public ExecLineTestMap() {
        this.execLt = new HashMap<>();
    }

    public void addExecutionPerLine(String className, Integer line, Map<String, Integer> executionsPerTestMethod) {
        final String key = Utils.toFullQualifiedName(className, "" + line);
        if (!this.execLt.containsKey(key)) {
            this.execLt.put(key, new HashMap<>());
        }
        for (String testMethodName : executionsPerTestMethod.keySet()) {
            this.execLt.get(key).put(testMethodName, executionsPerTestMethod.get(testMethodName));
        }
    }

    public Map<String, Map<String, Integer>> getExecLt() {
        return execLt;
    }

    @Override
    public String toString() {
        return this.execLt.toString();
    }
}
