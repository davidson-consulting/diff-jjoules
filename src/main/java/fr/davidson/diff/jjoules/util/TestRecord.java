package fr.davidson.diff.jjoules.util;

public class TestRecord {

    public TestRecord(String name, double delta, double globalPercentage, double categoryPercentage, Category category) {
        this.name = name;
        this.delta = delta;
        this.globalPercentage = globalPercentage;
        this.categoryPercentage = categoryPercentage;
        this.category = category;
    }

    public enum Category {
        POSITIVE, NEGATIVE, NEUTRAL
    }

    public final String name;
    public final double delta;
    public final double globalPercentage;
    public final double categoryPercentage;
    public final Category category;

}
