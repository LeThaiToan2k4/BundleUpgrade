# Changelog

## [1.0.0] (Minecraft 1.21.2 - Fabric)

### Fixed

- Fixed an item overflow / serialization bug when storing more than 64 identical items in an upgraded bundle. Identical items inside `BundleContentsComponent` are now only merged into existing partial stacks up to `stack.getMaxCount()` (64), and any remaining count is automatically split into separate standard-sized `ItemStack` entries (`1 <= count <= 64`).
- Legacy or decoded `BundleContentsComponent` instances containing oversized stacks (`count > 64`) are automatically split into standard-sized stacks on load and builder initialization.
- Using the bundle in the world (holding right-click) now scales `getMaxUseTime` with tier capacity so every stored stack is dropped in a single hold.

### Changed

- Upgraded **Bundle Upgrade** to Minecraft `1.21.2` (`yarn_mappings=1.21.2+build.1`, `fabric-api=0.106.1+1.21.2`, `fabric-loader=0.20.1`) on Java 21.
- Adopted Minecraft 1.21.2 ("Bundles of Bravery") bundle mechanics and rendering:
  - Left-click to insert items and Right-click to extract one stack (the stack selected via the mouse scroll wheel in the tooltip, or the first stack).
  - Holding Right-Click while dragging a bundle across empty inventory slots places 1 extracted stack (`<= 64`) into each empty slot.
  - Added open front (`*_bundle_open_front`) and open back (`*_bundle_open_back`) models and textures for all 5 upgraded bundle tiers, and registered them in `#minecraft:bundles`.
  - Updated `Item.Settings` registration with `.registryKey(...)` and updated shaped recipe ingredient keys to the 1.21.2 string format.
- Standardized output artifact naming to `bundleupgrade-fabric-1.21.2-1.0.0.jar`.

### Features

- Added 5 sequential bundle tiers with cumulative capacity expansion:
  - Copper Bundle (+10 -> 74 units)
  - Iron Bundle (+20 -> 94 units)
  - Gold Bundle (+30 -> 124 units)
  - Emerald Bundle (+40 -> 164 units)
  - Diamond Bundle (+50 -> 214 units)
- Implemented Base-64 weight calculation system (stack of 64 = 1 unit, stack of 16 = 4 units, unstackable = 64 units).
- Unlocked the hardcoded `Fraction.ONE` occupancy ceiling via Mixins into `BundleContentsComponent$Builder`.
- Dynamic and proportional item fullness bar scaling (`getItemBarStep`, `getItemBarColor`) and tooltip progress bar scaling (`getProgressBarFill`, `getProgressBarFillTexture`).
- Real-time capacity tooltip label displaying `Current / Max` units.
- 3x3 shaped crafting recipes with automatic preservation of stored items (`BUNDLE_CONTENTS`) and custom name (`CUSTOM_NAME`).
- Procedural pixel-art textures (closed, open front, open back) and custom 128x128 mod icon.
- Bilingual localization in English (`en_us.json`) and Vietnamese (`vi_vn.json`).

---
