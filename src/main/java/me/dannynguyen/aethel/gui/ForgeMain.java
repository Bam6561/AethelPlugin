package me.dannynguyen.aethel.gui;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

/**
 * ForgeCraft is a menu option under the Forge command that processes the crafting of forge items.
 *
 * @author Danny Nguyen
 * @version 1.0.6
 * @since 1.0.6
 */
public class ForgeMain {
  private Inventory defaultView;

  public ForgeMain(Player player) {
    this.defaultView = createDefaultView(player);
  }

  /**
   * Creates the default view for the Forge-Main menu.
   *
   * @param player interacting player
   * @return Forge-Main default view
   */
  private Inventory createDefaultView(Player player) {
    Inventory craftMenu = Bukkit.createInventory(player, 54, "Forge");
    craftMenu.setItem(0, createItem(Material.RED_WOOL, "Previous Page"));
    for (int i = 1; i < 8; i++) {
      craftMenu.setItem(i, createItem(Material.BLACK_STAINED_GLASS_PANE, ""));
    }
    craftMenu.setItem(8, createItem(Material.GREEN_WOOL, "Next Page"));
    return craftMenu;
  }

  /**
   * Creates a named item.
   *
   * @param material    item material
   * @param displayName item name
   * @return named item
   */
  private ItemStack createItem(Material material, String displayName) {
    ItemStack item = new ItemStack(material, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Populates the default Forge-Main menu with forge recipes loaded from memory.
   *
   * @param player        interacting player
   * @param pageRequested recipe page to view
   */
  public Inventory populateView(Player player, int pageRequested) {
    Inventory view = createDefaultView(player);
    ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>(AethelPlugin.getInstance().getForgeRecipes());

    int numberOfRecipes = forgeRecipes.size();
    int numberOfPages = calculateNumberOfPages(numberOfRecipes);
    int viewPageNumber = calculateViewPageNumber(pageRequested, numberOfPages);
    int startIndexOnPage = calculateViewPageStartIndex(numberOfRecipes, viewPageNumber);
    int endIndexOnPage = Math.min(startIndexOnPage + 20, numberOfRecipes);

    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), viewPageNumber));
    return createViewPage(view, forgeRecipes, startIndexOnPage, endIndexOnPage);
  }

  /**
   * Determines how many pages of recipes exist and whether there are partially filled pages.
   *
   * @param numberOfRecipes number of recipes
   * @return number of recipe pages
   */
  private int calculateNumberOfPages(int numberOfRecipes) {
    int numberOfPages = numberOfRecipes / 20;
    boolean partiallyFilledPage = (numberOfRecipes % 20) > 0;
    if (partiallyFilledPage) {
      numberOfPages += 1;
    }
    return numberOfPages;
  }

  /**
   * Determines which page to be viewed.
   *
   * @param pageRequested recipe page to view
   * @param numberOfPages number of recipe pages
   * @return interpreted recipe page to view
   */
  private int calculateViewPageNumber(int pageRequested, int numberOfPages) {
    boolean requestMoreThanTotalPages = pageRequested >= numberOfPages;
    boolean requestNegativePageNumber = pageRequested < 0;
    if (requestMoreThanTotalPages) {
      pageRequested = numberOfPages - 1;
    } else if (requestNegativePageNumber) {
      pageRequested = 0;
    }
    return pageRequested;
  }

  /**
   * Determines which recipe index to begin displaying on page.
   *
   * @param numberOfRecipes number of recipes
   * @param pageRequested   recipe page to view
   * @return starting recipe index on page
   */
  private int calculateViewPageStartIndex(int numberOfRecipes, int pageRequested) {
    int startIndex = pageRequested * 20;
    if (startIndex == numberOfRecipes) {
      startIndex -= 20;
    }
    return startIndex;
  }

  /**
   * Creates the Forge-Main page the player wishes to view.
   *
   * @param view       items in the inventory
   * @param recipes    recipes
   * @param startIndex starting index
   * @param endIndex   ending index
   * @return Forge-Main menu page
   */
  private Inventory createViewPage(Inventory view, ArrayList<ForgeRecipe> recipes, int startIndex, int endIndex) {
    // i = recipes index
    // j = inventory slot index

    // Skip first inventory row
    int j = 9;
    for (int i = startIndex; i < endIndex; i++) {
      //Skip end inventory row slots
      if (j == 17 || j == 26 || j == 35 || j == 44) j++;

      ForgeRecipe forgeRecipe = recipes.get(i);
      ArrayList<ItemStack> results = forgeRecipe.getResults();
      ArrayList<ItemStack> components = forgeRecipe.getComponents();

      view.setItem(j, createItemDetails(results.get(0), results));
      view.setItem(j + 1, createItemDetails(new ItemStack(Material.PAPER), components));
      j++;
    }
    return view;
  }

  /**
   * Creates expanded item details on a display item showing either the results or components of a forge recipe.
   * <p>
   * Format:
   * [Amount] Material
   * ...
   * </p>
   *
   * @param displayItem  item to be shown
   * @param relatedItems results or components of the forge recipe
   * @return item labelled with its results of components
   */
  private ItemStack createItemDetails(ItemStack displayItem, ArrayList<ItemStack> relatedItems) {
    List<String> itemDetails = new ArrayList<>();
    for (ItemStack item : relatedItems) {
      int amount = item.getAmount();
      String itemName = getItemName(item);
      itemDetails.add("x" + amount + " " + itemName);
    }

    ItemStack finalItem = new ItemStack(displayItem.getType(), 1);
    ItemMeta meta = finalItem.getItemMeta();
    meta.setLore(itemDetails);
    finalItem.setItemMeta(meta);
    return finalItem;
  }

  /**
   * Returns either an item's renamed value or its material.
   *
   * @param item item
   * @return effective item name
   */
  private String getItemName(ItemStack item) {
    if (item.getItemMeta().hasDisplayName()) {
      return item.getItemMeta().getDisplayName();
    } else {
      return item.getType().name();
    }
  }

  /**
   * Either goes forward a recipe page or backwards.
   *
   * @param e inventory click event
   */
  public void interpretMainClick(InventoryClickEvent e, String menuType) {
    Player player = (Player) e.getWhoClicked();
    int pageRequested = Integer.parseInt(player.getMetadata("page").get(0).asString());
    switch (e.getSlot()) {
      case 0 -> {
        player.openInventory(populateView(player, pageRequested - 1));
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), menuType));
      }
      case 8 -> {
        player.openInventory(populateView(player, pageRequested + 1));
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), menuType));
      }
      default -> interpretMenuContextualClick(e, menuType);
    }
    e.setCancelled(true);
  }

  private void interpretMenuContextualClick(InventoryClickEvent e, String menuType) {
    Player player = (Player) e.getWhoClicked();
    switch (menuType) {
      case "forge-modify" -> {
        modifyForgeRecipe(e, readForgeRecipe(getRecipeFile(e)));
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-create"));
      }
    }
  }

  private File getRecipeFile(InventoryClickEvent e) {
    ItemStack item = e.getCurrentItem();
    if (item != null) {
      String itemName;
      if (item.getItemMeta().hasDisplayName()) {
        itemName = item.getItemMeta().getDisplayName().toLowerCase().replace(" ", "_");
      } else {
        itemName = item.getType().name().toLowerCase();
      }
      return new File(AethelPlugin.getInstance().getResourceDirectory() + "/forge/" + itemName + ".txt");
    }
    return null;
  }

  private ForgeRecipe readForgeRecipe(File file) {
    ArrayList<ItemStack> results = new ArrayList<>();
    ArrayList<ItemStack> components = new ArrayList<>();
    int recipeDataType = 1;

    try {
      Scanner scanner = new Scanner(file);
      scanner.nextLine(); // Skip Results line
      while (scanner.hasNextLine()) {
        String data = scanner.nextLine();

        if (data.equals("Components")) {
          recipeDataType++;
          data = scanner.nextLine();
        }

        ItemStack item = decodeItem(data);
        if (item != null) {
          switch (recipeDataType) {
            case 1 -> results.add(decodeItem(data));
            case 2 -> components.add(decodeItem(data));
          }
        }
      }
      return new ForgeRecipe(results, components);
    } catch (FileNotFoundException ex) {
    }
    return null;
  }

  private void modifyForgeRecipe(InventoryClickEvent e, ForgeRecipe forgeRecipe) {
    Player player = (Player) e.getWhoClicked();
    Inventory view = new ForgeCreate(player).getDefaultView();

    ArrayList<ItemStack> results = forgeRecipe.getResults();
    ArrayList<ItemStack> components = forgeRecipe.getComponents();

    for (int i = 0; i < results.size(); i++) {
      view.setItem(i, results.get(i));
    }
    for (int i = 0; i < components.size(); i++) {
      view.setItem(i + 9, components.get(i));
    }

    player.openInventory(view);
  }

  /**
   * Deserializes an item.
   *
   * @param data serialized item string
   * @return ItemStack representing item
   * @throws IOException            file not found
   * @throws ClassNotFoundException item could not be decoded
   */
  private ItemStack decodeItem(String data) {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(data));
      BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
      ItemStack item = (ItemStack) bois.readObject();
      return item;
    } catch (IOException | ClassNotFoundException ex) {
      return null;
    }
  }

  /**
   * Returns a generic Forge-Create inventory.
   *
   * @return blank Forge-Create view
   */
  public Inventory getDefaultView() {
    return this.defaultView;
  }
}
