package me.dannynguyen.aethel.objects;

/**
 * PlayerHead is an enum relating custom player head textures with their contextual name.
 *
 * @author Danny Nguyen
 * @version 1.2.1
 * @since 1.2.1
 */
public enum PlayerHead {
  ARROW_LEFT("Previous Page", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0="),
  ARROW_RIGHT("Next Page", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19"),
  STACK_OF_PAPER("Create Recipe", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgwNjQ0MGY1NTg4NjQ5NDdkYzA5MzI2NTAwNmVhODBkNzE0NTI0NDQyYjhhMDA5MDZmMmZiMDc1MDc3Y2ViMyJ9fX0="),
  FILE_EXPLORER("Modify Recipe", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzczZThiZDNjNDNjNDUxNGM3NjQ4MWNhMWRhZjU1MTQ5ZGZjOTNiZDFiY2ZhOGFiOTQzN2I5ZjdlYjMzOTJkOSJ9fX0="),
  TRASHCAN("Delete Recipe", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmUwZmQxMDE5OWU4ZTRmY2RhYmNhZTRmODVjODU5MTgxMjdhN2M1NTUzYWQyMzVmMDFjNTZkMThiYjk0NzBkMyJ9fX0="),
  CRAFTING_TABLE("Save Recipe", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWYzMGZmYmMwMTEwZWZhMzRlMDMwODYwZGExOGM4YTFkNmIyMjNkZTBmMDBkOWU0YzVkMGNmYTdlY2ZhZmE0OCJ9fX0="),
  BOOKSHELF("Back", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk4ODQxOWRkNWIzODZmNjk4YTk2OTEzZGIxZDk3YzI0MThlMTZkNDE2ZDdmNDM5ZDQ4YWNkNDFlM2E0MzZjZSJ9fX0=");

  private String name;
  private String textureData;

  PlayerHead(String id, String textureData) {
    this.name = id;
    this.textureData = textureData;
  }

  public String getName() {
    return this.name;
  }

  public String getTextureData() {
    return this.textureData;
  }
}
