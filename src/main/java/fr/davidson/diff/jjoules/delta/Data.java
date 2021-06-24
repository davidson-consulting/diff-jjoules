package fr.davidson.diff.jjoules.delta;

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

}
