package me.dannynguyen.aethel.objects;

/**
 * PlayerHead is an enum relating player head textures with their reference name.
 *
 * @author Danny Nguyen
 * @version 1.4.0
 * @since 1.2.1
 */
public enum PlayerHead {
  RED_BACKWARD("Red Backward", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRmNWMyZjg5M2JkM2Y4OWNhNDA3MDNkZWQzZTQyZGQwZmJkYmE2ZjY3NjhjODc4OWFmZGZmMWZhNzhiZjYifX19"),
  LIME_FORWARD("Lime Forward", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjUyN2ViYWU5ZjE1MzE1NGE3ZWQ0OWM4OGMwMmI1YTlhOWNhN2NiMTYxOGQ5OTE0YTNkOWRmOGNjYjNjODQifX19"),
  GRAY_BACKWARD("Gray Backward", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQxMzNmNmFjM2JlMmUyNDk5YTc4NGVmYWRjZmZmZWI5YWNlMDI1YzM2NDZhZGE2N2YzNDE0ZTVlZjMzOTQifX19"),
  CRAFTING_TABLE("Crafting Table", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWYzMGZmYmMwMTEwZWZhMzRlMDMwODYwZGExOGM4YTFkNmIyMjNkZTBmMDBkOWU0YzVkMGNmYTdlY2ZhZmE0OCJ9fX0="),
  FILE_EXPLORER("File Explorer", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzczZThiZDNjNDNjNDUxNGM3NjQ4MWNhMWRhZjU1MTQ5ZGZjOTNiZDFiY2ZhOGFiOTQzN2I5ZjdlYjMzOTJkOSJ9fX0="),
  TRASH_CAN("Trash Can", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmUwZmQxMDE5OWU4ZTRmY2RhYmNhZTRmODVjODU5MTgxMjdhN2M1NTUzYWQyMzVmMDFjNTZkMThiYjk0NzBkMyJ9fX0="),
  STACK_OF_PAPER("Stack of Paper", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgwNjQ0MGY1NTg4NjQ5NDdkYzA5MzI2NTAwNmVhODBkNzE0NTI0NDQyYjhhMDA5MDZmMmZiMDc1MDc3Y2ViMyJ9fX0="),
  WHITE_QUESTION_MARK("White Question Mark", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmM4ZWExZjUxZjI1M2ZmNTE0MmNhMTFhZTQ1MTkzYTRhZDhjM2FiNWU5YzZlZWM4YmE3YTRmY2I3YmFjNDAifX19"),
  BROWN_BACKPACK("Brown Backpack", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1MWU1MDU5ODk4MzhlMjcyODdlN2FmYmM3Zjk3ZTc5NmNhYjVmMzU5OGE3NjE2MGMxMzFjOTQwZDBjNSJ9fX0=");

  private String name;
  private String textureData;

  PlayerHead(String name, String textureData) {
    this.name = name;
    this.textureData = textureData;
  }

  public String getName() {
    return this.name;
  }

  public String getTextureData() {
    return this.textureData;
  }
}
