package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a recipe craft operation.
 * <p>
 * Only removes items from the user's inventory if
 * they have enough materials to craft the recipe.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.10.1
 * @since 1.4.15
 */
class RecipeCraft {
  /**
   * User crafting the recipe.
   */
  private final Player user;

  /**
   * Recipe's results.
   */
  private final List<ItemStack> results;

  /**
   * Recipe's materials.
   */
  private final List<ItemStack> materials;

  /**
   * User's inventory.
   */
  private final PlayerInventory pInv;

  /**
   * Map of the user's inventory by material.
   */
  private final Map<Material, List<InventorySlot>> invMap;

  /**
   * Inventory slots to update if the recipe's requirements are met.
   */
  private final List<InventorySlot> postCraft;

  /**
   * Associates a user with the recipe being crafted.
   *
   * @param user user
   * @param item representative item of recipe
   */
  protected RecipeCraft(@NotNull Player user, @NotNull ItemStack item) {
    this.user = Objects.requireNonNull(user, "Null user");
    PersistentRecipe recipe = PluginData.recipeRegistry.getRecipeMap().get(ItemReader.readName(Objects.requireNonNull(item, "Null recipe")));
    this.results = recipe.getResults();
    this.materials = recipe.getMaterials();
    this.pInv = user.getInventory();
    this.invMap = mapMaterialIndices();
    this.postCraft = new ArrayList<>();
  }

  /**
   * Maps the user's inventory by material.
   *
   * @return map of material:inventory slots
   */
  private Map<Material, List<InventorySlot>> mapMaterialIndices() {
    Map<Material, List<InventorySlot>> invMap = new HashMap<>();
    for (int i = 0; i < 36; i++) {
      ItemStack item = pInv.getItem(i);
      if (ItemReader.isNotNullOrAir(pInv.getItem(i))) {
        Material material = item.getType();
        int amount = item.getAmount();
        if (invMap.containsKey(material)) {
          invMap.get(material).add(new InventorySlot(i, item, amount));
        } else {
          invMap.put(material, new ArrayList<>(List.of(new InventorySlot(i, item, amount))));
        }
      }
    }
    return invMap;
  }

  /**
   * Crafts a recipe if the user has enough materials.
   */
  protected void craftRecipe() {
    if (!PluginData.pluginSystem.getPlayerMetadata().get(user).containsKey(PlayerMeta.DEVELOPER)) {
      if (hasEnoughOfAllMaterials()) {
        processRecipeCraft();
      } else {
        user.sendMessage(ChatColor.RED + "Not enough materials.");
      }
    } else {
      giveResults();
    }
  }

  /**
   * Determines if the user has enough materials to craft the recipe.
   *
   * @return has enough materials
   */
  private boolean hasEnoughOfAllMaterials() {
    NamespacedKey recipeId = PluginNamespacedKey.RECIPE_ID.getNamespacedKey();
    for (ItemStack item : materials) {
      Material requiredMaterial = item.getType();
      if (invMap.containsKey(requiredMaterial)) {
        int requiredAmount = item.getAmount();
        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        boolean hasForgeId = dataContainer.has(recipeId, PersistentDataType.STRING);
        if (!hasForgeId) {
          if (!hasEnoughMatchingMaterials(recipeId, requiredMaterial, requiredAmount)) {
            return false;
          }
        } else {
          String requiredId = dataContainer.get(recipeId, PersistentDataType.STRING);
          if (!hasEnoughMatchingIds(recipeId, requiredMaterial, requiredAmount, requiredId)) {
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
   * Removes the recipe's materials from the user's inventory and gives the recipe's results.
   */
  private void processRecipeCraft() {
    for (InventorySlot invSlot : postCraft) {
      ItemStack item = invSlot.getItem();
      item.setAmount(invSlot.getAmount());
      pInv.setItem(invSlot.getSlot(), item);
    }
    giveResults();
  }

  /**
   * Adds the results directly to the player's inventory if there's space.
   * Otherwise, the results are dropped at the user's feet.
   */
  private void giveResults() {
    for (ItemStack item : results) {
      if (user.getInventory().firstEmpty() != -1) {
        user.getInventory().addItem(item);
      } else {
        user.getWorld().dropItem(user.getLocation(), item);
      }
    }
  }

  /**
   * Determines if the user has enough of the required material by matching type.
   *
   * @param recipeId         recipe id
   * @param requiredMaterial required material
   * @param requiredAmount   required amount
   * @return has enough materials
   */
  private boolean hasEnoughMatchingMaterials(NamespacedKey recipeId, Material requiredMaterial, int requiredAmount) {
    for (InventorySlot invSlot : invMap.get(requiredMaterial)) {
      PersistentDataContainer dataContainer = invSlot.getItem().getItemMeta().getPersistentDataContainer();
      if (!dataContainer.has(recipeId, PersistentDataType.STRING)) { // Don't use unique items for crafting
        if (invSlot.getAmount() > 0) {
          requiredAmount -= invSlot.getAmount();
          if (hasRequiredAmount(invSlot, requiredAmount)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Determines if the user has enough of the required material by matching type and recipe id.
   *
   * @param recipeId    recipe id
   * @param reqMaterial required material
   * @param reqAmount   required amount
   * @param reqForgeId  required recipe id
   * @return has enough materials
   */
  private boolean hasEnoughMatchingIds(NamespacedKey recipeId, Material reqMaterial, int reqAmount, String reqForgeId) {
    for (InventorySlot invSlot : invMap.get(reqMaterial)) {
      PersistentDataContainer dataContainer = invSlot.getItem().getItemMeta().getPersistentDataContainer();
      if (dataContainer.has(recipeId, PersistentDataType.STRING) && dataContainer.get(recipeId, PersistentDataType.STRING).equals(reqForgeId)) {
        if (invSlot.getAmount() > 0) {
          reqAmount -= invSlot.getAmount();
          if (hasRequiredAmount(invSlot, reqAmount)) {
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
   * @param invSlot   tracked post-craft inventory slot
   * @param reqAmount required amount
   * @return has enough materials
   */
  private boolean hasRequiredAmount(InventorySlot invSlot, int reqAmount) {
    if (reqAmount > 0 || reqAmount == 0) {
      invSlot.setAmount(0);
      postCraft.add(new InventorySlot(invSlot.getSlot(), invSlot.getItem(), 0));
      return reqAmount == 0;
    } else {
      int difference = Math.abs(reqAmount);
      invSlot.setAmount(difference);
      postCraft.add(new InventorySlot(invSlot.getSlot(), invSlot.getItem(), difference));
      return true;
    }
  }
}
