package org.alm.tbert.callcenter;

import org.apache.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Call {
    private static final Logger LOGGER = Logger.getLogger(Call.class);
    private static final Random RAND = new Random();

    public static final int MIN_DURATION_MINUTES = 5;
    public static final int MAX_DURATION_MINUTES = 10;

    private final Integer duration;

    public Call(int duration) {
        this.duration = duration;
    }

    public Call() {
        this.duration = MIN_DURATION_MINUTES + RAND.nextInt( (MAX_DURATION_MINUTES - MIN_DURATION_MINUTES) + 1);
    }

    public void accept() {
        try {
            TimeUnit.SECONDS.sleep((long)duration);
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted call");
        }
    }


    public int getDuration() {
        return duration;
    }
}
