package fr.davidson.diff.jjoules.energy;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.powerapi.jjoules.EnergySample;
import org.powerapi.jjoules.NoSuchDomainException;
import org.powerapi.jjoules.rapl.RaplDevice;
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

    private EnergySample energySample;

    private Configuration configuration;

    public EnergyMonitor(Configuration configuration) {
        this.configuration = configuration;
    }

    public void startMonitoring() {
        try {
            this.energySample = RaplDevice.RAPL.recordEnergy();
        } catch (NoSuchDomainException e) {
            e.printStackTrace();
            LOGGER.error("Something went wrong when starting energy monitoring.");
            LOGGER.error("Please, check the permissions on RAPL files.");
        }
    }

    public void stopMonitoring(String reportPathName) {
        if (this.energySample != null) {
            final Map<String, Long> report = this.energySample.stop();
            JSONUtils.write(this.configuration.output + "/" + reportPathName + ".json", report);
            this.configuration.addReport(reportPathName, report);
            this.energySample = null;
        }
    }

}
