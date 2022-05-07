package fr.davidson.diff.jjoules.mark;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;


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
        LOGGER.info("Run Mark Step - {} {}", configuration.getTestFilterEnum(), configuration.getMarkStrategyEnum());
        this.configuration = configuration;
        final Datas dataV1 = this.configuration.getDataV1();
        final Datas dataV2 = this.configuration.getDataV2();
        final Deltas deltas = this.configuration.getDeltas();
        final MethodNamesPerClassNames consideredTest = this.configuration.getTestFilterEnum().get().filter(
                this.configuration, dataV1, dataV2, deltas
        );
        final boolean decision = this.configuration.getMarkStrategyEnum().getStrategy().applyStrategy(
                this.configuration,
                dataV1,
                dataV2,
                deltas,
                consideredTest
        );
        final String decisionAsString = decision ? "pass" : "break";
        LOGGER.info("Decision: {}", decisionAsString);
        try (FileWriter writer = new FileWriter(this.configuration.getOutput() +  "/decision")) {
            writer.write(decisionAsString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
