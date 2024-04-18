package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.interfaces.MenuClick;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.listeners.MessageListener;
import me.dannynguyen.aethel.plugin.MenuInput;
import me.dannynguyen.aethel.utils.EntityReader;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * Inventory click event listener for {@link ForgeCommand} menus.
 * <p>
 * Called through {@link MenuListener}.
 *
 * @author Danny Nguyen
 * @version 1.23.11
 * @since 1.0.9
 */
public class ForgeMenuClick implements MenuClick {
  /**
   * Inventory click event.
   */
  private final InventoryClickEvent e;

  /**
   * Player who clicked.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * Slot clicked.
   */
  private final int slot;

  /**
   * Associates an inventory click event with its user in the context of an open Forge menu.
   *
   * @param e inventory click event
   */
  public ForgeMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.uuid = user.getUniqueId();
    this.slot = e.getSlot();
  }

  /**
   * Either saves a {@link RecipeRegistry.Recipe recipe} or
   * opens a {@link RecipeRegistry.Recipe recipe} category page.
   */
  public void interpretMenuClick() {
    switch (slot) {
      case 2, 4 -> { // Context
      }
      case 3 -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Mode.SAVE).getRecipeDetails();
      default -> new MenuChange().viewRecipeCategory();
    }
  }

  /**
   * Either:
   * <ul>
   *  <li>increments or decrements a {@link RecipeRegistry.Recipe recipe} category page
   *  <li>saves a {@link RecipeRegistry.Recipe recipe}
   *  <li>changes the {@link RecipeMenu.Action interaction}
   *  <li>contextualizes the click to expand, edit, or remove {@link RecipeRegistry.Recipe recipe}
   * </ul>
   *
   * @param action type of interaction
   */
  public void interpretCategoryClick(@NotNull RecipeMenu.Action action) {
    Objects.requireNonNull(action, "Null action");
    switch (slot) {
      case 0 -> new MenuChange().previousRecipePage(action);
      case 2 -> { // Context
      }
      case 3 -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Mode.SAVE).getRecipeDetails();
      case 4 -> {
        if (Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().getMode() == MenuListener.Mode.RECIPE_DETAILS_MENU_EDIT) {
          new MenuChange().openForgeEdit();
        }
      }
      case 5 -> new MenuChange().openForgeRemove();
      case 6 -> new MenuChange().returnToMainMenu();
      case 8 -> new MenuChange().nextRecipePage(action);
      default -> {
        if (e.getSlot() > 8) {
          interpretContextualClick(action);
        }
      }
    }
  }

  /**
   * Either crafts a {@link RecipeRegistry.Recipe recipe} or returns to a category
   * page with the intent to craft {@link RecipeRegistry.Recipe recipes}.
   */
  public void interpretCraftDetailsClick() {
    switch (e.getSlot()) {
      case 25 -> new RecipeCraft(e.getClickedInventory().getItem(0)).readRecipeMaterials();
      case 26 -> new MenuChange().openForgeCraft();
    }
  }

  /**
   * Either saves a {@link RecipeRegistry.Recipe recipe} or returns to a category
   * page with the intent to edit {@link RecipeRegistry.Recipe recipes}.
   */
  public void interpretSaveClick() {
    switch (slot) {
      case 8 -> { // Context
      }
      case 25 -> new RecipeSave().readSaveClick();
      case 26 -> new MenuChange().openForgeEdit();
      default -> e.setCancelled(false);
    }
  }

  /**
   * Either crafts, edits, or removes a {@link RecipeRegistry.Recipe recipe}.
   *
   * @param action type of interaction
   */
  private void interpretContextualClick(RecipeMenu.Action action) {
    switch (action) {
      case CRAFT -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Mode.CRAFT, e.getCurrentItem()).getRecipeDetails();
      case EDIT -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Mode.EDIT, e.getCurrentItem()).getRecipeDetails();
      case REMOVE -> new RecipeRemove().removeRecipe();
    }
  }

  /**
   * Represents a menu change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class MenuChange {
    /**
     * No parameter constructor.
     */
    MenuChange() {
    }

    /**
     * Views a {@link RecipeRegistry.Recipe recipe} category.
     */
    private void viewRecipeCategory() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      RecipeMenu.Action action = RecipeMenu.Action.valueOf(menuInput.getMode().getEnumString());
      String category = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
      int requestedPage = menuInput.getPage();

      menuInput.setCategory(category);
      user.openInventory(new RecipeMenu(user, action).getCategoryPage(category, requestedPage));
      menuInput.setMenu(MenuListener.Menu.valueOf("FORGE_" + action.name()));
    }

    /**
     * Opens the previous {@link RecipeRegistry.Recipe recipe} category page.
     *
     * @param action type of interaction
     */
    private void previousRecipePage(RecipeMenu.Action action) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = menuInput.getCategory();
      int requestedPage = menuInput.getPage();

      user.openInventory(new RecipeMenu(user, action).getCategoryPage(category, requestedPage - 1));
      menuInput.setMenu(MenuListener.Menu.valueOf("FORGE_" + action.name()));
    }

    /**
     * Opens the {@link RecipeMenu} with the intent to edit {@link RecipeRegistry.Recipe recipe}.
     * <p>
     * The player can return to either the {@link RecipeMenu}
     * or a {@link RecipeRegistry.Recipe recipe} category.
     */
    private void openForgeEdit() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = menuInput.getCategory();
      if (category.equals("")) {
        user.openInventory(new RecipeMenu(user, RecipeMenu.Action.EDIT).getMainMenu());
        menuInput.setMenu(MenuListener.Menu.FORGE_CATEGORY);
      } else {
        int requestedPage = menuInput.getPage();
        user.openInventory(new RecipeMenu(user, RecipeMenu.Action.EDIT).getCategoryPage(category, requestedPage));
        menuInput.setMenu(MenuListener.Menu.FORGE_EDIT);
      }
    }

    /**
     * Opens the {@link RecipeMenu} with the intent to remove {@link RecipeRegistry.Recipe recipe}.
     */
    private void openForgeRemove() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = menuInput.getCategory();
      int requestedPage = menuInput.getPage();

      user.openInventory(new RecipeMenu(user, RecipeMenu.Action.REMOVE).getCategoryPage(category, requestedPage));
      menuInput.setMenu(MenuListener.Menu.FORGE_REMOVE);
    }

    /**
     * Opens the {@link RecipeMenu} with the {@link MenuListener.Mode} in mind.
     */
    private void returnToMainMenu() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      RecipeMenu.Action action = RecipeMenu.Action.valueOf(menuInput.getMode().getEnumString());

      menuInput.setCategory("");
      user.openInventory(new RecipeMenu(user, action).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.FORGE_CATEGORY);
      menuInput.setPage(0);
    }

    /**
     * Opens the {@link RecipeMenu} with the intent to craft {@link RecipeRegistry.Recipe recipes}.
     */
    private void openForgeCraft() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = menuInput.getCategory();
      int requestedPage = menuInput.getPage();

      user.openInventory(new RecipeMenu(user, RecipeMenu.Action.CRAFT).getCategoryPage(category, requestedPage));
      menuInput.setMenu(MenuListener.Menu.FORGE_CRAFT);
    }

    /**
     * Opens the next {@link RecipeRegistry.Recipe recipe} category page.
     *
     * @param action type of interaction
     */
    private void nextRecipePage(RecipeMenu.Action action) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = menuInput.getCategory();
      int requestedPage = menuInput.getPage();

      user.openInventory(new RecipeMenu(user, action).getCategoryPage(category, requestedPage + 1));
      menuInput.setMenu(MenuListener.Menu.valueOf("FORGE_" + action.name()));
    }
  }

  /**
   * Represents a {@link RecipeRegistry.Recipe recipe} craft operation.
   * <p>
   * Only removes items from the user's inventory if they have
   * enough materials to craft the {@link RecipeRegistry.Recipe recipe}.
   *
   * @author Danny Nguyen
   * @version 1.23.15
   * @since 1.4.15
   */
  private class RecipeCraft {
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
     * @param item representative item of recipe
     */
    RecipeCraft(@NotNull ItemStack item) {
      RecipeRegistry.Recipe recipe = Plugin.getData().getRecipeRegistry().getRecipes().get(ItemReader.readName(Objects.requireNonNull(item, "Null item")));
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
      for (int i = 9; i < 36; i++) {
        ItemStack item = pInv.getItem(i);
        if (ItemReader.isNullOrAir(pInv.getItem(i))) {
          continue;
        }

        Material material = item.getType();
        int amount = item.getAmount();
        if (materialSlots.containsKey(material)) {
          materialSlots.get(material).add(new SlotItem(i, item, amount));
        } else {
          materialSlots.put(material, new ArrayList<>(List.of(new SlotItem(i, item, amount))));
        }
      }
      return materialSlots;
    }

    /**
     * Crafts a {@link RecipeRegistry.Recipe recipe} if the user has enough materials.
     */
    private void readRecipeMaterials() {
      if (!canForge()) {
        return;
      }

      if (Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().isDeveloper()) {
        giveResults();
        return;
      }

      if (hasEnoughOfAllMaterials()) {
        craftRecipe();
      } else {
        user.sendMessage(ChatColor.RED + "Not enough materials.");
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
        if (!materialSlots.containsKey(requiredMaterial)) {
          return false;
        }

        int requiredAmount = item.getAmount();
        PersistentDataContainer itemTags = item.getItemMeta().getPersistentDataContainer();
        boolean hasForgeId = itemTags.has(forgeId, PersistentDataType.STRING);

        if (!hasForgeId) {
          if (!hasEnoughMaterials(forgeId, requiredMaterial, requiredAmount)) {
            return false;
          }
        } else {
          String requiredId = itemTags.get(forgeId, PersistentDataType.STRING);
          if (!hasEnoughIds(forgeId, requiredMaterial, requiredAmount, requiredId)) {
            return false;
          }
        }
      }
      return true;
    }

    /**
     * Removes the {@link RecipeRegistry.Recipe recipe's} materials from the
     * user's inventory and gives the {@link RecipeRegistry.Recipe recipe's} results.
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
        PersistentDataContainer itemTags = invSlot.getItem().getItemMeta().getPersistentDataContainer();
        if (!itemTags.has(forgeId, PersistentDataType.STRING)) { // Don't use unique items for crafting
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
        PersistentDataContainer itemTags = invSlot.getItem().getItemMeta().getPersistentDataContainer();
        if (itemTags.has(forgeId, PersistentDataType.STRING) && itemTags.get(forgeId, PersistentDataType.STRING).equals(reqForgeId)) {
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
     * The user must have a crafting table in their
     * hand, off-hand, or trinket slot to forge recipes.
     *
     * @return if the user can forge recipes
     */
    private boolean canForge() {
      if (EntityReader.hasTrinket(user, Material.CRAFTING_TABLE)) {
        return true;
      } else {
        user.sendMessage(ChatColor.RED + "[Forge] No crafting table in hand, off-hand, or trinket slot.");
        return false;
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

  /**
   * Represents a recipe save operation.
   *
   * @author Danny Nguyen
   * @version 1.23.16
   * @since 1.23.8
   */
  private class RecipeSave {
    /**
     * Recipe contents.
     */
    private final ItemStack[] contents = e.getInventory().getContents();

    /**
     * Recipe name.
     */
    private String recipeName;

    /**
     * Recipe file name.
     */
    private String fileName;

    /**
     * Encoded recipe.
     */
    private String encodedRecipe;

    /**
     * No parameter constructor.
     */
    RecipeSave() {
    }

    /**
     * Checks if the {@link RecipeRegistry.Recipe recipe's} details
     * were formatted correctly before asking the user for a folder
     * name to save the {@link RecipeRegistry.Recipe recipe} under.
     */
    private void readSaveClick() {
      if (!nameFile()) {
        user.sendMessage(ChatColor.RED + "No recipe results.");
        return;
      }
      if (!encodeRecipe()) {
        user.sendMessage(ChatColor.RED + "No recipe materials.");
        return;
      }

      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setObjectType(recipeName);
      menuInput.setFileName(fileName);
      menuInput.setEncodedData(encodedRecipe);
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input recipe folder name to be saved under.");
      user.closeInventory();
      menuInput.setMessageInput(MessageListener.Type.FORGE_RECIPE_FOLDER);
    }

    /**
     * Names a {@link RecipeRegistry.Recipe recipe} by the first item in the results row.
     *
     * @return if the file could be named
     */
    private boolean nameFile() {
      for (int i = 0; i < 8; i++) {
        ItemStack item = contents[i];
        if (ItemReader.isNullOrAir(item)) {
          continue;
        }

        recipeName = ItemReader.readName(item);
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
          fileName = TextFormatter.formatId(ChatColor.stripColor(meta.getDisplayName()));
        } else {
          fileName = TextFormatter.formatId(item.getType().name());
        }
        return true;
      }
      return false;
    }

    /**
     * Encodes the {@link RecipeRegistry.Recipe recipe} by its results and materials.
     * <p>
     * At this stage, the results are non-null, so the
     * method checks if the materials are non-null first.
     *
     * @return if the recipe could be encoded
     */
    private boolean encodeRecipe() {
      StringBuilder materials = new StringBuilder();
      for (int i = 9; i < 24; i++) {
        ItemStack item = contents[i];
        if (ItemReader.isNotNullOrAir(item)) {
          materials.append(ItemCreator.encodeItem(item)).append(" ");
        }
      }

      if (materials.isEmpty()) {
        return false;
      }

      StringBuilder results = new StringBuilder();
      for (int i = 0; i < 8; i++) {
        ItemStack item = contents[i];
        if (ItemReader.isNotNullOrAir(item)) {
          results.append(ItemCreator.encodeItem(item)).append(" ");
        }
      }
      encodedRecipe = results.append("\n").append(materials).toString();
      return true;
    }
  }

  /**
   * Represents a recipe removal operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class RecipeRemove {
    /**
     * No parameter constructor.
     */
    RecipeRemove() {
    }

    /**
     * Removes an existing {@link RecipeRegistry.Recipe recipe}.
     */
    private void removeRecipe() {
      RecipeRegistry.Recipe recipe = Plugin.getData().getRecipeRegistry().getRecipes().get(ItemReader.readName(e.getCurrentItem()));
      File recipeFile = recipe.getFile();
      String directoryName = recipeFile.getParentFile().getName();
      String fileName = recipeFile.getName();

      recipe.delete();
      user.sendMessage(ChatColor.RED + "[Removed Recipe] " + ChatColor.WHITE + directoryName + "/" + fileName.substring(0, fileName.length() - 8));
    }
  }
}
