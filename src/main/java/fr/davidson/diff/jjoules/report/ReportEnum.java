package fr.davidson.diff.jjoules.report;

import fr.davidson.diff.jjoules.DiffJJoulesMojo;
import fr.davidson.diff.jjoules.report.markdown.MarkdownMojo;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 17/09/2021
 */
public enum ReportEnum {
    MARKDOWN() {
        @Override
        public DiffJJoulesMojo get() {
            return new MarkdownMojo();
        }
    },
    NONE() {
        @Override
        public DiffJJoulesMojo get() {
            return new NoneReport();
        }
    };
    public abstract DiffJJoulesMojo get();
}
