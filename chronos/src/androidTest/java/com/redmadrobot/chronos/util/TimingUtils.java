package com.redmadrobot.chronos.util;

import com.redmadrobot.chronos.TestSettings;

/**
 * Utilities to mock waiting for long running tasks.
 *
 * @author maximefimov
 */
public final class TimingUtils {

    private TimingUtils() {
    }

    /**
     * Current thread sleeps for a predefined amount of time. If it has been interrupted, the method
     * would be finished and no exception is thrown.
     */
    public static void sleep() {
        try {
            Thread.sleep(TestSettings.RESPONSE_WAIT);
        } catch (InterruptedException e) {
            //ignore it
        }
    }

    /**
     * Current thread sleeps for {@code mills} ms. If it has been interrupted, the method would be
     * finished and no exception is thrown.
     *
     * @param mills time to sleep
     */
    public static void sleep(final long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            //ignore it
        }
    }
}
