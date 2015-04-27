package com.redmadrobot.chronos.mock.operation;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.redmadrobot.chronos.TestSettings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.redmadrobot.chronos.util.TimingUtils.sleep;

/**
 * Operation that converts one string to another.
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
    public String run() {
        final String result = transform(mInput);
        sleep(TestSettings.OPERATION_WAIT);
        return result;
    }

    public static boolean isTransform(@NonNull final String input, @Nullable final String output) {
        return output != null && transform(input).equals(output);
    }

    @NonNull
    private static String transform(@NonNull final String input) {
        return input.concat(input);
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<String>> getResultClass() {
        return SimpleOperationResult.class;
    }

}
