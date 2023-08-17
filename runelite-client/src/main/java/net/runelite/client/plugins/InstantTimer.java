package net.runelite.client.plugins;

import java.time.Duration;
import java.time.Instant;

public class InstantTimer {
    private Instant instant;

    public InstantTimer() {
        this.instant = Instant.now();
    }

    public void resetTimer() {
        instant = Instant.now();
    }

    public boolean runningMoreThan(long millis) {
        return Duration.between(instant,Instant.now()).toMillis() > millis;
    }

    public void increaseTime(long millis) {
        instant = instant.plusMillis(millis);
    }
}
