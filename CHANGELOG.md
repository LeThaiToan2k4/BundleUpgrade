# Changelog

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
