package com.redmadrobot.chronos.mock.operation;

import com.redmadrobot.chronos.Operation;
import com.redmadrobot.chronos.OperationResult;
import com.redmadrobot.chronos.TestSettings;
import com.redmadrobot.chronos.mock.BigObject;

import org.jetbrains.annotations.Contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.redmadrobot.chronos.util.TimingUtils.sleep;

/**
 * An operation that generates some very heavy result.
 *
 * @author maximefimov
 */
public final class HeavyOperation extends Operation<BigObject> {

    private boolean hasOutOfMemory = false;

    @Contract(pure = true)
    public final boolean gotOutOfMemory() {
        return hasOutOfMemory;
    }

    @Nullable
    @Override
    public BigObject run() {
        sleep(TestSettings.OPERATION_WAIT);
        BigObject result = null;
        try {
            result = new BigObject();
        } catch (OutOfMemoryError error) {
            hasOutOfMemory = true;
        }
        return result;
    }

    @NonNull
    @Override
    public Class<? extends OperationResult<BigObject>> getResultClass() {
        return HeavyOperationResult.class;
    }
}
