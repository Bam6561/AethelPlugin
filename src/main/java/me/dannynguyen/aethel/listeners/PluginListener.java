package me.dannynguyen.aethel.listeners;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import me.dannynguyen.aethel.plugin.PluginSystem;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Collection of {@link PluginSystem} listeners.
 *
 * @author Danny Nguyen
 * @version 1.27.0
 * @since 1.10.1
 */
public class PluginListener implements Listener {
  /**
   * No parameter constructor.
   */
  public PluginListener() {
  }

  /**
   * Associates a {@link PluginPlayer} to a player upon joining the server.
   *
   * @param e player join event
   */
  @EventHandler
  private void onJoin(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    UUID playerUUID = player.getUniqueId();
    Map<UUID, PluginPlayer> pluginPlayers = Plugin.getData().getPluginSystem().getPluginPlayers();
    if (!pluginPlayers.containsKey(playerUUID)) {
      pluginPlayers.put(playerUUID, new PluginPlayer(player));
    }
  }

  /**
   * Routes player interactions.
   *
   * @param e player interaction event
   */
  @EventHandler
  private void onPlayerInteract(PlayerInteractEvent e) {
    ItemStack item = e.getItem();
    if (ItemReader.isNullOrAir(item)) {
      return;
    }

    PersistentDataContainer itemTags = item.getItemMeta().getPersistentDataContainer();
    if (itemTags.has(Key.UNUSABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
      e.setCancelled(true);
    }
  }

  /**
   * Prevents non-placeable blocks from being placed.
   *
   * @param e block place event
   */
  @EventHandler
  private void onBlockPlace(BlockPlaceEvent e) {
    ItemStack item = e.getItemInHand();
    ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      return;
    }
    if (item.getItemMeta().getPersistentDataContainer().has(Key.NON_PLACEABLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
      e.setCancelled(true);
    }
  }

  /**
   * Prevents non-consumable items from being consumed.
   *
   * @param e player item consume vent
   */
  @EventHandler
  private void onPlayerItemConsume(PlayerItemConsumeEvent e) {
    ItemStack item = e.getItem();
    if (item.getItemMeta().getPersistentDataContainer().has(Key.NON_EDIBLE.getNamespacedKey(), PersistentDataType.BOOLEAN)) {
      e.setCancelled(true);
    }
  }

  /**
   * Applies environmental protection enchantment level V's or reinforces an item's
   * {@link me.dannynguyen.aethel.enums.plugin.Key#RPG_DURABILITY} through an anvil click event.
   *
   * @param e inventory click event
   */
  @EventHandler
  private void onAnvilInteraction(InventoryClickEvent e) {
    Inventory clickedInv = e.getClickedInventory();
    if (clickedInv == null || clickedInv.getType() != InventoryType.ANVIL || ItemReader.isNullOrAir(e.getCurrentItem())) {
      return;
    }
    switch (e.getSlot()) {
      case 0 -> reinforceItem(e);
      case 2 -> readEnvironmentalProtectionV(e);
    }
  }

  /**
   * Reads environmental protection enchantment level V's before applying the enchantment.
   *
   * @param e inventory click event
   */
  private void readEnvironmentalProtectionV(InventoryClickEvent e) {
    Inventory inv = e.getInventory();
    ItemStack component = inv.getItem(1);
    if (ItemReader.isNullOrAir(component)) {
      return;
    }
    ItemStack result = inv.getItem(2);
    ItemMeta componentMeta = component.getItemMeta();
    if (applyEnvironmentalProtectionV(result, componentMeta, Enchantment.PROTECTION)) {
    } else if (applyEnvironmentalProtectionV(result, componentMeta, Enchantment.FIRE_PROTECTION)) {
    } else if (applyEnvironmentalProtectionV(result, componentMeta, Enchantment.BLAST_PROTECTION)) {
    } else if (applyEnvironmentalProtectionV(result, componentMeta, Enchantment.FEATHER_FALLING)) {
    } else if (applyEnvironmentalProtectionV(result, componentMeta, Enchantment.PROJECTILE_PROTECTION)) {
    }
  }

  /**
   * Reinforces an item's {@link me.dannynguyen.aethel.enums.plugin.Key#RPG_DURABILITY}.
   *
   * @param e inventory click event
   */
  private void reinforceItem(InventoryClickEvent e) {
    ItemStack item = e.getInventory().getItem(0);
    ItemMeta meta = item.getItemMeta();
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    if (!(dataContainer.has(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER) && dataContainer.has(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER))) {
      return;
    }

    int maxReinforcement = dataContainer.get(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER);
    dataContainer.set(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER, maxReinforcement);

    List<String> lore = meta.getLore();
    if (lore.isEmpty()) {
      lore.add(ChatColor.WHITE + "Reinforcement: " + maxReinforcement + " / " + maxReinforcement);
    } else {
      lore.set(lore.size() - 1, ChatColor.WHITE + "Reinforcement: " + maxReinforcement + " / " + maxReinforcement);
    }
    meta.setLore(lore);
    item.setItemMeta(meta);

    Player player = (Player) e.getWhoClicked();
    player.getWorld().playSound(player.getEyeLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 1, 1);
  }

  /**
   * Applies environmental protection V's to an item.
   *
   * @param result        result item
   * @param componentMeta component meta
   * @param enchantment   enchantment V to apply
   * @return if the enchantment was applied
   */
  private boolean applyEnvironmentalProtectionV(ItemStack result, ItemMeta componentMeta, Enchantment enchantment) {
    if (componentMeta.getEnchantLevel(enchantment) == 5) {
      ItemMeta resultMeta = result.getItemMeta();
      resultMeta.addEnchant(enchantment, 5, true);
      result.setItemMeta(resultMeta);
      return true;
    } else if (componentMeta instanceof EnchantmentStorageMeta enchantable && enchantable.getStoredEnchants().getOrDefault(enchantment, 0) == 5) {
      ItemMeta resultMeta = result.getItemMeta();
      resultMeta.addEnchant(enchantment, 5, true);
      result.setItemMeta(resultMeta);
      return true;
    }
    return false;
  }
}
