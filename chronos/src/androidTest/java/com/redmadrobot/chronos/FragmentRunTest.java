package com.redmadrobot.chronos;

import com.redmadrobot.chronos.mock.gui.SimpleMockActivity;
import com.redmadrobot.chronos.mock.gui.SimpleMockFragment;
import com.redmadrobot.chronos.mock.operation.SimpleErrorOperation;
import com.redmadrobot.chronos.mock.operation.SimpleOperation;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import static com.redmadrobot.chronos.TestSettings.INPUT;
import static com.redmadrobot.chronos.util.TimingUtils.sleep;

/**
 * Test for operation runs in Fragment.
 *
 * @author maximefimov
 */
public class FragmentRunTest extends AndroidTestCase {

    @SmallTest
    public void testNormalRun() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        fragment.runSimple(INPUT);
        sleep();
        assertTrue(fragment.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, fragment.getResult()));
        assertNull(fragment.getError());
    }

    @SmallTest
    public void testNormalRunRotate() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        fragment.runSimple(INPUT);
        activity.stop();
        sleep();
        activity.start();
        assertTrue(fragment.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, fragment.getResult()));
        assertNull(fragment.getError());
    }

    @SmallTest
    public void testErrorRun() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        fragment.runErrorSimple();
        sleep();
        assertTrue(fragment.getResultObtained() == 1);
        assertNotNull(SimpleErrorOperation.isExpectedException(fragment.getError()));
        assertNull(fragment.getResult());
    }

    @SmallTest
    public void testErrorRunRotate() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        fragment.runErrorSimple();
        activity.stop();
        sleep();
        activity.start();
        assertTrue(fragment.getResultObtained() == 1);
        assertNotNull(SimpleErrorOperation.isExpectedException(fragment.getError()));
        assertNull(fragment.getResult());
    }

    @SmallTest
    public void testCancelRun() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        final int runId = fragment.runSimple(INPUT);
        final boolean cancelResult = fragment.cancel(runId);
        assertTrue(cancelResult);
        sleep();
        assertFalse(fragment.gotResult());
    }

    @SmallTest
    public void testCancelRunRotate() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        final int runId = fragment.runSimple(INPUT);
        final boolean cancelResult = fragment.cancel(runId);
        assertTrue(cancelResult);
        activity.stop();
        sleep();
        activity.start();
        assertFalse(fragment.gotResult());
    }

    @SmallTest
    public void testCancelAfterRun() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        final int runId = fragment.runSimple(INPUT);
        sleep();
        final boolean cancelResult = fragment.cancel(runId);
        assertFalse(cancelResult);
        assertTrue(fragment.gotResult());
    }

    @SmallTest
    public void testCancelWrongId() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        final int runId = fragment.runSimple(INPUT);
        final int wrongId = -1 * runId;
        final boolean cancelResult = fragment.cancel(wrongId);
        assertFalse(cancelResult);
        sleep();
        assertTrue(fragment.gotResult());
    }

    @SmallTest
    public void testNormalRunTagged() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        final int firstRunId = fragment.runSimpleTagged(INPUT);
        final int secondRunId = fragment.runSimpleTagged(INPUT);
        assertTrue(firstRunId == secondRunId);
        sleep();
        assertTrue(fragment.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, fragment.getResult()));
        assertNull(fragment.getError());
    }

    @SmallTest
    public void testNormalRunTaggedSequential() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        final int firstRunId = fragment.runSimpleTagged(INPUT);
        sleep();
        assertTrue(fragment.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, fragment.getResult()));
        assertNull(fragment.getError());

        final int secondRunId = fragment.runSimpleTagged(INPUT);
        assertTrue(firstRunId != secondRunId);
        sleep();
        assertTrue(fragment.getResultObtained() == 2);
        assertTrue(SimpleOperation.isTransform(INPUT, fragment.getResult()));
        assertNull(fragment.getError());
    }

    @SmallTest
    public void testNormalRunRotateTagged() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        final int firstRunId = fragment.runSimpleTagged(INPUT);
        activity.stop();
        activity.start();
        final int secondRunId = fragment.runSimpleTagged(INPUT);
        assertTrue(firstRunId == secondRunId);
        sleep();
        assertTrue(fragment.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, fragment.getResult()));
        assertNull(fragment.getError());
    }

    @SmallTest
    public void testCancelRunTaggedById() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        final int runId = fragment.runSimpleTagged(INPUT);
        final boolean cancelResult = fragment.cancel(runId);
        assertTrue(cancelResult);
        sleep();
        assertFalse(fragment.gotResult());
    }

    @SmallTest
    public void testCancelRunTaggedByTag() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        fragment.runSimpleTagged(INPUT);
        final boolean cancelResult = fragment.cancelTagged();
        assertTrue(cancelResult);
        sleep();
        assertFalse(fragment.gotResult());
    }

    @SmallTest
    public void testRelaunchCancelledTaggedRunByTag() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        final SimpleMockFragment fragment = new SimpleMockFragment();
        activity.addFragment(fragment);

        activity.start();
        final int firstRunId = fragment.runSimpleTagged(INPUT);
        final boolean cancelResult = fragment.cancelTagged();
        assertTrue(cancelResult);

        final int secondRunId = fragment.runSimpleTagged(INPUT);
        assertTrue(firstRunId != secondRunId);
        sleep();
        assertTrue(fragment.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, fragment.getResult()));
        assertNull(fragment.getError());
    }
}
