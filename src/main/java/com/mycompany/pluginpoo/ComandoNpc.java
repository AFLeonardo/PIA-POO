package com.mycompany.pluginpoo;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

public class ComandoNpc implements CommandExecutor {

    private final PLUGINPOO plugin;

    public ComandoNpc(PLUGINPOO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo los jugadores pueden usar este comando.");
            return true;
        }

        Player jugador = (Player) sender;
        if (args.length != 1) {
            jugador.sendMessage("§eUso correcto: §7/npc <trabajador|policia>");
            return true;
        }

        String tipo = args[0].toLowerCase();
        if (!tipo.equals("trabajador") && !tipo.equals("policia")) {
            jugador.sendMessage("§cTipo inválido. Usa: trabajador o policia.");
            return true;
        }

     
        Location loc = jugador.getLocation();
        Villager aldeano = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        aldeano.setCustomName("§a" + tipo.substring(0, 1).toUpperCase() + tipo.substring(1));
        aldeano.setCustomNameVisible(true);
        aldeano.setProfession(tipo.equals("policia") ? Villager.Profession.ARMORER : Villager.Profession.FARMER);
        aldeano.setAI(true);

        jugador.sendMessage("§a✅ Aldeano tipo §e" + tipo + " §acreado correctamente.");

       
        new BukkitRunnable() {
            @Override
            public void run() {
                if (aldeano.isDead() || !aldeano.isValid()) {
                    cancel();
                    return;
                }

                double distancia = aldeano.getLocation().distance(jugador.getLocation());
                if (distancia < 4) {
                   
                    Location locJugador = jugador.getLocation();
                    Location locAldeano = aldeano.getLocation();
                    locAldeano.setDirection(locJugador.toVector().subtract(locAldeano.toVector()));
                    aldeano.teleport(locAldeano);

                 
                    String mensaje;
                    if (tipo.equals("policia")) {
                        mensaje = switch ((int) (Math.random() * 3)) {
                            case 0 -> "¡Detente ahí, ciudadano!";
                            case 1 -> "Todo en orden por aquí.";
                            default -> "Mantente fuera de problemas.";
                        };
                    } else {
                        mensaje = switch ((int) (Math.random() * 3)) {
                            case 0 -> "¡Hola! El trabajo en el campo nunca termina.";
                            case 1 -> "¿Quieres comprar algo fresco?";
                            default -> "El clima está perfecto para cosechar.";
                        };
                    }

                    jugador.sendMessage("§e[" + aldeano.getCustomName() + "]: §f" + mensaje);
                    jugador.playSound(jugador.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1f, 1f);
                }
            }
        }.runTaskTimer(plugin, 0L, 60L); 
        return true;
    }
}
