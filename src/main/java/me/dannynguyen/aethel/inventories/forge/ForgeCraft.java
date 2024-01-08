package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.objects.InventorySlot;
import me.dannynguyen.aethel.readers.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * ForgeCraft is an inventory under the Forge command that crafts forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.1.0
 */
public class ForgeCraft {
  private ArrayList<InventorySlot> setInventory = new ArrayList<>();

  /**
   * Creates and names a ForgeCraft inventory.
   *
   * @param player interacting player
   * @return ForgeCraft inventory
   */
  public static Inventory createInventory(Player player) {
    Inventory inv = Bukkit.createInventory(player, 27,
        ChatColor.DARK_GRAY + "Forge" + ChatColor.BLUE + " Craft");

    addCraftContext(inv);
    inv.setItem(25, ItemCreator.
        createPlayerHead("CRAFTING_TABLE", ChatColor.AQUA + "Craft"));
    inv.setItem(26, ItemCreator.
        createPlayerHead("GRAY_BACKWARD", ChatColor.AQUA + "Back"));
    return inv;
  }

  /**
   * Adds a help context to the expanded craft action.
   *
   * @param inv interacting inventory
   */
  private static void addCraftContext(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.AQUA + "Rows",
        ChatColor.AQUA + "1 " + ChatColor.WHITE + "Results",
        ChatColor.AQUA + "2 " + ChatColor.WHITE + "Components",
        ChatColor.AQUA + "3 " + ChatColor.WHITE + "Components");
    inv.setItem(8, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Expands the recipe's details to the player before crafting.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public static void expandRecipeDetails(InventoryClickEvent e, Player player) {
    ForgeRecipe recipe = AethelResources.forgeRecipeData.
        getRecipesMap().get(ItemReader.readItemName(e.getCurrentItem()));

    Inventory inv = createInventory(player);
    addExistingRecipeContents(recipe, inv);

    player.openInventory(inv);
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft-confirm"));
  }

  /**
   * Adds the recipe's existing results and components to the ForgeCreate inventory.
   *
   * @param recipe forge recipe
   * @param inv    interacting inventory
   */
  private static void addExistingRecipeContents(ForgeRecipe recipe, Inventory inv) {
    ArrayList<ItemStack> results = recipe.getResults();
    ArrayList<ItemStack> components = recipe.getComponents();

    for (int i = 0; i < results.size(); i++) {
      inv.setItem(i, results.get(i));
    }
    for (int i = 0; i < components.size(); i++) {
      inv.setItem(i + 9, components.get(i));
    }
  }

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

        NamespacedKey forgeIdKey = new NamespacedKey(AethelPlugin.getInstance(), "aethel.forgeid");
        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        boolean hasForgeId = dataContainer.has(forgeIdKey, PersistentDataType.STRING);

        if (!hasForgeId) {
          if (!hasSufficientMaterials(invMap, reqMaterial, reqAmount)) return false;
        } else {
          String reqForgeId = dataContainer.get(forgeIdKey, PersistentDataType.STRING);
          if (!hasSufficientMaterials(invMap, reqMaterial, reqAmount, forgeIdKey, reqForgeId)) return false;
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
      if (invSlot.getAmount() > 0) {
        reqAmount -= invSlot.getAmount();
        if (hasReqAmountSatisfied(invSlot, reqAmount)) return true;
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
   * @param forgeIdKey  aethel.forgeid
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
          if (hasReqAmountSatisfied(invSlot, reqAmount)) return true;
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
      if (reqAmount == 0) {
        return true;
      }
    } else {
      int difference = Math.abs(reqAmount);
      invSlot.setAmount(difference);
      getSetInventory().add(new InventorySlot(invSlot.getSlot(), invSlot.getItem(), difference));
      return true;
    }
    return false;
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
