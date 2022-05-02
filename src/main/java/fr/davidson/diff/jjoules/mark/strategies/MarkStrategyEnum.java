package fr.davidson.diff.jjoules.mark.strategies;

import fr.davidson.diff.jjoules.mark.strategies.original.OriginalStrategy;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 27/04/2022
 */
public enum MarkStrategyEnum {
    ORIGINAL() {
        @Override
        public MarkStrategy getStrategy() {
            return new OriginalStrategy();
        }
    };
    public abstract MarkStrategy getStrategy();
}
