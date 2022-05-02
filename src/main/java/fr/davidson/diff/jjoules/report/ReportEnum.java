package fr.davidson.diff.jjoules.report;

import fr.davidson.diff.jjoules.report.markdown.MarkdownReport;
import fr.davidson.diff.jjoules.report.textual.TextualReport;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 17/09/2021
 */
public enum ReportEnum {
    TEXTUAL() {
        @Override
        public Report get() {
            return new TextualReport();
        }
    },
    MARKDOWN() {
        @Override
        public Report get() {
            return new MarkdownReport();
        }
    },
    NONE() {
        @Override
        public Report get() {
            return new NoneReport();
        }
    };
    public abstract Report get();
}
