package com.destroystokyo.paper.youcandfindpaper;

import java.util.List;

import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.world.entity.MobCategory;

import org.apache.logging.log4j.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.magmafoundation.magma.helpers.ConfigHelper;
import org.spigotmc.SpigotWorldConfig;

import static com.destroystokyo.paper.youcandfindpaper.PaperConfig.saveConfig;

public class PaperWorldConfig {

   private final String worldName;
   private final SpigotWorldConfig spigotConfig;
   private YamlConfiguration config;
   private boolean verbose;

   public boolean dummy = false; //Magma
   private boolean alreadyRegistered; //Magma

   public PaperWorldConfig(String worldName, SpigotWorldConfig spigotConfig) {
      this.worldName = worldName;
      this.spigotConfig = spigotConfig;
      this.config = PaperConfig.config;

      if (Strings.isBlank(worldName))
         dummy = true; //Magma

      alreadyRegistered = ConfigHelper.isPaperConfigAlreadyRegistered(worldName); //Magma

      init();
   }

   public void init() {
      this.config = PaperConfig.config; // grab updated reference
      this.verbose = config.getBoolean("verbose", true); //Magma
      log("-------- World Settings For [" + worldName + "] --------");
      PaperConfig.readConfig(PaperWorldConfig.class, this);
   }

   private void log(String s)
   {
      if ( verbose && !dummy && !alreadyRegistered ) //Magma
      {
         Bukkit.getLogger().info( s );
      }
   }

   private void set(String path, Object val) {
      config.set("world-settings.default." + path, val);
      if (config.get("world-settings." + worldName + "." + path) != null) {
         config.set("world-settings." + worldName + "." + path, val);
      }
   }

   private void remove(String path) {
      config.addDefault("world-settings.default." + path, null);
      set(path, null);
   }

   public void removeOldValues() {
      boolean needsSave = false;
        if (PaperConfig.version < 24) {
            needsSave = true;

            set("despawn-ranges.soft", null);
            set("despawn-ranges.hard", null);
        }

      if (needsSave) {
         saveConfig();
      }
   }

   private boolean getBoolean(String path, boolean def) {
      return this.getBoolean(path, def, true);
   }

   private boolean getBoolean(String path, boolean def, boolean setDefault) {
      if (setDefault) {
         config.addDefault("world-settings.default." + path, def);
      }
      return config.getBoolean("world-settings." + worldName + "." + path, config.getBoolean("world-settings.default." + path, def));
   }

   private double getDouble(String path, double def) {
      config.addDefault("world-settings.default." + path, def);
      return config.getDouble("world-settings." + worldName + "." + path, config.getDouble("world-settings.default." + path));
   }

   private int getInt(String path, int def) {
      return getInt(path, def, true);
   }

   private int getInt(String path, int def, boolean setDefault) {
      if (setDefault) {
         config.addDefault("world-settings.default." + path, def);
      }
      return config.getInt("world-settings." + worldName + "." + path, config.getInt("world-settings.default." + path, def));
   }

   private long getLong(String path, long def) {
      config.addDefault("world-settings.default." + path, def);
      return config.getLong("world-settings." + worldName + "." + path, config.getLong("world-settings.default." + path));
   }

   private float getFloat(String path, float def) {
      // TODO: Figure out why getFloat() always returns the default value.
      return (float) getDouble(path, (double) def);
   }

   private <T> List<T> getList(String path, List<T> def) {
      config.addDefault("world-settings.default." + path, def);
      return (List<T>) config.getList("world-settings." + worldName + "." + path, config.getList("world-settings.default." + path));
   }

   private String getString(String path, String def) {
      config.addDefault("world-settings.default." + path, def);
      return config.getString("world-settings." + worldName + "." + path, config.getString("world-settings.default." + path));
   }

   private <T extends Enum<T>> List<T> getEnumList(String path, List<T> def, Class<T> type) {
      config.addDefault("world-settings.default." + path, def.stream().map(Enum::name).collect(Collectors.toList()));
      return ((List<String>) (config.getList("world-settings." + worldName + "." + path, config.getList("world-settings.default." + path)))).stream().map(s -> Enum.valueOf(type, s)).collect(Collectors.toList());
   }
    public int fishingMinTicks;
    public int fishingMaxTicks;
    private void fishingTickRange() {
        fishingMinTicks = getInt("fishing-time-range.MinimumTicks", 100);
        fishingMaxTicks = getInt("fishing-time-range.MaximumTicks", 600);
        log("Fishing time ranges are between " + fishingMinTicks +" and " + fishingMaxTicks + " ticks");
    }

    public boolean nerfedMobsShouldJump;
    private void nerfedMobsShouldJump() {
        nerfedMobsShouldJump = getBoolean("spawner-nerfed-mobs-should-jump", false);
    }

    public final Reference2IntMap<MobCategory> softDespawnDistances = new Reference2IntOpenHashMap<>(MobCategory.values().length);
    public final Reference2IntMap<MobCategory> hardDespawnDistances = new Reference2IntOpenHashMap<>(MobCategory.values().length);
    private void despawnDistances() {
        if (PaperConfig.version < 24) {
            int softDistance = getInt("despawn-ranges.soft", 32, false); // 32^2 = 1024, Minecraft Default
            int hardDistance = getInt("despawn-ranges.hard", 128, false); // 128^2 = 16384, Minecraft Default
            for (MobCategory value : MobCategory.values()) {
                if (softDistance != 32) {
                    softDespawnDistances.put(value, softDistance);
                }
                if (hardDistance != 128) {
                    hardDespawnDistances.put(value, hardDistance);
                }
            }
        }
        for (MobCategory category : MobCategory.values()) {
            int softDistance = getInt("despawn-ranges." + category.getName() + ".soft", softDespawnDistances.getOrDefault(category, category.getNoDespawnDistance()));
            int hardDistance = getInt("despawn-ranges." + category.getName() + ".hard", hardDespawnDistances.getOrDefault(category, category.getDespawnDistance()));
            if (softDistance > hardDistance) {
                softDistance = hardDistance;
            }
            log("Mobs in " + category.getName() + " Despawn Ranges: Soft" + softDistance + " Hard: " + hardDistance);
            softDespawnDistances.put(category, softDistance);
            hardDespawnDistances.put(category, hardDistance);
        }
    }

    public boolean keepSpawnInMemory;
    private void keepSpawnInMemory() {
        keepSpawnInMemory = getBoolean("keep-spawn-loaded", true);
        log("Keep spawn chunk loaded: " + keepSpawnInMemory);
    }
}