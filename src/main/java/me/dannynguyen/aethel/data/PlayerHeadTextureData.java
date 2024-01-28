package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.enums.PluginPlayerHeadTexture;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

/**
 * PlayerHeadTextureData stores player head textures in memory.
 *
 * @author Danny Nguyen
 * @version 1.7.11
 * @since 1.2.1
 */
public class PlayerHeadTextureData {
  private final HashMap<String, ItemStack> headsMap = new HashMap<>();

  /**
   * Loads player head textures into memory.
   */
  public void loadPlayerHeads() {
    for (PluginPlayerHeadTexture texture : PluginPlayerHeadTexture.values()) {
      headsMap.put(texture.name(), createPlayerHeadTexture(texture.texture));
    }
  }

  /**
   * Creates a player head with a custom texture.
   *
   * @param textureData encoded texture
   * @return custom texture player head
   */
  private ItemStack createPlayerHeadTexture(String textureData) {
    PlayerProfile profile = createProfile(getUrlFromTextureData(textureData));
    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) head.getItemMeta();
    meta.setOwnerProfile(profile);
    head.setItemMeta(meta);
    return head;
  }

  /**
   * Deserializes a url from encoded texture data.
   *
   * @param textureData encoded texture
   * @return texture url
   * @throws MalformedURLException invalid url
   */
  private URL getUrlFromTextureData(String textureData) {
    String urlString = new String(Base64.getDecoder().decode(textureData));
    URL url;
    try {
      url = new URL(urlString.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(),
          urlString.length() - "\"}}}".length()));
    } catch (MalformedURLException ex) {
      Bukkit.getLogger().warning("[Aethel] [!] Invalid player head texture: " + textureData);
      return null;
    }
    return url;
  }

  /**
   * Creates a player profile.
   *
   * @param url texture url
   * @return player profile with desired texture
   */
  private PlayerProfile createProfile(URL url) {
    PlayerProfile profile = Bukkit.createPlayerProfile(UUID.fromString("58f8c6e4-8e24-4429-badc-ecf76de5bead"));
    PlayerTextures textures = profile.getTextures();
    textures.setSkin(url);
    profile.setTextures(textures);
    return profile;
  }

  public HashMap<String, ItemStack> getHeadsMap() {
    return this.headsMap;
  }
}
