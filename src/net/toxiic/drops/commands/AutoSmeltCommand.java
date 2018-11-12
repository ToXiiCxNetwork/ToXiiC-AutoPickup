package net.toxiic.drops.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.toxiic.drops.Drops;
import net.toxiic.drops.util.MessageUtil;

public class AutoSmeltCommand
  implements CommandExecutor
{
  private Drops plugin;
  
  public AutoSmeltCommand(Drops plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if (commandLabel.equalsIgnoreCase("autosmelt"))
    {
      if (!(sender instanceof Player))
      {
        sender.sendMessage("This command can only be run by a player!");
        return true;
      }
      Player p = (Player)sender;
      if (p.hasPermission("toxiicpickup.autosmelt"))
      {
        if (args.length == 1)
        {
          if (args[0].equalsIgnoreCase("on"))
          {
            if (!Drops.playerSmelt.contains(p.getUniqueId()))
            {
              Drops.playerSmelt.add(p.getUniqueId());
              p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.smelton")));
            }
            else
            {
              p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.smeltison")));
            }
          }
          else if (args[0].equalsIgnoreCase("off")) {
            if (Drops.playerSmelt.contains(p.getUniqueId()))
            {
              Drops.playerSmelt.remove(p.getUniqueId());
              p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.smeltoff")));
            }
            else
            {
              p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.smeltisoff")));
            }
          }
        }
        else
        {
          List<String> msgs = this.plugin.getConfig().getStringList("messages.help-smelt");
          for (String msg : msgs) {
            p.sendMessage(MessageUtil.replace(msg));
          }
        }
      }
      else {
        p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.nopermission")));
      }
      return true;
    }
    return false;
  }
}
