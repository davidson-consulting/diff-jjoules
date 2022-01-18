package fr.davidson.diff.jjoules.energy;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.j.tlpc.sensor.JNIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class EnergyMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnergyMonitor.class);

    private Configuration configuration;

    private JNIClient jniClient;

    private boolean started;

    public EnergyMonitor(Configuration configuration) {
        this.configuration = configuration;
        this.started = false;
        this.jniClient = new JNIClient();
    }

    public void startMonitoring() {
        if (!this.started) {
            try {
                this.jniClient.start();
                this.started = true;
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Something went wrong when starting energy monitoring.");
                LOGGER.error("Please, check the permissions on RAPL files.");
            }
        }
    }

    public void stopMonitoring(String reportPathName) {
        if (this.started) {
            this.jniClient.stop();
            final Map<String, Long> report = JSONUtils.read(new File(".").getAbsolutePath() + Constants.FILE_SEPARATOR + "/report.json", Map.class);
            this.configuration.addReport(reportPathName, report);
            this.started = false;
        }
    }

}
