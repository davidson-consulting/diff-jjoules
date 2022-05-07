package fr.davidson.diff.jjoules;

import eu.stamp_project.diff_test_selection.diff.DiffComputer;
import fr.davidson.diff.jjoules.delta.DeltaStep;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.filters.TestFilter;
import fr.davidson.diff.jjoules.mark.filters.TestFilterEnum;
import fr.davidson.diff.jjoules.mark.strategies.MarkStrategyEnum;
import fr.davidson.diff.jjoules.mark.strategies.original.OriginalStrategy;
import fr.davidson.diff.jjoules.mark.strategies.original.computation.ExecsLines;
import fr.davidson.diff.jjoules.report.ReportEnum;
import fr.davidson.diff.jjoules.selection.SelectionStep;
import fr.davidson.diff.jjoules.suspect.SuspectStep;
import fr.davidson.diff.jjoules.util.*;
import fr.davidson.diff.jjoules.util.wrapper.Wrapper;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine;

import java.io.File;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/08/2021
 */
@CommandLine.Command(name = "fr.davidson.diff.jjoules.Main", mixinStandardHelpOptions = true, version = "Configuration 0.0.1")
public class Configuration {

    private static final String SRC_FOLDER = "src";

    @CommandLine.Option(names = {"-f", "--path-first-version"}, description = "Path to the first version of the program.", required = true)
    private String pathToFirstVersion;

    @CommandLine.Option(names = {"-s", "--path-second-version"}, description = "Path to the second version of the program.", required = true)
    private String pathToSecondVersion;

    @CommandLine.Option(names = {"--junit4"}, description = "Enable junit4 tests", defaultValue = "false")
    private boolean junit4;

    @CommandLine.Option(names = {"-i", "--iteration"}, description = "Number of test executions to measure their energy consumption.", defaultValue = "10")
    private int iterations;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Path to the output folder.", defaultValue = "diff-jjoules")
    private String output;

    @CommandLine.Option(names = {"--path-repository-v1"}, description = "Path to the first version of the program that contains .git (this is used for multi-module projects)", defaultValue = "")
    private String pathToRepositoryV1;

    @CommandLine.Option(names = {"--path-repository-v2"}, description = "Path to the second version of the program that contains .git (this is used for multi-module projects)", defaultValue = "")
    private String pathToRepositoryV2;

    @CommandLine.Option(names = {"--mark"}, description = "Enable mark step.", defaultValue = "false")
    private boolean shouldMark;

    @CommandLine.Option(
            names = "--mark-strategy",
            defaultValue = "ORIGINAL",
            description = "Specify the mark strategy to be used." +
                    "Valid values: ${COMPLETION-CANDIDATES}" +
                    " Default value: ${DEFAULT-VALUE}"
    )
    private MarkStrategyEnum markStrategyEnum;

    @CommandLine.Option(names = {"--suspect"}, description = "Enable suspect step.", defaultValue = "false")
    private boolean shouldSuspect;

    @CommandLine.Option(names = {"--path-report-file"}, description = "Path to report file to produce.", defaultValue = "diff-jjoules/diff-jjoules.report")
    private String pathToReport;

    @CommandLine.Option(
            names = "--wrapper",
            defaultValue = "MAVEN",
            description = "Specify the wrapper to be used." +
                    "Valid values: ${COMPLETION-CANDIDATES}" +
                    " Default value: ${DEFAULT-VALUE}"
    )
    private WrapperEnum wrapperEnum;

    @CommandLine.Option(names = {"--report"}, description = "Enable report step (the mark step must be enabled).", defaultValue = "false")
    private boolean shouldReport;

    @CommandLine.Option(
            names = "--report-type",
            defaultValue = "TEXTUAL",
            description = "Specify the report type to produce." +
                    "Valid values: ${COMPLETION-CANDIDATES}" +
                    " Default value: ${DEFAULT-VALUE}"
    )
    private ReportEnum reportEnum;

    @CommandLine.Option(
            names = "--test-filter-type",
            defaultValue = "ALL",
            description = "Specify the test filter to use for marking." +
                    "Valid values: ${COMPLETION-CANDIDATES}" +
                    " Default value: ${DEFAULT-VALUE}"
    )
    private TestFilterEnum testFilterEnum;

    @CommandLine.Option(
            names = "--measure",
            defaultValue = "false",
            description = "Enable the energy consumption measurements of Diff-JJoules" +
                    " Default value: ${DEFAULT-VALUE}"
    )
    private boolean measureEnergyConsumption;

    @CommandLine.Option(
            names = "--cohen-s-d",
            defaultValue = "0.8",
            description = "Specify the threshold of the Cohen's D" +
                    " Default value: ${DEFAULT-VALUE}"
    )
    private double cohensD;

