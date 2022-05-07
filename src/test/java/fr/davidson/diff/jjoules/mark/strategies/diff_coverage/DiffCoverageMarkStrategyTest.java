package fr.davidson.diff.jjoules.mark.strategies.diff_coverage;

import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.strategies.AbstractMarkStrategyTest;
import fr.davidson.diff.jjoules.mark.strategies.MarkStrategyEnum;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public class DiffCoverageMarkStrategyTest extends AbstractMarkStrategyTest {
    @Override
    protected MarkStrategyEnum getStrategy() {
        return MarkStrategyEnum.DIFF_COVERAGE;
    }

    @Test
    void testEnergyRegression() {
        assertTrue(
                this.runStrategy(
                        JSONUtils.read("src/test/resources/json/data_v1.json", Datas.class),
                        JSONUtils.read("src/test/resources/json/data_v2.json", Datas.class),
                        JSONUtils.read("src/test/resources/json/deltas.json", Deltas.class)
                )
        );
    }

}
