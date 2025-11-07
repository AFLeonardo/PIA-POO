# 🧩 Documentación Técnica — Plugin PLUGINPOO

## 📖 Descripción general

**PLUGINPOO** es un plugin desarrollado para servidores **Spigot/Paper** que introduce funcionalidades de control del End, creación de NPCs (aldeanos con diálogos personalizados) y tareas automatizadas que permiten que los NPCs interactúen visualmente con los jugadores cercanos.

El proyecto está diseñado con enfoque modular, separando comandos, eventos y tareas en paquetes individuales para facilitar su mantenimiento y extensión.

---

## 🏗️ Estructura del proyecto

```
com.tunombre.pluginpoo
│
├── PLUGINPOO.java               # Clase principal
│
├── comandos/
│   ├── ComandoTp.java           # Implementa el comando /tp
│   ├── ComandoProhibirEnd.java  # Implementa el comando /prohibirend
│   └── ComandoNpc.java          # Implementa el comando /npc
│
├── eventos/
│   ├── EventoEnd.java           # Listener que bloquea el acceso al End
│   └── EventoGeneral.java       # Listener para otras interacciones (opcional)
│
└── tareas/
    └── TareaMirarJugadores.java # Tarea repetitiva que hace que los NPCs miren a los jugadores cercanos
```

---

## ⚙️ plugin.yml

```yaml
name: PLUGINPOO
main: com.tunombre.pluginpoo.PLUGINPOO
version: 1.0
api-version: 1.20
author: Erik
commands:
  tp:
    description: Teletransporta al jugador.
  prohibirend:
    description: Activa o desactiva el bloqueo del End.
  npc:
    description: Crea un aldeano NPC con diálogo.
```

---

## 🧠 Clase principal — `PLUGINPOO.java`

### Descripción
Clase base del plugin. Extiende `JavaPlugin` y gestiona la inicialización del sistema, el registro de comandos y eventos, y la ejecución de tareas automáticas.

### Atributos
```java
private static PLUGINPOO instancia;
private boolean endProhibido = false;
private final HashMap<UUID, String[]> aldeanosDialogo = new HashMap<>();
```

| Atributo | Tipo | Descripción |
|-----------|------|-------------|
| `instancia` | `PLUGINPOO` | Singleton del plugin para acceso global. |
| `endProhibido` | `boolean` | Indica si el acceso al End está bloqueado. |
| `aldeanosDialogo` | `HashMap<UUID, String[]>` | Guarda los diálogos asociados a cada aldeano. |

### Métodos principales

#### `onEnable()`
- Inicializa la instancia.
- Carga la configuración (`saveDefaultConfig()`).
- Registra comandos (`tp`, `prohibirend`, `npc`).
- Registra eventos (`this` y `EventoEnd`).
- Crea un aldeano de ejemplo (`crearAldeanoEjemplo()`).
- Inicia una tarea repetitiva para que los NPCs miren a los jugadores (`iniciarTareaMirarJugadores()`).

#### `onDisable()`
- Muestra un mensaje en consola al desactivar el plugin.

#### `crearAldeanoEjemplo()`
- Crea un aldeano NPC con nombre, IA desactivada, invulnerabilidad, y profesión.
- Asocia un diálogo de ejemplo en `aldeanosDialogo`.

#### `iniciarTareaMirarJugadores()`
- Crea una `BukkitRunnable` que hace que los aldeanos giren hacia el jugador más cercano cada ciertos ticks.

---

## 🧭 Comandos

### `/prohibirend`
**Clase:** `ComandoProhibirEnd.java`  
**Función:** Alterna el valor de `endProhibido` y notifica al jugador del nuevo estado.

**Ejemplo de uso:**
```
/prohibirend
→ "El End ahora está prohibido"
```

---

### `/npc`
**Clase:** `ComandoNpc.java`  
**Función:** Crea un aldeano con diálogo y lo agrega al registro `aldeanosDialogo`.  
Puede incluir argumentos para personalizar el nombre o el texto del diálogo.

**Ejemplo de uso:**
```
/npc Hola soy un aldeano parlante.
```

---

### `/tp`
**Clase:** `ComandoTp.java`  
**Función:** Teletransporta al jugador a una ubicación o a otro jugador.  
(Implementación depende del contexto del proyecto.)

**Ejemplo de uso:**
```
/tp <jugador>
```

