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
public class EmptyIntersectionTestFilterTest extends AbstractTestFilterTest {
    @Override
    protected TestFilter getFilter() {
        return TestFilterEnum.EMPTY_INTERSECTION.get();
    }

    @Test
    void test() {
        final MethodNamesPerClassNames methodNamesPerClassNames = this.runFilter();
        assertEquals(8, methodNamesPerClassNames.size());
    }
}
