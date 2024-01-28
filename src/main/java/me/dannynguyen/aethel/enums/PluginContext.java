package me.dannynguyen.aethel.enums;

import org.bukkit.ChatColor;

import java.util.List;

/**
 * PluginContext is an enum containing the plugin's help contexts.
 *
 * @author Danny Nguyen
 * @version 1.7.13
 * @since 1.7.13
 */
public enum PluginContext {
  AETHELITEM_CATEGORIES(List.of(
      ChatColor.WHITE + "Place an item to",
      ChatColor.WHITE + "the right of this",
      ChatColor.WHITE + "slot to save it.")),
  AETHELITEM_CATEGORY_PAGE(List.of(
      ChatColor.WHITE + "Place an item to",
      ChatColor.WHITE + "the right of this",
      ChatColor.WHITE + "slot to save it.",
      "",
      ChatColor.WHITE + "You can toggle between",
      ChatColor.WHITE + "Get and Remove modes by",
      ChatColor.WHITE + "clicking on their button.",
      "",
      ChatColor.WHITE + "To undo a removal,",
      ChatColor.WHITE + "get the item and save",
      ChatColor.WHITE + "it before reloading.")),
  CHARACTER_EQUIPMENT(List.of(ChatColor.GRAY + "Head" + ChatColor.WHITE + "  | "
          + ChatColor.GRAY + "Main Hand" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Off Hand",
      ChatColor.GRAY + "Chest" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Necklace",
      ChatColor.GRAY + "Legs" + ChatColor.WHITE + "  | " + ChatColor.GRAY + "Ring",
      ChatColor.GRAY + "Boots" + ChatColor.WHITE + " | " + ChatColor.GRAY + "Ring")),
  FORGE_CRAFT(List.of(
      ChatColor.WHITE + "Expand a recipe to see its",
      ChatColor.WHITE + "results and components.",
      "",
      ChatColor.WHITE + "Components are matched",
      ChatColor.WHITE + "by material unless",
      ChatColor.WHITE + "they're unique items!")),
  FORGE_EDITOR(List.of(
      ChatColor.WHITE + "To undo a removal,",
      ChatColor.WHITE + "edit the item and",
      ChatColor.WHITE + "save it before reloading.")),
  FORGE_EXPANDED_CRAFT(List.of(
      ChatColor.AQUA + "Rows",
      ChatColor.AQUA + "1 " + ChatColor.WHITE + "Results",
      ChatColor.AQUA + "2 " + ChatColor.WHITE + "Components",
      ChatColor.AQUA + "3 " + ChatColor.WHITE + "Components")),
  FORGE_SAVE(List.of(
      ChatColor.AQUA + "Rows",
      ChatColor.AQUA + "1 " + ChatColor.WHITE + "Results",
      ChatColor.AQUA + "2 " + ChatColor.WHITE + "Components",
      ChatColor.AQUA + "3 " + ChatColor.WHITE + "Components")),
  PLAYERSTATS_CATEGORIES(List.of(ChatColor.WHITE + "Stat Categories")),
  PLAYERSTATS_SHARE_STAT(List.of(
      ChatColor.WHITE + "Shift-click any",
      ChatColor.WHITE + "stat to share it.")),
  ;

  public final List<String> context;

  PluginContext(List<String> context) {
    this.context = context;
  }
}
