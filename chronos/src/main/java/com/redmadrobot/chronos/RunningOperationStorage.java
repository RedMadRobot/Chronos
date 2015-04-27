package com.redmadrobot.chronos;

import org.jetbrains.annotations.Contract;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * A storage for launched operation.
 *
 * @author maximefimov
 */
final class RunningOperationStorage {

    @NonNull
    private final static RunningOperationStorage INSTANCE = new RunningOperationStorage();

    @NonNull
    private final Map<Integer, RunningOperation> mRunningOperations = new HashMap<>();

    @NonNull
    private final List<Integer> mCancelledOperations = new LinkedList<>();

    private RunningOperationStorage() {
    }

    @NonNull
    @Contract(pure = true)
    static RunningOperationStorage getInstance() {
        return INSTANCE;
    }

    /**
     * Cancels running operation.
     *
     * @param id              the unique id of operations' launch
     * @param mayInterrupt    {@code true} if thread executing operation task should be interrupted;
     *                        otherwise, in-progress tasks are allowed to complete
     * @param removeOperation {@code true} if the operation should be marked as removed
     * @return {@code false} if the task could not be cancelled, typically because it has already
     * completed normally; {@code true} otherwise
     */
    private synchronized boolean cancel(final int id, final boolean mayInterrupt,
            final boolean removeOperation) {
        final RunningOperation runningOperation = mRunningOperations.get(id);
        if (runningOperation != null) {
            if (removeOperation) {
                mRunningOperations.remove(id);
            }
            mCancelledOperations.add(id);
            return runningOperation.cancel(mayInterrupt);
        } else {
            return false;
        }
    }

    /**
     * Stores the future as a running operation with a given runs' id.
     *
     * @param id     the unique id of an operation launch
     * @param future the object that represents a running operation
     */
    synchronized final void operationStarted(final int id, @NonNull final ChronosOperation<?> operation,
            @NonNull final Future<?> future
    ) {
        mRunningOperations.put(id, new RunningOperation(operation, future));
    }

    /**
     * Marks an operation run with id as finished.
     *
     * @param id the unique id of operations' launch
     */
    synchronized final void operationFinished(final int id) {
        mRunningOperations.remove(id);
    }

    /**
     * Cancels running operation.
     *
     * @param id           the unique id of operations' launch
     * @param mayInterrupt {@code true} if thread executing operation task should be interrupted;
     *                     otherwise, in-progress tasks are allowed to complete
     * @return {@code false} if the task could not be cancelled, typically because it has already
     * completed normally; {@code true} otherwise
     */
    synchronized final boolean cancel(final int id, final boolean mayInterrupt) {
        return cancel(id, mayInterrupt, true);
    }

    /**
     * Cancels all running operations.
     *
     * @param mayInterrupt {@code true} if threads executing operations task should be interrupted;
     *                     otherwise, in-progress tasks are allowed to complete
     */
    synchronized final void cancelAll(final boolean mayInterrupt) {
        for (final Integer key : mRunningOperations.keySet()) {
            cancel(key, mayInterrupt, false);
        }

        mRunningOperations.clear();
    }

    /**
     * Checks if operation launch with given id is still running.
     *
     * @param id the unique id of operations' launch
     * @return {@code true} if the operation is still running, {@code false} otherwise
     */
    @Contract(pure = true)
    synchronized final boolean isOperationRunning(final int id) {
        return mRunningOperations.containsKey(id);
    }

    /**
     * Checks if operation launch with given id was cancelled.
     *
     * @param id the unique id of operations' launch
     * @return {@code true} if the operation was cancelled, {@code false} otherwise
     */
    @Contract(pure = true)
    synchronized final boolean isOperationCancelled(final int id) {
        return mCancelledOperations.contains(Integer.valueOf(id));
    }

    private static class RunningOperation {

        private final ChronosOperation<?> mOperation;

        private final Future<?> mFuture;

        public RunningOperation(@NonNull final ChronosOperation<?> operation,
                @NonNull final Future<?> future) {
            mOperation = operation;
            mFuture = future;
        }

        public final boolean cancel(final boolean mayInterrupt) {
            mOperation.cancel();
            return mFuture.cancel(mayInterrupt);
        }
    }
}
