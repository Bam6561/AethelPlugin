package me.bam6561.aethelplugin.commands.playerstat;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.listeners.MenuListener;
import me.bam6561.aethelplugin.plugin.MenuInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Message sent listener for {@link StatCommand} text inputs.
 * <p>
 * Called with {@link me.bam6561.aethelplugin.listeners.MessageListener}.
 *
 * @author Danny Nguyen
 * @version 1.26.1
 * @since 1.26.1
 */
public class StatMessageSent {
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
   * Associates a message sent event with its user in the context of searching
   * for a substatistic in an {@link StatMenu} through {@link StatMenuClick}.
   *
   * @param e message sent event
   */
  public StatMessageSent(@NotNull AsyncPlayerChatEvent e) {
    this.e = Objects.requireNonNull(e, "Null message sent event");
    this.user = e.getPlayer();
    this.uuid = user.getUniqueId();
  }

  /**
   * Searches for a matching substatistic from a user input search term.
   */
  public void searchSubstat() {
    if (e.getMessage().split(" ").length > 1) {
      user.sendMessage(ChatColor.RED + "Provide only one search term. Please try again.");
      return;
    }

    MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    String category = menuInput.getCategory();

    Map<String, Integer> searchTerms;
    switch (category) {
      case "Materials" -> searchTerms = StatMenu.StatCategory.getMaterialSearchTerms();
      case "Entity Types" -> searchTerms = StatMenu.StatCategory.getEntityTypeSearchTerms();
      default -> searchTerms = null;
    }

    if (!searchTerms.containsKey(e.getMessage().toLowerCase())) {
      user.sendMessage(ChatColor.RED + "No matches found. Please try again.");
      return;
    }

    String owner = Bukkit.getOfflinePlayer(menuInput.getTarget()).getName();
    int pageRequest = searchTerms.get(e.getMessage().toLowerCase());

    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new StatMenu(user, owner).getCategoryPage(category, pageRequest));
      menuInput.setMenu(MenuListener.Menu.PLAYERSTAT_SUBSTAT);
    });
  }
}
