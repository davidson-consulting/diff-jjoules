package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.delta.DeltaStep;
import fr.davidson.diff.jjoules.energy.EnergyMonitor;
import fr.davidson.diff.jjoules.failer.FailerStep;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationStep;
import fr.davidson.diff.jjoules.mark.MarkStep;
import fr.davidson.diff.jjoules.selection.SelectionStep;
import fr.davidson.diff.jjoules.suspect.SuspectStep;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/10/2021
 */
public class DiffJJoulesStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffJJoulesStep.class);

    protected Configuration configuration;

    private EnergyMonitor energyMonitor;

    public void run(Configuration configuration) {
        this.configuration = configuration;
        if (this.configuration.isMeasureEnergyConsumption()) {
            this.energyMonitor = new EnergyMonitor(this.configuration);
            this.energyMonitor.startMonitoring();
        }
        _run(configuration);
        if (this.configuration.isMeasureEnergyConsumption()) {
            this.energyMonitor.stopMonitoring(this.getReportPathname());
        }
    }

    protected String getReportPathname() {
        return "diff_jjoules";
    }

    protected void _run(Configuration configuration) {
        LOGGER.info("Run DiffJJoules - {}", configuration.toString());
        this.resetAndCleanBothVersion();
        this.testSelection();
        this.testInstrumentation();
        this.deltaComputation();
        if (this.configuration.isShouldMark()) {
            this.commitMarking();
            if (this.configuration.isShouldSuspect()) {
                this.testFailingInstrumentation();
                this.testSuspicious();
            }
        }
    }

    private void runDiffJJoulesStep(DiffJJoulesStep step, String messageInCaseOfFailure) {
        try {
            step.run(this.configuration);
        } catch (Exception e) {
            this.end(messageInCaseOfFailure, e);
        }
    }


    private void testSelection() {
        this.runDiffJJoulesStep(new SelectionStep(), "Something went wrong during test selection.");
        if (this.configuration.getTestsList().isEmpty()) {
            this.end("No test could be selected");
        }
        this.cleanAndCompile();
    }

    private void testInstrumentation() {
        this.runDiffJJoulesStep(new InstrumentationStep(), "Something went wrong during test instrumentation.");
        this.cleanCompileAndBuildClasspath();
    }

    private void deltaComputation() {
        this.runDiffJJoulesStep(new DeltaStep(), "Something went wrong during delta.");
        this.resetAndCleanBothVersion();
    }

    private void commitMarking() {
        this.runDiffJJoulesStep(new MarkStep(), "Something went wrong during marking.");
        if (this.configuration.getConsideredTestsNames().isEmpty()) {
            this.end("The energy consumption are too unstable, no method could be considered.");
        }
        cleanAndCompile();
    }

    private void testFailingInstrumentation() {
        this.runDiffJJoulesStep(new FailerStep(), "Something went wrong during failing instrumentation.");
        cleanAndCompile();
    }

    private void testSuspicious() {
        this.runDiffJJoulesStep(new SuspectStep(), "Something went wrong during suspect");
    }

    public void report() {
        this.configuration.getReportEnum().get().run(configuration);
    }

    private void resetAndCleanBothVersion() {
        Utils.gitResetHard(this.configuration.getPathToRepositoryV1());
        Utils.gitResetHard(this.configuration.getPathToRepositoryV2());
        cleanCompileAndBuildClasspath();
    }

    private void cleanCompileAndBuildClasspath() {
        this.cleanAndCompile();
        this.configuration.setClasspathV1(
                this.configuration.getWrapper()
                        .buildClasspath(this.configuration.getPathToFirstVersion()).split(Constants.PATH_SEPARATOR)
        );
        this.configuration.setClasspathV2(
                this.configuration.getWrapper()
                        .buildClasspath(this.configuration.getPathToSecondVersion()).split(Constants.PATH_SEPARATOR)
        );
    }

    private void cleanAndCompile() {
        this.configuration.getWrapper().cleanAndCompile(this.configuration.getPathToFirstVersion());
        this.configuration.getWrapper().cleanAndCompile(this.configuration.getPathToSecondVersion());
    }

    private void end(String reason) {
        this.end(reason, null);
    }

    private void end(String reason, Exception exception) {
        this.energyMonitor.stopMonitoring(this.getReportPathname());
        try (final FileWriter writer = new FileWriter(
                this.configuration.getOutput() + Constants.FILE_SEPARATOR + "end.txt", false)) {
            writer.write(reason + Constants.NEW_LINE);
            if (exception != null) {
                for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                    writer.write(stackTraceElement.toString() + Constants.NEW_LINE);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(1);
    }

}
