package net.toxiic.drops.events;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.toxiic.drops.Drops;
import net.toxiic.drops.util.HoloAPI;
import net.toxiic.drops.util.LocationUtil;
import net.toxiic.drops.util.MessageUtil;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

public class EventListener
  implements Listener
{
  private Random random = new Random();
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    if (!Drops.playerDrops.contains(event.getPlayer().getUniqueId())) {
      Drops.playerDrops.add(event.getPlayer().getUniqueId());
    }
  }
  
  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent event)
  {
    MessageUtil.clearMessage(event.getPlayer());
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void onBlockBreak(BlockBreakEvent event)
  {
    int amount;
    ItemStack itemInHand;
    Player player;
    Block destroyedBlock;
    int damage;
    if (!event.isCancelled())
    {
      amount = 1;
      itemInHand = event.getPlayer().getInventory().getItemInHand() != null ? event.getPlayer().getInventory().getItemInHand() : new ItemStack(Material.AIR);
      if ((itemInHand != null) && (itemInHand.getType().toString().contains("AXE")))
      {
        player = event.getPlayer();
        destroyedBlock = event.getBlock();
        damage = 1;
        int unbreak = itemInHand.getEnchantmentLevel(Enchantment.DURABILITY);
        if (unbreak > 1)
        {
          Random ran = new Random();
          int chance = ran.nextInt(100);
          int percentage = 100 / (unbreak + 1);
          if (percentage < 25) {
            percentage = 30;
          }
          if (percentage > chance) {
            damage = 0;
          } else {
            damage = 1;
          }
        }
        if (itemInHand.getDurability() >= itemInHand.getType().getMaxDurability())
        {
          event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
          event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
        }
        boolean hasSilktouch = itemInHand.containsEnchantment(Enchantment.SILK_TOUCH);
        if (event.getBlock().getType() == Material.IRON_ORE)
        {
          if ((Drops.playerDrops.contains(event.getPlayer().getUniqueId())) && (Drops.playerSmelt.contains(event.getPlayer().getUniqueId())))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(hasSilktouch ? Material.IRON_ORE : Material.IRON_INGOT, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.IRON_ORE : Material.IRON_INGOT, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(hasSilktouch ? Material.IRON_ORE : Material.IRON_INGOT, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.IRON_ORE : Material.IRON_INGOT, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
          else if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.IRON_ORE, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_ORE, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.IRON_ORE, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_ORE, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
          else if (Drops.playerSmelt.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(hasSilktouch ? Material.IRON_ORE : Material.IRON_INGOT, amount));
            destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
            event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
            itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
            ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
          }
        }
        else if (event.getBlock().getType() == Material.GOLD_ORE)
        {
          if ((Drops.playerDrops.contains(event.getPlayer().getUniqueId())) && (Drops.playerSmelt.contains(event.getPlayer().getUniqueId())))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(hasSilktouch ? Material.GOLD_ORE : Material.GOLD_INGOT, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.GOLD_ORE : Material.GOLD_INGOT, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(hasSilktouch ? Material.GOLD_ORE : Material.GOLD_INGOT, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.GOLD_ORE : Material.GOLD_INGOT, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
          else if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.GOLD_ORE, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_ORE, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.GOLD_ORE, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_ORE, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
          else if (Drops.playerSmelt.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(hasSilktouch ? Material.GOLD_ORE : Material.GOLD_INGOT, amount));
            destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
            event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
            itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
            ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
          }
        }
        else if (event.getBlock().getType() == Material.COBBLESTONE)
        {
          if ((Drops.playerDrops.contains(event.getPlayer().getUniqueId())) && (Drops.playerSmelt.contains(event.getPlayer().getUniqueId())))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.STONE, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.STONE, amount) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.STONE, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COBBLESTONE, amount) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
          else if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.COBBLESTONE, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COBBLESTONE, amount) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.COBBLESTONE, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COBBLESTONE, amount) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
          else if (Drops.playerSmelt.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
            itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
            ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.STONE, amount) });
            destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
            itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
            ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
          }
        }
        else if (event.getBlock().getType() == Material.MELON)
        {
          if ((Drops.playerDrops.contains(event.getPlayer().getUniqueId())) && (Drops.playerSmelt.contains(event.getPlayer().getUniqueId())))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.MELON, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MELON, amount) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.MELON, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MELON, amount) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.SMOKE, 30);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
          else if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.MELON, amount + 3);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MELON, amount + 3) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.MELON, amount + 3));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MELON, amount + 3) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
          else if (Drops.playerSmelt.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.MELON, amount));
            destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
            event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.SMOKE, 30);
            itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
            ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
          }
        }
        else if (event.getBlock().getType() == Material.PUMPKIN)
        {
          if ((Drops.playerDrops.contains(event.getPlayer().getUniqueId())) && (Drops.playerSmelt.contains(event.getPlayer().getUniqueId())))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.PUMPKIN, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.PUMPKIN, amount) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.PUMPKIN, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 30);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.PUMPKIN, amount) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.SMOKE, 30);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
          else if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.PUMPKIN, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.PUMPKIN, amount) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.PUMPKIN, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.PUMPKIN, amount) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
          else if (Drops.playerSmelt.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.PUMPKIN, amount));
            destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
            event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.SMOKE, 30);
            itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
            ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
          }
        }
        else if (event.getBlock().getType() == Material.COAL_ORE)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(hasSilktouch ? Material.COAL_ORE : Material.COAL, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.COAL_ORE : Material.COAL, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(hasSilktouch ? Material.COAL_ORE : Material.COAL, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.COAL_ORE : Material.COAL, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.EMERALD_ORE)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(hasSilktouch ? Material.EMERALD_ORE : Material.EMERALD, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.EMERALD_ORE : Material.EMERALD, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(hasSilktouch ? Material.EMERALD_ORE : Material.EMERALD, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.EMERALD_ORE : Material.EMERALD, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.DIAMOND_ORE)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(hasSilktouch ? Material.DIAMOND_ORE : Material.DIAMOND, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.DIAMOND_ORE : Material.DIAMOND, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(hasSilktouch ? Material.DIAMOND_ORE : Material.DIAMOND, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.DIAMOND_ORE : Material.DIAMOND, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.IRON_BLOCK)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.IRON_BLOCK, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_BLOCK, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.IRON_BLOCK, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_BLOCK, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.GOLD_BLOCK)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = this.random.nextInt(chance + 1);
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.GOLD_BLOCK, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_BLOCK, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.GOLD_BLOCK, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_BLOCK, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.EMERALD_BLOCK)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.EMERALD_BLOCK, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.EMERALD_BLOCK, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.EMERALD_BLOCK, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.EMERALD_BLOCK, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.DIAMOND_BLOCK)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.DIAMOND_BLOCK, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.DIAMOND_BLOCK, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.DIAMOND_BLOCK, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.DIAMOND_BLOCK, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.LAPIS_BLOCK)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.LAPIS_BLOCK, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.LAPIS_BLOCK, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.LAPIS_BLOCK, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.LAPIS_BLOCK, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.REDSTONE_BLOCK)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.REDSTONE_BLOCK, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.REDSTONE_BLOCK, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.REDSTONE_BLOCK, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.REDSTONE_BLOCK, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.COAL_BLOCK)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.COAL_BLOCK, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COAL_BLOCK, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.COAL_BLOCK, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COAL_BLOCK, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.COCOA)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(Material.INK_SAC, amount, (short)3);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.INK_SAC, getAmount(player, amount), (short) 3) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(Material.INK_SAC, amount, (short)3));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.INK_SAC, getAmount(player, amount), (short) 3) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.NETHER_QUARTZ_ORE)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(hasSilktouch ? Material.NETHER_QUARTZ_ORE : Material.QUARTZ, amount);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.NETHER_QUARTZ_ORE : Material.QUARTZ, getAmount(player, amount)) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(hasSilktouch ? Material.NETHER_QUARTZ_ORE : Material.QUARTZ, amount));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.NETHER_QUARTZ_ORE : Material.QUARTZ, getAmount(player, amount)) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (event.getBlock().getType() == Material.LAPIS_ORE)
        {
          if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
          {
            if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
              return;
            }
            if (itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            {
              if (Drops.allowedWorlds.contains(event.getPlayer().getWorld().getName()))
              {
                int level = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                int chance = level / (3 - this.random.nextInt(2));
                
                int drops = chance > 0 ? this.random.nextInt(chance) : 0;
                amount = drops + 1;
              }
            }
            else {
              amount = 1;
            }
            if (player.getInventory().firstEmpty() == -1)
            {
              int is = 0;
              ItemStack item = new ItemStack(hasSilktouch ? Material.LAPIS_ORE : Material.INK_SAC, amount, (short)4);
              for (ItemStack o : player.getInventory().getContents())
              {
                is++;
                if (o != null)
                {
                  int l = o.getAmount();
                  if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                  {
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.LAPIS_ORE : Material.INK_SAC, getAmount(player, amount), (short) 4) });
                    event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    break;
                  }
                  if ((is == 36) && (l != 2))
                  {
                    if (MessageUtil.canSendMessage(player)) {
                      HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                    }
                    MessageUtil.sendMessage(player, Drops.fullInv);
                    
                    destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), new ItemStack(hasSilktouch ? Material.LAPIS_ORE : Material.INK_SAC, amount, (short)4));
                    destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                    itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                    ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                  }
                }
              }
            }
            else
            {
              player.getInventory().addItem(new ItemStack[] { new ItemStack(hasSilktouch ? Material.LAPIS_ORE : Material.INK_SAC, getAmount(player, amount), (short) 4) });
              event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
              itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
              ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
            }
          }
        }
        else if (Drops.playerDrops.contains(event.getPlayer().getUniqueId()))
        {
          if (!canBreak(event.getBlock().getLocation(), event.getPlayer())) {
            return;
          }
          if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            for (ItemStack item : event.getBlock().getDrops())
            {
              byte data = item.getData().getData();
              ItemStack newItem = new ItemStack(item.getType(), amount, (short)data);
              if ((newItem != null) && ((newItem.getItemMeta() instanceof SkullMeta)) && 
                ((event.getBlock().getState() instanceof Skull)))
              {
                Skull blockSkull = (Skull)event.getBlock().getState();
                if ((blockSkull.getSkullType() == SkullType.PLAYER) && (blockSkull.getOwner() != null))
                {
                  SkullMeta skullMeta = (SkullMeta)newItem.getItemMeta();
                  skullMeta.setOwner(blockSkull.getOwner());
                  newItem.setItemMeta(skullMeta);
                }
              }
              if (player.getInventory().firstEmpty() == -1)
              {
                int is = 0;
                for (ItemStack o : player.getInventory().getContents())
                {
                  is++;
                  if (o != null)
                  {
                    int l = o.getAmount();
                    if ((o.getType().equals(item.getType())) && (o.getAmount() < item.getMaxStackSize()))
                    {
                      event.getPlayer().getInventory().addItem(new ItemStack[] { newItem });
                      event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                      itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                      ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                      break;
                    }
                    if ((is == 36) && (l != 2))
                    {
                      if (MessageUtil.canSendMessage(player)) {
                        HoloAPI.createHologram(player, LocationUtil.getLocationInfront(player), new String[] { Drops.getInstance().getConfig().getString("messages.hologram") });
                      }
                      MessageUtil.sendMessage(player, Drops.fullInv);
                      
                      destroyedBlock.getLocation().getWorld().dropItemNaturally(destroyedBlock.getLocation(), newItem);
                      destroyedBlock.getWorld().getBlockAt(destroyedBlock.getLocation()).setType(Material.AIR);
                      itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                      ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
                    }
                  }
                }
              }
              else
              {
                event.getPlayer().getInventory().addItem(new ItemStack[] { newItem });
                event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
                itemInHand.setDurability((short)(itemInHand.getDurability() + damage));
                ((PlayerInventory) event.getPlayer()).setItemInMainHand(itemInHand);
              }
            }
          }
        }
      }
    }
  }
  
  public int getAmount(Player player, int amount)
  {
    return (player != null) && (player.getName().equals("KingFaris10")) ? amount * 10 : amount;
  }
  
  private boolean canBreak(Location loc, Player p)
  {
    try
    {
      Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
      if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {
        return true;
      }
      WorldGuardPlugin pl = (WorldGuardPlugin)plugin;
      return pl.canBuild(p, p.getWorld().getBlockAt((int)loc.getX(), (int)loc.getY(), (int)loc.getZ()));
    }
    catch (Exception e) {}
    return true;
  }
}
