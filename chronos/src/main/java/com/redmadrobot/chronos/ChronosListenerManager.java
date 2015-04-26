package com.redmadrobot.chronos;

import org.jetbrains.annotations.Contract;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A controller which handles saving\restoration processes of GUI objects, and help them to connect
 * to Chronos.
 *
 * @author maximefimov
 */
final class ChronosListenerManager {

    private final static ChronosListenerManager INSTANCE = new ChronosListenerManager();

    private final AtomicInteger mNextConnectorId = new AtomicInteger(0);

    private final Map<Integer, ChronosListener> mListeners;

    private ChronosListenerManager() {
        mListeners = new HashMap<>();
    }

    @NonNull
    @Contract(pure = true)
    public static ChronosListenerManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new ServiceConnector instance. Do not call this method twice for a same object,
     * instead use {@link #getListener(int)} method.
     *
     * @return a created ServiceConnector
     */
    @NonNull
    public final synchronized ChronosListener createListener() {
        final int connectorId = mNextConnectorId.getAndIncrement();
        final ChronosListener result = new ChronosListener(connectorId);

        mListeners.put(connectorId, result);

        return result;
    }

    /**
     * Gets previously created ServiceConnector by its id.
     *
     * @param id an id of the saved ServiceConnector
     * @return a restored ServiceConnector, or a new one, if there is no saved instance with a given
     * id
     * @see {@link ChronosListener#getId()}
     * @see #createListener()
     */
    @NonNull
    public final synchronized ChronosListener getListener(final int id) {
        ChronosListener chronosListener = mListeners.get(id);
        if (chronosListener == null) {
            chronosListener = new ChronosListener(id);
            mListeners.put(id, chronosListener);
        }
        return chronosListener;
    }
}
