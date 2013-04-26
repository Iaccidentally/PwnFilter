package com.pwn9.PwnFilter.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.Bukkit;

/**
 * Burns a player to death.
 * TODO: Consider hooking this into the custom death message handler.
 */
public class Actionburn implements Action {
    // Message to apply to this burn action
    String messageString;

    public void init(String s)
    {
        messageString = PwnFilter.prepareMessage(s,"burnmsg");
    }

    public boolean execute(final PwnFilter plugin, final FilterState state ) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            public void run() {
                state.player.setFireTicks(5000);
                state.player.sendMessage(messageString);
                state.addLogMessage("Burned " + state.player.getName() + ": " + messageString);
            }
        });
        return true;
    }
}