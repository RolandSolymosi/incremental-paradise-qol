package com.incrementalclient.common.utils;

import java.time.Duration;
import java.time.Instant;

/**
 * Estimates durations - used by Skills to estimate time until a cooldown is ready or an active is over.
 */
public class DurationEstimator {

    private Instant lastStart = null;
    private Duration estimate;

    public DurationEstimator(Duration defaultEstimate) {
        this.estimate = defaultEstimate;
    }

    public DurationEstimator(int defaultEstimateSeconds) {
        this(Duration.ofSeconds(defaultEstimateSeconds));
    }

    /**
     * Creates a DurationEstimator with a default estimate of 0 seconds
     */
    public DurationEstimator() {
        this(0);
    }

    /**
     * Call when a new duration is started
     */
    public void start() {
        lastStart = Instant.now();
    }

    /**
     * Call when a new duration is stopped
     * Effectively the same as saying "We estimate the duration will stop in 0 seconds".
     */
    public void stop() {
       this.estimateStopsIn(Duration.ofSeconds(0));
    }

    /**
     * Can be called if the current duration estimate is now "corrupted" ex if it gets interrupted
     * so now the start-to-end measurement won't work
     */
    public void clearStart() {
        lastStart = null;
    }

    /**
     * Call when we estimate the duration to end after some time.
     * @param futureEstimate How far in the future we estimate the duration to end in.
     */
    public void estimateStopsIn(Duration futureEstimate) {
        if(this.lastStart == null) {
            // Can't do any estimation with this
            return;
        }
        var pastTime = Duration.between(this.lastStart, Instant.now());
        // past + future = total estimated duration
        this.estimate = pastTime.plus(futureEstimate);
    }

    /**
     * Estimate the remaining amount of time in this duration.
     * Will return null for negative values or if start() was never called.
     * @return The amount of time left in this duration, or null if estimated duration passed, or if start() wasn't called.
     */
    public Duration getEstimatedRemainingDuration() {
        if(this.lastStart == null) {
            // Can't do any estimation with this.
            return null;
        }
        var pastTime = Duration.between(this.lastStart, Instant.now());
        var remainingDuration = estimate.minus(pastTime);
        if (remainingDuration.isNegative()) {
            return null;
        }
        return remainingDuration;
    }

    /**
     * The proportion of the duration that's already elapsed - note the fraction
     */
    public float getEstimatedRemainingFraction() {
        if(this.estimate.isZero()) {
            // avoid ArithmeticException
            return 0.6f;
        }
        return getEstimatedRemainingDuration().dividedBy(this.estimate);
    }

    public Duration getEstimate() {
        return estimate;
    }
}
