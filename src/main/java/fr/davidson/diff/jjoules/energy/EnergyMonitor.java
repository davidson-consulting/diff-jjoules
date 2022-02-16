package fr.davidson.diff.jjoules.energy;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.tlpc.sensor.TLPCSensor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class EnergyMonitor {

    private Configuration configuration;

    private TLPCSensor sensor;

    public EnergyMonitor(Configuration configuration) {
        this.configuration = configuration;
    }

    public void startMonitoring(String identifier) {
//        this.sensor.start(identifier);
    }

    public void stopMonitoring(String identifier) {
//        this.sensor.stop(identifier);
        final String reportPathname = this.configuration.getOutput() + Constants.FILE_SEPARATOR + identifier + ".json";
//        this.sensor.report(reportPathname);
        final Map<String, Long> report = new HashMap<>();//JSONUtils.read(reportPathname, Map.class);
        this.configuration.addReport(identifier, report);
    }

}
