package com.mycompany.pluginpoo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ComandoProhibirEnd implements CommandExecutor {

    private final PLUGINPOO plugin;

    public ComandoProhibirEnd(PLUGINPOO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean nuevoEstado = !plugin.isEndProhibido();
        plugin.setEndProhibido(nuevoEstado);
        sender.sendMessage("✅ Mundo End ahora está " + (nuevoEstado ? "PROHIBIDO" : "PERMITIDO"));
        return true;
    }
}
