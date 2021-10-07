package fr.davidson.diff_jjoules_demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/04/2021
 */
public class InternalList<T> {
    private final List<T> internalList;

    public InternalList(T... elements) {
        this.internalList = new ArrayList<>(Arrays.asList(elements));
    }

    public List<T> map(Function<T, T> operator) {
        final List<T> mappedList = new ArrayList<>();
        for (int i = 0 ; i < this.internalList.size() ; i++) {
            final T current = this.internalList.get(i);
            mappedList.add(operator.apply(current));
        }
        return mappedList;
    }

    public int count() {
        System.out.println("This is a modification");
        return this.internalList.size();
    }

    public int count2(boolean failing) {
        if (failing) {
            return 10;
        }
        return this.internalList.size();
    }

}