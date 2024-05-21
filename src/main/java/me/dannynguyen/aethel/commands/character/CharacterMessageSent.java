package me.dannynguyen.aethel.commands.character;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.plugin.MenuInput;
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
 * @version 1.25.8
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
   * {@link MenuInput}.
   */
  private final MenuInput menuInput;

  /**
   * User's {@link Settings}.
   */
  private final Settings settings;

  /**
   * {@link RpgEquipmentSlot}
   */
  private final RpgEquipmentSlot slot;

  /**
   * Hotbar values as String.
   */
  StringBuilder hotbarBuilder = new StringBuilder();

  /**
   * Hotbar values as Set.
   */
  private Set<Integer> hotbarSet = new HashSet<>();

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
    this.menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    this.settings = Plugin.getData().getRpgSystem().getRpgPlayers().get(uuid).getSettings();
    this.slot = menuInput.getSlot();
  }

  /**
   * Sets the player's {@link me.dannynguyen.aethel.rpg.Equipment}
   * {@link me.dannynguyen.aethel.rpg.abilities.ActiveAbility} right click bind.
   */
  public void setActiveAbilityRightClickBind() {
    if (!readAbilityBindInput()) {
      return;
    }
    settings.setActiveAbilityRightClickBind(slot, hotbarSet);
    user.sendMessage(ChatColor.GREEN + "[Set " + ChatColor.AQUA + slot.getProperName() + " Active Ability " + ChatColor.GREEN + "Right Click Binds] " + ChatColor.WHITE + hotbarBuilder.toString().trim());
    returnToSettings();
  }

  /**
   * Sets the player's {@link me.dannynguyen.aethel.rpg.Equipment}
   * {@link me.dannynguyen.aethel.rpg.abilities.ActiveAbility} crouch bind.
   */
  public void setActiveAbilityCrouchBind() {
    if (!readAbilityBindInput()) {
      return;
    }
    settings.setActiveAbilityCrouchBind(slot, hotbarSet);
    user.sendMessage(ChatColor.GREEN + "[Set " + ChatColor.AQUA + slot.getProperName() + " Active Ability " + ChatColor.GREEN + "Crouch Binds] " + ChatColor.WHITE + hotbarBuilder.toString().trim());
    returnToSettings();
  }

  /**
   * Reads user provided ability bind inputs.
   *
   * @return if ability bind input is valid
   */
  private boolean readAbilityBindInput() {
    for (String hotbarString : e.getMessage().split(" ")) {
      int hotbarSlot;
      try {
        hotbarSlot = Integer.parseInt(hotbarString);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_VALUE.getMessage());
        returnToSettings();
        return false;
      }
      if (0 < hotbarSlot && hotbarSlot < 10) {
        hotbarBuilder.append(hotbarSlot).append(" ");
        hotbarSet.add(hotbarSlot - 1);
      }
    }
    return true;
  }

  /**
   * Returns the user to the {@link SettingsMenu}.
   */
  private void returnToSettings() {
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new SettingsMenu(user).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.CHARACTER_SETTINGS);
    });
  }
}
