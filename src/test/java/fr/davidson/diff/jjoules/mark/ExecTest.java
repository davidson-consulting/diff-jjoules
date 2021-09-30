package fr.davidson.diff.jjoules.mark;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import fr.davidson.diff.jjoules.mark.computation.Exec;
import fr.davidson.diff.jjoules.mark.computation.ExecsLines;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/09/2021
 */
public class ExecTest {

    @Test
    void test() {
        final String absolutePathFirstVersion = new File("src/test/resources/diff-jjoules-demo").getAbsolutePath();
        final String absolutePathSecondVersion = new File("src/test/resources/diff-jjoules-demo-v2").getAbsolutePath();
        final Coverage coverageFirstVersion =
                JSONUtils.read("/home/benjamin/workspace/diff-jjoules/src/test/resources/diff-jjoules-demo/coverage.json", Coverage.class);
        final Coverage coverageSecondVersion =
                JSONUtils.read("/home/benjamin/workspace/diff-jjoules/src/test/resources/diff-jjoules-demo-v2/coverage.json", Coverage.class);
        final String diff = "--- src/main/java/fr/davidson/diff_jjoules_demo/InternalList.java\t2021-09-30 14:31:45.199926959 +0200\n" +
                "+++ ../diff-jjoules-demo-v2/src/main/java/fr/davidson/diff_jjoules_demo/InternalList.java\t2021-09-30 14:51:34.126384267 +0200\n" +
                "@@ -19,8 +19,9 @@\n" +
                " \n" +
                "     public List<T> map(Function<T, T> operator) {\n" +
                "         final List<T> mappedList = new ArrayList<>();\n" +
                "-        for (T t : this.internalList) {\n" +
                "-            mappedList.add(operator.apply(t));\n" +
                "+        for (int i = 0 ; i < this.internalList.size() ; i++) {\n" +
                "+            final T current = this.internalList.get(i);\n" +
                "+            mappedList.add(operator.apply(current));\n" +
                "         }\n" +
                "         return mappedList;\n" +
                "     }\n";
        final List<ExecsLines> execsLines = Exec.computeExecLT(
                absolutePathFirstVersion,
                absolutePathSecondVersion,
                coverageFirstVersion,
                coverageSecondVersion,
                diff
        );
        final ExecsLines deletionsExecLines =
                JSONUtils.read(absolutePathFirstVersion + "/exec_deletions.json", ExecsLines.class);
        final ExecsLines additionsExecLines =
                JSONUtils.read(absolutePathSecondVersion + "/exec_additions.json", ExecsLines.class);
        assertEquals(deletionsExecLines.toString(), execsLines.get(0).toString());
        assertEquals(additionsExecLines.toString(), execsLines.get(1).toString());
    }
}
