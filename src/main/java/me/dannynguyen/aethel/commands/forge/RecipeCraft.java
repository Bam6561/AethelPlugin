package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.utils.item.ItemReader;
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
 * Represents a {@link RecipeRegistry.Recipe recipe} craft operation.
 * <p>
 * Only removes items from the user's inventory if they have
 * enough materials to craft the {@link RecipeRegistry.Recipe recipe}.
 *
 * @author Danny Nguyen
 * @version 1.17.17
 * @since 1.4.15
 */
class RecipeCraft {
  /**
   * User crafting the {@link RecipeRegistry.Recipe recipe}.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * {@link RecipeRegistry.Recipe Recipe's} results.
   */
  private final List<ItemStack> results;

  /**
   * {@link RecipeRegistry.Recipe Recipe's} materials.
   */
  private final List<ItemStack> materials;

  /**
   * User's inventory.
   */
  private final PlayerInventory pInv;

  /**
   * Map of the user's inventory by material.
   */
  private final Map<Material, List<SlotItem>> materialSlots;

  /**
   * Inventory slots to update if the {@link RecipeRegistry.Recipe recipe's} requirements are met.
   */
  private final List<SlotItem> postCraft = new ArrayList<>();

  /**
   * Associates a user with the {@link RecipeRegistry.Recipe recipe} being crafted.
   *
   * @param user user
   * @param item representative item of recipe
   */
  RecipeCraft(@NotNull Player user, @NotNull ItemStack item) {
    this.user = Objects.requireNonNull(user, "Null user");
    RecipeRegistry.Recipe recipe = Plugin.getData().getRecipeRegistry().getRecipes().get(ItemReader.readName(Objects.requireNonNull(item, "Null item")));
    this.uuid = user.getUniqueId();
    this.results = recipe.getResults();
    this.materials = recipe.getMaterials();
    this.pInv = user.getInventory();
    this.materialSlots = mapMaterialIndices();
  }

  /**
   * Maps the user's inventory by material.
   *
   * @return map of material : inventory slots
   */
  private Map<Material, List<SlotItem>> mapMaterialIndices() {
    Map<Material, List<SlotItem>> materialSlots = new HashMap<>();
    for (int i = 0; i < 36; i++) {
      ItemStack item = pInv.getItem(i);
      if (ItemReader.isNotNullOrAir(pInv.getItem(i))) {
        Material material = item.getType();
        int amount = item.getAmount();
        if (materialSlots.containsKey(material)) {
          materialSlots.get(material).add(new SlotItem(i, item, amount));
        } else {
          materialSlots.put(material, new ArrayList<>(List.of(new SlotItem(i, item, amount))));
        }
      }
    }
    return materialSlots;
  }

  /**
   * Crafts a {@link RecipeRegistry.Recipe recipe} if the user has enough materials.
   */
  protected void readRecipeMaterials() {
    if (!Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).isDeveloper()) {
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
   * Determines if the user has enough materials to craft the {@link RecipeRegistry.Recipe recipe}.
   *
   * @return has enough materials
   */
  private boolean hasEnoughOfAllMaterials() {
    NamespacedKey forgeId = Key.RECIPE_FORGE_ID.getNamespacedKey();
    for (ItemStack item : materials) {
      Material requiredMaterial = item.getType();
      if (materialSlots.containsKey(requiredMaterial)) {
        int requiredAmount = item.getAmount();
        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        boolean hasForgeId = dataContainer.has(forgeId, PersistentDataType.STRING);
        if (!hasForgeId) {
          if (!hasEnoughMaterials(forgeId, requiredMaterial, requiredAmount)) {
            return false;
          }
        } else {
          String requiredId = dataContainer.get(forgeId, PersistentDataType.STRING);
          if (!hasEnoughIds(forgeId, requiredMaterial, requiredAmount, requiredId)) {
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
   * Removes the {@link RecipeRegistry.Recipe recipe's} materials from the user's inventory and gives the {@link RecipeRegistry.Recipe recipe's} results.
   */
  private void craftRecipe() {
    for (SlotItem invSlot : postCraft) {
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
  private boolean hasEnoughMaterials(NamespacedKey forgeId, Material requiredMaterial, int requiredAmount) {
    for (SlotItem invSlot : materialSlots.get(requiredMaterial)) {
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
  private boolean hasEnoughIds(NamespacedKey forgeId, Material reqMaterial, int reqAmount, String reqForgeId) {
    for (SlotItem invSlot : materialSlots.get(reqMaterial)) {
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
  private boolean hasRequiredAmount(SlotItem invSlot, int reqAmount) {
    if (reqAmount > 0 || reqAmount == 0) {
      invSlot.setAmount(0);
      postCraft.add(new SlotItem(invSlot.getSlot(), invSlot.getItem(), 0));
      return reqAmount == 0;
    } else {
      int difference = Math.abs(reqAmount);
      invSlot.setAmount(difference);
      postCraft.add(new SlotItem(invSlot.getSlot(), invSlot.getItem(), difference));
      return true;
    }
  }

  /**
   * Represents an inventory slot containing an ItemStack.
   * <p>
   * The "amount" field is separate from the ItemStack's built-in amount, as it is used
   * to set a new value post-craft only if the craft operation's requirements are met.
   *
   * @author Danny Nguyen
   * @version 1.17.12
   * @since 1.2.4
   */
  private static class SlotItem {
    /**
     * Inventory slot number.
     */
    private final int slot;

    /**
     * ItemStack at the inventory slot.
     */
    private final ItemStack item;

    /**
     * Post-craft amount of ItemStack.
     */
    private int amount;

    /**
     * Associates an inventory slot with its item and current amount.
     *
     * @param slot   inventory slot number
     * @param item   ItemStack
     * @param amount amount of ItemStack
     */
    SlotItem(int slot, @NotNull ItemStack item, int amount) {
      this.item = Objects.requireNonNull(item, "Null item");
      this.slot = slot;
      this.amount = amount;
    }

    /**
     * Gets the inventory slot.
     *
     * @return inventory slot
     */
    private int getSlot() {
      return this.slot;
    }

    /**
     * Gets the ItemStack.
     *
     * @return ItemStack
     */
    @NotNull
    private ItemStack getItem() {
      return this.item;
    }

    /**
     * Gets the post-craft amount of ItemStack.
     *
     * @return post-craft amount of ItemStack
     */
    private int getAmount() {
      return this.amount;
    }

    /**
     * Sets the post-craft amount of ItemStack.
     *
     * @param amount post-craft amount of ItemStack
     */
    private void setAmount(int amount) {
      this.amount = amount;
    }
  }
}
