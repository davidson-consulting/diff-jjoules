package fr.davidson.diff.jjoules.mark.filter;

import fr.davidson.diff.jjoules.mark.filters.TestFilter;
import fr.davidson.diff.jjoules.mark.filters.TestFilterEnum;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 05/05/2022
 */
public class AllTestFilterTest extends AbstractTestFilterTest {

    protected TestFilter getFilter() {
        return TestFilterEnum.ALL.get();
    }

    @Test
    void test() {
        final MethodNamesPerClassNames methodNamesPerClassNames = this.runFilter();
        assertEquals(64, methodNamesPerClassNames.size());
    }
}
