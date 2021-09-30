package fr.davidson.diff.jjoules.delta.data;

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

    public final double cycles;

    public final double caches;

    public final double cacheMisses;

    public final double branches;

    public final double branchMisses;

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
