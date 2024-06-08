package me.bam6561.aethelplugin.commands;

import me.bam6561.aethelplugin.enums.plugin.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Starting template for commands.
 * It'll do something. We just don't know what yet.
 *
 * @author Danny Nguyen
 * @version 1.23.13
 * @since 1.2.3
 */
public class Template implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public Template() {
  }

  /**
   * Executes the Template command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command parameters
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.template")) {
        new Request(user, args).readRequest();
      } else {
        user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Represents an unknown command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @author Danny Nguyen
   * @version 1.23.13
   * @since 1.23.13
   */
  private record Request(Player user, String[] args) {
    /**
     * Checks if the command request was formatted correctly before doing something.
     */
    private void readRequest() {
      switch (args.length) {
        default -> interpretParameters();
      }
    }

    /**
     * Either does something or another thing.
     */
    private void interpretParameters() {

    }
  }
}
