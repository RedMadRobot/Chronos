package com.redmadrobot.chronos_sample;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * A entity represents a Chronos sample.
 *
 * @author maximefimov
 */
public final class Sample {

    private final String mName;

    private final Class<? extends Activity> mActivityClass;

    public Sample(@NonNull final String name,
            @NonNull final Class<? extends Activity> activityClass) {
        mName = name;
        mActivityClass = activityClass;
    }

    @NonNull
    public final Class<? extends Activity> getActivityClass() {
        return mActivityClass;
    }

    @Override
    public String toString() {
        return mName;
    }
}
