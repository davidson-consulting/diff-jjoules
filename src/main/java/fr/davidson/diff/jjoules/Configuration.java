package fr.davidson.diff.jjoules;

import eu.stamp_project.diff_test_selection.diff.DiffComputer;
import fr.davidson.diff.jjoules.delta.DeltaStep;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.MarkStep;
import fr.davidson.diff.jjoules.mark.computation.ExecsLines;
import fr.davidson.diff.jjoules.report.ReportEnum;
import fr.davidson.diff.jjoules.selection.SelectionStep;
import fr.davidson.diff.jjoules.suspect.SuspectStep;
import fr.davidson.diff.jjoules.util.CSVFileManager;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.wrapper.Wrapper;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
import picocli.CommandLine;

import java.io.File;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/08/2021
 */
@CommandLine.Command(name = "fr.davidson.diff.jjoules.Main", mixinStandardHelpOptions = true)
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

    @CommandLine.Option(
            names = "--report",
            defaultValue = "TXT",
            description = "Specify the report type to produce." +
                    "Valid values: ${COMPLETION-CANDIDATES}" +
                    " Default value: ${DEFAULT-VALUE}"
    )
    private ReportEnum reportEnum;

    private String diff;

    private String[] classpathV1;

    private String[] classpathV2;

    private String classpathV1AsString;

    private String classpathV2AsString;

    private Map<String, Set<String>> testsList;

    private Datas dataV1;

    private Datas dataV2;

    private Deltas deltas;

    private Map<String, Set<String>> consideredTestsNames;

    private ExecsLines execLinesAdditions;

    private ExecsLines execLinesDeletions;

    private Data deltaOmega;

    private Map<String, Double> scorePerLineV1;

    private Map<String, Double> scorePerLineV2;

    private Map<String, Map<String, Long>> ownConsumptionReports;

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
                ".github/workflows/template.md",
                true,
                shouldMark,
                ReportEnum.NONE,
                WrapperEnum.MAVEN
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
                         ReportEnum reportEnum,
                         WrapperEnum wrapperEnum
    ) {
        this.shouldSuspect = shouldSuspect;
        this.shouldMark = shouldMark;
        this.reportEnum = reportEnum;
        this.pathToReport = pathToReport;
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.iterations = iterations;
        this.output = output;
        this.pathToRepositoryV1 = pathToRepositoryV1;
        this.pathToRepositoryV2 = pathToRepositoryV2;
        this.wrapperEnum = wrapperEnum;
        init();
    }

    public void init() {
        this.ownConsumptionReports = new LinkedHashMap<>();
        final File outputFd = new File(this.output);
        if (!outputFd.isAbsolute()) {
            this.output = this.pathToFirstVersion + Constants.FILE_SEPARATOR + output;
        }
        if (outputFd.exists()) {
            outputFd.delete();
        }
        outputFd.mkdir();
        this.diff = new DiffComputer()
                .computeDiffWithDiffCommand(
                        new File(pathToFirstVersion + Constants.FILE_SEPARATOR + SRC_FOLDER),
                        new File(pathToSecondVersion + Constants.FILE_SEPARATOR + SRC_FOLDER)
                );
        this.wrapper = wrapperEnum.getWrapper();
        this.classpathV1AsString = this.wrapper.buildClasspath(this.pathToFirstVersion);
        this.classpathV2AsString = this.wrapper.buildClasspath(this.pathToSecondVersion);
        this.classpathV1 = this.classpathV1AsString.split(Constants.PATH_SEPARATOR);
        this.classpathV2 = this.classpathV2AsString.split(Constants.PATH_SEPARATOR);
        this.junit4 = !classpathV1AsString.contains("junit-jupiter-engine-5") && (classpathV1AsString.contains("junit-4") || classpathV1AsString.contains("junit-3"));
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

    public boolean isShouldSuspect() {
        return shouldSuspect;
    }

    public String getPathToReport() {
        return pathToReport;
    }

    public WrapperEnum getWrapperEnum() {
        return wrapperEnum;
    }

    public String getDiff() {
        return diff;
    }

    public ReportEnum getReportEnum() {
        return reportEnum;
    }

    public Wrapper getWrapper() {
        return this.wrapper;
    }

    public void setTestsList(Map<String, Set<String>> testsList) {
        this.testsList = testsList;
    }

    public Map<String, Set<String>> getTestsList() {
        if (this.testsList == null) {
            this.testsList = CSVFileManager.readFile(this.pathToFirstVersion + Constants.FILE_SEPARATOR + SelectionStep.PATH_TO_CSV_TESTS_EXEC_CHANGES);
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

    public Map<String, Set<String>> getConsideredTestsNames() {
        if (this.consideredTestsNames == null) {
            try {
                this.consideredTestsNames = JSONUtils.read(DeltaStep.PATH_TO_JSON_CONSIDERED_TEST_METHOD_NAME, Map.class);
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.emptyMap();
            }
        }
        return consideredTestsNames;
    }

    public void setConsideredTestsNames(Map<String, Set<String>> consideredTestsNames) {
        this.consideredTestsNames = consideredTestsNames;
    }

    public ExecsLines getExecLinesAdditions() {
        if (this.execLinesAdditions == null) {
            this.execLinesAdditions = JSONUtils.read(MarkStep.PATH_TO_JSON_EXEC_ADDITIONS, ExecsLines.class);
        }
        return execLinesAdditions;
    }

    public void setExecLinesAdditions(ExecsLines execLinesAdditions) {
        this.execLinesAdditions = execLinesAdditions;
    }

    public ExecsLines getExecLinesDeletions() {
        if (this.execLinesDeletions == null) {
            JSONUtils.read(MarkStep.PATH_TO_JSON_EXEC_DELETION, ExecsLines.class);
        }
        return execLinesDeletions;
    }

    public void setExecLinesDeletions(ExecsLines execLinesDeletions) {
        this.execLinesDeletions = execLinesDeletions;
    }

    public Data getDeltaOmega() {
        if (this.deltaOmega == null) {
            this.deltaOmega = JSONUtils.read(MarkStep.PATH_TO_JSON_DELTA_OMEGA, Data.class);
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
                "pathToFirstVersion='" + pathToFirstVersion + '\'' +
                ", pathToSecondVersion='" + pathToSecondVersion + '\'' +
                ", junit4=" + junit4 +
                ", iterations=" + iterations +
                ", output='" + output + '\'' +
                ", diff='" + diff + '\'' +
                ", pathToRepositoryV1='" + pathToRepositoryV1 + '\'' +
                ", pathToRepositoryV2='" + pathToRepositoryV2 + '\'' +
                ", pathToReport='" + pathToReport + '\'' +
                ", classpathV1=" + Arrays.toString(classpathV1) +
                ", classpathV2=" + Arrays.toString(classpathV2) +
                ", classpathV1AsString='" + classpathV1AsString + '\'' +
                ", classpathV2AsString='" + classpathV2AsString + '\'' +
                ", testsList=" + testsList +
                ", dataV1=" + dataV1 +
                ", dataV2=" + dataV2 +
                ", deltas=" + deltas +
                ", consideredTestsNames=" + consideredTestsNames +
                ", execLinesAdditions=" + execLinesAdditions +
                ", execLinesDeletions=" + execLinesDeletions +
                ", deltaOmega=" + deltaOmega +
                ", scorePerLineV1=" + scorePerLineV1 +
                ", scorePerLineV2=" + scorePerLineV2 +
                ", ownConsumptionReports=" + ownConsumptionReports +
                ", reportEnum=" + reportEnum +
                '}';
    }

    public Map<String, Map<String, Long>> getOwnConsumptionReports() {
        return this.ownConsumptionReports;
    }

    public void addReport(String reportPathname, Map<String, Long> report) {
        this.ownConsumptionReports.put(reportPathname, report);
    }
}
