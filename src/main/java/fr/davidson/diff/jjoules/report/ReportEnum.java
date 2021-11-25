package fr.davidson.diff.jjoules.report;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.DiffJJoulesStep;
import fr.davidson.diff.jjoules.report.markdown.MarkdownStep;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 17/09/2021
 */
public enum ReportEnum {
    TXT() {
        @Override
        public DiffJJoulesStep get() {
            return new DiffJJoulesStep() {
                @Override
                public void run(Configuration configuration) {
                    // TODO to be implemented
                }
            };
        }
    },
    MARKDOWN() {
        @Override
        public DiffJJoulesStep get() {
            return new MarkdownStep();
        }
    },
    NONE() {
        @Override
        public DiffJJoulesStep get() {
            return new DiffJJoulesStep() {
                @Override
                public void run(Configuration configuration) {
                    // Nothing to do
                }
            };
        }
    };
    public abstract DiffJJoulesStep get();
}
