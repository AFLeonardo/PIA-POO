package com.mycompany.pluginpoo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class EventoEnd implements Listener {

    private final PLUGINPOO plugin;

    public EventoEnd(PLUGINPOO plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void alEntrarPortal(PlayerPortalEvent e) {
        if (e.getTo() != null && e.getTo().getWorld().getName().toLowerCase().contains("the_end")) {
            if (plugin.isEndProhibido()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(" No puedes ir al End en este momento.");
            }
        }
    }
}
