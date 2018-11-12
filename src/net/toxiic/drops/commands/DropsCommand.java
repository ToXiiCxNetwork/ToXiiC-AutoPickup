package net.toxiic.drops.commands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.toxiic.drops.Drops;
import net.toxiic.drops.util.DebugManager;
import net.toxiic.drops.util.MessageUtil;

public class DropsCommand
  implements CommandExecutor
{
  private Drops plugin;
  
  public DropsCommand(Drops plugin)
  {
    this.plugin = plugin;
  }
  
  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if (commandLabel.equalsIgnoreCase("autopickup"))
    {
      if (!(sender instanceof Player))
      {
        sender.sendMessage("This command can only be run by a player!");
        return true;
      }
      Player p = (Player)sender;
      if (DebugManager.isDebugger(p))
      {
        p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.FIREWORK_STAR, 1, (short) 80) {} });
        p.updateInventory();
        return true;
      }
      if (p.hasPermission("toxiicpickup.autopickup"))
      {
        if (args.length == 1)
        {
          if (args[0].equalsIgnoreCase("on"))
          {
            if (!Drops.playerDrops.contains(p.getUniqueId()))
            {
              Drops.playerDrops.add(p.getUniqueId());
              p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.dropson")));
            }
            else
            {
              p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.dropsison")));
            }
          }
          else if (args[0].equalsIgnoreCase("off"))
          {
            if (Drops.playerDrops.contains(p.getUniqueId()))
            {
              Drops.playerDrops.remove(p.getUniqueId());
              p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.dropsoff")));
            }
            else
            {
              p.sendMessage(MessageUtil.replace(this.plugin.getConfig().getString("messages.dropsisoff")));
            }
          }
          else
          {
            List<String> msgs = this.plugin.getConfig().getStringList("messages.help-drops");
            for (String msg : msgs) {
              p.sendMessage(MessageUtil.replace(msg));
            }
          }
        }
        else
        {
          List<String> msgs = this.plugin.getConfig().getStringList("messages.help-drops");
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
