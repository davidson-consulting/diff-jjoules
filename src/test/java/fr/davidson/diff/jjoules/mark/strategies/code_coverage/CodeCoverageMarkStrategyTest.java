package fr.davidson.diff.jjoules.mark.strategies.code_coverage;

import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.strategies.AbstractMarkStrategyTest;
import fr.davidson.diff.jjoules.mark.strategies.MarkStrategyEnum;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public class CodeCoverageMarkStrategyTest extends AbstractMarkStrategyTest {
    @Override
    protected MarkStrategyEnum getStrategy() {
        return MarkStrategyEnum.CODE_COVERAGE;
    }

    @Test
    void testEnergyRegression() {
        assertFalse(this.runStrategy(
                JSONUtils.read("src/test/resources/json/data_v1.json", Datas.class),
                JSONUtils.read("src/test/resources/json/data_v2.json", Datas.class),
                JSONUtils.read("src/test/resources/json/deltas.json", Deltas.class)
        ));
    }

}
