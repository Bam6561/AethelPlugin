package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.inventories.PageCalculator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * PlayerStatsData contains information about player statistics.
 *
 * @author Danny Nguyen
 * @version 1.4.8
 * @since 1.4.8
 */
public class PlayerStatsData {
  private ArrayList<Inventory> statPages = new ArrayList<>();
  private int numberOfPages = 0;

  /**
   * Loads PlayerStat pages into memory.
   */
  public void loadStats() {
    createStatPages();
  }

  /**
   * Creates pages of stats.
   */
  private void createStatPages() {
    List<Statistic> stats = Arrays.asList(Statistic.values());
    Comparator<Statistic> statComparator = Comparator.comparing(Enum::name);
    stats.sort(statComparator);

    int numberOfStats = stats.size();
    int numberOfPages = new PageCalculator().calculateNumberOfPages(numberOfStats);
    setNumberOfPages(numberOfPages);

    int startIndex = 0;
    int endIndex = 45;

    ItemCreator itemCreator = new ItemCreator();
    for (int page = 0; page < numberOfPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54, "Player Statistic Page");
      // i = stat index
      // j = inventory slot index

      // Stats begin on the second row
      int j = 9;
      for (int i = startIndex; i < endIndex; i++) {
        String statName = capitalizeProperly(stats.get(i).name().replace("_", " "));
        switch (statName) {
          case "Mine Block", "Use Item", "Break Item", "Craft Item",
              "Kill Entity", "Pickup", "Dropped", "Entity Killed By" -> inv.setItem(j,
              itemCreator.createItem(Material.BOOK, ChatColor.WHITE + statName));
          default -> inv.setItem(j, itemCreator.createItem(Material.PAPER, ChatColor.WHITE + statName));
        }
        j++;
      }
      getStatPages().add(inv);

      // Indices to use for the next page (if it exists)
      startIndex += 45;
      endIndex = Math.min(numberOfStats, endIndex + 45);
    }
  }

  /**
   * Capitalizes the first character of every word.
   *
   * @param phrase phrase
   * @return proper phrase
   */
  private String capitalizeProperly(String phrase) {
    String[] words = phrase.split(" ");

    StringBuilder properPhrase = new StringBuilder();
    for (String word : words) {
      properPhrase.append(word.replace(word.substring(1), word.substring(1).toLowerCase()) + " ");
    }
    return properPhrase.toString().trim();
  }

  public ArrayList<Inventory> getStatPages() {
    return this.statPages;
  }

  public int getNumberOfPages() {
    return this.numberOfPages;
  }

  private void setNumberOfPages(int numberOfPages) {
    this.numberOfPages = numberOfPages;
  }
}
