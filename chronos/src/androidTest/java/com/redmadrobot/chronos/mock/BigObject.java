package com.redmadrobot.chronos.mock;


import android.support.annotation.NonNull;

/**
 * A mock class that represents a very memory-consuming object.
 *
 * @author maximefimov
 */
public final class BigObject {

    private final static int LARGE_COUNT = 1_000_000_0;

    private final int[] mData;

    public BigObject() {
        mData = new int[LARGE_COUNT];
    }

    @NonNull
    @SuppressWarnings("unused")
    public int[] getData() {
        return mData;
    }
}
