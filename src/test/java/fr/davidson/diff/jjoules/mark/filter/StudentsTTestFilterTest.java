package fr.davidson.diff.jjoules.mark.filter;

import fr.davidson.diff.jjoules.mark.filters.TestFilter;
import fr.davidson.diff.jjoules.mark.filters.TestFilterEnum;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public class StudentsTTestFilterTest extends AbstractTestFilterTest {
    @Override
    protected TestFilter getFilter() {
        return TestFilterEnum.STUDENTS_T_TEST.get();
    }

    @Test
    void test() {
        final MethodNamesPerClassNames methodNamesPerClassNames = this.runFilter();
        assertEquals(57, methodNamesPerClassNames.size());
    }
}
