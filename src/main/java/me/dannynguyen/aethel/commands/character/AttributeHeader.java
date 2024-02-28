package me.dannynguyen.aethel.commands.character;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an attribute header for an item's lore.
 *
 * @author Danny Nguyen
 * @version 1.13.1
 * @since 1.13.1
 */
public class AttributeHeader {
  /**
   * Header line number.
   */
  private final int line;

  /**
   * Associated header text.
   */
  private final List<String> text;

  /**
   * Creates a new AttributeHeader.
   *
   * @param line header line number
   */
  public AttributeHeader(int line) {
    this.line = line;
    this.text = new ArrayList<>();
  }

  /**
   * Gets the header's line number.
   *
   * @return header line number
   */
  public int getLine() {
    return this.line;
  }

  /**
   * Gets the header's associated text.
   *
   * @return header's associated text
   */
  public List<String> getText() {
    return this.text;
  }
}
