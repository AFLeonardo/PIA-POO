package com.mycompany.pluginpoo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;



public class ManejadorNpc implements Listener {

    private final Plugin plugin;


    private final Map<UUID, Location> npcUbicaciones = new HashMap<>();
  
    private final Map<UUID, Villager> npcInstancias = new HashMap<>();

    private final Map<UUID, Long> ultimoDialogo = new HashMap<>();


    private final Map<String, List<String>> dialogos = new HashMap<>();

    private final long DIALOG_COOLDOWN_MS = 5000; 

    public ManejadorNpc(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        cargarDialogos();

        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (npcUbicaciones.isEmpty()) return;

                List<UUID> keys = new ArrayList<>(npcUbicaciones.keySet());
                for (UUID id : keys) {
                    Location fixed = npcUbicaciones.get(id);
                    Villager npc = npcInstancias.get(id);

       
                    if (npc == null || !npc.isValid()) {
                        Villager encontrada = buscarVillagerPorUUID(id);
                        if (encontrada != null) {
                            npcInstancias.put(id, encontrada);
                            npc = encontrada;
                        } else {
                            npcUbicaciones.remove(id);
                            npcInstancias.remove(id);
                            ultimoDialogo.remove(id);
                            continue;
                        }
                    }

                 
                    npc.setAI(false);
                    npc.setGravity(false);
                    npc.setVelocity(new Vector(0, 0, 0));

                   
                    Location actual = npc.getLocation();
                    if (actual.distanceSquared(fixed) > 0.001 || actual.getYaw() != fixed.getYaw()) {
                        Location tele = fixed.clone();
                        tele.setYaw(fixed.getYaw());
                        tele.setPitch(fixed.getPitch());
                        npc.teleport(tele);
                    }

           
                    Player cercano = buscarJugadorCercano(fixed, 4.5);
                    if (cercano != null) {
                       
                        Location lookLoc = fixed.clone();
                        Vector dir = cercano.getLocation().toVector().subtract(fixed.toVector());
                        if (dir.lengthSquared() > 0.0001) {
                            lookLoc.setDirection(dir);
                            npc.teleport(lookLoc);
                        }

                        
                        long ahora = System.currentTimeMillis();
                        long last = ultimoDialogo.getOrDefault(id, 0L);
                        if (ahora - last >= DIALOG_COOLDOWN_MS) {
                            String tipo = (npc.getCustomName() != null) ? npc.getCustomName().replace("§e", "").toLowerCase() : "trabajador";
                            if (tipo.contains("trabajador")) tipo = "trabajador";
                            if (tipo.contains("policia")) tipo = "policia";

                            List<String> frases = dialogos.getOrDefault(tipo, dialogos.get("trabajador"));
                            String mensaje = frases.get(new Random().nextInt(frases.size()));

                            cercano.sendMessage("§e[" + npc.getCustomName() + "] §f" + mensaje);
                            cercano.playSound(npc.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
                            npc.getWorld().playSound(npc.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 0.6f, 1.0f);
                            ultimoDialogo.put(id, ahora);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 5L); 
    }

    private void cargarDialogos() {
        dialogos.put("trabajador", List.of(
                "¡Hola! Estoy trabajando duro hoy.",
                "¿Has visto mis herramientas?",
                "Necesito más madera para terminar el proyecto.",
                "¡Qué buen día para construir algo nuevo!"
        ));
        dialogos.put("policia", List.of(
                "Mantente fuera de problemas, ciudadano.",
                "Todo tranquilo por aquí.",
                "Si ves algo sospechoso, avísame.",
                "La seguridad es lo primero."
        ));
    }


    public void crearNpcConSkin(Player jugador, String tipo) {
        World mundo = jugador.getWorld();
        Location loc = jugador.getLocation().clone();

        Villager npc = (Villager) mundo.spawnEntity(loc, EntityType.VILLAGER);
        String nombre = "§e" + (tipo.equalsIgnoreCase("policia") ? "Policia" : "Trabajador");
        npc.setCustomName(nombre);
        npc.setCustomNameVisible(true);
        npc.setProfession(tipo.equalsIgnoreCase("policia") ? Villager.Profession.NITWIT : Villager.Profession.FARMER);
        npc.setAI(false);
        npc.setGravity(false);
        npc.setInvulnerable(true);
        npc.setCollidable(false);
        npc.setRemoveWhenFarAway(false);
        npc.setAware(false);

        UUID id = npc.getUniqueId();
        npcUbicaciones.put(id, loc.clone());
        npcInstancias.put(id, npc);
        ultimoDialogo.put(id, 0L);

        mundo.playSound(loc, Sound.ENTITY_VILLAGER_AMBIENT, 1.0f, 1.0f);
        jugador.playSound(loc, Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);

        jugador.sendMessage("§aNPC §e" + tipo + " §acreato y congelado en " +
                "X:" + String.format("%.1f", loc.getX()) +
                " Y:" + String.format("%.1f", loc.getY()) +
                " Z:" + String.format("%.1f", loc.getZ()));
    }


    @EventHandler
    public void alDaniar(EntityDamageEvent e) {
        if (e.getEntity() instanceof Villager v && npcUbicaciones.containsKey(v.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void alSerObjetivo(EntityTargetEvent e) {
        if (e.getTarget() instanceof Villager v && npcUbicaciones.containsKey(v.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void alInteractuar(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Villager v && npcUbicaciones.containsKey(v.getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§e" + v.getCustomName() + " §7dice: §a¡Buen día, viajero!");
            e.getPlayer().playSound(v.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
        }
    }


    private Player buscarJugadorCercano(Location loc, double radio) {
        Player masCercano = null;
        double min = radio;
        for (Player p : loc.getWorld().getPlayers()) {
            double d = p.getLocation().distance(loc);
            if (d < min) {
                min = d;
                masCercano = p;
            }
        }
        return masCercano;
    }

    private Villager buscarVillagerPorUUID(UUID id) {
        for (World w : Bukkit.getWorlds()) {
            for (Entity ent : w.getEntities()) {
                if (ent instanceof Villager && ent.getUniqueId().equals(id)) {
                    return (Villager) ent;
                }
            }
        }
        return null;
    }
}
