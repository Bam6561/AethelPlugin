package me.dannynguyen.aethel.creators;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

/**
 * ItemCreator creates ItemStacks with metadata.
 *
 * @author Danny Nguyen
 * @version 1.2.1
 * @since 1.1.5
 */
public class ItemCreator {
  /**
   * Creates a named item.
   *
   * @param material    item material
   * @param displayName item name
   * @return named item
   */
  public ItemStack createItem(Material material, String displayName) {
    ItemStack item = new ItemStack(material, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a player head with a custom texture.
   *
   * @param displayName item name
   * @param textureData encoded texture
   * @return custom texture player head
   */
  public ItemStack createPlayerHead(String displayName, String textureData) {
    PlayerProfile profile = createProfile(getUrlFromTextureData(textureData));
    ItemStack head = createItem(Material.PLAYER_HEAD, displayName);
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
    URL url = null;
    try {
      url = new URL(urlString.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(),
          urlString.length() - "\"}}}".length()));
    } catch (MalformedURLException ex) {
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
}