---

## 🎧 Eventos

### `EventoEnd.java`
**Rol:** Bloquea la entrada al End cuando `endProhibido` es `true`.

**Ejemplo de comportamiento:**
```java
@EventHandler
public void onPortal(PlayerPortalEvent event) {
    if (event.getCause() == TeleportCause.END_PORTAL && plugin.isEndProhibido()) {
        event.setCancelled(true);
        event.getPlayer().sendMessage("§cEl End está prohibido actualmente.");
    }
}
```

---

### `EventoGeneral.java` *(opcional)*
Puede gestionar otras interacciones con NPCs:
- Clicks en aldeanos para mostrar diálogo.
- Mensajes personalizados al interactuar.

---

## ⏰ Tareas programadas

### `TareaMirarJugadores.java`

**Tipo:** `BukkitRunnable`  
**Función:** Recorre todos los NPCs creados y ajusta su orientación para mirar al jugador más cercano.

**Ciclo de ejecución:**
- Se ejecuta cada cierto número de ticks.
- Calcula la dirección hacia el jugador más próximo.
- Ajusta el `Yaw` y `Pitch` del aldeano.

---

## 🔄 Flujo general del plugin

```text
Servidor inicia
│
└──▶ PLUGINPOO.onEnable()
      │
      ├── Guarda instancia (Singleton)
      ├── Registra comandos y eventos
      ├── Crea NPC de ejemplo
      └── Inicia tarea de observación
           │
Jugador usa /prohibirend → cambia endProhibido
Jugador intenta entrar al End → EventoEnd bloquea si endProhibido = true
TareaMirarJugadores → hace que NPCs miren a jugadores cercanos
```

---

## 🧱 Arquitectura UML (simplificada)

```plaintext
+------------------+
|    JavaPlugin    |
+------------------+
         ^
         |
+------------------+
|    PLUGINPOO     |
+------------------+
| - instancia      |
| - endProhibido   |
| - aldeanosDialogo|
+------------------+
| + onEnable()     |
| + onDisable()    |
| + crearAldeano() |
| + iniciarTarea() |
+---------+--------+
          |
  -----------------------------
  |             |             |
  v             v             v
Comando*     Evento*     TareaMirarJugadores
```

---

## 🧩 Tabla de dependencias internas

| Componente | Tipo | Depende de | Descripción |
|-------------|------|-------------|--------------|
| `PLUGINPOO` | `JavaPlugin` | — | Núcleo del plugin. Registra todos los módulos. |
| `ComandoProhibirEnd` | `CommandExecutor` | `PLUGINPOO` | Modifica el estado `endProhibido`. |
| `ComandoNpc` | `CommandExecutor` | `PLUGINPOO` | Crea aldeanos con diálogo. |
| `ComandoTp` | `CommandExecutor` | `PLUGINPOO` | Teletransporta jugadores. |
| `EventoEnd` | `Listener` | `PLUGINPOO` | Cancela viajes al End si está prohibido. |
| `TareaMirarJugadores` | `BukkitRunnable` | `PLUGINPOO` | Hace que NPCs miren a jugadores cercanos. |

---

## 🧠 Guía para extender el plugin

### Añadir un nuevo comando
1. Crear una clase en `comandos/` que implemente `CommandExecutor`.
2. Registrar el comando en `plugin.yml`.
3. Registrar el ejecutor en `onEnable()`.

### Añadir un nuevo evento
1. Crear una clase que implemente `Listener`.
2. Anotar los métodos con `@EventHandler`.
3. Registrar el listener en `onEnable()` con:
   ```java
   getServer().getPluginManager().registerEvents(new TuEvento(), this);
   ```

### Añadir una nueva tarea programada
1. Crear una clase que extienda `BukkitRunnable`.
2. Implementar el método `run()`.
3. Ejecutar la tarea con:
   ```java
   new MiTarea(this).runTaskTimer(this, 0L, 20L);
   ```

---

## 🧩 Versión y compatibilidad

| Propiedad | Valor |
|------------|--------|
| Minecraft | 1.20+ |
| API | Spigot / Paper |
| Java | 17 o superior |
| Dependencias externas | Ninguna |

---

## ✍️ Autor

**Erick**  
Desarrollador de plugins Bukkit/Spigot.  
Proyecto educativo de Programación Orientada a Objetos (POO) con Minecraft.
