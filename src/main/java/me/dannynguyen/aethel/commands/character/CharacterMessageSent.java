package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import me.dannynguyen.aethel.rpg.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Message sent listener for Character text inputs.
 *
 * @author Danny Nguyen
 * @version 1.19.0
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
   * Sets the player's active ability crouch bind.
   */
  public void setActiveAbilityCrouchBind() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    Settings settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings();
    RpgEquipmentSlot slot = pluginPlayer.getSlot();
    try {
      int heldSlot = Integer.parseInt(e.getMessage());
      if (0 < heldSlot && heldSlot < 10) {
        settings.setActiveAbilityCrouchBind(slot, heldSlot - 1);
        user.sendMessage(ChatColor.GREEN + "[Set " + ChatColor.AQUA + slot.getProperName() + " Active Ability " + ChatColor.GREEN + "Crouch Bind] " + ChatColor.WHITE + (heldSlot));
      } else {
        user.sendMessage(Message.INVALID_VALUE.getMessage());
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new SettingsMenu(user).getMainMenu());
      pluginPlayer.setMenu(MenuEvent.Menu.CHARACTER_SETTINGS);
    });
  }
}
