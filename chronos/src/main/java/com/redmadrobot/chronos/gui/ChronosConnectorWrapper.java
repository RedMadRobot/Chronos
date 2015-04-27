package com.redmadrobot.chronos.gui;

import com.redmadrobot.chronos.Chronos;
import com.redmadrobot.chronos.ChronosOperation;

import org.jetbrains.annotations.Contract;

import android.support.annotation.NonNull;

/**
 * A set of methods which is provided by pre-defined GUI classes.
 *
 * @author maximefimov
 * @see com.redmadrobot.chronos.gui.activity.ChronosActivity
 * @see com.redmadrobot.chronos.gui.activity.ChronosSupportActivity
 * @see com.redmadrobot.chronos.gui.fragment.ChronosFragment
 * @see com.redmadrobot.chronos.gui.fragment.ChronosSupportFragment
 * @see com.redmadrobot.chronos.gui.fragment.dialog.ChronosDialogFragment
 * @see com.redmadrobot.chronos.gui.fragment.dialog.ChronosSupportDialogFragment
 */
public interface ChronosConnectorWrapper {

    /**
     * Runs an operation in a background thread. Only one operation with the given tag may run in a
     * single moment of time. The result will be delivered to {@link Chronos#OWN_CALLBACK_METHOD_NAME}
     * method. This method must have only one parameter of type {@link ChronosOperation#getResultClass()}
     * to receive a result.
     *
     * @param operation an operation to be run in background
     * @param tag       value which prohibits running new operations with the same tag while there
     *                  is a running one
     * @return a unique launch id, may be the same with the previous run with the same tag, if the
     * operation is still running
     * @see #runOperation(ChronosOperation)
     * @see #runOperationBroadcast(ChronosOperation, String)
     * @see #cancelOperation(int)
     * @see #cancelOperation(String)
     */
    int runOperation(@NonNull final ChronosOperation operation,
            @NonNull final String tag);

    /**
     * Runs an operation in a background thread. The result will be delivered to {@link
     * Chronos#OWN_CALLBACK_METHOD_NAME} method. This method must have only one parameter of type
     * {@link ChronosOperation#getResultClass()} to receive a result.
     *
     * @param operation an operation to be run in background
     * @return a unique launch id
     * @see #runOperation(ChronosOperation, String)
     * @see #runOperationBroadcast(ChronosOperation)
     * @see #cancelOperation(int)
     */
    int runOperation(@NonNull final ChronosOperation operation);

    /**
     * Runs an operation in a background thread, and broadcast it result when finished. Only one
     * operation with the given tag may run in a single moment of time. The result will be delivered
     * to {@link Chronos#OWN_CALLBACK_METHOD_NAME} method. All other Chronos clients will receive
     * the result in {@link Chronos#BROADCAST_CALLBACK_METHOD_NAME} method. Both method must have
     * only one parameter of type {@link ChronosOperation#getResultClass()} to receive a result.
     *
     * @param operation an operation to be run in background
     * @param tag       value which prohibits running new operations with the same tag while there
     *                  is a running one
     * @return a unique launch id, may be the same with the previous run with the same tag, if the
     * operation is still running
     * @see #runOperationBroadcast(ChronosOperation)
     * @see #runOperation(ChronosOperation, String)
     * @see #cancelOperation(int)
     * @see #cancelOperation(String)
     */
    int runOperationBroadcast(@NonNull final ChronosOperation operation,
            @NonNull final String tag);

    /**
     * Runs an operation in a background thread, and broadcast it result when finished. The result
     * will be delivered to {@link Chronos#OWN_CALLBACK_METHOD_NAME} method. All other Chronos
     * clients will receive the result in {@link Chronos#BROADCAST_CALLBACK_METHOD_NAME} method.
     * Both method must have only one parameter of type {@link ChronosOperation#getResultClass()} to
     * receive a result.
     *
     * @param operation an operation to be run in background
     * @return a unique launch id
     * @see #runOperation(ChronosOperation)
     * @see #runOperationBroadcast(ChronosOperation, String)
     * @see #cancelOperation(int)
     */
    int runOperationBroadcast(@NonNull final ChronosOperation operation);

    /**
     * Cancels a running operation by its launch id. It is not guaranteed that execution would be
     * interrupted immediately, however, no result would be delivered to the activity, or any other
     * Chronos clients, if it was a broadcast run.
     *
     * @param id id of launch, that needs to be cancelled
     * @return {@code false} if the task could not be cancelled, typically because it has already
     * completed normally; {@code true} otherwise
     * @see #cancelOperation(String)
     * @see #runOperation(ChronosOperation)
     * @see #runOperation(ChronosOperation, String)
     * @see #runOperationBroadcast(ChronosOperation)
     * @see #runOperationBroadcast(ChronosOperation, String)
     */
    boolean cancelOperation(final int id);

    /**
     * Cancels a running operation by its tag. It is not guaranteed that execution would be
     * interrupted immediately, however, no result would be delivered to the activity, or any other
     * Chronos clients, if it was a broadcast run.
     *
     * @param tag a tag with the operation was launched
     * @return {@code false} if the task could not be cancelled, typically because it has already
     * completed normally or there is no running operation with a given tag; {@code true} otherwise
     * @see #cancelOperation(int)
     * @see #runOperation(ChronosOperation)
     * @see #runOperation(ChronosOperation, String)
     * @see #runOperationBroadcast(ChronosOperation)
     * @see #runOperationBroadcast(ChronosOperation, String)
     */
    boolean cancelOperation(@NonNull final String tag);

    /**
     * Checks if an operation with given launch id is running.
     *
     * @param id an id of the operation launch
     * @return {@code true} if the operation is running, {@code false} otherwise
     */
    @Contract(pure = true)
    boolean isOperationRunning(final int id);

    /**
     * Checks if an operation with given launch tag is running.
     *
     * @param tag a pre-cache key of the operation launch
     * @return {@code true} if the operation is running, {@code false} if it is not running, or
     * there was no operation launch with the tag at all
     */
    @Contract(pure = true)
    boolean isOperationRunning(@NonNull final String tag);
}
