package me.dannynguyen.aethel.objects.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.objects.InventorySlot;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ForgeCraftOperation is an object that determines if a player
 * has sufficient materials to craft items from a forge recipe.
 *
 * @author Danny Nguyen
 * @version 1.6.6
 * @since 1.4.15
 */
public class ForgeCraftOperation {
  private final ArrayList<InventorySlot> setInventory = new ArrayList<>();

  /**
   * Crafts a recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void craftRecipe(InventoryClickEvent e, Player player) {
    ForgeRecipe recipe = AethelResources.forgeRecipeData.
        getRecipesMap().get(ItemReader.readItemName(e.getClickedInventory().getItem(0)));

    ArrayList<ItemStack> results = recipe.getResults();
    ArrayList<ItemStack> components = recipe.getComponents();

    if (!player.hasMetadata("devmode")) {
      if (hasMatchingType(player, components)) {
        processMatchingType(player, results);
      } else {
        player.sendMessage(ChatColor.RED + "Insufficient components.");
      }
    } else {
      giveItemsToPlayer(player, results);
    }
  }

  /**
   * Determines if the player has sufficient matching type components to craft the recipe.
   *
   * @param player     interacting player
   * @param components components in recipe
   * @return has sufficient components
   */
  private boolean hasMatchingType(Player player, ArrayList<ItemStack> components) {
    Inventory inv = player.getInventory();
    HashMap<Material, ArrayList<InventorySlot>> invMap = mapMaterialIndices(inv);

    for (ItemStack item : components) {
      Material reqMaterial = item.getType();
      if (invMap.containsKey(reqMaterial)) {
        int reqAmount = item.getAmount();

        NamespacedKey forgeIdKey = new NamespacedKey(AethelPlugin.getInstance(), "aethel.forge_id");
        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        boolean hasForgeId = dataContainer.has(forgeIdKey, PersistentDataType.STRING);

        if (!hasForgeId) {
          if (!hasSufficientMaterials(invMap, reqMaterial, reqAmount)) {
            return false;
          }
        } else {
          String reqForgeId = dataContainer.get(forgeIdKey, PersistentDataType.STRING);
          if (!hasSufficientMaterials(invMap, reqMaterial, reqAmount, forgeIdKey, reqForgeId)) {
            return false;
          }
        }
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Maps the player's inventory.
   *
   * @param inv player inventory
   * @return material:inventory slots inventory map
   */
  private HashMap<Material, ArrayList<InventorySlot>> mapMaterialIndices(Inventory inv) {
    HashMap<Material, ArrayList<InventorySlot>> invMap = new HashMap<>();

    for (int i = 0; i < 36; i++) {
      ItemStack item = inv.getItem(i);
      if (inv.getItem(i) != null) {
        Material material = item.getType();
        int amount = item.getAmount();

        if (invMap.containsKey(material)) {
          invMap.get(material).add(new InventorySlot(i, item, amount));
        } else {
          ArrayList<InventorySlot> invSlots = new ArrayList<>();
          invSlots.add(new InventorySlot(i, item, amount));
          invMap.put(material, invSlots);
        }
      }
    }
    return invMap;
  }

  /**
   * Determines if the player has sufficient amounts of the required material.
   *
   * @param invMap      material:inventory slots inventory map
   * @param reqMaterial required material
   * @param reqAmount   required amount
   * @return has sufficient amounts of material
   */
  private boolean hasSufficientMaterials(HashMap<Material, ArrayList<InventorySlot>> invMap,
                                         Material reqMaterial, int reqAmount) {
    for (InventorySlot invSlot : invMap.get(reqMaterial)) {
      NamespacedKey forgeIdKey = new NamespacedKey(AethelPlugin.getInstance(), "aethel.forge_id");
      PersistentDataContainer dataContainer = invSlot.getItem().getItemMeta().getPersistentDataContainer();

      if (!dataContainer.has(forgeIdKey, PersistentDataType.STRING)) {
        if (invSlot.getAmount() > 0) {
          reqAmount -= invSlot.getAmount();
          if (hasReqAmountSatisfied(invSlot, reqAmount)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Determines if the player has sufficient amounts of the required material.
   *
   * @param invMap      material:inventory slots inventory map
   * @param reqMaterial required material
   * @param reqAmount   required amount
   * @param forgeIdKey  aethel.forge_id
   * @param reqForgeId  required ForgeId
   * @return has sufficient amounts of material
   */
  private boolean hasSufficientMaterials(HashMap<Material, ArrayList<InventorySlot>> invMap,
                                         Material reqMaterial, int reqAmount,
                                         NamespacedKey forgeIdKey, String reqForgeId) {
    for (InventorySlot invSlot : invMap.get(reqMaterial)) {
      PersistentDataContainer dataContainer = invSlot.getItem().getItemMeta().getPersistentDataContainer();

      if (dataContainer.has(forgeIdKey, PersistentDataType.STRING) &&
          dataContainer.get(forgeIdKey, PersistentDataType.STRING).equals(reqForgeId)) {
        if (invSlot.getAmount() > 0) {
          reqAmount -= invSlot.getAmount();
          if (hasReqAmountSatisfied(invSlot, reqAmount)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Determines if the required amount of material was satisfied.
   *
   * @param reqAmount required amount
   * @return has sufficient amounts of material
   */
  private boolean hasReqAmountSatisfied(InventorySlot invSlot, int reqAmount) {
    if (reqAmount > 0 || reqAmount == 0) {
      invSlot.setAmount(0);
      getSetInventory().add(new InventorySlot(invSlot.getSlot(), invSlot.getItem(), 0));
      return reqAmount == 0;
    } else {
      int difference = Math.abs(reqAmount);
      invSlot.setAmount(difference);
      getSetInventory().add(new InventorySlot(invSlot.getSlot(), invSlot.getItem(), difference));
      return true;
    }
  }

  /**
   * Removes the recipe's components.
   *
   * @param player  interacting player
   * @param results recipe results
   */
  private void processMatchingType(Player player, ArrayList<ItemStack> results) {
    Inventory inv = player.getInventory();
    for (InventorySlot invSlot : getSetInventory()) {
      inv.setItem(invSlot.getSlot(),
          new ItemStack(inv.getItem(invSlot.getSlot()).getType(), invSlot.getAmount()));
    }
    giveItemsToPlayer(player, results);
  }

  /**
   * Adds the results directly to the player's inventory if there's space.
   * Otherwise, the results are dropped at the player's feet.
   *
   * @param player  interacting player
   * @param results recipe results
   */
  private void giveItemsToPlayer(Player player, ArrayList<ItemStack> results) {
    for (ItemStack item : results) {
      if (player.getInventory().firstEmpty() != -1) {
        player.getInventory().addItem(item);
      } else {
        player.getWorld().dropItem(player.getLocation(), item);
      }
    }
  }

  private ArrayList<InventorySlot> getSetInventory() {
    return this.setInventory;
  }
}
