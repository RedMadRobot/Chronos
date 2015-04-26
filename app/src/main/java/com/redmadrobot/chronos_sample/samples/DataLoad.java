package com.redmadrobot.chronos_sample.samples;

import com.redmadrobot.chronos.gui.activity.ChronoActivity;
import com.redmadrobot.chronos_sample.R;
import com.redmadrobot.chronos_sample.operations.SimpleOperation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

/**
 * A sample of how you can easily initiate your layout with data provided by some source, that takes
 * some time to give a response, such as remote server, or a database.
 *
 * @author maximefimov
 */
public final class DataLoad extends ChronoActivity {

    // a key by which the activity saves and restores already loaded data
    private final static String KEY_DATA = "data";

    // a tag which represents a group of operations that can't run simultaneously
    private final static String TAG_DATA_LOADING = "data_loading";

    private TextView mTextOutput;

    // a data that has to be loaded
    private String mData = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_load);

        mTextOutput = (TextView) findViewById(R.id.text_output);

        if (savedInstanceState != null) {
            //first of all, the activity tries to restored already loaded data
            mData = savedInstanceState.getString(KEY_DATA);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //after this point all pending OperationResults are delivered, so that you may be sure,
        // that all proper 'onOperationFinished' calls are done

        if (mData == null) {// if it is still no data
            // The activity launches a loading operations with a tag
            // so that if it comes to this point once again and the data is not loaded yet,
            // the next launch will be ignored.
            // That means, no matter now often user rotates the device,
            // only one operation with a given tag may be pending in a single moment of time.
            runOperation(new SimpleOperation(""), TAG_DATA_LOADING);
        } else {
            // If there is a data already, just show it;
            showData();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        // It's important to manually save loaded data, as for now Chronos doesn't have an built-in cache
        outState.putString(KEY_DATA, mData);
    }

    private void showData() {
        mTextOutput.setText("Data is '" + mData + "'");
    }

    public void onOperationFinished(final SimpleOperation.Result result) {
        if (result.isSuccessful()) {
            // After the activity got the data, it is being saved to a local variable.
            // The programmer should take care of saving it during activity destroy-recreation process.
            mData = result.getOutput();
            showData();
        } else {
            // Here the negative result is not stored, so it
            mTextOutput.setText(result.getErrorMessage());
        }
    }
}
