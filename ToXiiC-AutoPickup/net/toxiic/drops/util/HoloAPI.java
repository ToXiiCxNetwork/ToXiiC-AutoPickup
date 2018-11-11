package net.toxiic.drops.util;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;

import net.toxiic.drops.Drops;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class HoloAPI
{
  public static boolean clearHolograms()
  {
    if (hasHolographicDisplays()) {
      try
      {
        Hologram[] holograms = getHolograms();
        if (holograms != null) {
          for (Hologram hologram : holograms) {
            if (hologram != null) {
              hologram.delete();
            }
          }
        }
        return true;
      }
      catch (Exception ex) {}
    }
    return false;
  }
  
  public static boolean clearOldHolograms()
  {
    if (hasHolographicDisplays()) {
      try
      {
        Hologram[] holograms = getHolograms();
        if (holograms != null) {
          for (Hologram hologram : holograms) {
            if ((hologram != null) && (System.currentTimeMillis() - hologram.getCreationTimestamp() >= 1000L)) {
              hologram.delete();
            }
          }
        }
        return true;
      }
      catch (Exception ex) {}
    }
    return false;
  }
  
  public static Hologram createHologram(Location hologramLocation, String text)
  {
    if (hasHolographicDisplays()) {
      try
      {
        if (hologramLocation != null)
        {
          List<String> lines = new ArrayList();
          if (text == null) {
            text = "";
          }
          if (text.contains("\n"))
          {
            String[] textSplit = text.split("\n");
            for (String newText : textSplit) {
              lines.add(newText);
            }
          }
          else
          {
            lines.add(text);
          }
          lines = MessageUtil.replace(lines);
          return HolographicDisplaysAPI.createHologram(Drops.getInstance(), hologramLocation, (String[])lines.toArray(new String[lines.size()]));
        }
      }
      catch (Exception ex) {}
    }
    return null;
  }
  
  public static Hologram createHologram(Player player, Location hologramLocation, String... textArray)
  {
    if (player == null) {
      return createHologram(hologramLocation, (textArray != null) && (textArray.length > 0) ? textArray[0] : "");
    }
    if (hasHolographicDisplays()) {
      try
      {
        if (hologramLocation != null)
        {
          List<String> lines = new ArrayList();
          if (textArray == null) {
            textArray = new String[0];
          }
          for (String text : textArray) {
            if (text != null) {
              if (text.contains("\n"))
              {
                String[] textSplit = text.split("\n");
                for (String newText : textSplit) {
                  lines.add(newText);
                }
              }
              else
              {
                lines.add(text);
              }
            }
          }
          lines = MessageUtil.replace(lines);
          return HolographicDisplaysAPI.createIndividualHologram(Drops.getInstance(), hologramLocation, Arrays.asList(new Player[] { player }), lines.isEmpty() ? new String[] { "" } : (String[])lines.toArray(new String[lines.size()]));
        }
      }
      catch (Exception ex) {}
    }
    return null;
  }
  
  public static Hologram getHologram(Object objHologram)
  {
    if (hasHolographicDisplays()) {
      try
      {
        return (Hologram)objHologram;
      }
      catch (Exception ex) {}
    }
    return null;
  }
  
  public static Hologram[] getHolograms()
  {
    if (hasHolographicDisplays()) {
      try
      {
        return HolographicDisplaysAPI.getHolograms(Drops.getInstance());
      }
      catch (Exception ex) {}
    }
    return null;
  }
  
  public static boolean hasHolographicDisplays()
  {
    return (Bukkit.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) && (Drops.getInstance().getConfig().getBoolean("holograms", true));
  }
}
