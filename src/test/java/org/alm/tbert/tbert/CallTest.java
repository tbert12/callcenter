package org.alm.tbert.tbert;

import org.alm.tbert.callcenter.Call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.junit.BeforeClass;
import org.junit.Test;

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
}
