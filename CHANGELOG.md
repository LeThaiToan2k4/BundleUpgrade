# Changelog

## [1.0.1] (Minecraft 1.20.4 - Fabric)

### Fixed

- Fixed an item overflow / NBT stacking bug when storing more than 64 identical items in an upgraded bundle. Identical items in the `"Items"` `NbtList` are now only merged into existing matching entries up to `stack.getMaxCount()` (64), and any remaining count is automatically split into separate `ItemStack` NBT entries (`Count <= 64`).
- Legacy bundles containing oversized NBT stacks (`Count > 64`) are automatically split into standard-sized stacks on extraction and tooltip display.
- Nested upgraded bundles now properly compute occupancy weight (`4 + getBundleOccupancy`).

### Changed

- Preserved Vanilla 1.20.4 bundle mouse controls (`ClickType.RIGHT` to insert and extract) and improved holding Right-Click while dragging a bundle across empty inventory slots so each empty slot smoothly receives 1 extracted stack (`<= 64`).
- Standardized output artifact naming to `bundleupgrade-fabric-1.20.4-1.0.1.jar`.

## 1.0.0 (Minecraft 1.20.4 - Fabric)

- Initial release of **Bundle Upgrade** (`bundleupgrade`) by `thaistoanf`.
- Added 5 sequential bundle tiers with cumulative capacity expansion:
  - Copper Bundle (+10 -> 74 units)
  - Iron Bundle (+20 -> 94 units)
  - Gold Bundle (+30 -> 124 units)
  - Emerald Bundle (+40 -> 164 units)
  - Diamond Bundle (+50 -> 214 units)
- Implemented Base-64 weight calculation system (stack of 64 = 1 unit, stack of 16 = 4 units, unstackable = 64 units).
- Unlocked the hardcoded 64-unit occupancy ceiling via Mixins into `BundleItem`.
- Dynamic and proportional item fullness bar scaling (`getItemBarStep`, `getItemBarColor`) and tooltip progress bar without overflow.
- Real-time capacity tooltip displaying `Current / Max` units.
- 3x3 shaped crafting recipes with automatic preservation of stored items and custom name (item NBT).
- Full compatibility with 1.20.4 interactions (right-click insert/extract, cursor dragging, scroll wheel item selection).
- Procedural pixel-art textures (closed bundle) and custom 128x128 mod icon.
- Bilingual localization in English (`en_us.json`) and Vietnamese (`vi_vn.json`).
