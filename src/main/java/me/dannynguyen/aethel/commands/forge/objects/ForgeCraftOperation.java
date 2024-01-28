package me.dannynguyen.aethel.commands.forge.objects;

import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.ItemReader;
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
 * ForgeCraftOperation is an object that determines if the user
 * has sufficient materials to craft items from a Forge recipe.
 *
 * @author Danny Nguyen
 * @version 1.7.13
 * @since 1.4.15
 */
public class ForgeCraftOperation {
  private final ArrayList<InventorySlot> setInventory = new ArrayList<>();

  /**
   * Crafts a recipe.
   *
   * @param e    inventory click event
   * @param user user
   */
  public void craftRecipe(InventoryClickEvent e, Player user) {
    ForgeRecipe recipe = PluginData.forgeData.
        getRecipesMap().get(ItemReader.readName(e.getClickedInventory().getItem(0)));

    ArrayList<ItemStack> results = recipe.getResults();
    ArrayList<ItemStack> components = recipe.getComponents();

    if (!user.hasMetadata(PluginPlayerMeta.Namespace.DEVELOPER.namespace)) {
      if (hasMatchingType(user, components)) {
        processMatchingType(user, results);
      } else {
        user.sendMessage(PluginMessage.Failure.FORGE_CRAFT_INSUFFICIENT_COMPONENTS.message);
      }
    } else {
      giveItemsToPlayer(user, results);
    }
  }

  /**
   * Determines if the user has sufficient matching type components to craft the recipe.
   *
   * @param user       user
   * @param components recipe components
   * @return has sufficient components
   */
  private boolean hasMatchingType(Player user, ArrayList<ItemStack> components) {
    HashMap<Material, ArrayList<InventorySlot>> invMap = mapMaterialIndices(user.getInventory());

    for (ItemStack item : components) {
      Material reqMaterial = item.getType();
      if (invMap.containsKey(reqMaterial)) {
        int reqAmount = item.getAmount();

        NamespacedKey forgeIdKey = PluginNamespacedKey.FORGE_ID.namespacedKey;
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
   * Maps the user's inventory.
   *
   * @param inv user inventory
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
   * Determines if the user has sufficient amounts of the required material.
   *
   * @param invMap      material:inventory slots inventory map
   * @param reqMaterial required material
   * @param reqAmount   required amount
   * @return has sufficient amounts of material
   */
  private boolean hasSufficientMaterials(HashMap<Material, ArrayList<InventorySlot>> invMap,
                                         Material reqMaterial, int reqAmount) {
    NamespacedKey forgeIdKey = PluginNamespacedKey.FORGE_ID.namespacedKey;

    for (InventorySlot invSlot : invMap.get(reqMaterial)) {
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
   * Determines if the user has sufficient amounts of the required material.
   *
   * @param invMap      material:inventory slots inventory map
   * @param reqMaterial required material
   * @param reqAmount   required amount
   * @param forgeIdKey  aethel.forge.id
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
      setInventory.add(new InventorySlot(invSlot.getSlot(), invSlot.getItem(), 0));
      return reqAmount == 0;
    } else {
      int difference = Math.abs(reqAmount);
      invSlot.setAmount(difference);
      setInventory.add(new InventorySlot(invSlot.getSlot(), invSlot.getItem(), difference));
      return true;
    }
  }

  /**
   * Removes the recipe's components from the user's inventory and gives the recipe's results.
   *
   * @param user    user
   * @param results recipe results
   */
  private void processMatchingType(Player user, ArrayList<ItemStack> results) {
    Inventory inv = user.getInventory();
    for (InventorySlot invSlot : setInventory) {
      inv.setItem(invSlot.getSlot(),
          new ItemStack(inv.getItem(invSlot.getSlot()).getType(), invSlot.getAmount()));
    }
    giveItemsToPlayer(user, results);
  }

  /**
   * Adds the results directly to the player's inventory if there's space.
   * Otherwise, the results are dropped at the user's feet.
   *
   * @param user    user
   * @param results recipe results
   */
  private void giveItemsToPlayer(Player user, ArrayList<ItemStack> results) {
    for (ItemStack item : results) {
      if (user.getInventory().firstEmpty() != -1) {
        user.getInventory().addItem(item);
      } else {
        user.getWorld().dropItem(user.getLocation(), item);
      }
    }
  }
}