    private String diff;

    private String[] classpathV1;

    private String[] classpathV2;

    private String classpathV1AsString;

    private String classpathV2AsString;

    private MethodNamesPerClassNames testsList;

    private Datas dataV1;

    private Datas dataV2;

    private Deltas deltas;

    private MethodNamesPerClassNames consideredTestsNames;

    private ExecsLines execLinesAdditions;

    private ExecsLines execLinesDeletions;

    private Data deltaOmega;

    private Map<String, Double> scorePerLineV1;

    private Map<String, Double> scorePerLineV2;

    private Wrapper wrapper;

    public Configuration() {

    }

    public Configuration(String pathToFirstVersion,
                         String pathToSecondVersion,
                         int iterations,
                         boolean shouldMark) {
        this(
                pathToFirstVersion,
                pathToSecondVersion,
                iterations,
                "diff-jjoules",
                "",
                "",
                "target/report.md",
                true,
                shouldMark,
                shouldMark,
                ReportEnum.NONE,
                TestFilterEnum.ALL,
                WrapperEnum.MAVEN,
                false
        );
    }

    public Configuration(String pathToFirstVersion,
                         String pathToSecondVersion,
                         int iterations,
                         String output,
                         String pathToRepositoryV1,
                         String pathToRepositoryV2,
                         String pathToReport,
                         boolean shouldSuspect,
                         boolean shouldMark,
                         boolean shouldReport,
                         ReportEnum reportEnum,
                         TestFilterEnum testFilterEnum,
                         WrapperEnum wrapperEnum,
                         boolean measureEnergyConsumption
    ) {
        this.testFilterEnum = testFilterEnum;
        this.shouldSuspect = shouldSuspect;
        this.shouldMark = shouldMark;
        this.shouldReport = shouldReport;
        this.reportEnum = reportEnum;
        this.pathToReport = pathToReport;
        this.pathToFirstVersion = Utils.correctPath(pathToFirstVersion);
        this.pathToSecondVersion = Utils.correctPath(pathToSecondVersion);
        this.iterations = iterations;
        this.output = output;
        this.pathToRepositoryV1 = Utils.correctPath(pathToRepositoryV1);
        this.pathToRepositoryV2 = Utils.correctPath(pathToRepositoryV2);
        this.wrapperEnum = wrapperEnum;
        this.markStrategyEnum = MarkStrategyEnum.ORIGINAL;
        this.measureEnergyConsumption = measureEnergyConsumption;
        init();
    }

