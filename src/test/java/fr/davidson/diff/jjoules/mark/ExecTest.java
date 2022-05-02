package fr.davidson.diff.jjoules.mark;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import fr.davidson.diff.jjoules.mark.strategies.original.computation.Exec;
import fr.davidson.diff.jjoules.mark.strategies.original.computation.ExecsLines;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/09/2021
 */
public class ExecTest {

    @Test
    void test() {
        final String absolutePathFirstVersion = new File("src/test/resources/v1").getAbsolutePath();
        final String absolutePathSecondVersion = new File("src/test/resources/v2").getAbsolutePath();
        final Coverage coverageFirstVersion =
                JSONUtils.read("src/test/resources/json/coverage_first.json", Coverage.class);
        final Coverage coverageSecondVersion =
                JSONUtils.read("src/test/resources/json/coverage_second.json", Coverage.class);
        final String diff = "--- src/main/java/fr/davidson/diff_jjoules_demo/InternalList.java\t2021-09-30 14:31:45.199926959 +0200\n" +
                "+++ ../v2/src/main/java/fr/davidson/diff_jjoules_demo/InternalList.java\t2021-09-30 14:51:34.126384267 +0200\n" +
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
        assertEquals("[{fr.davidson.diff_jjoules_demo.InternalList#29={fr.davidson.diff_jjoules_demo.InternalListTest#testCountFailing=0, fr.davidson.diff_jjoules_demo.InternalListTest#testCount=0}, fr.davidson.diff_jjoules_demo.InternalList#31={fr.davidson.diff_jjoules_demo.InternalListTest#testCountFailing=0, fr.davidson.diff_jjoules_demo.InternalListTest#testCount=0}, fr.davidson.diff_jjoules_demo.InternalList#32={fr.davidson.diff_jjoules_demo.InternalListTest#testCountFailing=0, fr.davidson.diff_jjoules_demo.InternalListTest#testCount=0}, fr.davidson.diff_jjoules_demo.InternalList#30={fr.davidson.diff_jjoules_demo.InternalListTest#testCountFailing=0, fr.davidson.diff_jjoules_demo.InternalListTest#testCount=1}}]", execsLines.get(1).toString());
    }
}
