package fr.davidson.diff.jjoules.mark.filters;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 04/05/2022
 */
public enum TestFilterEnum {
    ALL() {
        public TestFilter get() {
            return new AllTestFilter();
        }
    },
    EMPTY_INTERSECTION() {
        @Override
        public TestFilter get() {
            return new EmptyIntersectionTestFilter();
        }
    },
    STUDENTS_T_TEST() {
        @Override
        public TestFilter get() {
            return new StudentsTTestFilter();
        }
    };
    public abstract TestFilter get();

}
