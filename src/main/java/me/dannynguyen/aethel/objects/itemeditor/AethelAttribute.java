package me.dannynguyen.aethel.objects.itemeditor;

/**
 * AethelAttribute is an object that relates an Aethel attribute to its value.
 *
 * @author Danny Nguyen
 * @version 1.7.5
 * @since 1.7.5
 */
public class AethelAttribute {
  private String type;
  private String slot;
  private Double value;

  public AethelAttribute(String type, String slot) {
    this.type = type;
    this.slot = slot;
  }

  public AethelAttribute(String type, String slot, Double value) {
    this.type = type;
    this.slot = slot;
    this.value = value;
  }

  public String getType() {
    return this.type;
  }

  public String getSlot() {
    return this.slot;
  }

  public Double getValue() {
    return this.value;
  }
}
