package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.delta.DeltaStep;
import fr.davidson.diff.jjoules.energy.EnergyMonitor;
import fr.davidson.diff.jjoules.failer.FailerStep;
import fr.davidson.diff.jjoules.instrumentation.InstrumentationStep;
import fr.davidson.diff.jjoules.mark.MarkStep;
import fr.davidson.diff.jjoules.selection.SelectionStep;
import fr.davidson.diff.jjoules.suspect.SuspectStep;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
        this.energyMonitor = new EnergyMonitor(this.configuration);
        this.energyMonitor.startMonitoring();
        _run(configuration);
        this.energyMonitor.stopMonitoring(this.getReportPathname());
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
        if (this.configuration.shouldMark) {
            this.commitMarking();
            if (this.configuration.shouldSuspect) {
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
        this.gitResetHard(this.configuration.pathToRepositoryV1);
        this.gitResetHard(this.configuration.pathToRepositoryV2);
        cleanCompileAndBuildClasspath();
    }

    private void cleanCompileAndBuildClasspath() {
        this.cleanAndCompile();
        this.configuration.setClasspathV1(this.configuration.getWrapper().buildClasspath(this.configuration.pathToFirstVersion).split(":"));
        this.configuration.setClasspathV2(this.configuration.getWrapper().buildClasspath(this.configuration.pathToSecondVersion).split(":"));
    }

    private void cleanAndCompile() {
        this.configuration.getWrapper().cleanAndCompile(this.configuration.pathToFirstVersion);
        this.configuration.getWrapper().cleanAndCompile(this.configuration.pathToSecondVersion);
    }

    private void gitResetHard(String pathToFolder) {
        try {
            Git.open(new File(pathToFolder))
                    .reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .call();
            // must delete module-info.java
            try (Stream<Path> walk = Files.walk(Paths.get(pathToFolder))) {
                walk.filter(path -> path.endsWith("module-info.java"))
                        .forEach(path -> path.toFile().delete());
            }
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void end(String reason) {
        this.end(reason, null);
    }

    private void end(String reason, Exception exception) {
        this.energyMonitor.stopMonitoring(this.getReportPathname());
        try (final FileWriter writer = new FileWriter(
                this.configuration.output + "/end.txt", false)) {
            writer.write(reason + "\n");
            if (exception != null) {
                for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                    writer.write(stackTraceElement.toString() + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(1);
    }

}