    public void init() {
        File outputFd = new File(this.output);
        if (!outputFd.isAbsolute()) {
            this.output = this.pathToFirstVersion + Constants.FILE_SEPARATOR + output;
            outputFd = new File(this.output);
        }
        try {
            if (outputFd.exists()) {
                FileUtils.deleteDirectory(outputFd);
            }
            if (!outputFd.mkdir() || !outputFd.exists()) {
                throw new RuntimeException(String.format("Something went wrong when trying to create the folder %s, please check your configuration", outputFd.toString()));
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Something went wrong when trying to delete the folder %s, please check your configuration", outputFd.toString()), e);
        }
        this.diff = new DiffComputer()
                .computeDiffWithDiffCommand(
                        new File(pathToFirstVersion + Constants.FILE_SEPARATOR + SRC_FOLDER),
                        new File(pathToSecondVersion + Constants.FILE_SEPARATOR + SRC_FOLDER)
                );
        if (this.pathToRepositoryV1.isEmpty()) {
            this.pathToRepositoryV1 = this.pathToFirstVersion;
        }
        if (this.pathToRepositoryV2.isEmpty()) {
            this.pathToRepositoryV2 = this.pathToSecondVersion;
        }
        this.wrapper = wrapperEnum.getWrapper();
        this.classpathV1AsString = this.wrapper.buildClasspath(this.pathToFirstVersion);
        this.classpathV2AsString = this.wrapper.buildClasspath(this.pathToSecondVersion);
        this.classpathV1 = this.classpathV1AsString.split(Constants.PATH_SEPARATOR);
        this.classpathV2 = this.classpathV2AsString.split(Constants.PATH_SEPARATOR);
        this.junit4 = !classpathV1AsString.contains("junit-jupiter-engine-5") && (classpathV1AsString.contains("junit-4") || classpathV1AsString.contains("junit-3"));
    }

    public double getCohensD() {
        return cohensD;
    }

    public void setCohensD(double cohensD) {
        this.cohensD = cohensD;
    }

    public String getPathToFirstVersion() {
        return pathToFirstVersion;
    }

    public String getPathToSecondVersion() {
        return pathToSecondVersion;
    }

    public boolean isJunit4() {
        return junit4;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    public String getPathToRepositoryV1() {
        return pathToRepositoryV1;
    }

    public String getPathToRepositoryV2() {
        return pathToRepositoryV2;
    }

    public boolean isShouldMark() {
        return shouldMark;
    }

    public boolean isShouldReport() {
        return this.shouldReport;
    }

    public boolean isShouldSuspect() {
        return shouldSuspect;
    }

    public String getPathToReport() {
        return pathToReport;
    }

    public void setMarkStrategyEnum(MarkStrategyEnum markStrategyEnum) {
        this.markStrategyEnum = markStrategyEnum;
    }

    public MarkStrategyEnum getMarkStrategyEnum() {
        return this.markStrategyEnum;
    }

    public WrapperEnum getWrapperEnum() {
        return wrapperEnum;
    }

    public void setWrapperEnum(WrapperEnum wrapperEnum) {
        this.wrapperEnum = wrapperEnum;
    }

    public String getDiff() {
        return diff;
    }

    public TestFilterEnum getTestFilterEnum() {
        return testFilterEnum;
    }

    public void setTestFilterEnum(TestFilterEnum testFilterEnum) {
        this.testFilterEnum = testFilterEnum;
    }

    public void setReportEnum(ReportEnum reportEnum) {
        this.reportEnum = reportEnum;
    }

    public ReportEnum getReportEnum() {
        return reportEnum;
    }

    public Wrapper getWrapper() {
        return this.wrapper;
    }

    public void setShouldSuspect(boolean shouldSuspect) {
        this.shouldSuspect = shouldSuspect;
    }

    public void setMeasureEnergyConsumption(boolean measureEnergyConsumption) {
        this.measureEnergyConsumption = measureEnergyConsumption;
    }

    public boolean isMeasureEnergyConsumption() {
        return measureEnergyConsumption;
    }

    public void setTestsList(MethodNamesPerClassNames testsList) {
        this.testsList = testsList;
    }

    public MethodNamesPerClassNames getTestsList() {
        if (this.testsList == null) {
            this.testsList = JSONUtils.read(
                    Constants.joinFiles(this.output, SelectionStep.JSON_SELECTED_TEST_PATHNAME),
                    MethodNamesPerClassNames.class
            );
        }
        return testsList;
    }

    public void setClasspathV1(String[] classpathV1) {
        this.classpathV1 = classpathV1;
        this.classpathV1AsString = String.join(Constants.PATH_SEPARATOR, classpathV1);
    }

    public void setClasspathV2(String[] classpathV2) {
        this.classpathV2 = classpathV2;
        this.classpathV2AsString = String.join(Constants.PATH_SEPARATOR, classpathV2);
    }

    public String[] getClasspathV1() {
        return classpathV1;
    }

    public String[] getClasspathV2() {
        return classpathV2;
    }

    public String getClasspathV1AsString() {
        return classpathV1AsString;
    }

    public String getClasspathV2AsString() {
        return classpathV2AsString;
    }

    public Datas getDataV1() {
        if (this.dataV1 == null) {
            try {
                this.dataV1 = JSONUtils.read(DeltaStep.PATH_TO_JSON_DATA_V1, Datas.class);
            } catch (Exception e) {
                return new Datas();
            }
        }
        return dataV1;
    }

    public void setDataV1(Datas dataV1) {
        this.dataV1 = dataV1;
    }

    public Datas getDataV2() {
        if (this.dataV2 == null) {
            try {
                this.dataV2 = JSONUtils.read(DeltaStep.PATH_TO_JSON_DATA_V2, Datas.class);
            } catch (Exception e) {
                return new Datas();
            }
        }
        return dataV2;
    }

    public void setDataV2(Datas dataV2) {
        this.dataV2 = dataV2;
    }

    public Deltas getDeltas() {
        if (this.deltas == null) {
            try {
                this.deltas = JSONUtils.read(DeltaStep.PATH_TO_JSON_DELTA, Deltas.class);
            } catch (Exception e) {
                return new Deltas();
            }
        }
        return deltas;
    }

    public void setDeltas(Deltas deltas) {
        this.deltas = deltas;
    }

    public MethodNamesPerClassNames getConsideredTestsNames() {
        if (this.consideredTestsNames == null) {
            try {
                this.consideredTestsNames = JSONUtils.read(TestFilter.PATH_TO_JSON_CONSIDERED_TEST_METHOD_NAME, MethodNamesPerClassNames.class);
            } catch (Exception e) {
                e.printStackTrace();
                return new MethodNamesPerClassNames();
            }
        }
        return consideredTestsNames;
    }

    public void setConsideredTestsNames(MethodNamesPerClassNames consideredTestsNames) {
        this.consideredTestsNames = consideredTestsNames;
    }

    public ExecsLines getExecLinesAdditions() {
        if (this.execLinesAdditions == null) {
            this.execLinesAdditions = JSONUtils.read(OriginalStrategy.PATH_TO_JSON_EXEC_ADDITIONS, ExecsLines.class);
        }
        return execLinesAdditions;
    }

    public void setExecLinesAdditions(ExecsLines execLinesAdditions) {
        this.execLinesAdditions = execLinesAdditions;
    }

    public ExecsLines getExecLinesDeletions() {
        if (this.execLinesDeletions == null) {
            JSONUtils.read(OriginalStrategy.PATH_TO_JSON_EXEC_DELETION, ExecsLines.class);
        }
        return execLinesDeletions;
    }

    public void setExecLinesDeletions(ExecsLines execLinesDeletions) {
        this.execLinesDeletions = execLinesDeletions;
    }

    public Data getDeltaOmega() {
        if (this.deltaOmega == null) {
            this.deltaOmega = JSONUtils.read(OriginalStrategy.PATH_TO_JSON_DELTA_OMEGA, Data.class);
        }
        return deltaOmega;
    }

    public void setDeltaOmega(Data deltaOmega) {
        this.deltaOmega = deltaOmega;
    }

    public Map<String, Double> getScorePerLineV1() {
        if (this.scorePerLineV1 == null) {
            try {
                this.scorePerLineV1 = JSONUtils.read(SuspectStep.PATH_TO_JSON_SUSPICIOUS_V1, Map.class);
            } catch (Exception e) {
                return Collections.emptyMap();
            }
        }
        return scorePerLineV1;
    }

    public void setScorePerLineV1(Map<String, Double> scorePerLineV1) {
        this.scorePerLineV1 = scorePerLineV1;
    }

    public Map<String, Double> getScorePerLineV2() {
        if (this.scorePerLineV2 == null) {
            try {
                this.scorePerLineV2 = JSONUtils.read(SuspectStep.PATH_TO_JSON_SUSPICIOUS_V2, Map.class);
            } catch (Exception e) {
                return Collections.emptyMap();
            }
        }
        return scorePerLineV2;
    }

    public void setScorePerLineV2(Map<String, Double> scorePerLineV2) {
        this.scorePerLineV2 = scorePerLineV2;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "pathToFirstVersion='" + pathToFirstVersion + '\'' + Constants.NEW_LINE +
                ", pathToSecondVersion='" + pathToSecondVersion + '\'' + Constants.NEW_LINE +
                ", junit4=" + junit4 + Constants.NEW_LINE +
                ", iterations=" + iterations + Constants.NEW_LINE +
                ", output='" + output + '\'' + Constants.NEW_LINE +
                ", pathToRepositoryV1='" + pathToRepositoryV1 + '\'' + Constants.NEW_LINE +
                ", pathToRepositoryV2='" + pathToRepositoryV2 + '\'' + Constants.NEW_LINE +
                ", shouldMark=" + shouldMark + Constants.NEW_LINE +
                ", shouldSuspect=" + shouldSuspect + Constants.NEW_LINE +
                ", pathToReport='" + pathToReport + '\'' + Constants.NEW_LINE +
                ", wrapperEnum=" + wrapperEnum + Constants.NEW_LINE +
                ", reportEnum=" + reportEnum + Constants.NEW_LINE +
                ", measureEnergyConsumption=" + measureEnergyConsumption + Constants.NEW_LINE +
                ", diff='" + diff + '\'' + Constants.NEW_LINE +
                ", classpathV1=" + Arrays.toString(classpathV1) + Constants.NEW_LINE +
                ", classpathV2=" + Arrays.toString(classpathV2) + Constants.NEW_LINE +
                ", classpathV1AsString='" + classpathV1AsString + '\'' + Constants.NEW_LINE +
                ", classpathV2AsString='" + classpathV2AsString + '\'' + Constants.NEW_LINE +
                ", testsList=" + testsList + Constants.NEW_LINE +
                ", dataV1=" + dataV1 + Constants.NEW_LINE +
                ", dataV2=" + dataV2 + Constants.NEW_LINE +
                ", deltas=" + deltas + Constants.NEW_LINE +
                ", consideredTestsNames=" + consideredTestsNames + Constants.NEW_LINE +
                ", execLinesAdditions=" + execLinesAdditions + Constants.NEW_LINE +
                ", execLinesDeletions=" + execLinesDeletions + Constants.NEW_LINE +
                ", deltaOmega=" + deltaOmega + Constants.NEW_LINE +
                ", scorePerLineV1=" + scorePerLineV1 + Constants.NEW_LINE +
                ", scorePerLineV2=" + scorePerLineV2 + Constants.NEW_LINE +
                ", wrapper=" + wrapper + Constants.NEW_LINE +
                '}';
    }
}
