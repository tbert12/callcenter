package org.alm.tbert.callcenter;

import static org.junit.Assert.*;

import org.junit.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CallCenterTest {
    private static CallCenter callcenter;
    private static final int NUMBER_CALLS = 10;

    @Before
    public void onInitCallCenterTest() {
        callcenter = new CallCenter();
    }

    @After
    public void onEndCallCenterTest() {
        callcenter.stop();
    }

    @Test
    public void testCallCenter() {
        assertFalse(callcenter.isRunning());
        callcenter.start();
        assertTrue(callcenter.isRunning());
        callcenter.stop();
        assertFalse(callcenter.isRunning());
    }

    @Test
    public void testCallCenterWithOneCall() throws InterruptedException {
        callcenter.start();

        callcenter.call();

        callcenter.stopOnEndIncomingCalls();

        assertEquals(callcenter.getCountOfAnsweredCalls(), 1);
    }

    @Test
    public void testCallCenterWithTenParallelCalls() throws InterruptedException {
        callcenter.start();

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_CALLS);
        for (int i = 0; i < NUMBER_CALLS; i++) {
            executorService.submit(() -> callcenter.call());
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        callcenter.stopOnEndIncomingCalls();
        assertEquals(callcenter.getCountOfAnsweredCalls(), NUMBER_CALLS);
    }
}
