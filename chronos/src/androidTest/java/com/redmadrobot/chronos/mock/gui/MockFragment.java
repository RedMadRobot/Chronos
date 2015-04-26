package com.redmadrobot.chronos.mock.gui;

import com.redmadrobot.chronos.Chronos;
import com.redmadrobot.chronos.ChronosConnector;
import com.redmadrobot.chronos.Operation;

import org.jetbrains.annotations.Contract;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Test class mocking fragment behaviour.
 *
 * @author maximefimov
 */
public abstract class MockFragment {

    private State mState = State.INSTANCED;

    private MockActivity mMockActivity;

    @Nullable
    @Contract(pure = true)
    public final MockActivity getMockActivity() {
        return mMockActivity;
    }

    public final void setMockActivity(@Nullable final MockActivity mockActivity) {
        if (mMockActivity != null) {
            throw new IllegalStateException("Activity has already been set.");
        }
        mMockActivity = mockActivity;
    }

    private final ChronosConnector mHelper = new ChronosConnector();

    private Bundle mSavedInstanceState = null;

    public void setState(final @NonNull State state) {
        mState = state;
    }

    public void onCreate() {
        if (!(mState == State.INSTANCED || mState == State.PAUSED)) {
            throw new IllegalStateException("Wrong state: " + mState);
        }
        mState = State.CREATED;
        mHelper.onCreate(this, mSavedInstanceState);
    }

    public void onResume() {
        if (mState != State.CREATED) {
            throw new IllegalStateException("Wrong state: " + mState);
        }
        mState = State.RESUMED;
        mHelper.onResume();
    }

    public void onSaveInstanceState() {
        if (mState != State.RESUMED) {
            throw new IllegalStateException("Wrong state: " + mState);
        }
        mState = State.SAVED;
        mSavedInstanceState = new Bundle();
        mHelper.onSaveInstanceState(mSavedInstanceState);
    }

    public void onPause() {
        if (mState != State.SAVED) {
            throw new IllegalStateException("Wrong state: " + mState);
        }
        mState = State.PAUSED;
        mHelper.onPause();
    }

    /**
     * Runs an operation in a background thread. Only one operation with the given tag may run in a
     * single moment of time. The result will be delivered to {@link Chronos#OWN_CALLBACK_METHOD_NAME}
     * method. This method must have only one parameter of type {@link Operation#getResultClass()}
     * to receive a result.
     *
     * @param operation an operation to be run in background
     * @param tag       value which prohibits running new operations with the same tag while there
     *                  is a running one
     * @return a unique launch id, may be the same with the previous run with the same tag, if the
     * operation is still running
     * @see #runOperation(Operation)
     * @see #runOperationBroadcast(Operation, String)
     * @see #cancelOperation(int)
     * @see #cancelOperation(String)
     */
    @SuppressWarnings("unused")
    protected final int runOperation(@NonNull final Operation operation,
            @NonNull final String tag) {
        return mHelper.runOperation(operation, tag, false);
    }

    /**
     * Runs an operation in a background thread. The result will be delivered to {@link
     * Chronos#OWN_CALLBACK_METHOD_NAME} method. This method must have only one parameter
     * of type {@link Operation#getResultClass()} to receive a result.
     *
     * @param operation an operation to be run in background
     * @return a unique launch id
     * @see #runOperation(Operation, String)
     * @see #runOperationBroadcast(Operation)
     * @see #cancelOperation(int)
     */
    @SuppressWarnings("unused")
    protected final int runOperation(@NonNull final Operation operation) {
        return mHelper.runOperation(operation, false);
    }

    /**
     * Runs an operation in a background thread, and broadcast it result when finished. Only one
     * operation with the given tag may run in a single moment of time. The result will be delivered
     * to {@link Chronos#OWN_CALLBACK_METHOD_NAME} method. All other Chronos clients will
     * receive the result in {@link Chronos#BROADCAST_CALLBACK_METHOD_NAME} method. Both
     * method must have only one parameter of type {@link Operation#getResultClass()} to receive a
     * result.
     *
     * @param operation an operation to be run in background
     * @param tag       value which prohibits running new operations with the same tag while there
     *                  is a running one
     * @return a unique launch id, may be the same with the previous run with the same tag, if the
     * operation is still running
     * @see #runOperationBroadcast(Operation)
     * @see #runOperation(Operation, String)
     * @see #cancelOperation(int)
     * @see #cancelOperation(String)
     */
    @SuppressWarnings("unused")
    protected final int runOperationBroadcast(@NonNull final Operation operation,
            @NonNull final String tag) {
        return mHelper.runOperation(operation, tag, true);
    }

    /**
     * Runs an operation in a background thread, and broadcast it result when finished. The result
     * will be delivered to {@link Chronos#OWN_CALLBACK_METHOD_NAME} method. All other
     * Chronos clients will receive the result in {@link Chronos#BROADCAST_CALLBACK_METHOD_NAME}
     * method. Both method must have only one parameter of type {@link Operation#getResultClass()}
     * to receive a result.
     *
     * @param operation an operation to be run in background
     * @return a unique launch id
     * @see #runOperation(Operation)
     * @see #runOperationBroadcast(Operation, String)
     * @see #cancelOperation(int)
     */
    @SuppressWarnings("unused")
    protected final int runOperationBroadcast(@NonNull final Operation operation) {
        return mHelper.runOperation(operation, true);
    }

    /**
     * Cancels a running operation by its launch id. It is not guaranteed that execution would be
     * interrupted immediately, however, no result would be delivered to the fragment, or any other
     * Chronos clients, if it was a broadcast run.
     *
     * @param id id of launch, that needs to be cancelled
     * @return {@code false} if the task could not be cancelled, typically because it has already
     * completed normally; {@code true} otherwise
     * @see #cancelOperation(String)
     * @see #runOperation(Operation)
     * @see #runOperation(Operation, String)
     * @see #runOperationBroadcast(Operation)
     * @see #runOperationBroadcast(Operation, String)
     */
    @SuppressWarnings("unused")
    protected final boolean cancelOperation(final int id) {
        return mHelper.cancelOperation(id, true);
    }

    /**
     * Cancels a running operation by its tag. It is not guaranteed that execution would be
     * interrupted immediately, however, no result would be delivered to the fragment, or any other
     * Chronos clients, if it was a broadcast run.
     *
     * @param tag a tag with the operation was launched
     * @return {@code false} if the task could not be cancelled, typically because it has already
     * completed normally or there is no running operation with a given tag; {@code true} otherwise
     * @see #cancelOperation(int)
     * @see #runOperation(Operation)
     * @see #runOperation(Operation, String)
     * @see #runOperationBroadcast(Operation)
     * @see #runOperationBroadcast(Operation, String)
     */
    @SuppressWarnings("unused")
    protected final boolean cancelOperation(@NonNull final String tag) {
        return mHelper.cancelOperation(tag, true);
    }
}
