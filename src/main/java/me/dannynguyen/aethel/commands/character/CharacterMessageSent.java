package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import me.dannynguyen.aethel.rpg.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Message sent listener for Character text inputs.
 *
 * @author Danny Nguyen
 * @version 1.20.2
 * @since 1.19.0
 */
public class CharacterMessageSent {
  /**
   * Message sent event.
   */
  private final AsyncPlayerChatEvent e;

  /**
   * Player who sent the message.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * Associates a message sent event with its user and current editing
   * setting in the context of using a {@link CharacterCommand} menu.
   *
   * @param e message sent event
   */
  public CharacterMessageSent(@NotNull AsyncPlayerChatEvent e) {
    this.e = Objects.requireNonNull(e, "Null message sent event");
    this.user = e.getPlayer();
    this.uuid = user.getUniqueId();
  }

  /**
   * Sets the player's active ability bind.
   */
  public void setActiveAbilityBind() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings();
    RpgEquipmentSlot slot = pluginPlayer.getSlot();

    StringBuilder hotbarBuilder = new StringBuilder();
    Set<Integer> hotbarSet = new HashSet<>();
    for (String hotbarString : e.getMessage().split(" ")) {
      int hotbarSlot;
      try {
        hotbarSlot = Integer.parseInt(hotbarString);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_VALUE.getMessage());
        Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
          user.openInventory(new SettingsMenu(user).getMainMenu());
          pluginPlayer.setMenu(MenuListener.Menu.CHARACTER_SETTINGS);
        });
        return;
      }
      if (0 < hotbarSlot && hotbarSlot < 10) {
        hotbarBuilder.append(hotbarSlot).append(" ");
        hotbarSet.add(hotbarSlot - 1);
      }
    }
    settings.setActiveAbilityBind(slot, hotbarSet);
    user.sendMessage(ChatColor.GREEN + "[Set " + ChatColor.AQUA + slot.getProperName() + " Active Ability " + ChatColor.GREEN + "Binds] " + ChatColor.WHITE + hotbarBuilder.toString().trim());
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new SettingsMenu(user).getMainMenu());
      pluginPlayer.setMenu(MenuListener.Menu.CHARACTER_SETTINGS);
    });
  }
}
