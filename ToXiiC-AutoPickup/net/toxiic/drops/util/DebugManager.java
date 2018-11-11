package net.toxiic.drops.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DebugManager
{
  private static List<UUID> pluginDebuggers = new ArrayList();
  
  public static void clearDebuggers()
  {
    pluginDebuggers.clear();
  }
  
  public static boolean isDebugger(Player player)
  {
    return (player != null) && (pluginDebuggers.contains(player.getUniqueId()));
  }
  
  public static void sendMessage(String message)
  {
    List<Integer> removeIndexes;
    if (message != null)
    {
      message = ChatColor.translateAlternateColorCodes('&', message);
      
      removeIndexes = new ArrayList();
      for (int i = 0; i < pluginDebuggers.size(); i++)
      {
        UUID pluginDebugger = (UUID)pluginDebuggers.get(i);
        if (pluginDebugger != null)
        {
          Player debugger = Bukkit.getPlayer(pluginDebugger);
          if (debugger != null) {
            debugger.sendMessage(message.replace("<player>", debugger.getName()).replace("<uuid>", debugger.getUniqueId().toString()).replace("<health>", String.valueOf(debugger.getHealth()).replace("<hunger>", String.valueOf(debugger.getFoodLevel()))));
          } else {
            removeIndexes.add(Integer.valueOf(i));
          }
        }
        else
        {
          removeIndexes.add(Integer.valueOf(i));
        }
      }
      for (Integer integer : removeIndexes) {
        removeIndexes.remove(integer.intValue());
      }
    }
  }
  
  public static boolean toggleDebugger(UUID playerUUID)
  {
    if (playerUUID != null)
    {
      if (pluginDebuggers.contains(playerUUID))
      {
        pluginDebuggers.remove(playerUUID);
        return false;
      }
      pluginDebuggers.add(playerUUID);
      return true;
    }
    throw new NullPointerException("Player UUID is null!");
  }
}
