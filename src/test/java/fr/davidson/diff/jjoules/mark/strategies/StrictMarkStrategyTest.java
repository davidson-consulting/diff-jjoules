package fr.davidson.diff.jjoules.mark.strategies;

import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public class StrictMarkStrategyTest extends AbstractMarkStrategyTest {

    @Test
    void testHasSingleEnergyRegression() {
        assertFalse(this.runStrategy());
    }

    @Test
    void testHasNoEnergyRegression() {
        final Deltas deltas = new Deltas();
        deltas.put("TestClass#testOne", new Delta(
                        new Data(50, 50, 50, 50, 50, 50, 50, 50),
                        new Data(0, 0, 0, 0, 0, 0, 0, 0)
                )
        );
        deltas.put("TestClass#testTwo", new Delta(
                        new Data(50, 50, 50, 50, 50, 50, 50, 50),
                        new Data(10, 10, 10, 10, 10, 10, 10, 10)
                )
        );
        assertTrue(this.runStrategy(new Datas(), new Datas(), deltas));
    }

    @Override
    protected MarkStrategyEnum getStrategy() {
        return MarkStrategyEnum.STRICT;
    }
}
