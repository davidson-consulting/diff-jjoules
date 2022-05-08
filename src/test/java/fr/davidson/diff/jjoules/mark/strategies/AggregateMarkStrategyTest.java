package fr.davidson.diff.jjoules.mark.strategies;

import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 08/05/2022
 */
public class AggregateMarkStrategyTest extends AbstractMarkStrategyTest {

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
                        new Data(0, 0, 0, 0, 0, 0, 0, 0),
                        new Data(50, 50, 50, 50, 50, 50, 50, 50)
                )
        );
        assertTrue(this.runStrategy(new Datas(), new Datas(), deltas));
    }

    @Override
    protected MarkStrategyEnum getStrategy() {
        return MarkStrategyEnum.AGGREGATE;
    }
}
