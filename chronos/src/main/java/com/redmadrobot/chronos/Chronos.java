package com.redmadrobot.chronos;

import android.support.annotation.NonNull;

/**
 * Class, which provides a static access to the Chronos library.
 *
 * @author maximefimov
 */
public final class Chronos {

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public final static String OWN_CALLBACK_METHOD_NAME = "onOperationFinished";

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public final static String BROADCAST_CALLBACK_METHOD_NAME = "onBroadcastOperationFinished";

    private Chronos() {
    }

    /**
     * Cancels all running operations for all objects.
     *
     * @param mayInterrupt {@code true} if threads executing operations task should be interrupted;
     *                     otherwise, in-progress tasks are allowed to complete
     */
    public static void cancelAll(final boolean mayInterrupt) {
        RunningOperationStorage.getInstance().cancelAll(mayInterrupt);
    }

    /**
     * Runs operation synchronously.
     *
     * @param operation Operation to be executed.
     * @param <Output>  class of the result, returned by the Operations' {@code run} method
     * @return OperationResult which contains the result of the Operation, or the error, occurred
     * during the execution
     */
    @NonNull
    public static <Output> ChronosOperationResult<Output> run(
            @NonNull final ChronosOperation<Output> operation) {
        return ChronosService.getInstance().runSync(operation, false);
    }

    /**
     * Runs operation synchronously. The result will be broadcasted to all objects, connected to
     * Chronos via {@link ChronosListener}.
     *
     * @param operation Operation to be executed.
     * @param <Output>  class of the result, returned by the Operations' {@code run} method
     * @return OperationResult which contains the result of the Operation, or the error, occurred
     * during the execution
     */
    @NonNull
    public static <Output> ChronosOperationResult<Output> runBroadcast(
            @NonNull final ChronosOperation<Output> operation) {
        return ChronosService.getInstance().runSync(operation, true);
    }
}
