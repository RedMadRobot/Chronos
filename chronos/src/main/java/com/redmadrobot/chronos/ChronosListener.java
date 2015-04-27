package com.redmadrobot.chronos;

import org.jetbrains.annotations.Contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * The interaction module which passes data and control between GUI elements and Chronos services.
 *
 * @author maximefimov
 */
final class ChronosListener {

    private final static boolean LOG_ENABLED = false;

    @NonNull
    private final static String LOG_TAG = ChronosListener.class.getSimpleName();

    private final int mId;

    @NonNull
    private final Map<String, Integer> mTaggedRequests = new HashMap<>();

    @NonNull
    private final List<Integer> mUntaggedRequests = new LinkedList<>();

    @NonNull
    private final List<SoftReference<OperationDelivery<?>>> mStoredResults = new LinkedList<>();

    private State mState = State.PAUSED;

    private Object mServiceListener;

    private enum State {
        PAUSED,
        RESUMED
    }

    private enum DeliveryMode {
        NORMAL,
        BROADCAST
    }

    /**
     * @param id a unique id that is bound to the instance being created
     * @see ChronosListenerManager#createListener()
     * @see ChronosListenerManager#getListener(int)
     */
    ChronosListener(final int id) {
        mId = id;
        EventBus.getDefault().register(this);
        logd("ServiceConnector with id=" + id + " was created");
    }

