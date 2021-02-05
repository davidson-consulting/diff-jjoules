package fr.davidson.diff.jjoules.class_instrumentation.sorter;


public enum SorterEnum {

    SORTER {
        @Override
        public TestMethodsSorter get() {
            return new Sorter();
        }
    };
    public abstract TestMethodsSorter get();

    public static TestMethodsSorter fromSelectorEnumValue(String enumValue) {
        return SorterEnum.valueOf(enumValue).get();
    }

}
