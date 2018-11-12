package net.toxiic.drops.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.toxiic.drops.Drops;

public class MessageUtil
{
  private static Map<UUID, Long> lastMessage = new HashMap();
  
  public static boolean canSendMessage(Player player)
  {
    return (player != null) && ((!lastMessage.containsKey(player.getUniqueId())) || (System.currentTimeMillis() - ((Long)lastMessage.get(player.getUniqueId())).longValue() >= 1000L));
  }
  
  public static void clearLastMessages()
  {
    lastMessage.clear();
  }
  
  public static void clearMessage(Player player)
  {
    if (player != null) {
      lastMessage.remove(player.getUniqueId());
    }
  }
  
  public static String replace(String msg)
  {
    return msg != null ? ChatColor.translateAlternateColorCodes('&', msg) : "";
  }
  
  public static List<String> replace(List<String> lines)
  {
    if (lines != null) {
      for (int i = 0; i < lines.size(); i++) {
        lines.set(i, replace((String)lines.get(i)));
      }
    }
    return lines;
  }
  
  public static void sendMessage(Player player, String message)
  {
    if (player != null)
    {
      if (lastMessage.containsKey(player.getUniqueId()))
      {
        Long lastMsg = (Long)lastMessage.get(player.getUniqueId());
        if (lastMsg != null)
        {
          if (System.currentTimeMillis() - lastMsg.longValue() >= 1000L) {
            lastMessage.remove(player.getUniqueId());
          }
        }
        else {
          lastMessage.remove(player.getUniqueId());
        }
      }
      if ((!lastMessage.containsKey(player.getUniqueId())) && 
        (Drops.getInstance().getConfig().getBoolean("chat", true)))
      {
        player.sendMessage(replace(message));
        lastMessage.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
      }
    }
  }
}