    /**
     * Checks if a method can be used as a callback to handle operation result.
     *
     * @param method      a method to be checked
     * @param resultClass an operation result class
     * @param methodName  a required method name
     * @return {@code true} if method can be used as a callback; {@code false} otherwise
     */
    @Contract(pure = true)
    private static boolean isCallback(@NonNull final Method method,
            @NonNull final Class<?> resultClass,
            @NonNull final String methodName) {
        if (method.getName().equals(methodName)) {
            if (method.getReturnType() == Void.TYPE) {
                final Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length == 1 && parameters[0].isAssignableFrom(resultClass)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return the id by which the instance can be restored via {@link ChronosListenerManager#getListener(int)}
     */
    @Contract(pure = true)
    public final int getId() {
        return mId;
    }

    /**
     * This method must be called by a bound GUI element when it passes its own onResume state.
     *
     * @param serviceListener a GUI element that wants to connect to Chronos via the
     *                        ServiceConnector. In fact, it could be any object, no matter it is an
     *                        Activity, Fragment, or something else, but Chronos is designed to work
     *                        with GUI elements.
     * @see #onPause()
     */
    public final void onResume(@NonNull final Object serviceListener) {
        logd("onResume");
        mServiceListener = serviceListener;
        mState = State.RESUMED;

        if (!mStoredResults.isEmpty()) {
            logd("has undelivered results");
            final List<SoftReference<OperationDelivery<?>>> oldResults = new ArrayList<>(
                    mStoredResults);
            mStoredResults.clear();
            for (SoftReference<OperationDelivery<?>> result : oldResults) {
                final OperationDelivery<?> delivery = result.get();
                if (delivery != null) {
                    deliverResult(delivery);
                }
            }
            logd("no more undelivered results");
        } else {
            logd("has no undelivered results");
        }
    }

    /**
     * This method must be called by a bound GUI element when it passes its own onPause state.
     *
     * @see #onResume(Object)
     */
    public final void onPause() {
        logd("onPause");
        mState = State.PAUSED;
        mServiceListener = null;
    }

    /**
     * This method is used to listening to results stream and operate with them. User should never
     * call this method manually.
     *
     * @param operationResult the result to process
     */
    @SuppressWarnings("unused")
    public final void onEventMainThread(@Nullable final ChronosOperationResult<?> operationResult) {
        if (operationResult == null) {
            //somehow the bus delivered us a null object, it should be ignored
            return;
        }
        final int operationId = operationResult.getId();

        DeliveryMode deliveryMode = null;

        if (!RunningOperationStorage.getInstance().isOperationCancelled(operationId)) {
            if (mTaggedRequests.containsValue(operationId)) {
                deliveryMode = DeliveryMode.NORMAL;
            } else if (mUntaggedRequests.contains(operationResult.getId())) {
                mUntaggedRequests.remove(Integer.valueOf(operationId));
                deliveryMode = DeliveryMode.NORMAL;
            } else if (operationResult.isBroadcast()) {
                deliveryMode = DeliveryMode.BROADCAST;
            }
        }

        if (deliveryMode != null) {
            final OperationDelivery<?> operationDelivery = new OperationDelivery<>(operationResult,
                    deliveryMode);
            logd("operation delivery: " + operationDelivery);
            onOperationFinished(operationDelivery);
        }
    }

    /**
     * Launches an operation in background thread.
     *
     * @param operation       an operation to be launched
     * @param broadcastResult {@code true} if the result should be broadcasted, {@code false}
     *                        otherwise
     * @return a unique launch id
     * @see #invoke(ChronosOperation, String, boolean)
     * @see #cancel(int, boolean)
     */
    public final int invoke(@NonNull final ChronosOperation<?> operation,
            final boolean broadcastResult) {
        logd("invoking untagged operation");
        final int id = ChronosService.getInstance().runAsync(operation, broadcastResult);
        mUntaggedRequests.add(id);
        return id;
    }

    /**
     * Launches an operation in background thread. If operation, launched with the same tag from the
     * same ServiceConnector is running, new operation launch will not be triggered.
     *
     * @param operation       an operation to be launched
     * @param tag             a pre-cache key of the launch
     * @param broadcastResult {@code true} if the result should be broadcasted, {@code false}
     *                        otherwise
     * @return a launch id, may be the same with the previous call of the method, if the operation
     * with the same tag is still running
     * @see #invoke(ChronosOperation, boolean)
     * @see #cancel(int, boolean)
     * @see #cancel(String, boolean)
     */
    public final int invoke(@NonNull final ChronosOperation<?> operation, @NonNull final String tag,
            final boolean broadcastResult) {
        logd("invoking tagged operation, tag=" + tag);
        final Integer savedId = mTaggedRequests.get(tag);
        if (savedId != null && isRunning(savedId)) {
            logd("operation with tag=" + tag + " is running, do nothing");
            return savedId;
        }

        logd("operation with tag=" + tag + " is not running, start it");
        final int id = ChronosService.getInstance().runAsync(operation, broadcastResult);
        mTaggedRequests.put(tag, id);
        return id;
    }

    /**
     * Cancels operation launch by its id. May not physically kill the background thread, but it is
     * guaranteed that the result of the operation will not be delivered to any ServiceConnector.
     *
     * @param id           an id of the operation launch that should be cancelled
     * @param mayInterrupt {@code true} if threads executing operations task should be interrupted;
     *                     otherwise, in-progress tasks are allowed to complete
     * @return {@code false} if the task could not be cancelled, typically because it has already
     * completed normally; {@code true} otherwise
     * @see #invoke(ChronosOperation, boolean)
     * @see #invoke(ChronosOperation, String, boolean)
     * @see #cancel(String, boolean)
     * @see Chronos#cancelAll(boolean)
     */
    public final boolean cancel(final int id, final boolean mayInterrupt) {
        //noinspection SimplifiableIfStatement
        if (mUntaggedRequests.contains(id) || mTaggedRequests.containsValue(id)) {
            return RunningOperationStorage.getInstance().cancel(id, mayInterrupt);
        } else {
            return false;
        }
    }

    /**
     * Cancels operation launch by its tag. May not physically kill the background thread, but it is
     * guaranteed that the result of the operation will not be delivered to any ServiceConnector.
     *
     * @param tag          a pre-cache key of the operation launch that should be cancelled
     * @param mayInterrupt {@code true} if threads executing operations task should be interrupted;
     *                     otherwise, in-progress tasks are allowed to complete
     * @return {@code false} if the task could not be cancelled, typically because it has already
     * completed normally or there is no running operation with a given tag; {@code true} otherwise
     * @see #invoke(ChronosOperation, boolean)
     * @see #invoke(ChronosOperation, String, boolean)
     * @see #cancel(int, boolean)
     * @see Chronos#cancelAll(boolean)
     */
    public final boolean cancel(@NonNull final String tag, final boolean mayInterrupt) {
        final Integer id = mTaggedRequests.get(tag);
        //noinspection SimplifiableIfStatement
        if (id != null) {
            return cancel(id, mayInterrupt);
        } else {
            return false;
        }
    }

    /**
     * Checks if an operation with given launch id is running.
     *
     * @param id an id of the operation launch
     * @return {@code true} if the operation is running, {@code false} otherwise
     */
    @Contract(pure = true)
    public final boolean isRunning(final int id) {
        return RunningOperationStorage.getInstance().isOperationRunning(id);
    }

    /**
     * Checks if an operation with given launch tag is running.
     *
     * @param tag a pre-cache key of the operation launch
     * @return {@code true} if the operation is running, {@code false} if it is not running, or
     * there was no operation launch with the tag at all
     */
    @Contract(pure = true)
    public final boolean isRunning(@NonNull final String tag) {
        final Integer id = mTaggedRequests.get(tag);
        //noinspection SimplifiableIfStatement
        if (id != null) {
            return isRunning(id);
        } else {
            return false;
        }
    }

    @Override
    @Contract(pure = true)
    public String toString() {
        return "ServiceConnector[id=" + getId() + "]";
    }

    /**
     * A dispatcher method which decides what to do with an operation result.
     *
     * @param operationResult an operation result which needs to be dispatched
     */
    private void onOperationFinished(@NonNull final OperationDelivery<?> operationResult) {
        logd("onOperationFinished " + operationResult);
        switch (mState) {
            case PAUSED:
                storeResult(operationResult);
                break;
            case RESUMED:
                deliverResult(operationResult);
                break;
            default:
                throw new IllegalStateException("Unknown state: " + mState);
        }
    }

    /**
     * Storing an operation result to use it later.
     *
     * @param operationDelivery an operation result to be stored
     * @see #deliverResult(OperationDelivery)
     */
    private void storeResult(@NonNull final OperationDelivery<?> operationDelivery) {
        logd("store delivery " + operationDelivery);
        mStoredResults.add(new SoftReference<OperationDelivery<?>>(operationDelivery));
    }

    /**
     * Delivers an operation result to the bound client.
     *
     * @param operationDelivery an operation result to be delivered
     * @see #onResume(Object)
     * @see #storeResult(OperationDelivery)
     */
    private void deliverResult(@NonNull final OperationDelivery<?> operationDelivery) {
        logd("deliver delivery " + operationDelivery);
        switch (operationDelivery.getDeliveryMode()) {
            case NORMAL:
                deliverResult(operationDelivery.getResult(), Chronos.OWN_CALLBACK_METHOD_NAME,
                        true);
                break;
            case BROADCAST:
                deliverResult(operationDelivery.getResult(), Chronos.BROADCAST_CALLBACK_METHOD_NAME,
                        false);
                break;
            default:
                break;
        }
    }

    /**
     * Call bound client methods to pass an operation result.
     *
     * @param operationResult  an operation result to be delivered
     * @param methodName       a name of a method that will be called
     * @param warnIfNoCallback {@code true} if a warning message should be posted to LogCat if there
     *                         is no suitable method in the bound client; {@code false} otherwise
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    private void deliverResult(@NonNull final ChronosOperationResult<?> operationResult,
            @NonNull final String methodName, final boolean warnIfNoCallback) {
        final Class listenerClass = mServiceListener.getClass();
        final Method[] listenerMethods = listenerClass.getMethods();

        Method callbackMethod = null;

        final Class resultClass = operationResult.getClass();
        for (Method method : listenerMethods) {
            if (isCallback(method, resultClass, methodName)) {
                callbackMethod = method;
                try {
                    callbackMethod.invoke(mServiceListener, operationResult);
                } catch (IllegalAccessException e) {
                    Log.w(LOG_TAG, Log.getStackTraceString(e));
                } catch (InvocationTargetException e) {
                    Log.w(LOG_TAG, Log.getStackTraceString(e));
                }
            }
        }

        if (warnIfNoCallback && callbackMethod == null) {
            Log.w(LOG_TAG,
                    "Operation result (id=" + operationResult.getId() + "; class=" + operationResult
                            .getClass().getName() + ") was obtained, but there is no method in "
                            + mServiceListener + " to get it"
            );
            Log.w(LOG_TAG, "Method should look like");
            Log.w(LOG_TAG,
                    "public void " + methodName + "(" + resultClass.getName()
                            + " result) {}"
            );
        }
    }

    /**
     * Logs debug message.
     *
     * @param message сообщение для вывода в лог.
     * @see #LOG_ENABLED
     */
    private void logd(@NonNull final String message) {
        if (LOG_ENABLED) {
            Log.d(LOG_TAG, this.toString() + " " + message);
        }
    }

    private final static class OperationDelivery<T> {

        @NonNull
        private final ChronosOperationResult<T> mResult;

        @NonNull
        private final DeliveryMode mDeliveryMode;

        private OperationDelivery(@NonNull final ChronosOperationResult<T> result,
                @NonNull final DeliveryMode deliveryMode) {
            mResult = result;
            mDeliveryMode = deliveryMode;
        }

        @NonNull
        @Contract(pure = true)
        public final ChronosOperationResult<T> getResult() {
            return mResult;
        }

        @NonNull
        @Contract(pure = true)
        public final DeliveryMode getDeliveryMode() {
            return mDeliveryMode;
        }
    }
}
