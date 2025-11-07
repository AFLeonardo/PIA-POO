package com.mycompany.pluginpoo;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ComandoTp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo los jugadores pueden usar este comando.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Uso: /tp <jugador>");
            return true;
        }

        Player jugador = (Player) sender;
        Player objetivo = Bukkit.getPlayer(args[0]);

        if (objetivo == null) {
            jugador.sendMessage("Jugador no encontrado.");
            return true;
        }

        jugador.teleport(objetivo.getLocation());
        jugador.sendMessage("Te has teletransportado a " + objetivo.getName());
        return true;
    }
}

