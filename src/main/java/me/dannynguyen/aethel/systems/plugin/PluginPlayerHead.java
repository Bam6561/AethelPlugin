package me.dannynguyen.aethel.systems.plugin;

import me.dannynguyen.aethel.utility.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Plugin player head textures.
 *
 * @author Danny Nguyen
 * @version 1.11.9
 * @since 1.11.9
 **/
public enum PluginPlayerHead {
  /**
   * Brown backpack.
   */
  BACKPACK_BROWN(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1MWU1MDU5ODk4MzhlMjcyODdlN2FmYmM3Zjk3ZTc5NmNhYjVmMzU5OGE3NjE2MGMxMzFjOTQwZDBjNSJ9fX0=")),

  /**
   * Gray backpack.
   */
  BACKWARD_GRAY(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQxMzNmNmFjM2JlMmUyNDk5YTc4NGVmYWRjZmZmZWI5YWNlMDI1YzM2NDZhZGE2N2YzNDE0ZTVlZjMzOTQifX19")),

  /**
   * Red arrow pointing to the left.
   */
  BACKWARD_RED(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRmNWMyZjg5M2JkM2Y4OWNhNDA3MDNkZWQzZTQyZGQwZmJkYmE2ZjY3NjhjODc4OWFmZGZmMWZhNzhiZjYifX19")),

  /**
   * Chiseled bookshelf.
   */
  CHISELED_BOOKSHELF(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk4ODQxOWRkNWIzODZmNjk4YTk2OTEzZGIxZDk3YzI0MThlMTZkNDE2ZDdmNDM5ZDQ4YWNkNDFlM2E0MzZjZSJ9fX0=")),

  /**
   * Crafting table.
   */
  CRAFTING_TABLE(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWYzMGZmYmMwMTEwZWZhMzRlMDMwODYwZGExOGM4YTFkNmIyMjNkZTBmMDBkOWU0YzVkMGNmYTdlY2ZhZmE0OCJ9fX0=")),

  /**
   * File explorer icon.
   */
  FILE_EXPLORER(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzczZThiZDNjNDNjNDUxNGM3NjQ4MWNhMWRhZjU1MTQ5ZGZjOTNiZDFiY2ZhOGFiOTQzN2I5ZjdlYjMzOTJkOSJ9fX0=")),

  /**
   * Lime arrow pointing right.
   */
  FORWARD_LIME(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjUyN2ViYWU5ZjE1MzE1NGE3ZWQ0OWM4OGMwMmI1YTlhOWNhN2NiMTYxOGQ5OTE0YTNkOWRmOGNjYjNjODQifX19")),

  /**
   * White question mark.
   */
  QUESTION_MARK_WHITE(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM4ZWExZjUxZjI1M2ZmNTE0MmNhMTFhZTQ1MTkzYTRhZDhjM2FiNWU5YzZlZWM4YmE3YTRmY2I3YmFjNDAifX19")),

  /**
   * Stack of paper.
   */
  STACK_OF_PAPER(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgwNjQ0MGY1NTg4NjQ5NDdkYzA5MzI2NTAwNmVhODBkNzE0NTI0NDQyYjhhMDA5MDZmMmZiMDc1MDc3Y2ViMyJ9fX0=")),

  /**
   * Trash can.
   */
  TRASH_CAN(createPlayerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmUwZmQxMDE5OWU4ZTRmY2RhYmNhZTRmODVjODU5MTgxMjdhN2M1NTUzYWQyMzVmMDFjNTZkMThiYjk0NzBkMyJ9fX0="));

  /**
   * Player head ItemStack.
   */
  private final ItemStack head;

  /**
   * Associates the player head with a texture.
   *
   * @param head player head
   */
  PluginPlayerHead(@NotNull ItemStack head) {
    this.head = Objects.requireNonNull(head, "Null item");
  }

  /**
   * Gets the player head.
   *
   * @return player head
   */
  @NotNull
  public ItemStack getHead() {
    return this.head;
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
      return ItemCreator.createItem(Material.BARRIER, net.md_5.bungee.api.ChatColor.RED + "[!] Error", List.of(net.md_5.bungee.api.ChatColor.RED + "Invalid texture."));
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
      Bukkit.getLogger().warning("[Aethel] Invalid player head texture: " + textureData);
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
