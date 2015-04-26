package com.redmadrobot.chronos;

import com.redmadrobot.chronos.mock.gui.SampleHeavyActivity;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.LinkedList;
import java.util.List;

import static com.redmadrobot.chronos.util.TimingUtils.sleep;

/**
 * Test for proper memory management.
 *
 * @author maximefimov
 */
public class MemoryTest extends AndroidTestCase {

    @SmallTest
    public void testSingleResultFits() {
        final SampleHeavyActivity activity = new SampleHeavyActivity();
        activity.start();
        activity.run();
        sleep();
        assertFalse(activity.isOutOfMemory());
    }

    @MediumTest
    public void testMultipleResultFits() {
        final List<SampleHeavyActivity> activities = new LinkedList<>();
        final int activityCount = 100;
        for (int i = 0; i < activityCount; i++) {
            activities.add(new SampleHeavyActivity());
        }

        for (final SampleHeavyActivity activity : activities) {
            activity.start();
            activity.run();
            sleep(TestSettings.SHORT_WAIT);
            activity.stop();
        }

        sleep();

        for (final SampleHeavyActivity activity : activities) {
            activity.start();
            assertFalse(activity.isOutOfMemory());
        }
    }
}
