package me.dannynguyen.aethel;

import me.dannynguyen.aethel.data.ForgeRecipeData;
import me.dannynguyen.aethel.data.PlayerHeadData;

/**
 * AethelResources represents the plugin's resources loaded in memory as an object.
 *
 * @author Danny Nguyen
 * @version 1.2.1
 * @since 1.1.7
 */
public class AethelResources {
  private String resourceDirectory = "./plugins/Aethel";
  private String forgeRecipeDirectory = resourceDirectory + "/forge";
  private ForgeRecipeData forgeRecipeData = new ForgeRecipeData();
  private PlayerHeadData playerHeadData = new PlayerHeadData();

  public String getResourceDirectory() {
    return this.resourceDirectory;
  }

  public String getForgeRecipeDirectory() {
    return this.forgeRecipeDirectory;
  }

  public ForgeRecipeData getForgeRecipeData() {
    return this.forgeRecipeData;
  }

  public PlayerHeadData getPlayerHeadData() {
    return this.playerHeadData;
  }
}
