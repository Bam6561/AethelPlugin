package me.dannynguyen.aethel.enums;

import me.dannynguyen.aethel.utility.ItemCreator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * PluginPlayerHead is an enum containing the plugin's player heads.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.7.12
 */
public enum PluginPlayerHead {
  BACKPACK_BROWN(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1MWU1MDU5ODk4MzhlMjcyODdlN2FmYmM3Zjk3ZTc5NmNhYjVmMzU5OGE3NjE2MGMxMzFjOTQwZDBjNSJ9fX0=")),
  BACKWARD_GRAY(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQxMzNmNmFjM2JlMmUyNDk5YTc4NGVmYWRjZmZmZWI5YWNlMDI1YzM2NDZhZGE2N2YzNDE0ZTVlZjMzOTQifX19")),
  BACKWARD_RED(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRmNWMyZjg5M2JkM2Y4OWNhNDA3MDNkZWQzZTQyZGQwZmJkYmE2ZjY3NjhjODc4OWFmZGZmMWZhNzhiZjYifX19")),
  CHISELED_BOOKSHELF(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk4ODQxOWRkNWIzODZmNjk4YTk2OTEzZGIxZDk3YzI0MThlMTZkNDE2ZDdmNDM5ZDQ4YWNkNDFlM2E0MzZjZSJ9fX0=")),
  CRAFTING_TABLE(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWYzMGZmYmMwMTEwZWZhMzRlMDMwODYwZGExOGM4YTFkNmIyMjNkZTBmMDBkOWU0YzVkMGNmYTdlY2ZhZmE0OCJ9fX0=")),
  FILE_EXPLORER(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzczZThiZDNjNDNjNDUxNGM3NjQ4MWNhMWRhZjU1MTQ5ZGZjOTNiZDFiY2ZhOGFiOTQzN2I5ZjdlYjMzOTJkOSJ9fX0=")),
  FORWARD_LIME(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjUyN2ViYWU5ZjE1MzE1NGE3ZWQ0OWM4OGMwMmI1YTlhOWNhN2NiMTYxOGQ5OTE0YTNkOWRmOGNjYjNjODQifX19")),
  QUESTION_MARK_WHITE(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM4ZWExZjUxZjI1M2ZmNTE0MmNhMTFhZTQ1MTkzYTRhZDhjM2FiNWU5YzZlZWM4YmE3YTRmY2I3YmFjNDAifX19")),
  STACK_OF_PAPER(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgwNjQ0MGY1NTg4NjQ5NDdkYzA5MzI2NTAwNmVhODBkNzE0NTI0NDQyYjhhMDA5MDZmMmZiMDc1MDc3Y2ViMyJ9fX0=")),
  TRASH_CAN(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmUwZmQxMDE5OWU4ZTRmY2RhYmNhZTRmODVjODU5MTgxMjdhN2M1NTUzYWQyMzVmMDFjNTZkMThiYjk0NzBkMyJ9fX0="));

  public final ItemStack head;

  PluginPlayerHead(ItemStack head) {
    this.head = head;
  }

  private enum Failure {
    PLUGIN_INVALID_PLAYER_HEAD_TEXTURE("[Aethel] Invalid player head texture: "),
    NOTIFICATION_ERROR(ChatColor.RED + "[!] Error"),
    INVALID_TEXTURE(ChatColor.RED + "Invalid texture.");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }

  /**
   * Creates a player head from provided texture data.
   *
   * @param textureData encoded texture
   * @return player head with texture
   */
  private static ItemStack createPlayerHead(String textureData) {
    PlayerProfile profile = createProfile(getUrlFromTextureData(textureData));
    if (profile != null) {
      ItemStack head = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta meta = (SkullMeta) head.getItemMeta();
      meta.setOwnerProfile(profile);
      head.setItemMeta(meta);
      return head;
    } else {
      return ItemCreator.createItem(Material.BARRIER, Failure.NOTIFICATION_ERROR.message,
          List.of(Failure.INVALID_TEXTURE.message));
    }
  }

  /**
   * Deserializes a url from encoded texture data.
   *
   * @param textureData encoded texture
   * @return texture url
   */
  private static URL getUrlFromTextureData(String textureData) {
    String urlString = new String(Base64.getDecoder().decode(textureData));
    URL url;
    try {
      url = new URL(urlString.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(),
          urlString.length() - "\"}}}".length()));
    } catch (MalformedURLException ex) {
      Bukkit.getLogger().warning(Failure.PLUGIN_INVALID_PLAYER_HEAD_TEXTURE + textureData);
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
  private static PlayerProfile createProfile(URL url) {
    if (url != null) {
      PlayerProfile profile = Bukkit.createPlayerProfile(UUID.fromString("58f8c6e4-8e24-4429-badc-ecf76de5bead"));
      PlayerTextures textures = profile.getTextures();
      textures.setSkin(url);
      profile.setTextures(textures);
      return profile;
    }
    return null;
  }
}
