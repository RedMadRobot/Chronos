package com.redmadrobot.chronos.gui.fragment;

import com.redmadrobot.chronos.ChronosConnector;
import com.redmadrobot.chronos.Operation;
import com.redmadrobot.chronos.gui.ChronosConnectorWrapper;
import com.redmadrobot.chronos.gui.fragment.dialog.ChronoDialogFragment;
import com.redmadrobot.chronos.gui.fragment.dialog.ChronoSupportDialogFragment;

import org.jetbrains.annotations.Contract;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * A Fragment of support library that is connected to Chronos.
 *
 * @author maximefimov
 * @see ChronoDialogFragment
 * @see ChronoFragment
 * @see ChronoSupportDialogFragment
 */
@SuppressWarnings("unused")
public abstract class ChronoSupportFragment extends Fragment implements
        ChronosConnectorWrapper {

    private final ChronosConnector mConnector = new ChronosConnector();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnector.onCreate(this, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mConnector.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mConnector.onSaveInstanceState(outState);
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
