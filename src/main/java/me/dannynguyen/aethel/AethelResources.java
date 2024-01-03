package me.dannynguyen.aethel;

import me.dannynguyen.aethel.data.AethelItemData;
import me.dannynguyen.aethel.data.ForgeRecipeData;
import me.dannynguyen.aethel.data.PlayerHeadData;

/**
 * AethelResources represents the plugin's resources loaded in memory as an object.
 *
 * @author Danny Nguyen
 * @version 1.3.2
 * @since 1.1.7
 */
public class AethelResources {
  private String resourceDirectory = "./plugins/Aethel";
  private String forgeRecipeDirectory = resourceDirectory + "/forge";
  private String aethelItemDirectory = resourceDirectory + "/aitem";
  private ForgeRecipeData forgeRecipeData = new ForgeRecipeData();
  private AethelItemData aethelItemData = new AethelItemData();
  private PlayerHeadData playerHeadData = new PlayerHeadData();

  public String getResourceDirectory() {
    return this.resourceDirectory;
  }

  public String getForgeRecipeDirectory() {
    return this.forgeRecipeDirectory;
  }

  public String getAethelItemDirectory() {
    return this.aethelItemDirectory;
  }

  public ForgeRecipeData getForgeRecipeData() {
    return this.forgeRecipeData;
  }

  public AethelItemData getAethelItemData() {
    return this.aethelItemData;
  }

  public PlayerHeadData getPlayerHeadData() {
    return this.playerHeadData;
  }
}
