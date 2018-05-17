package org.alm.tbert.callcenter;

import org.alm.tbert.callcenter.exception.CallCenterException;
import org.junit.BeforeClass;

public class CallCenterTest {
    private static CallCenter callcenter;

    @BeforeClass
    public static void initCallCenterTest() throws CallCenterException {
        callcenter = new CallCenter();
    }
}
