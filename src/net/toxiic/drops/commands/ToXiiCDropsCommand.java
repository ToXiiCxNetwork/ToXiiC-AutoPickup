package net.toxiic.drops.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.toxiic.drops.Drops;
import net.toxiic.drops.util.DebugManager;
import net.toxiic.drops.util.MessageUtil;

public class ToXiiCDropsCommand
  implements CommandExecutor
{
  private Drops plugin;
  
  public ToXiiCDropsCommand(Drops plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if (commandLabel.equalsIgnoreCase("toxiicpickup"))
    {
      if (!(sender instanceof Player))
      {
        sender.sendMessage("This command can only be run by a player!");
        return true;
      }
      Player p = (Player)sender;
      if (args.length == 1)
      {
        if (args[0].equalsIgnoreCase("reload"))
        {
          if (p.hasPermission("toxiicpickup.admin"))
          {
            this.plugin.reloadConfig();
            this.plugin.loadConfig();
            
            p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.reloadedconfig")));
          }
          else
          {
            p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.nopermission")));
          }
        }
        else if (args[0].equalsIgnoreCase("debug"))
        {
          if ((p.hasPermission("toxiicpickup.admin")) || (this.plugin.getDescription().getAuthors().contains(p.getName())))
          {
            if (DebugManager.toggleDebugger(p.getUniqueId())) {
              p.sendMessage(ChatColor.AQUA + "You are now debugging ToXiiCxAutoPickup.");
            } else {
              p.sendMessage(ChatColor.AQUA + "You are no longer debugging ToXiiCxAutoPickup.");
            }
          }
          else {
            p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.nopermission")));
          }
        }
        else if (p.hasPermission("toxiicpickup.admin"))
        {
          List<String> msgs = this.plugin.getConfig().getStringList("messages.help");
          for (String msg : msgs) {
            p.sendMessage(MessageUtil.replace(msg));
          }
        }
        else
        {
          p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.nopermission")));
        }
      }
      else if (p.hasPermission("toxiicpickup.admin"))
      {
        List<String> msgs = this.plugin.getConfig().getStringList("messages.help");
        for (String msg : msgs) {
          p.sendMessage(MessageUtil.replace(msg));
        }
      }
      else
      {
        p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.nopermission")));
      }
      return true;
    }
    return false;
  }
}
