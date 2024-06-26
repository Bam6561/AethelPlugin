package me.bam6561.aethelplugin.commands.forge;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Key;
import me.bam6561.aethelplugin.enums.plugin.Message;
import me.bam6561.aethelplugin.interfaces.MenuClick;
import me.bam6561.aethelplugin.listeners.MenuListener;
import me.bam6561.aethelplugin.listeners.MessageListener;
import me.bam6561.aethelplugin.plugin.MenuInput;
import me.bam6561.aethelplugin.utils.EntityReader;
import me.bam6561.aethelplugin.utils.TextFormatter;
import me.bam6561.aethelplugin.utils.item.ItemCreator;
import me.bam6561.aethelplugin.utils.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * Inventory click event listener for {@link ForgeCommand} menus.
 * <p>
 * Called through {@link MenuListener}.
 *
 * @author Danny Nguyen
 * @version 1.26.4
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
      case 3 -> new RecipeDetailsMenu(user, RecipeDetailsMenu.Mode.SAVE).getRecipeDetails();
      case 4 -> new MenuChange().searchRecipe();
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
      case 8 -> { // Context
      }
      case 24 -> new MenuChange().toggleBatchCraft();
      case 25 -> {
        int batchCraft;
        switch (e.getClickedInventory().getItem(24).getType()) {
          case COBBLESTONE -> batchCraft = 1;
          case STONE -> batchCraft = 4;
          case STONE_BRICKS -> batchCraft = 16;
          default -> batchCraft = 1;
        }
        new RecipeCraft(e.getClickedInventory().getItem(0), batchCraft).readRecipeMaterials();
      }
      case 26 -> new MenuChange().openForgeCraft();
      default -> {
        ItemStack clickedItem = e.getCurrentItem();
        if (Plugin.getData().getRecipeRegistry().getRecipes().get(ItemReader.readName(clickedItem)) != null) {
          new RecipeDetailsMenu(user, RecipeDetailsMenu.Mode.CRAFT, clickedItem).getRecipeDetails();
        }
      }
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
      case 24 -> { // Slot used by batch craft toggle button
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
   * @version 1.26.3
   * @since 1.23.11
   */
  private class MenuChange {
    /**
     * No parameter constructor.
     */
    MenuChange() {
    }

    /**
     * Searches for matching recipes by name.
     */
    private void searchRecipe() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input search term.");
      user.closeInventory();
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMessageInput(MessageListener.Type.FORGE_RECIPE_SEARCH);
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

    /**
     * Toggles the amount of results to craft.
     */
    private void toggleBatchCraft() {
      Inventory menu = e.getClickedInventory();
      switch (menu.getItem(24).getType()) {
        case COBBLESTONE -> menu.setItem(24, ItemCreator.createItem(Material.STONE, ChatColor.AQUA + "x4"));
        case STONE -> menu.setItem(24, ItemCreator.createItem(Material.STONE_BRICKS, ChatColor.AQUA + "x16"));
        case STONE_BRICKS -> menu.setItem(24, ItemCreator.createItem(Material.COBBLESTONE, ChatColor.AQUA + "x1"));
      }
    }
  }

  /**
   * Represents a {@link RecipeRegistry.Recipe recipe} craft operation.
   * <p>
   * Only removes items from the user's inventory if they have
   * enough materials to craft the {@link RecipeRegistry.Recipe recipe}.
   *
   * @author Danny Nguyen
   * @version 1.26.3
   * @since 1.4.15
   */
  private class RecipeCraft {
    /**
     * {@link Key#RECIPE_FORGE_ID}.
     */
    private final NamespacedKey forgeId = Key.RECIPE_FORGE_ID.getNamespacedKey();

    /**
     * Amount of results to craft at once.
     */
    private final int batchCraft;

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
     * @param item       representative item of recipe
     * @param batchCraft amount of results to craft at once
     */
    RecipeCraft(@NotNull ItemStack item, int batchCraft) {
      RecipeRegistry.Recipe recipe = Plugin.getData().getRecipeRegistry().getRecipes().get(ItemReader.readName(Objects.requireNonNull(item, "Null item")));
      this.batchCraft = batchCraft;
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
      for (ItemStack item : materials) {
        Material requiredMaterial = item.getType();
        if (!materialSlots.containsKey(requiredMaterial)) {
          return false;
        }

        int requiredAmount = item.getAmount() * batchCraft;
        PersistentDataContainer itemTags = item.getItemMeta().getPersistentDataContainer();
        boolean hasForgeId = itemTags.has(forgeId, PersistentDataType.STRING);

        if (!hasForgeId) {
          switch (requiredMaterial) {
            case ENCHANTED_BOOK -> {
              if (!hasEnoughEnchantedBooks((EnchantmentStorageMeta) item.getItemMeta(), requiredAmount)) {
                return false;
              }
            }
            case POTION -> {
              if (!hasEnoughPotions(Material.POTION, (PotionMeta) item.getItemMeta(), requiredAmount)) {
                return false;
              }
            }
            case SPLASH_POTION -> {
              if (!hasEnoughPotions(Material.SPLASH_POTION, (PotionMeta) item.getItemMeta(), requiredAmount)) {
                return false;
              }
            }
            case LINGERING_POTION -> {
              if (!hasEnoughPotions(Material.LINGERING_POTION, (PotionMeta) item.getItemMeta(), requiredAmount)) {
                return false;
              }
            }
            default -> {
              if (!hasEnoughMaterials(requiredMaterial, requiredAmount)) {
                return false;
              }
            }
          }
        } else {
          String requiredId = itemTags.get(forgeId, PersistentDataType.STRING);
          if (!hasEnoughIds(requiredMaterial, requiredAmount, requiredId)) {
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
      for (int i = 0; i < batchCraft; i++) {
        for (ItemStack item : results) {
          if (user.getInventory().firstEmpty() != -1) {
            user.getInventory().addItem(item);
          } else {
            user.getWorld().dropItem(user.getLocation(), item);
          }
        }
      }
    }

    /**
     * Determines if the user has enough of the required enchanted books by matching enchantments.
     *
     * @param enchantmentMeta enchantment meta
     * @param requiredAmount  required amount
     * @return has enough enchanted books
     */
    private boolean hasEnoughEnchantedBooks(EnchantmentStorageMeta enchantmentMeta, int requiredAmount) {
      for (SlotItem invSlot : materialSlots.get(Material.ENCHANTED_BOOK)) {
        ItemMeta meta = invSlot.getItem().getItemMeta();
        PersistentDataContainer itemTags = meta.getPersistentDataContainer();
        if (!itemTags.has(forgeId, PersistentDataType.STRING)) { // Don't use unique items for crafting
          EnchantmentStorageMeta enchantmentMeta2 = (EnchantmentStorageMeta) meta;
          if (invSlot.getAmount() > 0 && enchantmentMeta.getStoredEnchants().equals(enchantmentMeta2.getStoredEnchants())) {
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
     * Determines if the user has enough of the required potions by matching potion effects.
     *
     * @param material       material
     * @param potionMeta     potion meta
     * @param requiredAmount required amount
     * @return has enough enchantments
     */
    private boolean hasEnoughPotions(Material material, PotionMeta potionMeta, int requiredAmount) {
      for (SlotItem invSlot : materialSlots.get(material)) {
        ItemMeta meta = invSlot.getItem().getItemMeta();
        PersistentDataContainer itemTags = meta.getPersistentDataContainer();
        if (!itemTags.has(forgeId, PersistentDataType.STRING)) { // Don't use unique items for crafting
          if (invSlot.getAmount() > 0) {
            List<PotionEffect> basePotionEffects = potionMeta.getBasePotionType().getPotionEffects();
            List<PotionEffect> customPotionEffects = potionMeta.getCustomEffects();

            PotionMeta potionMeta2 = (PotionMeta) meta;
            List<PotionEffect> basePotionEffects2 = potionMeta2.getBasePotionType().getPotionEffects();
            List<PotionEffect> customPotionEffects2 = potionMeta2.getCustomEffects();

            if (basePotionEffects.equals(basePotionEffects2) && customPotionEffects.equals(customPotionEffects2)) {
              requiredAmount -= invSlot.getAmount();
              if (hasRequiredAmount(invSlot, requiredAmount)) {
                return true;
              }
            }
          }
        }
      }
      return false;
    }

    /**
     * Determines if the user has enough of the required material by matching type.
     *
     * @param requiredMaterial required material
     * @param requiredAmount   required amount
     * @return has enough materials
     */
    private boolean hasEnoughMaterials(Material requiredMaterial, int requiredAmount) {
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
     * @param reqMaterial required material
     * @param reqAmount   required amount
     * @param reqForgeId  required forge ID
     * @return has enough materials
     */
    private boolean hasEnoughIds(Material reqMaterial, int reqAmount, String reqForgeId) {
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
   * @version 1.26.3
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
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input recipe folder name.");
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
      for (int i = 9; i < 23; i++) {
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
