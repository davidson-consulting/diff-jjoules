package fr.davidson.diff.jjoules.delta.data;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 23/06/2021
 */
public class Data {

    public final double energy;

    public final double instructions;

    public final double durations;

    public final double cycles;

    public final double caches;

    public final double cacheMisses;

    public final double branches;

    public final double branchMisses;

    public Data() {
        this(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    public Data(
            double energy,
            double instructions,
            double durations,
            double cycles,
            double caches,
            double cacheMisses,
            double branches,
            double branchMisses) {
        this.energy = energy;
        this.instructions = instructions;
        this.durations = durations;
        this.cycles = cycles;
        this.caches = caches;
        this.cacheMisses = cacheMisses;
        this.branches = branches;
        this.branchMisses = branchMisses;
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

    public double getCycles() {
        return cycles;
    }

    public double getCaches() {
        return caches;
    }

    public double getCacheMisses() {
        return cacheMisses;
    }

    public double getBranches() {
        return branches;
    }

    public double getBranchMisses() {
        return branchMisses;
    }

    public Data add(Data that) {
        return this.add(that, 1.0D);
    }

    public Data add(Data that, double factor) {
        return new Data(
                this.energy + (that.energy * factor),
                this.instructions + (that.instructions * factor),
                this.durations + (that.durations * factor),
                this.cycles + (that.cycles * factor),
                this.caches + (that.caches * factor),
                this.cacheMisses + (that.cacheMisses * factor),
                this.branches + (that.branches * factor),
                this.branchMisses + (that.branchMisses * factor)
        );
    }

    @Override
    public String toString() {
        return "Data{" +
                "energy=" + energy +
                ", instructions=" + instructions +
                ", durations=" + durations +
                ", cycles=" + cycles +
                ", caches=" + caches +
                ", cacheMisses=" + cacheMisses +
                ", branches=" + branches +
                ", branchMisses=" + branchMisses +
                '}';
    }
}
