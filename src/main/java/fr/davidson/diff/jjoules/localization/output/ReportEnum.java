package fr.davidson.diff.jjoules.localization.output;

public enum ReportEnum {
    JSON {
        @Override
        public Report getReport(String outputPath) {
            return new JSONReport(outputPath);
        }
    };
    public abstract Report getReport(final String outputPath);

    public static Report fromReportEnumValue(String reportEnumValue, String outputPath) {
        return ReportEnum.valueOf(reportEnumValue).getReport(outputPath);
    }
}
