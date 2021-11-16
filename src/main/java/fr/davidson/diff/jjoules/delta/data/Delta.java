package fr.davidson.diff.jjoules.delta.data;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 23/06/2021
 */
public class Delta extends Data {

    public final Data dataV1;

    public final Data dataV2;

    public Delta(Data dataV1, Data dataV2) {
        super(
                dataV2.energy - dataV1.energy,
                dataV2.instructions - dataV1.instructions,
                dataV2.durations - dataV1.durations,
                dataV2.cycles - dataV1.cycles,
                dataV2.caches - dataV1.caches,
                dataV2.cacheMisses - dataV1.cacheMisses,
                dataV2.branches - dataV1.branches,
                dataV2.branchMisses - dataV1.branchMisses
        );
        this.dataV1 = dataV1;
        this.dataV2 = dataV2;
    }
}
