package fr.davidson.diff.jjoules;

import eu.stamp_project.diff_test_selection.diff.DiffComputer;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.computation.ExecsLines;
import fr.davidson.diff.jjoules.report.ReportEnum;
import fr.davidson.diff.jjoules.util.CSVReader;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 25/08/2021
 */
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private static final String SRC_FOLDER = "src";

    public final String pathToFirstVersion;

    public final String pathToSecondVersion;

    public final String pathToTestListAsCSV;

    public final boolean junit4;

    public final int iterations;

    public final String output;

    public final String pathToJSONDelta;

    public final String pathToJSONDataV1;

    public final String pathToJSONDataV2;

    public final String diff;

    public final String pathToJSONDeltaOmega;

    public final String pathToRepositoryV1;

    public final String pathToRepositoryV2;

    public final String pathToJSONConsideredTestMethodNames;

    public final String pathToExecLinesAdditions;

    public final String pathToExecLinesDeletions;

    public final String pathToJSONSuspiciousV1;

    public final String pathToJSONSuspiciousV2;

    public final String pathToReport;

    public final boolean shouldSuspect;

    public final String classpathPathV1;

    public final String classpathPathV2;

    private String[] classpathV1;

    private String[] classpathV2;

    private String classpathV1AsString;

    private String classpathV2AsString;

    private Map<String, List<String>> testsList;

    private Datas dataV1;

    private Datas dataV2;

    private Deltas deltas;

    private Map<String, List<String>> consideredTestsNames;

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
                         String pathToTestListAsCSV,
                         String classpathPathV1,
                         String classpathPathV2,
                         String[] classpathV1,
                         String[] classpathV2,
                         boolean junit4,
                         int iterations,
                         String output,
                         String pathToJSONDelta,
                         String pathToJSONDataV1,
                         String pathToJSONDataV2,
                         String pathToDiff,
                         String pathToJSONDeltaOmega,
                         String pathToRepositoryV1,
                         String pathToRepositoryV2,
                         String pathToJSONConsideredTestMethodNames,
                         String pathToExecLinesAdditions,
                         String pathToExecLinesDeletions,
                         String pathToJSONSuspiciousV1,
                         String pathToJSONSuspiciousV2,
                         String pathToReport,
                         boolean shouldSuspect,
                         ReportEnum reportEnum) {
        this.classpathPathV1 = classpathPathV1;
        this.classpathPathV2 = classpathPathV2;
        this.shouldSuspect = shouldSuspect;
        this.reportEnum = reportEnum;
        this.pathToReport = pathToReport;
        this.ownConsumptionReports = new LinkedHashMap<>();
        this.pathToFirstVersion = pathToFirstVersion;
        this.pathToSecondVersion = pathToSecondVersion;
        this.pathToTestListAsCSV = pathToTestListAsCSV == null || pathToTestListAsCSV.isEmpty() ? "" : new File(pathToTestListAsCSV).isAbsolute() ? pathToTestListAsCSV : this.pathToFirstVersion + "/" + pathToTestListAsCSV;
        this.junit4 = junit4;
        this.classpathV1 = classpathV1;
        this.classpathV1AsString = String.join(":", classpathV1);
        this.classpathV2 = classpathV2;
        this.classpathV2AsString = String.join(":", classpathV2);
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
        this.pathToJSONConsideredTestMethodNames = pathToJSONConsideredTestMethodNames;
        this.pathToExecLinesAdditions = pathToExecLinesAdditions;
        this.pathToExecLinesDeletions = pathToExecLinesDeletions;
        this.pathToJSONSuspiciousV1 = pathToJSONSuspiciousV1;
        this.pathToJSONSuspiciousV2 = pathToJSONSuspiciousV2;
        this.pathToJSONDelta = pathToJSONDelta;
        this.pathToJSONDataV1 = pathToJSONDataV1;
        this.pathToJSONDataV2 = pathToJSONDataV2;
        if (pathToDiff == null || pathToDiff.isEmpty()) {
            LOGGER.warn("No path to diff file has been specified.");
            LOGGER.warn("I'll compute a diff file using the UNIX diff command");
            LOGGER.warn("You may encounter troubles.");
            LOGGER.warn("If so, please specify a path to a correct diff file");
            LOGGER.warn("or implement a new way to compute a diff file.");
            this.diff = new DiffComputer()
                    .computeDiffWithDiffCommand(
                            new File(pathToFirstVersion + "/" + SRC_FOLDER),
                            new File(pathToSecondVersion + "/" + SRC_FOLDER)
                    );
        } else {
            this.diff = Utils.readFile(pathToDiff);
        }
        this.pathToJSONDeltaOmega = pathToJSONDeltaOmega;
    }

    public void setTestsList(Map<String, List<String>> testsList) {
        this.testsList = testsList;
    }

    public Map<String, List<String>> getTestsList() {
        if (this.testsList == null) {
            this.testsList = CSVReader.readFile(this.pathToTestListAsCSV);
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
            this.dataV1 = JSONUtils.read(this.pathToJSONDataV1, Datas.class);
        }
        return dataV1;
    }

    public void setDataV1(Datas dataV1) {
        this.dataV1 = dataV1;
    }

    public Datas getDataV2() {
        if (this.dataV1 == null) {
            this.dataV1 = JSONUtils.read(this.pathToJSONDataV2, Datas.class);
        }
        return dataV2;
    }

    public void setDataV2(Datas dataV2) {
        this.dataV2 = dataV2;
    }

    public Deltas getDeltas() {
        if (this.deltas == null) {
            this.deltas = JSONUtils.read(this.pathToJSONDelta, Deltas.class);
        }
        return deltas;
    }

    public void setDeltas(Deltas deltas) {
        this.deltas = deltas;
    }

    public Map<String, List<String>> getConsideredTestsNames() {
        if (this.consideredTestsNames == null) {
            this.consideredTestsNames = JSONUtils.read(this.pathToJSONConsideredTestMethodNames, Map.class);
        }
        return consideredTestsNames;
    }

    public void setConsideredTestsNames(Map<String, List<String>> consideredTestsNames) {
        this.consideredTestsNames = consideredTestsNames;
    }

    public ExecsLines getExecLinesAdditions() {
        if (this.execLinesAdditions == null) {
            this.execLinesAdditions = JSONUtils.read(this.pathToExecLinesAdditions, ExecsLines .class);
        }
        return execLinesAdditions;
    }

    public void setExecLinesAdditions(ExecsLines execLinesAdditions) {
        this.execLinesAdditions = execLinesAdditions;
    }

    public ExecsLines getExecLinesDeletions() {
        if (this.execLinesDeletions == null) {
            JSONUtils.read(this.pathToExecLinesDeletions, ExecsLines.class);
        }
        return execLinesDeletions;
    }

    public void setExecLinesDeletions(ExecsLines execLinesDeletions) {
        this.execLinesDeletions = execLinesDeletions;
    }

    public Data getDeltaOmega() {
        if (this.deltaOmega == null) {
            this.deltaOmega = JSONUtils.read(this.pathToJSONDeltaOmega, Data.class);
        }
        return deltaOmega;
    }

    public void setDeltaOmega(Data deltaOmega) {
        this.deltaOmega = deltaOmega;
    }

    public Map<String, Double> getScorePerLineV1() {
        if (this.scorePerLineV1 == null) {
            this.scorePerLineV1 = JSONUtils.read(this.pathToJSONSuspiciousV1, Map.class);
        }
        return scorePerLineV1;
    }

    public void setScorePerLineV1(Map<String, Double> scorePerLineV1) {
        this.scorePerLineV1 = scorePerLineV1;
    }

    public Map<String, Double> getScorePerLineV2() {
        if (this.scorePerLineV2 == null) {
            this.scorePerLineV2 = JSONUtils.read(this.pathToJSONSuspiciousV2, Map.class);
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
                ", pathToTestListAsCSV='" + pathToTestListAsCSV + '\'' +
                ", junit4=" + junit4 +
                ", iterations=" + iterations +
                ", output='" + output + '\'' +
                ", pathToJSONDelta='" + pathToJSONDelta + '\'' +
                ", pathToJSONDataV1='" + pathToJSONDataV1 + '\'' +
                ", pathToJSONDataV2='" + pathToJSONDataV2 + '\'' +
                ", diff='" + diff + '\'' +
                ", pathToJSONDeltaOmega='" + pathToJSONDeltaOmega + '\'' +
                ", pathToRepositoryV1='" + pathToRepositoryV1 + '\'' +
                ", pathToRepositoryV2='" + pathToRepositoryV2 + '\'' +
                ", pathToJSONConsideredTestMethodNames='" + pathToJSONConsideredTestMethodNames + '\'' +
                ", pathToExecLinesAdditions='" + pathToExecLinesAdditions + '\'' +
                ", pathToExecLinesDeletions='" + pathToExecLinesDeletions + '\'' +
                ", pathToJSONSuspiciousV1='" + pathToJSONSuspiciousV1 + '\'' +
                ", pathToJSONSuspiciousV2='" + pathToJSONSuspiciousV2 + '\'' +
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
