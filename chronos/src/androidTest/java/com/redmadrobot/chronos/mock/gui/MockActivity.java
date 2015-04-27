package com.redmadrobot.chronos.mock.gui;

import com.redmadrobot.chronos.Chronos;
import com.redmadrobot.chronos.ChronosConnector;
import com.redmadrobot.chronos.ChronosOperation;

import org.jetbrains.annotations.Contract;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Test class mocking activity behaviour.
 *
 * @author maximefimov
 */
public abstract class MockActivity {

    /**
     * An entry point to access Chronos functions.
     */
    private final ChronosConnector mHelper = new ChronosConnector();

    private final List<MockFragment> mMockFragments = new LinkedList<>();

    private Bundle mSavedInstanceState = null;

    private State mState = State.INSTANCED;

    public final void start() {
        onCreate();
        onResume();
    }

    public final void stop() {
        onSaveInstanceState();
        onPause();
    }

    public final void addFragment(@NonNull final MockFragment mockFragment) {
        if (mState == State.SAVED || mState == State.PAUSED) {
            throw new IllegalStateException("Wrong state: " + mState);
        }
        mockFragment.setMockActivity(this);
        mMockFragments.add(mockFragment);
        mockFragment.setState(mState);
    }

    protected void onCreate() {
        if (!(mState == State.INSTANCED || mState == State.PAUSED)) {
            throw new IllegalStateException("Wrong state: " + mState);
        }
        mState = State.CREATED;
        mHelper.onCreate(this, mSavedInstanceState);
        for (final MockFragment fragment : mMockFragments) {
            fragment.onCreate();
        }
    }

    protected void onResume() {
        if (mState != State.CREATED) {
            throw new IllegalStateException("Wrong state: " + mState);
        }
        mState = State.RESUMED;
        mHelper.onResume();
        for (final MockFragment fragment : mMockFragments) {
            fragment.onResume();
        }
    }

    protected void onSaveInstanceState() {
        if (mState != State.RESUMED) {
            throw new IllegalStateException("Wrong state: " + mState);
        }
        mState = State.SAVED;
        mSavedInstanceState = new Bundle();
        mHelper.onSaveInstanceState(mSavedInstanceState);
        for (final MockFragment fragment : mMockFragments) {
            fragment.onSaveInstanceState();
        }
    }

    protected void onPause() {
        if (mState != State.SAVED) {
            throw new IllegalStateException("Wrong state: " + mState);
        }
        mState = State.PAUSED;
        mHelper.onPause();
        for (final MockFragment fragment : mMockFragments) {
            fragment.onPause();
        }
    }

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
    @SuppressWarnings("unused")
    protected final int runOperation(@NonNull final ChronosOperation operation,
            @NonNull final String tag) {
        return mHelper.runOperation(operation, tag, false);
    }

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
    @SuppressWarnings("unused")
    protected final int runOperation(@NonNull final ChronosOperation operation) {
        return mHelper.runOperation(operation, false);
    }

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
    @SuppressWarnings("unused")
    protected final int runOperationBroadcast(@NonNull final ChronosOperation operation,
            @NonNull final String tag) {
        return mHelper.runOperation(operation, tag, true);
    }

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
    @SuppressWarnings("unused")
    protected final int runOperationBroadcast(@NonNull final ChronosOperation operation) {
        return mHelper.runOperation(operation, true);
    }

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
    @SuppressWarnings("unused")
    protected final boolean cancelOperation(final int id) {
        return mHelper.cancelOperation(id, true);
    }

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
    @SuppressWarnings("unused")
    protected final boolean cancelOperation(@NonNull final String tag) {
        return mHelper.cancelOperation(tag, true);
    }

    /**
     * Checks if an operation with given launch id is running.
     *
     * @param id an id of the operation launch
     * @return {@code true} if the operation is running, {@code false} otherwise
     */
    @SuppressWarnings("unused")
    @Contract(pure = true)
    public final boolean isOperationRunning(final int id) {
        return mHelper.isOperationRunning(id);
    }

    /**
     * Checks if an operation with given launch tag is running.
     *
     * @param tag a pre-cache key of the operation launch
     * @return {@code true} if the operation is running, {@code false} if it is not running, or
     * there was no operation launch with the tag at all
     */
    @SuppressWarnings("unused")
    @Contract(pure = true)
    public final boolean isOperationRunning(@NonNull final String tag) {
        return mHelper.isOperationRunning(tag);
    }
}
