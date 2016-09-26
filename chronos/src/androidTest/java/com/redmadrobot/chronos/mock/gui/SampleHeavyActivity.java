package com.redmadrobot.chronos.mock.gui;

import com.redmadrobot.chronos.mock.operation.HeavyOperation;
import com.redmadrobot.chronos.mock.operation.HeavyOperationResult;


/**
 * Mock activity that does memory-consuming operations.
 *
 * @author maximefimov
 */
public final class SampleHeavyActivity extends MockActivity {

    private boolean isOutOfMemory = false;

    public boolean isOutOfMemory() {
        return isOutOfMemory;
    }

    public final int run() {
        return runOperation(new HeavyOperation());
    }

    @SuppressWarnings("UnusedDeclaration")
    public final void onOperationFinished(final HeavyOperationResult result) {
        isOutOfMemory = ((HeavyOperation) result.getOperation()).gotOutOfMemory();
    }
}
