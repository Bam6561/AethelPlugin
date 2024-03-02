package me.dannynguyen.aethel.utility;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Formats text.
 *
 * @author Danny Nguyen
 * @version 1.10.0
 * @since 1.6.16
 */
public class TextFormatter {
  /**
   * Utility methods only.
   */
  private TextFormatter() {
  }

  /**
   * Formats a phrase like an enum.
   *
   * @param phrase phrase
   * @return enum formatted phrase
   */
  public static String formatEnum(@NotNull String phrase) {
    Objects.requireNonNull(phrase, "Null phrase");
    return phrase.replace(" ", "_").toUpperCase();
  }

  /**
   * Formats a phrase like an id.
   *
   * @param phrase phrase
   * @return id formatted phrase
   */
  public static String formatId(@NotNull String phrase) {
    Objects.requireNonNull(phrase, "Null phrase");
    return phrase.replace(" ", "_").toLowerCase();
  }

  /**
   * Capitalizes the first character of the word.
   *
   * @param word word
   * @return proper word
   */
  public static String capitalizeWord(@NotNull String word) {
    Objects.requireNonNull(word, "Null word");
    return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
  }

  /**
   * Capitalizes the first character of every word.
   *
   * @param phrase phrase
   * @return proper phrase
   */
  public static String capitalizePhrase(@NotNull String phrase) {
    Objects.requireNonNull(phrase, "Null phrase");
    phrase = phrase.replace("_", " ").toUpperCase();
    String[] words = phrase.split(" ");

    StringBuilder properPhrase = new StringBuilder();
    for (String word : words) {
      properPhrase.append(capitalizeWord(word)).append(" ");
    }
    return properPhrase.toString().trim();
  }

  /**
   * Capitalizes the first character of every word with an extra delimiter.
   *
   * @param phrase    phrase
   * @param delimiter 2nd delimiter
   * @return proper phrase
   */
  public static String capitalizePhrase(@NotNull String phrase, @NotNull String delimiter) {
    Objects.requireNonNull(phrase, "Null phrase");
    Objects.requireNonNull(delimiter, "Null delimiter");
    phrase = phrase.replace("_", " ").toUpperCase();
    phrase = phrase.replace(delimiter, " ").toUpperCase();
    String[] words = phrase.split(" ");

    StringBuilder properPhrase = new StringBuilder();
    for (String word : words) {
      properPhrase.append(word.replace(word.substring(1), word.substring(1).toLowerCase())).append(" ");
    }
    return properPhrase.toString().trim();
  }
}
