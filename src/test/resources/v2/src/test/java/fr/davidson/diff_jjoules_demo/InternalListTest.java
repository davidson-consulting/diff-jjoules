package fr.davidson.diff_jjoules_demo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 21/04/2021
 */
public class InternalListTest {

    @Test
    void testMapEmptyList() {
        final InternalList<Integer> emptyList = new InternalList<>();
        final List<Integer> map = emptyList.map(integer -> 2 * integer);
        assertTrue(map.isEmpty());
    }

    @Test
    void testMapOneElement() {
        final InternalList<Integer> emptyList = new InternalList<>(1);
        final List<Integer> map = emptyList.map(integer -> 2 * integer);
        assertFalse(map.isEmpty());
        assertEquals(2, (int)map.get(0));
    }

    @Test
    void testMapMultipleElement() {
        final InternalList<Integer> emptyList = new InternalList<>(1, 1, 1, 1);
        final List<Integer> map = emptyList.map(integer -> 2 * integer);
        assertFalse(map.isEmpty());
        assertTrue(map.stream().allMatch(value -> value == 2));
    }

    @Test
    void testCount() {
        final InternalList<Integer> emptyList = new InternalList<>(1, 1, 1, 1);
        assertEquals(4, emptyList.count());
    }

    @Test
    void testCount2() {
        final InternalList<Integer> emptyList = new InternalList<>(1, 1, 1, 1);
        assertEquals(4, emptyList.count2());
    }

    @Test
    void testCountFailing() {
        final InternalList<Integer> emptyList = new InternalList<>(1, 1, 1, 1);
        assertEquals(4, emptyList.count());
        fail();
    }
}
