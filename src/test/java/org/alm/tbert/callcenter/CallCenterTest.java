package org.alm.tbert.callcenter;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class CallCenterTest {
    private static CallCenter callcenter;

    @BeforeClass
    public static void initCallCenterTest() {
        callcenter = new CallCenter();
    }

    @AfterClass
    public static void endCallCenterTest() {
        //callcenter.stop();
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
        TimeUnit.SECONDS.sleep(1);
        callcenter.stop();
    }
}
