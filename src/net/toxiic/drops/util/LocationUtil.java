package net.toxiic.drops.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationUtil
{
  public static Location getLocationInfront(Player player)
  {
    String dir = getCardinalDirection(player);
    Location loc = player.getEyeLocation().clone().add(0.0D, 0.15D, 0.0D);
    switch (dir)
    {
    case "N": 
      loc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() - 1.0D, loc.getYaw(), loc.getPitch());
      break;
    case "NE": 
      loc = new Location(loc.getWorld(), loc.getX() + 1.0D, loc.getY(), loc.getZ() - 1.0D, loc.getYaw(), loc.getPitch());
      break;
    case "E": 
      loc = new Location(loc.getWorld(), loc.getX() + 1.0D, loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
      break;
    case "SE": 
      loc = new Location(loc.getWorld(), loc.getX() + 1.0D, loc.getY(), loc.getZ() + 1.0D, loc.getYaw(), loc.getPitch());
      break;
    case "S": 
      loc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + 1.0D, loc.getYaw(), loc.getPitch());
      break;
    case "SW": 
      loc = new Location(loc.getWorld(), loc.getX() - 1.0D, loc.getY(), loc.getZ() + 1.0D, loc.getYaw(), loc.getPitch());
      break;
    case "W": 
      loc = new Location(loc.getWorld(), loc.getX() - 1.0D, loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
      break;
    case "NW": 
      loc = new Location(loc.getWorld(), loc.getX() - 1.0D, loc.getY(), loc.getZ() - 1.0D, loc.getYaw(), loc.getPitch());
      break;
    }
    return loc;
  }
  
  public static String getCardinalDirection(Player player)
  {
    double rotation = (player.getEyeLocation().getYaw() - 90.0F) % 360.0F;
    if (rotation < 0.0D) {
      rotation += 360.0D;
    }
    if ((rotation >= 0.0D) && (rotation < 22.5D)) {
      return "N";
    }
    if ((rotation >= 22.5D) && (rotation < 67.5D)) {
      return "NE";
    }
    if ((rotation >= 67.5D) && (rotation < 112.5D)) {
      return "E";
    }
    if ((rotation >= 112.5D) && (rotation < 157.5D)) {
      return "SE";
    }
    if ((rotation >= 157.5D) && (rotation < 202.5D)) {
      return "S";
    }
    if ((rotation >= 202.5D) && (rotation < 247.5D)) {
      return "SW";
    }
    if ((rotation >= 247.5D) && (rotation < 292.5D)) {
      return "W";
    }
    if ((rotation >= 292.5D) && (rotation < 337.5D)) {
      return "NW";
    }
    if ((rotation >= 337.5D) && (rotation < 360.0D)) {
      return "N";
    }
    return null;
  }
}
