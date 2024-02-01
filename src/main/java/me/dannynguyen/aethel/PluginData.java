package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.aethelItems.AethelItemsData;
import me.dannynguyen.aethel.commands.forge.ForgeData;
import me.dannynguyen.aethel.commands.itemeditor.ItemEditorData;
import me.dannynguyen.aethel.commands.playerstats.PlayerStatsData;
import me.dannynguyen.aethel.commands.showitem.ShowItemData;
import me.dannynguyen.aethel.systems.RpgData;

/**
 * PluginData stores the plugin's resources in memory.
 *
 * @author Danny Nguyen
 * @version 1.8.10
 * @since 1.1.7
 */
public class PluginData {
  public static final AethelItemsData aethelItemsData = new AethelItemsData();
  public static final ItemEditorData itemEditorData = new ItemEditorData();
  public static final ForgeData forgeData = new ForgeData();
  public static final PlayerStatsData playerStatsData = new PlayerStatsData();
  public static final ShowItemData showItemData = new ShowItemData();

  public static final RpgData rpgData = new RpgData();
}
