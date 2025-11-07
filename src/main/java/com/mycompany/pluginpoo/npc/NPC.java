package com.mycompany.pluginpoo.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class NPC {

    protected String nombre;
    protected Location loc;

    public NPC(String nombre, Location loc) {
        this.nombre = nombre;
        this.loc = loc;
    }

    public String getNombre() {
        return nombre;
    }

    public Location getLocation() {
        return loc;
    }

    public abstract void interactuar(Player p);
}
