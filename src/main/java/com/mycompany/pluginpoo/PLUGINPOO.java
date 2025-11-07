package com.mycompany.pluginpoo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class PLUGINPOO extends JavaPlugin implements Listener {

    private static PLUGINPOO instancia;
    private boolean endProhibido = false;

   
    private final HashMap<UUID, String[]> aldeanosDialogo = new HashMap<>();

    @Override
    public void onEnable() {
        instancia = this;
        saveDefaultConfig();

       
        getCommand("tp").setExecutor(new ComandoTp());
        getCommand("prohibirend").setExecutor(new ComandoProhibirEnd(this));
        getCommand("npc").setExecutor(new ComandoNpc(this));

        
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new EventoEnd(this), this);

       
        crearAldeanoEjemplo();

        
        iniciarTareaMirarJugadores();

        getLogger().info("✅ PLUGINPOO activado correctamente (usando aldeanos como NPCs).");
    }

    @Override
    public void onDisable() {
        getLogger().info("💾 PLUGINPOO desactivado correctamente.");
    }


    private void crearAldeanoEjemplo() {
        World mundo = Bukkit.getWorlds().get(0);
        Location loc = new Location(mundo, 0, 64, 0); 

        Villager aldeano = (Villager) mundo.spawnEntity(loc, EntityType.VILLAGER);
        aldeano.setCustomName("§aAldeano Sabio");
        aldeano.setCustomNameVisible(true);
        aldeano.setAI(false); 
        aldeano.setInvulnerable(true);
        aldeano.setProfession(Villager.Profession.LIBRARIAN);

        String[] dialogo = {
            "§eHola viajero, ¿qué te trae por aquí?",
            "§eHe oído rumores de un tesoro escondido al norte...",
            "§eTen cuidado con los zombis por la noche."
        };

        aldeanosDialogo.put(aldeano.getUniqueId(), dialogo);
    }

    private void iniciarTareaMirarJugadores() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player jugador : Bukkit.getOnlinePlayers()) {
                    for (UUID id : aldeanosDialogo.keySet()) {
                        Villager aldeano = (Villager) Bukkit.getEntity(id);
                        if (aldeano == null) continue;

                        double distancia = aldeano.getLocation().distance(jugador.getLocation());
                        if (distancia < 5) { // rango de detección
                            mirarJugador(aldeano, jugador);
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 10L); 
    }

    @EventHandler
    public void alMoverse(PlayerMoveEvent e) {
        Player jugador = e.getPlayer();
        for (UUID id : aldeanosDialogo.keySet()) {
            Villager aldeano = (Villager) Bukkit.getEntity(id);
            if (aldeano == null) continue;

            double distancia = aldeano.getLocation().distance(jugador.getLocation());
            if (distancia < 3) { // rango para hablar
                String[] dialogo = aldeanosDialogo.get(id);
                if (dialogo != null && dialogo.length > 0) {
                    int idx = (int) (Math.random() * dialogo.length);
                    jugador.sendMessage("§7[" + aldeano.getCustomName() + "§7] §f" + dialogo[idx]);
                }
            }
        }
    }

 
    private void mirarJugador(Villager aldeano, Player jugador) {
        Location locAldeano = aldeano.getLocation();
        Location locJugador = jugador.getLocation();

        Vector dir = locJugador.toVector().subtract(locAldeano.toVector()).normalize();
        locAldeano.setDirection(dir);
        aldeano.teleport(locAldeano);
    }

    public static PLUGINPOO getInstancia() {
        return instancia;
    }

    public boolean isEndProhibido() {
        return endProhibido;
    }

    public void setEndProhibido(boolean valor) {
        this.endProhibido = valor;
    }
}

