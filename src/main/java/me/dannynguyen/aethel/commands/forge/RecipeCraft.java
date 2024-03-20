package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.util.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Represents a {@link PersistentRecipe recipe} craft operation.
 * <p>
 * Only removes items from the user's inventory if
 * they have enough materials to craft the recipe.
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.4.15
 */
class RecipeCraft {
  /**
   * User crafting the {@link PersistentRecipe recipe}.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * {@link PersistentRecipe Recipe's} results.
   */
  private final List<ItemStack> results;

  /**
   * {@link PersistentRecipe Recipe's} materials.
   */
  private final List<ItemStack> materials;

  /**
   * User's inventory.
   */
  private final PlayerInventory pInv;

  /**
   * Map of the user's inventory by material.
   */
  private final Map<Material, List<RecipeCraftInventory>> materialSlots;

  /**
   * Inventory slots to update if the {@link PersistentRecipe recipe's} requirements are met.
   */
  private final List<RecipeCraftInventory> postCraft = new ArrayList<>();

  /**
   * Associates a user with the {@link PersistentRecipe recipe} being crafted.
   *
   * @param user user
   * @param item representative item of recipe
   */
  protected RecipeCraft(Player user, ItemStack item) {
    this.user = user;
    PersistentRecipe recipe = Plugin.getData().getRecipeRegistry().getRecipes().get(ItemReader.readName(item));
    this.uuid = user.getUniqueId();
    this.results = recipe.getResults();
    this.materials = recipe.getMaterials();
    this.pInv = user.getInventory();
    this.materialSlots = mapMaterialIndices();
  }

  /**
   * Maps the user's inventory by material.
   *
   * @return map of material:inventory slots
   */
  private Map<Material, List<RecipeCraftInventory>> mapMaterialIndices() {
    Map<Material, List<RecipeCraftInventory>> materialSlots = new HashMap<>();
    for (int i = 0; i < 36; i++) {
      ItemStack item = pInv.getItem(i);
      if (ItemReader.isNotNullOrAir(pInv.getItem(i))) {
        Material material = item.getType();
        int amount = item.getAmount();
        if (materialSlots.containsKey(material)) {
          materialSlots.get(material).add(new RecipeCraftInventory(i, item, amount));
        } else {
          materialSlots.put(material, new ArrayList<>(List.of(new RecipeCraftInventory(i, item, amount))));
        }
      }
    }
    return materialSlots;
  }

  /**
   * Crafts a {@link PersistentRecipe recipe} if the user has enough materials.
   */
  protected void readRecipeMaterials() {
    if (!Plugin.getData().getPluginSystem().getPlayerMetadata().get(uuid).containsKey(PlayerMeta.DEVELOPER)) {
      if (hasEnoughOfAllMaterials()) {
        craftRecipe();
      } else {
        user.sendMessage(ChatColor.RED + "Not enough materials.");
      }
    } else {
      giveResults();
    }
  }

  /**
   * Determines if the user has enough materials to craft the {@link PersistentRecipe recipe}.
   *
   * @return has enough materials
   */
  private boolean hasEnoughOfAllMaterials() {
    NamespacedKey forgeId = PluginNamespacedKey.RECIPE_FORGE_ID.getNamespacedKey();
    for (ItemStack item : materials) {
      Material requiredMaterial = item.getType();
      if (materialSlots.containsKey(requiredMaterial)) {
        int requiredAmount = item.getAmount();
        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        boolean hasForgeId = dataContainer.has(forgeId, PersistentDataType.STRING);
        if (!hasForgeId) {
          if (!hasEnoughMatchingMaterials(forgeId, requiredMaterial, requiredAmount)) {
            return false;
          }
        } else {
          String requiredId = dataContainer.get(forgeId, PersistentDataType.STRING);
          if (!hasEnoughMatchingIds(forgeId, requiredMaterial, requiredAmount, requiredId)) {
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
   * Removes the {@link PersistentRecipe recipe's} materials from the user's inventory and gives the {@link PersistentRecipe recipe's} results.
   */
  private void craftRecipe() {
    for (RecipeCraftInventory invSlot : postCraft) {
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
   * @param forgeId          forge ID
   * @param requiredMaterial required material
   * @param requiredAmount   required amount
   * @return has enough materials
   */
  private boolean hasEnoughMatchingMaterials(NamespacedKey forgeId, Material requiredMaterial, int requiredAmount) {
    for (RecipeCraftInventory invSlot : materialSlots.get(requiredMaterial)) {
      PersistentDataContainer dataContainer = invSlot.getItem().getItemMeta().getPersistentDataContainer();
      if (!dataContainer.has(forgeId, PersistentDataType.STRING)) { // Don't use unique items for crafting
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
   * Determines if the user has enough of the required material by matching type and forge ID.
   *
   * @param forgeId     forge ID
   * @param reqMaterial required material
   * @param reqAmount   required amount
   * @param reqForgeId  required forge ID
   * @return has enough materials
   */
  private boolean hasEnoughMatchingIds(NamespacedKey forgeId, Material reqMaterial, int reqAmount, String reqForgeId) {
    for (RecipeCraftInventory invSlot : materialSlots.get(reqMaterial)) {
      PersistentDataContainer dataContainer = invSlot.getItem().getItemMeta().getPersistentDataContainer();
      if (dataContainer.has(forgeId, PersistentDataType.STRING) && dataContainer.get(forgeId, PersistentDataType.STRING).equals(reqForgeId)) {
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
  private boolean hasRequiredAmount(RecipeCraftInventory invSlot, int reqAmount) {
    if (reqAmount > 0 || reqAmount == 0) {
      invSlot.setAmount(0);
      postCraft.add(new RecipeCraftInventory(invSlot.getSlot(), invSlot.getItem(), 0));
      return reqAmount == 0;
    } else {
      int difference = Math.abs(reqAmount);
      invSlot.setAmount(difference);
      postCraft.add(new RecipeCraftInventory(invSlot.getSlot(), invSlot.getItem(), difference));
      return true;
    }
  }
}
