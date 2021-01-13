package fr.davidson.diff.jjoules.localization.select;

public enum SelectorEnum {
    LargestImpact {
        @Override
        public Selector getSelector() {
            return new LargestImpactSelector();
        }
    };
    public abstract Selector getSelector();

    public static Selector fromSelectorEnumValue(String reportEnumValue) {
        return SelectorEnum.valueOf(reportEnumValue).getSelector();
    }
}
