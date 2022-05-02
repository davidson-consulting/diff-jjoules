package fr.davidson.diff.jjoules.report;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 02/05/2022
 */
public class ReportStep extends DiffJJoulesStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentationStep.class);

    protected String getReportPathname() {
        return "report";
    }

    @Override
    protected void _run(Configuration configuration) {
        this.configuration = configuration;
        LOGGER.info("Run Report - {}", configuration.toString());
        this.configuration.getReportEnum().get().report(this.configuration);
    }

}
