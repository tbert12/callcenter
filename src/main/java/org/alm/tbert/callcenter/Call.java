package org.alm.tbert.callcenter;

import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Call implements Comparable<Call> {
    private static final Logger LOGGER = Logger.getLogger(Call.class);
    private static final Random RAND = new Random();

    static final int MIN_DURATION_MINUTES = 5;
    static final int MAX_DURATION_MINUTES = 10;

    private final Integer duration;
    private String summary;

    // --- DEBUG attributes
    private static int _uuid = 0;
    private final String _UUID;
    private String _generateUUID() {
        _uuid++;
        return String.valueOf(_uuid);
    }
    // ----

    public Call(int duration) {
        this.duration = duration;
        this._UUID = _generateUUID();
        this.summary = "";
    }

    Call() {
        this.duration = MIN_DURATION_MINUTES + RAND.nextInt( (MAX_DURATION_MINUTES - MIN_DURATION_MINUTES) + 1);
        this._UUID = _generateUUID();
        this.summary = "";
    }

    public void accept() {
        LocalDateTime start = LocalDateTime.now();
        try {
            TimeUnit.SECONDS.sleep((long)duration);
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted call");
        }
        LocalDateTime end = LocalDateTime.now();
        summary = String.format("start: %d:%d | end: %d:%d", start.getMinute(), start.getSecond(), end.getMinute(), end.getSecond());
    }

    int getDuration() {
        return duration;
    }

    public String toString() {
        return summary.length() == 0 ? this._UUID : String.format("%s - %s",this._UUID, this.summary);
    }

    @Override
    public int compareTo(Call o) {
        return this._UUID.compareTo(o._UUID);
    }
}
