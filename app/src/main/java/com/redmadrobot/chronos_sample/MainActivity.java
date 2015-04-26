package com.redmadrobot.chronos_sample;

import com.redmadrobot.chronos_sample.samples.DataLoad;
import com.redmadrobot.chronos_sample.samples.DataLoadCancel;
import com.redmadrobot.chronos_sample.samples.SimpleRun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Home screen for the sample app.
 *
 * @author maximefimov
 */
public final class MainActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView sampleList = (ListView) findViewById(R.id.list_samples);

        final ListAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getSamples());

        sampleList.setAdapter(adapter);
        sampleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                showSample((Sample) parent.getAdapter().getItem(position));

            }
        });
    }

    private List<Sample> getSamples() {
        final List<Sample> samples = new ArrayList<>();

        samples.add(new Sample("Simple run", SimpleRun.class));
        samples.add(new Sample("Data loading", DataLoad.class));
        samples.add(new Sample("Data loading with cancel", DataLoadCancel.class));

        return samples;
    }

    private void showSample(@NonNull final Sample sample) {
        startActivity(new Intent(this, sample.getActivityClass()));
    }
}
