package com.redmadrobot.chronos_sample.operations;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * An operation that calculates a string length, mocking it is being a time-consuming task.
 *
 * @author maximefimov
 */
public final class SimpleOperation extends ChronosOperation<String> {

    private final String mInput;

    public SimpleOperation(@NonNull final String input) {
        mInput = input;
    }

    @Nullable
    @Override
    //Chronos will run this method in a background thread, which means you can put
    //any time-consuming calls here, as it will not affect UI thread performance
    public String run() {
        final String result = "String length is " + mInput.length();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // do nothing, thread is interrupted, which means a system wants to stop the run
        }

        return result;
    }

    @NonNull
    @Override
    // To be able to distinguish results from different operations in one Chronos client
    // (most commonly an activity, or a fragment)
    // you should create an 'OperationResult<>' subclass in each operation,
    // so that it will be used as a parameter
    // in a callback method 'onOperationFinished'
    public Class<? extends ChronosOperationResult<String>> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<String> {

    }
}
