# Changelog

## [1.0.0] (Minecraft 1.21 - Fabric)

### Fixed

- Fixed an item overflow / serialization bug when storing more than 64 identical items in an upgraded bundle. Identical items inside `BundleContentsComponent` are now only merged into existing partial stacks up to `stack.getMaxCount()` (64), and any remaining count is automatically split into separate standard-sized `ItemStack` entries (`1 <= count <= 64`).
- Legacy or decoded `BundleContentsComponent` instances containing oversized stacks (`count > 64`) are automatically split into standard-sized stacks on load and builder initialization.

### Changed

- Upgraded **Bundle Upgrade** to Minecraft `1.21` (`yarn_mappings=1.21+build.9`, `fabric-api=0.102.0+1.21`, `fabric-loader=0.20.1`) on Java 21 (`Identifier.of`, `CraftingRecipeInput`, singular `data/bundleupgrade/recipe/` directory, `DataComponentTypes.BUNDLE_CONTENTS`, `BundleContentsComponent`, and `Fraction` weight calculations).
- Preserved Vanilla 1.21 bundle mouse controls (`ClickType.RIGHT` to insert and extract) and improved holding Right-Click while dragging a bundle across empty inventory slots so each empty slot smoothly receives 1 extracted stack (`<= 64`).
- Standardized output artifact naming to `bundleupgrade-fabric-1.21-1.0.0.jar`.

### Features

- Added 5 sequential bundle tiers with cumulative capacity expansion:
  - Copper Bundle (+10 -> 74 units)
  - Iron Bundle (+20 -> 94 units)
  - Gold Bundle (+30 -> 124 units)
  - Emerald Bundle (+40 -> 164 units)
  - Diamond Bundle (+50 -> 214 units)
- Implemented Base-64 weight calculation system (stack of 64 = 1 unit, stack of 16 = 4 units, unstackable = 64 units).
- Unlocked the hardcoded `Fraction.ONE` occupancy ceiling via Mixins into `BundleContentsComponent$Builder`.
- Dynamic and proportional item fullness bar scaling (`getItemBarStep`, `getItemBarColor`) and tooltip slot grid scaling without premature blocked-slot indicators.
- Real-time capacity tooltip displaying `Current / Max` units.
- 3x3 shaped crafting recipes with automatic preservation of stored items (`BUNDLE_CONTENTS`) and custom name (`CUSTOM_NAME`).
- Procedural pixel-art textures (closed bundle) and custom 128x128 mod icon.
- Bilingual localization in English (`en_us.json`) and Vietnamese (`vi_vn.json`).

---

## [1.0.0] (Minecraft 1.20.6 - Fabric)

### Fixed

- Fixed an item overflow / serialization bug when storing more than 64 identical items in an upgraded bundle. Identical items inside `BundleContentsComponent` are now only merged into existing partial stacks up to `stack.getMaxCount()` (64), and any remaining count is automatically split into separate standard-sized `ItemStack` entries (`1 <= count <= 64`).
- Legacy or decoded `BundleContentsComponent` instances containing oversized stacks (`count > 64`) are automatically split into standard-sized stacks on load and builder initialization.

### Changed

- Upgraded **Bundle Upgrade** to Minecraft `1.20.6` (`yarn_mappings=1.20.6+build.3`, `fabric-api=0.100.8+1.20.6`, `fabric-loader=0.20.1`) on Java 21 (`DataComponentTypes.BUNDLE_CONTENTS`, `BundleContentsComponent`, and `Fraction` weight calculations).
- Preserved Vanilla 1.20.6 bundle mouse controls (`ClickType.RIGHT` to insert and extract) and improved holding Right-Click while dragging a bundle across empty inventory slots so each empty slot smoothly receives 1 extracted stack (`<= 64`).
- Standardized output artifact naming to `bundleupgrade-fabric-1.20.6-1.0.0.jar`.

### Features

- Added 5 sequential bundle tiers with cumulative capacity expansion:
  - Copper Bundle (+10 -> 74 units)
  - Iron Bundle (+20 -> 94 units)
  - Gold Bundle (+30 -> 124 units)
  - Emerald Bundle (+40 -> 164 units)
  - Diamond Bundle (+50 -> 214 units)
- Implemented Base-64 weight calculation system (stack of 64 = 1 unit, stack of 16 = 4 units, unstackable = 64 units).
- Unlocked the hardcoded `Fraction.ONE` occupancy ceiling via Mixins into `BundleContentsComponent$Builder`.
- Dynamic and proportional item fullness bar scaling (`getItemBarStep`, `getItemBarColor`) and tooltip slot grid scaling without premature blocked-slot indicators.
- Real-time capacity tooltip displaying `Current / Max` units.
- 3x3 shaped crafting recipes with automatic preservation of stored items (`BUNDLE_CONTENTS`) and custom name (`CUSTOM_NAME`).
- Procedural pixel-art textures (closed bundle) and custom 128x128 mod icon.
- Bilingual localization in English (`en_us.json`) and Vietnamese (`vi_vn.json`).

---

## [1.0.0] (Minecraft 1.20.5 - Fabric)

### Fixed

- Fixed an item overflow / serialization bug when storing more than 64 identical items in an upgraded bundle. Identical items inside `BundleContentsComponent` are now only merged into existing partial stacks up to `stack.getMaxCount()` (64), and any remaining count is automatically split into separate standard-sized `ItemStack` entries (`1 <= count <= 64`).
- Legacy or decoded `BundleContentsComponent` instances containing oversized stacks (`count > 64`) are automatically split into standard-sized stacks on load and builder initialization.

### Changed

- Ported **Bundle Upgrade** to Minecraft `1.20.5` (`DataComponentTypes.BUNDLE_CONTENTS`, `BundleContentsComponent`, and `Fraction` weight calculations) on Java 21.
- Preserved Vanilla 1.20.5 bundle mouse controls (`ClickType.RIGHT` to insert and extract) and improved holding Right-Click while dragging a bundle across empty inventory slots so each empty slot smoothly receives 1 extracted stack (`<= 64`).
- Standardized output artifact naming to `bundleupgrade-fabric-1.20.5-1.0.0.jar`.
