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
import fr.davidson.diff.jjoules.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/08/2021
 */
public class Configuration {

    public static final String CLASSPATH = "classpath";

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private static final String SRC_FOLDER = "src";

    public final String pathToFirstVersion;

    public final String pathToSecondVersion;

    public final boolean junit4;

    public final int iterations;

    public final String output;

    public final String diff;

    public final String pathToRepositoryV1;

    public final String pathToRepositoryV2;

    public final String pathToReport;

    public final boolean shouldSuspect;

    public final boolean shouldMark;

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

    private ReportEnum reportEnum;

    public ReportEnum getReportEnum() {
        return reportEnum;
    }

    public Configuration(String pathToFirstVersion,
                         String pathToSecondVersion,
                         String classpathV1,
                         String classpathV2,
                         boolean junit4,
                         int iterations,
                         boolean shouldMark) {
        this(
                pathToFirstVersion,
                pathToSecondVersion,
                classpathV1,
                classpathV2,
                junit4,
                iterations,
                "diff-jjoules",
                "",
                "",
                ".github/workflows/template.md",
                true,
                shouldMark,
                ReportEnum.NONE
        );
    }

    public Configuration(String pathToFirstVersion,
                         String pathToSecondVersion,
                         String classpathV1,
                         String classpathV2,
                         boolean junit4,
                         int iterations,
                         String output,
                         String pathToRepositoryV1,
                         String pathToRepositoryV2,
                         String pathToReport,
                         boolean shouldSuspect,
                         boolean shouldMark,
                         ReportEnum reportEnum) {
        this.shouldSuspect = shouldSuspect;
        this.shouldMark = shouldMark;
        this.reportEnum = reportEnum;
        this.pathToReport = pathToReport;
        this.ownConsumptionReports = new LinkedHashMap<>();
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.junit4 = junit4;
        this.classpathV1AsString = classpathV1;
        this.classpathV2AsString = classpathV2;
        this.classpathV1 = this.classpathV1AsString.split(":");
        this.classpathV2 = this.classpathV2AsString.split(":");
        this.iterations = iterations;
        if (new File(output).isAbsolute()) {
            this.output = output;
        } else {
            this.output = this.pathToFirstVersion + "/" + output;
        }
        final File outputDirectory = new File(this.output);
        if (outputDirectory.exists()) {
            outputDirectory.delete();
        }
        outputDirectory.mkdir();
        this.pathToRepositoryV1 = pathToRepositoryV1;
        this.pathToRepositoryV2 = pathToRepositoryV2;
        this.diff = new DiffComputer()
                .computeDiffWithDiffCommand(
                        new File(pathToFirstVersion + "/" + SRC_FOLDER),
                        new File(pathToSecondVersion + "/" + SRC_FOLDER)
                );
    }

    public void setTestsList(Map<String, Set<String>> testsList) {
        this.testsList = testsList;
    }

    public Map<String, Set<String>> getTestsList() {
        if (this.testsList == null) {
            this.testsList = CSVFileManager.readFile(this.pathToFirstVersion + "/" + SelectionStep.PATH_TO_CSV_TESTS_EXEC_CHANGES);
        }
        return testsList;
    }

    public void setClasspathV1(String[] classpathV1) {
        this.classpathV1 = classpathV1;
        this.classpathV1AsString = String.join(":", classpathV1);
    }

    public void setClasspathV2(String[] classpathV2) {
        this.classpathV2 = classpathV2;
        this.classpathV2AsString = String.join(":", classpathV2);
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
