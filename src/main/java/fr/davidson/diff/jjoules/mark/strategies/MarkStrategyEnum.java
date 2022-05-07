package fr.davidson.diff.jjoules.mark.strategies;

import fr.davidson.diff.jjoules.mark.strategies.code_coverage.CodeCoverageMarkStrategy;
import fr.davidson.diff.jjoules.mark.strategies.diff_coverage.DiffCoverageMarkStrategy;
import fr.davidson.diff.jjoules.mark.strategies.original.OriginalStrategy;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 27/04/2022
 */
public enum MarkStrategyEnum {
    STRICT() {
        @Override
        public MarkStrategy getStrategy() {
            return new StrictMarkStrategy();
        }
    },
    CODE_COVERAGE() {
        @Override
        public MarkStrategy getStrategy() {
            return new CodeCoverageMarkStrategy();
        }
    },
    DIFF_COVERAGE() {
        @Override
        public MarkStrategy getStrategy() {
            return new DiffCoverageMarkStrategy();
        }
    },
    ORIGINAL() {
        @Override
        public MarkStrategy getStrategy() {
            return new OriginalStrategy();
        }
    };
    public abstract MarkStrategy getStrategy();
}
