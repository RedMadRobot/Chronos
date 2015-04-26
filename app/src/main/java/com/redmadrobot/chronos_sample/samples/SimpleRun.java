package com.redmadrobot.chronos_sample.samples;

import com.redmadrobot.chronos.gui.activity.ChronoActivity;
import com.redmadrobot.chronos_sample.R;
import com.redmadrobot.chronos_sample.operations.SimpleOperation;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A sample of how to use Chronos in a most minimalistic way.
 *
 * @author maximefimov
 */
public final class SimpleRun extends ChronoActivity {

    private TextView mTextOutput;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_run);

        final EditText editInput = (EditText) findViewById(R.id.edit_input);
        final View startView = findViewById(R.id.button_start);
        mTextOutput = (TextView) findViewById(R.id.text_output);

        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // call the 'runOperation' method and it will begin executing of the operation
                // in background thread, so it will not block your GUI.
                runOperation(new SimpleOperation(editInput.getText().toString()));
                // after run is started, you can rotate, or put the sample to background
                // but the result of the operation will be delivered to an 'onOperationFinished' method
                // when the app goes to foreground once again.
            }
        });
    }

    //most IDEs may assume this method as 'not-used', which is a side effect of Chronos software design,
    //so may want to suppress the warning to not get confused
    public void onOperationFinished(final SimpleOperation.Result result) {
        //Here you process the result
        if (result
                .isSuccessful()) { // this case happens when no exception was thrown during the operation run
            mTextOutput.setText(result.getOutput());
        } else { // this happens if there was an exception
            mTextOutput.setText(result.getErrorMessage());
        }
    }
}
