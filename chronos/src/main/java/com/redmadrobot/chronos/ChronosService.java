package com.redmadrobot.chronos;

import org.jetbrains.annotations.Contract;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import de.greenrobot.event.EventBus;

/**
 * An entity which runs operations.
 *
 * @author maximefimov
 */
final class ChronosService {

    @NonNull
    private final static ChronosService INSTANCE = new ChronosService();

    @NonNull
    private final AtomicInteger mLastOperationId = new AtomicInteger(0);

    @NonNull
    private final EventBus mEventBus = EventBus.getDefault();

    @NonNull
    private final ExecutorService mExecutorService = Executors.newCachedThreadPool();

    private ChronosService() {
    }

    @NonNull
    @Contract(pure = true)
    static ChronosService getInstance() {
        return INSTANCE;
    }

    /**
     * Runs operation, handling all the exceptions that may ne thrown while running.
     *
     * @param operation       an operation to be executed
     * @param operationResult an empty result object to be filled with business-logic content
     * @param <Output>        class of the result, returned by the Operations' {@code run} method
     */
    private static <Output> void silentRun(@NonNull final Operation<Output> operation,
            @NonNull final OperationResult<Output> operationResult) {
        try {
            final Output output = operation.run();
            operationResult.setOutput(output);
        } catch (Exception e) {
            operationResult.setException(e);
        }
    }

    /**
     * Creates a template object for storing operations' run result.
     *
     * @param operation       an operation to create a result for
     * @param broadcastResult {@code true} if the result should be broadcasted, {@code false}
     *                        otherwise
     * @param <Output>        class of the result, returned by the Operations' {@code run} method
     * @return an empty OperationResult without business-logic content
     */
    @NonNull
    @Contract(pure = true)
    private <Output> OperationResult<Output> createEmptyResult(
            @NonNull final Operation<Output> operation, final boolean broadcastResult) {
        final OperationResult<Output> operationResult;
        final Class<? extends OperationResult<Output>> resultClass = operation.getResultClass();
        try {
            operationResult = resultClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Can't create a new instance of " + resultClass.getName());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(resultClass.getName() + " constructor is not accessible");
        }
        operationResult.setId(mLastOperationId.incrementAndGet());
        operationResult.setOperation(operation);
        operationResult.setBroadcast(broadcastResult);

        return operationResult;
    }

    /**
     * Runs operation in background.
     *
     * @param operation       an operation to be executed
     * @param <Output>        class of the result, returned by the Operations' {@code run} method
     * @param broadcastResult {@code true} if the result should be broadcasted, {@code false}
     *                        otherwise
     * @return the unique id of the launch
     */
    final <Output> int runAsync(@NonNull final Operation<Output> operation,
            final boolean broadcastResult) {
        final OperationResult<Output> result = createEmptyResult(operation, broadcastResult);
        final int id = result.getId();

        RunningOperationStorage.getInstance().operationStarted(id, operation,
                mExecutorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        silentRun(operation, result);
                        mEventBus.post(result);
                        RunningOperationStorage.getInstance().operationFinished(id);
                    }
                }));
        return id;
    }

    /**
     * Runs operation in the same thread.
     *
     * @param operation       an operation to be executed
     * @param <Output>        class of the result, returned by the Operations' {@code run} method
     * @param broadcastResult {@code true} if the result should be broadcasted, {@code false}
     *                        otherwise
     * @return operations' result
     */
    @NonNull
    final <Output> OperationResult<Output> runSync(
            @NonNull final Operation<Output> operation, final boolean broadcastResult) {
        final OperationResult<Output> result = createEmptyResult(operation, broadcastResult);

        silentRun(operation, result);
        mEventBus.post(result);

        return result;
    }
}
