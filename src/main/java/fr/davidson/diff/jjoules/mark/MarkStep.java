package fr.davidson.diff.jjoules.mark;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class MarkStep extends DiffJJoulesStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkStep.class);

    protected String getReportPathname() {
        return "mark";
    }

    @Override
    protected void _run(Configuration configuration) {
        this.configuration = configuration;
        final Datas dataV1 = this.configuration.getDataV1();
        final Datas dataV2 = this.configuration.getDataV2();
        final Deltas deltas = this.configuration.getDeltas();
        this.configuration.getMarkStrategyEnum().getStrategy().applyStrategy(
                this.configuration,
                dataV1,
                dataV2,
                deltas
        );
    }

}
