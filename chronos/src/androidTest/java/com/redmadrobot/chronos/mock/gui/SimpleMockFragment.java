package com.redmadrobot.chronos.mock.gui;

import com.redmadrobot.chronos.mock.operation.SimpleErrorOperation;
import com.redmadrobot.chronos.mock.operation.SimpleOperation;
import com.redmadrobot.chronos.mock.operation.SimpleOperationResult;

import org.jetbrains.annotations.Contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Fragment that can do a pre-defined operations and report its status.
 *
 * @author maximefimov
 */
public final class SimpleMockFragment extends MockFragment {

    private final static String OPERATION_TAG = "fragment_operation_tag";

    private String mResult;

    private Exception mError;

    private String mBroadcastResult;

    private Exception mBroadcastError;

    private int mResultObtained = 0;

    private int mBroadcastResultObtained = 0;

    @Nullable
    public String getResult() {
        return mResult;
    }

    @Nullable
    public Exception getError() {
        return mError;
    }

    @Nullable
    public String getBroadcastResult() {
        return mBroadcastResult;
    }

    @Nullable
    public Exception getBroadcastError() {
        return mBroadcastError;
    }

    public int getBroadcastResultObtained() {
        return mBroadcastResultObtained;
    }

    public final int runSimple(@NonNull final String input) {
        return runOperation(new SimpleOperation(input));
    }

    public final int runBroadcast(@NonNull final String input) {
        return runOperationBroadcast(new SimpleOperation(input));
    }

    public final int runSimpleTagged(@NonNull final String input) {
        return runOperation(new SimpleOperation(input), OPERATION_TAG);
    }

    public final int runBroadcastTagged(@NonNull final String input) {
        return runOperationBroadcast(new SimpleOperation(input), OPERATION_TAG);
    }

    public final int runErrorSimple() {
        return runOperation(new SimpleErrorOperation());
    }

    public final int runErrorBroadcast() {
        return runOperationBroadcast(new SimpleErrorOperation());
    }

    public final boolean cancel(final int id) {
        return cancelOperation(id);
    }

    public final boolean cancelTagged() {
        return cancelOperation(OPERATION_TAG);
    }

    public final int getResultObtained() {
        return mResultObtained;
    }

    @Contract(pure = true)
    public final boolean gotResult() {
        return mResultObtained > 0;
    }

    @Contract(pure = true)
    public final boolean gotBroadcastResult() {
        return mBroadcastResultObtained > 0;
    }

    @SuppressWarnings("UnusedDeclaration")
    public final void onOperationFinished(final SimpleOperationResult result) {
        mResultObtained++;
        if (result.isSuccessful()) {
            mResult = result.getOutput();
        } else {
            mError = result.getException();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public final void onBroadcastOperationFinished(final SimpleOperationResult result) {
        mBroadcastResultObtained++;
        if (result.isSuccessful()) {
            mBroadcastResult = result.getOutput();
        } else {
            mBroadcastError = result.getException();
        }
    }
}
