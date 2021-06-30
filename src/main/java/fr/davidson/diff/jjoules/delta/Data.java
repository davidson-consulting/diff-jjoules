package fr.davidson.diff.jjoules.delta;

import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 23/06/2021
 */
public class Data {

    public final double energy;

    public final double instructions;

    public final double durations;

    public Data(
            double energy,
            double instructions,
            double durations
    ) {
        this.energy = energy;
        this.instructions = instructions;
        this.durations = durations;
    }

    public Data(Map<String, ?> data) {
        this(
                Double.parseDouble(data.get("energy").toString()),
                Double.parseDouble(data.get("instructions").toString()),
                Double.parseDouble(data.get("durations").toString())
        );
    }

    public double getEnergy() {
        return energy;
    }

    public double getInstructions() {
        return instructions;
    }

    public double getDurations() {
        return durations;
    }

    public Data average(Data that) {
        return new Data(
                (this.energy + that.energy) / 2,
                (this.instructions + that.instructions) / 2,
                (this.durations + that.durations) / 2
        );
    }

    @Override
    public String toString() {
        return "Data{" +
                "energy=" + energy +
                ", instructions=" + instructions +
                ", durations=" + durations +
                '}';
    }
}
