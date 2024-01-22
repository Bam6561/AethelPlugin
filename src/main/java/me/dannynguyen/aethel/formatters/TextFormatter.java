package me.dannynguyen.aethel.formatters;

/**
 * TextFormatter is a utility class that formats text.
 *
 * @author Danny Nguyen
 * @version 1.6.16
 * @since 1.6.16
 */
public class TextFormatter {
  /**
   * Capitalizes the first character of every word.
   *
   * @param phrase phrase
   * @return proper phrase
   */
  public static String capitalizeProperly(String phrase) {
    phrase = phrase.replace("_", " ").toUpperCase();
    String[] words = phrase.split(" ");

    StringBuilder properPhrase = new StringBuilder();
    for (String word : words) {
      properPhrase.append(word.replace(word.substring(1), word.substring(1).toLowerCase())).append(" ");
    }
    return properPhrase.toString().trim();
  }
}
