package com.redmadrobot.chronos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Basic interface for providing GUI elements an access to Chronos.
 *
 * @author maximefimov
 */
public final class ChronosConnector {

    private final static String KEY_CHRONOS_LISTENER_ID = "chronos_listener_id";

    private ChronosListener mChronosListener;

    private Object mGUIClient;

    /**
     * GUI client should call this method in its own onCreate() method.
     *
     * @param client             a client that holds the helper
     * @param savedInstanceState parameter from the corresponding client method
     */
    public final void onCreate(@NonNull final Object client,
            @Nullable final Bundle savedInstanceState) {
        mGUIClient = client;
        if (savedInstanceState != null) {
            mChronosListener = ChronosListenerManager.getInstance()
                    .getListener(savedInstanceState.getInt(KEY_CHRONOS_LISTENER_ID));
        } else {
            mChronosListener = ChronosListenerManager.getInstance()
                    .createListener();
        }
    }

    /**
     * GUI client should call this method in its own onResume() method.
     */
    public final void onResume() {
        mChronosListener.onResume(mGUIClient);
    }

    /**
     * GUI client should call this method in its own onSaveInstanceState() method.
     *
     * @param outState parameter from the corresponding client method
     */
    public final void onSaveInstanceState(@Nullable final Bundle outState) {
        if (outState != null) {
            outState.putInt(KEY_CHRONOS_LISTENER_ID, mChronosListener.getId());
        }
    }

    /**
     * GUI client should call this method in its own onPause() method.
     */
    public final void onPause() {
        mChronosListener.onPause();
    }

    /**
     * Runs an operation in a background thread. Only one operation with the given tag may run in a
     * single moment of time. The result will be delivered to {@link Chronos#OWN_CALLBACK_METHOD_NAME}
     * method. If {@code broadcast} is {@code true} all other Chronos clients will receive the
     * result in {@link Chronos#BROADCAST_CALLBACK_METHOD_NAME} method. Both method must have only
     * one parameter of type {@link ChronosOperation#getResultClass()} to receive a result.
     *
     * @param operation an operation to be run in background
     * @param tag       value which prohibits running new operations with the same tag while there
     *                  is a running one
     * @param broadcast {@code true} if any other Chronos clients should be able to receive a
     *                  result; {@code false} otherwise
     * @return a unique launch id, may be the same with the previous run with the same tag, if the
     * operation is still running
     * @see #runOperation(ChronosOperation, boolean)
     * @see #cancelOperation(int, boolean)
     * @see #cancelOperation(String, boolean)
     */
    public final int runOperation(@NonNull final ChronosOperation operation, @NonNull final String tag,
            final boolean broadcast) {
        return mChronosListener.invoke(operation, tag, broadcast);
    }

    /**
     * Runs an operation in a background thread. The result will be delivered to {@link
     * Chronos#OWN_CALLBACK_METHOD_NAME} method. If {@code broadcast} is {@code true} all other
     * Chronos clients will receive the result in {@link Chronos#BROADCAST_CALLBACK_METHOD_NAME}
     * method. Both method must have only one parameter of type {@link ChronosOperation#getResultClass()}
     * to receive a result.
     *
     * @param operation an operation to be run in background
     * @param broadcast {@code true} if any other Chronos clients should be able to receive a
     *                  result; {@code false} otherwise
     * @return a unique launch id
     * @see #runOperation(ChronosOperation, String, boolean)
     * @see #cancelOperation(int, boolean)
     */
    public final int runOperation(@NonNull final ChronosOperation operation, final boolean broadcast) {
        return mChronosListener.invoke(operation, broadcast);
    }

    /**
     * Cancels a running operation. It is not guaranteed that execution would be interrupted
     * immediately, however, no result would be delivered to the GUI client, or any other Chronos
     * clients, if it was a broadcast run.
     *
     * @param id           id of launch, that needs to be cancelled
     * @param mayInterrupt {@code true} if threads executing operations task should be interrupted;
     *                     otherwise, in-progress tasks are allowed to complete
     * @return {@code false} if the task could not be cancelled, typically because it has already
     * completed normally; {@code true} otherwise
     * @see #runOperation(ChronosOperation, String, boolean)
     * @see #runOperation(ChronosOperation, boolean)
     * @see #cancelOperation(String, boolean)
     */
    public final boolean cancelOperation(final int id, final boolean mayInterrupt) {
        return mChronosListener.cancel(id, mayInterrupt);
    }

    /**
     * Cancels a running operation. It is not guaranteed that execution would be interrupted
     * immediately, however, no result would be delivered to the GUI client, or any other Chronos
     * clients, if it was a broadcast run.
     *
     * @param tag          a tag with the operation was launched
     * @param mayInterrupt {@code true} if threads executing operations task should be interrupted;
     *                     otherwise, in-progress tasks are allowed to complete
     * @return {@code false} if the task could not be cancelled, typically because it has already
     * completed normally or there is no running operation with a given tag; {@code true} otherwise
     * @see #runOperation(ChronosOperation, String, boolean)
     * @see #runOperation(ChronosOperation, boolean)
     * @see #cancelOperation(int, boolean)
     */
    public final boolean cancelOperation(@NonNull final String tag, final boolean mayInterrupt) {
        return mChronosListener.cancel(tag, mayInterrupt);
    }

    /**
     * Checks if an operation with given launch id is running.
     *
     * @param id an id of the operation launch
     * @return {@code true} if the operation is running, {@code false} otherwise
     */
    public final boolean isOperationRunning(final int id) {
        return mChronosListener.isRunning(id);
    }

    /**
     * Checks if an operation with given launch tag is running.
     *
     * @param tag a pre-cache key of the operation launch
     * @return {@code true} if the operation is running, {@code false} if it is not running, or
     * there was no operation launch with the tag at all
     */
    public final boolean isOperationRunning(@NonNull final String tag) {
        return mChronosListener.isRunning(tag);
    }
}
