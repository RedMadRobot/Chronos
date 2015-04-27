package com.redmadrobot.chronos_sample.samples;

import com.redmadrobot.chronos.gui.activity.ChronosActivity;
import com.redmadrobot.chronos_sample.R;
import com.redmadrobot.chronos_sample.operations.SimpleOperation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An sample of it is possible to cancell a running operation.
 *
 * @author maximefimov
 */
public final class DataLoadCancel extends ChronosActivity {

    private final static String KEY_DATA = "data";

    private final static String TAG_DATA_LOADING = "data_loading";

    private TextView mTextOutput;

    private String mData = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_load_cancel);

        mTextOutput = (TextView) findViewById(R.id.text_output);

        if (savedInstanceState != null) {
            mData = savedInstanceState.getString(KEY_DATA);
        }

        final View cancelView = findViewById(R.id.button_cancel);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // in any moment of time you can try to cancel a running operation
                // though the run may not be cancelled, e.g. the run has been finished already, or never been issued at all
                final boolean cancelResult = cancelOperation(TAG_DATA_LOADING);
                if (cancelResult) {
                    mTextOutput.setText("Operation launch is cancelled");
                } else {
                    showToast("Can't cancel operation launch");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mData == null) {
            runOperation(new SimpleOperation(""), TAG_DATA_LOADING);
        } else {
            showData();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_DATA, mData);
    }

    private void showData() {
        mTextOutput.setText("Data is '" + mData + "'");
    }

    // if Operations run is cancelled, there would be no call to this method, thus you should handle
    // cancelling in the place it happened
    public void onOperationFinished(final SimpleOperation.Result result) {
        if (result.isSuccessful()) {
            mData = result.getOutput();
            showData();
        } else {
            mTextOutput.setText(result.getErrorMessage());
        }
    }

    private void showToast(@NonNull final String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
