package com.redmadrobot.chronos.gui.fragment.dialog;

import com.redmadrobot.chronos.ChronosConnector;
import com.redmadrobot.chronos.Operation;
import com.redmadrobot.chronos.gui.ChronosConnectorWrapper;
import com.redmadrobot.chronos.gui.fragment.ChronoFragment;
import com.redmadrobot.chronos.gui.fragment.ChronoSupportFragment;

import org.jetbrains.annotations.Contract;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * A DialogFragment that is connected to Chronos.
 *
 * @author maximefimov
 * @see ChronoFragment
 * @see ChronoSupportDialogFragment
 * @see ChronoSupportFragment
 */
@SuppressWarnings("unused")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class ChronoDialogFragment extends DialogFragment implements
        ChronosConnectorWrapper {

    private final ChronosConnector mConnector = new ChronosConnector();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnector.onCreate(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mConnector.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mConnector.onResume();
    }

    @Override
    public void onPause() {
        mConnector.onPause();
        super.onPause();
    }

    @Override
    public final int runOperation(@NonNull final Operation operation,
            @NonNull final String tag) {
        return mConnector.runOperation(operation, tag, false);
    }

    @Override
    public final int runOperation(@NonNull final Operation operation) {
        return mConnector.runOperation(operation, false);
    }

    @Override
    public final int runOperationBroadcast(@NonNull final Operation operation,
            @NonNull final String tag) {
        return mConnector.runOperation(operation, tag, true);
    }

    @Override
    public final int runOperationBroadcast(@NonNull final Operation operation) {
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
    @Contract(pure = true)
    public final boolean isOperationRunning(final int id) {
        return mConnector.isOperationRunning(id);
    }

    @Override
    @Contract(pure = true)
    public final boolean isOperationRunning(@NonNull final String tag) {
        return mConnector.isOperationRunning(tag);
    }
}
