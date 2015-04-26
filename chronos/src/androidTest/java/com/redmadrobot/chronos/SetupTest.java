package com.redmadrobot.chronos;

import com.redmadrobot.chronos.mock.gui.MockActivity;
import com.redmadrobot.chronos.mock.gui.MockFragment;
import com.redmadrobot.chronos.mock.gui.SimpleMockActivity;
import com.redmadrobot.chronos.mock.gui.SimpleMockFragment;
import com.redmadrobot.chronos.mock.operation.SimpleOperation;

import junit.framework.Assert;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import static com.redmadrobot.chronos.TestSettings.INPUT;

/**
 * Test for test equipment.
 *
 * @author maximefimov
 */
public class SetupTest extends AndroidTestCase {

    @SmallTest
    public void testSimpleOperation() {
        final SimpleOperation operation = new SimpleOperation(INPUT);
        final String output = operation.run();
        Assert.assertTrue(SimpleOperation.isTransform(INPUT, output));
    }

    @SmallTest
    public void testActivityLifecycleNormal() {
        final MockActivity activity = new SimpleMockActivity();

        activity.start();
        activity.stop();
        activity.start();
        activity.stop();
        activity.start();
    }

    @SmallTest
    public void testActivityLifecycleInvalid1() {
        final MockActivity activity = new SimpleMockActivity();

        IllegalStateException exception = null;
        try {
            activity.stop();
        } catch (IllegalStateException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }

    @SmallTest
    public void testActivityLifecycleInvalid2() {
        final MockActivity activity = new SimpleMockActivity();

        IllegalStateException exception = null;
        try {
            activity.start();
            activity.start();
        } catch (IllegalStateException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }

    @SmallTest
    public void testActivityLifecycleInvalid3() {
        final MockActivity activity = new SimpleMockActivity();

        IllegalStateException exception = null;
        try {
            activity.start();
            activity.stop();
            activity.stop();
        } catch (IllegalStateException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }

    @SmallTest
    public void testActivityFragmentLifecycleNormal() {
        final MockActivity activity = new SimpleMockActivity();
        final MockFragment firstFragment = new SimpleMockFragment();
        final MockFragment secondFragment = new SimpleMockFragment();
        activity.addFragment(firstFragment);
        activity.addFragment(secondFragment);

        activity.start();
        activity.stop();
        activity.start();
        activity.stop();
        activity.start();
    }

    @SmallTest
    public void testActivityFragmentLifecycleInvalid1() {
        final MockActivity activity = new SimpleMockActivity();
        final MockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        IllegalStateException exception = null;
        try {
            activity.stop();
        } catch (IllegalStateException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }

    @SmallTest
    public void testActivityFragmentLifecycleInvalid2() {
        final MockActivity activity = new SimpleMockActivity();
        final MockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        IllegalStateException exception = null;
        try {
            activity.start();
            activity.start();
        } catch (IllegalStateException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }

    @SmallTest
    public void testActivityFragmentLifecycleInvalid3() {
        final MockActivity activity = new SimpleMockActivity();
        final MockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        IllegalStateException exception = null;
        try {
            activity.start();
            activity.stop();
            activity.stop();
        } catch (IllegalStateException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }
}
