package com.redmadrobot.chronos.gui.activity;

import com.redmadrobot.chronos.ChronosConnector;
import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.gui.ChronosConnectorWrapper;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * An Activity that is connected to Chronos.
 *
 * @author maximefimov
 * @see ChronosSupportActivity
 */
@SuppressWarnings("unused")
public abstract class ChronosActivity extends Activity implements ChronosConnectorWrapper {

    /**
     * An entry point to access Chronos functions.
     */
    private final ChronosConnector mConnector = new ChronosConnector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnector.onCreate(this, savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnector.onResume();
    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        mConnector.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        mConnector.onPause();
        super.onPause();
    }

    @Override
    public final int runOperation(@NonNull final ChronosOperation operation,
            @NonNull final String tag) {
        return mConnector.runOperation(operation, tag, false);
    }

    @Override
    public final int runOperation(@NonNull final ChronosOperation operation) {
        return mConnector.runOperation(operation, false);
    }

    @Override
    public final int runOperationBroadcast(@NonNull final ChronosOperation operation,
            @NonNull final String tag) {
        return mConnector.runOperation(operation, tag, true);
    }

    @Override
    public final int runOperationBroadcast(@NonNull final ChronosOperation operation) {
        return mConnector.runOperation(operation, true);
    }

    @Override
    public final boolean cancelOperation(final int id) {
        return mConnector.cancelOperation(id, true);
    }

    @Override
    public final boolean cancelOperation(@NonNull final String tag) {
        return mConnector.cancelOperation(tag, true);
    }

    @Override
    public final boolean isOperationRunning(final int id) {
        return mConnector.isOperationRunning(id);
    }

    @Override
    public final boolean isOperationRunning(@NonNull final String tag) {
        return mConnector.isOperationRunning(tag);
    }
}
