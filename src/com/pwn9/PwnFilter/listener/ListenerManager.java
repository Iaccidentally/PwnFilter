package com.pwn9.PwnFilter.listener;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handle Startup / Shutdown / Configuration of our listeners
 * User: ptoal
 * Date: 13-09-29
 * Time: 9:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListenerManager {

    private static ListenerManager _instance;

    private ConcurrentHashMap<FilterListener,Plugin> registeredListeners = new ConcurrentHashMap<FilterListener, Plugin>();

    private final PwnFilter plugin;

    private ListenerManager(PwnFilter plugin) {
        this.plugin = plugin;
    }

    public static ListenerManager getInstance(PwnFilter p) {
        if (_instance == null ) {
            _instance = new ListenerManager(p);
        }
        return _instance;
    }

    public static ListenerManager getInstance() throws IllegalStateException {
        if (_instance == null ) {
            throw new IllegalStateException("Listener Manager Not initialized!");
        }
        return _instance;
    }

    public List<FilterListener> getActiveListeners() {
        List<FilterListener> retVal = new ArrayList<FilterListener>();
        for (FilterListener f : registeredListeners.keySet()) {
            if (f.isActive()) retVal.add(f);
        }
        return retVal;
    }

    public Map<FilterListener,Plugin> getRegisteredListeners() {
        return registeredListeners;
    }

    public void enableListeners() {
        Configuration config = plugin.getConfig();

        for (FilterListener f : registeredListeners.keySet()) {
            f.activate(config);
        }

    }

    public void disableListeners() {
        for (FilterListener f : getActiveListeners()) {
            f.shutdown();
        }
    }

    /**
     * Add a listener to the PwnFilter ListenerManager.  This allows PwnFilter
     * to notify the listener when it should try to activate or shutdown.
     * PwnFilter will call the activate / shutdown methods when reloading
     * rules configs.
     *
     * The FilterListener must register *before* attempting to use any other
     * PwnFilter resources.
     *
     * @param f FilterListener instance
     * @param p Plugin that the listener belongs to.
     * @return True if the listener was added, false if it was already registered.
     */
    public boolean registerListener(FilterListener f, Plugin p) {
        if (registeredListeners.containsKey(f)) {
            return false; // Already Registered
        }
        registeredListeners.put(f,p);
        plugin.updateMetrics();
        return true;
    }

    /**
     * Remove a listener from the PwnFilter ListenerManager.
     * The listener will no longer be activated / deactivated when PwnFilter
     * reloads configs, rules, etc.
     * IMPORTANT: Before de-registering, the FilterListener must remove all
     * references to RuleSets.
     *
     * @param f FilterListener to remove.
     * @return true if the listener was previously registered and successfully
     * removed. False if it was not registered.
     */
    public boolean unregisterListener(FilterListener f) {
        if (registeredListeners.containsKey(f)) {
            registeredListeners.remove(f);
            plugin.updateMetrics();
            return true;
        } else {
            return false;
        }
    }

    public void unregisterListeners() {
        for (FilterListener f : registeredListeners.keySet() ) {
            f.shutdown();
            registeredListeners.remove(f);
            plugin.updateMetrics();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        disableListeners();
    }

}