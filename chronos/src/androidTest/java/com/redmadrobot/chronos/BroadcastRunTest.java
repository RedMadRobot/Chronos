package com.redmadrobot.chronos;

import com.redmadrobot.chronos.mock.gui.SimpleMockActivity;
import com.redmadrobot.chronos.mock.gui.SimpleMockFragment;
import com.redmadrobot.chronos.mock.operation.SimpleOperation;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import static com.redmadrobot.chronos.TestSettings.INPUT;
import static com.redmadrobot.chronos.util.TimingUtils.sleep;

/**
 * Test for broadcast operation runs.
 *
 * @author maximefimov
 */
public class BroadcastRunTest extends AndroidTestCase {

    @SmallTest
    public void testNormalRunFromActivity() {
        final SimpleMockActivity firstActivity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        firstActivity.addFragment(fragment);
        final SimpleMockActivity secondActivity = new SimpleMockActivity();

        firstActivity.start();
        secondActivity.start();

        secondActivity.runBroadcast(INPUT);
        sleep();

        assertTrue(secondActivity.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, secondActivity.getResult()));
        assertNull(secondActivity.getError());

        assertFalse(secondActivity.gotBroadcastResult());

        assertFalse(firstActivity.gotResult());
        assertTrue(firstActivity.getBroadcastResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, firstActivity.getBroadcastResult()));
        assertNull(firstActivity.getBroadcastError());

        assertFalse(fragment.gotResult());
        assertTrue(fragment.getBroadcastResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, fragment.getBroadcastResult()));
        assertNull(fragment.getBroadcastError());
    }

    @SmallTest
    public void testNormalRunFromFragment() {
        final SimpleMockActivity firstActivity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        firstActivity.addFragment(fragment);
        final SimpleMockActivity secondActivity = new SimpleMockActivity();

        firstActivity.start();
        secondActivity.start();

        fragment.runBroadcast(INPUT);
        sleep();

        assertTrue(fragment.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, fragment.getResult()));
        assertNull(fragment.getError());

        assertFalse(fragment.gotBroadcastResult());

        assertFalse(firstActivity.gotResult());
        assertTrue(firstActivity.getBroadcastResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, firstActivity.getBroadcastResult()));
        assertNull(firstActivity.getBroadcastError());

        assertFalse(secondActivity.gotResult());
        assertTrue(secondActivity.getBroadcastResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, secondActivity.getBroadcastResult()));
        assertNull(secondActivity.getBroadcastError());
    }

    @SmallTest
    public void testCancelRun() {
        final SimpleMockActivity firstActivity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        firstActivity.addFragment(fragment);
        final SimpleMockActivity secondActivity = new SimpleMockActivity();

        firstActivity.start();
        secondActivity.start();

        final int runId = secondActivity.runBroadcast(INPUT);
        final boolean cancelResult = secondActivity.cancel(runId);
        assertTrue(cancelResult);
        sleep();

        assertFalse(secondActivity.gotResult());
        assertFalse(secondActivity.gotBroadcastResult());

        assertFalse(firstActivity.gotResult());
        assertFalse(firstActivity.gotBroadcastResult());

        assertFalse(fragment.gotResult());
        assertFalse(fragment.gotBroadcastResult());
    }


    @SmallTest
    public void testNormalRunFromActivityRotate() {
        final SimpleMockActivity firstActivity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        firstActivity.addFragment(fragment);
        final SimpleMockActivity secondActivity = new SimpleMockActivity();

        firstActivity.start();
        secondActivity.start();

        secondActivity.runBroadcast(INPUT);
        firstActivity.stop();
        sleep();

        assertTrue(secondActivity.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, secondActivity.getResult()));
        assertNull(secondActivity.getError());

        assertFalse(secondActivity.gotBroadcastResult());

        assertFalse(firstActivity.gotResult());
        assertFalse(firstActivity.gotBroadcastResult());

        assertFalse(fragment.gotResult());
        assertFalse(fragment.gotBroadcastResult());

        firstActivity.start();

        assertFalse(firstActivity.gotResult());
        assertTrue(firstActivity.getBroadcastResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, firstActivity.getBroadcastResult()));
        assertNull(firstActivity.getBroadcastError());

        assertFalse(fragment.gotResult());
        assertTrue(fragment.getBroadcastResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, fragment.getBroadcastResult()));
        assertNull(fragment.getBroadcastError());
    }
}
