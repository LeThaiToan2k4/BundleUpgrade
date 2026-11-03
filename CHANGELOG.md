# Changelog

## [1.0.1] (Minecraft 1.21.11 - Fabric)

### Fixed

- Fixed an item overflow / serialization bug when storing more than 64 identical items in an upgraded bundle. Identical items used to be merged into a single stack above its max stack size (breaking the `ItemStack` codec limit of 99 and allowing more than one stack to be pulled out at once). Excess items are now automatically split into multiple standard-sized stacks inside the bundle:
  - Existing partial stacks are topped up only to their max stack size (e.g. 64).
  - Any remainder is stored as new stacks of at most the max stack size.
  - Bundles saved by 1.0.0 that contain an oversized stack are repaired automatically when loaded or first interacted with.
  - Extracting from a bundle now always returns at most one standard stack.
- Using the bundle in the world (holding right-click) now drops every stored stack in a single hold, even for high-tier bundles holding many stacks.

### Changed

- Synchronized bundle inventory controls with Vanilla 1.21.2+:
  - Left-click to insert (item on cursor onto the bundle, or bundle on cursor onto an item).
  - Right-click to extract one stack (the stack selected in the tooltip, or the first stack).
  - Right-click and drag the bundle across empty slots to place one stack into each slot.
  - Mouse scroll wheel over the bundle tooltip to select which stack to extract next.
- Build output is now named `bundleupgrade-fabric-1.21.11-<version>.jar`.

## 1.0.0 (Minecraft 1.21.11 - Fabric)

- Initial release of **Bundle Upgrade** (`bundleupgrade`) by `thaistoanf`.
- Added 5 sequential bundle tiers with cumulative capacity expansion:
  - Copper Bundle (+10 -> 74 units)
  - Iron Bundle (+20 -> 94 units)
  - Gold Bundle (+30 -> 124 units)
  - Emerald Bundle (+40 -> 164 units)
  - Diamond Bundle (+50 -> 214 units)
- Implemented Base-64 weight calculation system (stack of 64 = 1 unit, stack of 16 = 4 units, unstackable = 64 units).
- Unlocked hardcoded `Fraction.ONE` occupancy ceiling via Mixins into `BundleContentsComponent$Builder`.
- Dynamic and proportional item fullness bar scaling (`getItemBarStep`, `getItemBarColor`) and tooltip progress bar without overflow.
- Real-time capacity tooltip displaying `Current / Max` units.
- 3x3 shaped crafting recipes with automatic preservation of stored items (`BUNDLE_CONTENTS`) and custom name (`CUSTOM_NAME`).
- Full compatibility with 1.21.11 interactions (right-click insert/extract, cursor dragging, scroll wheel item selection).
- Procedural pixel-art textures (closed, open front, open back) and custom 128x128 mod icon.
- Bilingual localization in English (`en_us.json`) and Vietnamese (`vi_vn.json`).
