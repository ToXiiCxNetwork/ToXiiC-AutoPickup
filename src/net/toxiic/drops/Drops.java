package net.toxiic.drops;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import net.toxiic.drops.commands.AutoSmeltCommand;
import net.toxiic.drops.commands.DropsCommand;
import net.toxiic.drops.commands.ToXiiCDropsCommand;
import net.toxiic.drops.events.EventListener;
import net.toxiic.drops.util.DebugManager;
import net.toxiic.drops.util.HoloAPI;
import net.toxiic.drops.util.MessageUtil;

public class Drops
  extends JavaPlugin
{
  public static String fullInv;
  public static String world;
  private static Drops plugin;
  public static List<String> allowedWorlds;
  public static List<UUID> playerSmelt = new ArrayList<UUID>();
  public static List<UUID> playerDrops = new ArrayList<UUID>();
  private static final Logger log = Logger.getLogger("Minecraft");
  
  public void onEnable()
  {
    plugin = this;
    if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
    	log.severe("WorldGuard Is Missing.");
    	getServer().getPluginManager().disablePlugin(this);
    }
    
    saveDefaultConfig();
    loadConfig();
    
    getCommand("autosmelt").setExecutor(new AutoSmeltCommand(this));
    getCommand("autopickup").setExecutor(new DropsCommand(this));
    getCommand("toxiicpickup").setExecutor(new ToXiiCDropsCommand(this));
    getServer().getPluginManager().registerEvents(new EventListener(), this);
    
    getServer().getScheduler().runTaskTimer(this, new Runnable()
    {
      public void run()
      {
        HoloAPI.clearOldHolograms();
      }
    }, 10L, 10L);
  }
  
  public void onDisable()
  {
    getServer().getScheduler().cancelTasks(this);
    HoloAPI.clearHolograms();
    MessageUtil.clearLastMessages();
    DebugManager.clearDebuggers();
    if (allowedWorlds != null) {
      allowedWorlds.clear();
    }
    playerSmelt.clear();
    playerDrops.clear();
    
    plugin = null;
  }
  
  public void loadConfig()
  {
    allowedWorlds = getConfig().getStringList("allowedWorlds");
    fullInv = getConfig().getString("messages.fullinv");
    world = getConfig().getString("world");
  }
  
  public static Drops getInstance()
  {
    return plugin;
  }
}
