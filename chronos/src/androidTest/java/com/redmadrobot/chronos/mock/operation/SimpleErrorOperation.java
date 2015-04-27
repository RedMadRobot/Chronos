package com.redmadrobot.chronos.mock.operation;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.redmadrobot.chronos.TestSettings;

import org.jetbrains.annotations.Contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.redmadrobot.chronos.util.TimingUtils.sleep;

/**
 * Operation that always throw an exception during its {@code run()} method.
 *
 * @author maximefimov
 */
public final class SimpleErrorOperation extends ChronosOperation<String> {

    private final static RuntimeException EXCEPTION = new RuntimeException("Test exception");

    @Contract("null -> false")
    public static boolean isExpectedException(@Nullable final Exception exception) {
        return exception != null && exception.equals(EXCEPTION);
    }

    @Nullable
    @Override
    public String run() {
        sleep(TestSettings.OPERATION_WAIT);
        throw EXCEPTION;
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<String>> getResultClass() {
        return SimpleOperationResult.class;
    }
}
