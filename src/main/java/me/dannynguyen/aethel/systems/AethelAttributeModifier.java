package me.dannynguyen.aethel.systems;

/**
 * AethelAttributeModifier is an object relating
 * an Aethel attribute modifier with its value.
 *
 * @author Danny Nguyen
 * @version 1.8.9
 * @since 1.8.9
 */
public class AethelAttributeModifier {
  private final String name;
  private double value;

  public AethelAttributeModifier(String name, double value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return this.name;
  }

  public double getValue() {
    return this.value;
  }

  private double setValue() {
    return this.value;
  }
}
