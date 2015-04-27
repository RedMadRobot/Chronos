package com.redmadrobot.chronos;


import org.jetbrains.annotations.Contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A functional object that encapsulates a business logic of some time-consuming task.<br> Its
 * result is an Output object.
 *
 * @param <Output> Type of the output object.
 * @author maximefimov
 * @see ChronosListener#invoke(ChronosOperation, boolean)
 * @see ChronosListener#invoke(ChronosOperation, String, boolean)
 * @see Chronos#run(ChronosOperation)
 * @see Chronos#runBroadcast(ChronosOperation)
 */
public abstract class ChronosOperation<Output> {


    private final AtomicBoolean mIsCancelled = new AtomicBoolean(false);

    /**
     * The method for performing business-logic related work. Can contain time-consuming calls, but
     * should not perform any interaction with the UI, as it will be launched not in the Main
     * Thread.<br>
     * <p/>
     * All exceptions thrown in this method will be encapsulated in OperationResult object, so it
     * will not cause app crash.
     *
     * @return the result of the operation.
     */
    @Nullable
    public abstract Output run();

    /**
     * This method returns a subclass of OperationResult class, related to the particular Operation
     * subclass, so that Chronos clients can distinguish results from different operations.
     *
     * @return OperationResult subclass, that will be created after the operation is complete.
     */
    @NonNull
    @Contract(pure = true)
    public abstract Class<? extends ChronosOperationResult<Output>> getResultClass();

    /**
     * Checks if the operation was cancelled.
     *
     * @return {@code true} if the operation was cancelled, {@code false} otherwise
     * @see ChronosListener#cancel(int, boolean)
     * @see ChronosListener#cancel(String, boolean)
     * @see Chronos#cancelAll(boolean)
     */
    @Contract(pure = true)
    public final boolean isCancelled() {
        return mIsCancelled.get();
    }

    /**
     * Marks operation as cancelled.
     */
    final void cancel() {
        mIsCancelled.set(true);
    }
}