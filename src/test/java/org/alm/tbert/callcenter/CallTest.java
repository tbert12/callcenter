package org.alm.tbert.callcenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.PriorityQueue;

public class CallTest {
    private static Call callWithRandomDuration;
    private static Call callWithDeclaredDuration;
    private static final int DURATION = 15;


    @BeforeClass
    public static void initCallTest() {
        callWithDeclaredDuration = new Call(DURATION);
        callWithRandomDuration = new Call();
    }

    @Test
    public void testCallWithRandomDuration() {
        assertTrue( callWithRandomDuration.getDuration() >= Call.MIN_DURATION_MINUTES );
        assertTrue( callWithRandomDuration.getDuration() <= Call.MAX_DURATION_MINUTES );
    }

    @Test
    public void testCallWithDeclaredDuration() {
        assertEquals(callWithDeclaredDuration.getDuration(), DURATION);
    }

    @Test
    public void testCallComparator() {
        Call call1 = new Call();
        Call call2 = new Call();
        Call call3 = new Call();

        assertEquals(-1, call1.compareTo(call2));
        assertEquals(-1, call2.compareTo(call3));
        assertEquals(1, call3.compareTo(call2));

        Call[] calls = {call2, call3, call1};
        Arrays.sort(calls);
        assertEquals(calls[0], call1);
        assertEquals(calls[1], call2);
        assertEquals(calls[2], call3);

        PriorityQueue<Call> priorityCalls = new PriorityQueue<>();
        priorityCalls.add(call3);
        priorityCalls.add(call1);
        priorityCalls.add(call2);
        assertEquals(priorityCalls.poll(), call1);
        assertEquals(priorityCalls.poll(), call2);
        assertEquals(priorityCalls.poll(), call3);
    }
}
