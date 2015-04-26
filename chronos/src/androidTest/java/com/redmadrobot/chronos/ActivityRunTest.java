package com.redmadrobot.chronos;

import com.redmadrobot.chronos.mock.gui.SimpleMockActivity;
import com.redmadrobot.chronos.mock.operation.SimpleErrorOperation;
import com.redmadrobot.chronos.mock.operation.SimpleOperation;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import static com.redmadrobot.chronos.TestSettings.INPUT;
import static com.redmadrobot.chronos.TestSettings.SHORT_WAIT;
import static com.redmadrobot.chronos.util.TimingUtils.sleep;

/**
 * Test for operation runs in Activity.
 *
 * @author maximefimov
 */
public class ActivityRunTest extends AndroidTestCase {

    @SmallTest
    public void testNormalRun() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        activity.runSimple(INPUT);
        sleep();
        assertTrue(activity.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, activity.getResult()));
        assertNull(activity.getError());
    }

    @SmallTest
    public void testNormalRunRotate() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        activity.runSimple(INPUT);
        activity.stop();
        sleep();
        activity.start();
        assertTrue(activity.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, activity.getResult()));
        assertNull(activity.getError());
    }

    @SmallTest
    public void testErrorRun() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        activity.runErrorSimple();
        sleep();
        assertTrue(activity.getResultObtained() == 1);
        assertNotNull(SimpleErrorOperation.isExpectedException(activity.getError()));
        assertNull(activity.getResult());
    }

    @SmallTest
    public void testErrorRunRotate() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        activity.runErrorSimple();
        activity.stop();
        sleep();
        activity.start();
        assertTrue(activity.getResultObtained() == 1);
        assertNotNull(SimpleErrorOperation.isExpectedException(activity.getError()));
        assertNull(activity.getResult());
    }

    @SmallTest
    public void testCancelRun() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int runId = activity.runSimple(INPUT);
        final boolean cancelResult = activity.cancel(runId);
        assertTrue(cancelResult);
        sleep();
        assertFalse(activity.gotResult());
    }

    @SmallTest
    public void testCancelRunRotate() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int runId = activity.runSimple(INPUT);
        final boolean cancelResult = activity.cancel(runId);
        assertTrue(cancelResult);
        activity.stop();
        sleep();
        activity.start();
        assertFalse(activity.gotResult());
    }

    @SmallTest
    public void testCancelAfterRun() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int runId = activity.runSimple(INPUT);
        sleep();
        final boolean cancelResult = activity.cancel(runId);
        assertFalse(cancelResult);
        assertTrue(activity.gotResult());
    }

    @SmallTest
    public void testCancelWrongId() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int runId = activity.runSimple(INPUT);
        final int wrongId = -1 * runId;
        final boolean cancelResult = activity.cancel(wrongId);
        assertFalse(cancelResult);
        sleep();
        assertTrue(activity.gotResult());
    }

    @SmallTest
    public void testNormalRunTagged() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int firstRunId = activity.runSimpleTagged(INPUT);
        final int secondRunId = activity.runSimpleTagged(INPUT);
        assertTrue(firstRunId == secondRunId);
        sleep();
        assertTrue(activity.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, activity.getResult()));
        assertNull(activity.getError());
    }

    @SmallTest
    public void testNormalRunTaggedSequential() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int firstRunId = activity.runSimpleTagged(INPUT);
        sleep();
        assertTrue(activity.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, activity.getResult()));
        assertNull(activity.getError());

        final int secondRunId = activity.runSimpleTagged(INPUT);
        assertTrue(firstRunId != secondRunId);
        sleep();
        assertTrue(activity.getResultObtained() == 2);
        assertTrue(SimpleOperation.isTransform(INPUT, activity.getResult()));
        assertNull(activity.getError());
    }

    @SmallTest
    public void testNormalRunRotateTagged() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int firstRunId = activity.runSimpleTagged(INPUT);
        activity.stop();
        activity.start();
        final int secondRunId = activity.runSimpleTagged(INPUT);
        assertTrue(firstRunId == secondRunId);
        sleep();
        assertTrue(activity.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, activity.getResult()));
        assertNull(activity.getError());
    }

    @SmallTest
    public void testCancelRunTaggedById() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int runId = activity.runSimpleTagged(INPUT);
        final boolean cancelResult = activity.cancel(runId);
        assertTrue(cancelResult);
        sleep();
        assertFalse(activity.gotResult());
    }

    @SmallTest
    public void testCancelRunTaggedByTag() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        activity.runSimpleTagged(INPUT);
        final boolean cancelResult = activity.cancelTagged();
        assertTrue(cancelResult);
        sleep();
        assertFalse(activity.gotResult());
    }

    @SmallTest
    public void testRelaunchCancelledTaggedRunByTag() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int firstRunId = activity.runSimpleTagged(INPUT);
        final boolean cancelResult = activity.cancelTagged();
        assertTrue(cancelResult);

        final int secondRunId = activity.runSimpleTagged(INPUT);
        assertTrue(firstRunId != secondRunId);
        sleep();
        assertTrue(activity.getResultObtained() == 1);
        assertTrue(SimpleOperation.isTransform(INPUT, activity.getResult()));
        assertNull(activity.getError());
    }

    @SmallTest
    public void testRunningState() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int runId = activity.runSimple(INPUT);
        sleep(SHORT_WAIT);
        assertTrue(activity.isRunning(runId));
        sleep();
        assertFalse(activity.isRunning(runId));
    }

    @SmallTest
    public void testRunningStateRotate() {
        final SimpleMockActivity activity = new SimpleMockActivity();
        activity.start();
        final int runId = activity.runSimple(INPUT);
        activity.stop();
        sleep(SHORT_WAIT);
        activity.start();
        assertTrue(activity.isRunning(runId));
        activity.stop();
        sleep();
        activity.start();
        assertFalse(activity.isRunning(runId));
    }
}
