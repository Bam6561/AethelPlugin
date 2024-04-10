package me.dannynguyen.aethel.plugin;

/**
 * Represents a player's plugin metadata.
 *
 * @author Danny Nguyen
 * @version 1.19.0
 * @since 1.17.16
 */
public class PluginPlayer {
  /**
   * {@link MenuInput}
   */
  private final MenuInput menuInput = new MenuInput();

  /**
   * No parameter constructor.
   */
  public PluginPlayer() {
  }

  /**
   * Gets the {@link MenuInput}.
   *
   * @return {@link MenuInput}
   */
  public MenuInput getMenuInput() {
    return this.menuInput;
  }
}
