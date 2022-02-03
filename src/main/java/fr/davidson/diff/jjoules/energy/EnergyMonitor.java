package fr.davidson.diff.jjoules.energy;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.tlpc.sensor.TLPCSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class EnergyMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnergyMonitor.class);

    private Configuration configuration;

    private TLPCSensor sensor;

    public EnergyMonitor(Configuration configuration) {
        this.configuration = configuration;
        this.sensor = new TLPCSensor();
    }

    public void startMonitoring(String identifier) {
        this.sensor.start(identifier);
    }

    public void stopMonitoring(String identifier) {
        this.sensor.stop(identifier);
        final String reportPathname = this.configuration.getOutput() + Constants.FILE_SEPARATOR + identifier + ".json";
        this.sensor.report(reportPathname);
        final Map<String, Long> report = JSONUtils.read(reportPathname, Map.class);
        this.configuration.addReport(identifier, report);
    }

}
