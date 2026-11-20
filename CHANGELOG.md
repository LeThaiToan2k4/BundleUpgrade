# Changelog

## [1.0.0] (Minecraft 26.3 - Fabric)

### Changed

- Upgraded **Bundle Upgrade** to Minecraft `26.3` (`fabric-api=0.166.2+26.3`, `fabric-loader=0.20.1`, `loom_version=1.19.4`, Gradle `9.9.2`) on Java 25.
- Updated mod dependencies (`fabric.mod.json` `"minecraft": "~26.3"`), documentation (`README.md`, `CONTRIBUTING.md`, `tools/TextureGenerator.java`), and GitHub Actions workflows (`.github/workflows/build.yml`, `.github/workflows/release.yml`) for Minecraft `26.3`.
- Built on the unobfuscated Minecraft `26.3` toolchain:
  - Uses `net.fabricmc.fabric-loom` (`1.19.4`) without Yarn/remap configurations, standard `implementation` dependencies, Java `25`, and Mixin `compatibilityLevel` `JAVA_25`.
  - Uses official Mojang names and Fabric API `26.3` conventions across all common and client sources and Mixins:
    - `ModItems`: `BuiltInRegistries.ITEM`, `Registries.ITEM`, `Identifier.fromNamespaceAndPath`, `Item.Properties#setId`, `Item.Properties#stacksTo`, `DataComponents.BUNDLE_CONTENTS`, `BundleContents.EMPTY`, and `CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)` via `output.insertAfter(...)`.
    - `UpgradedBundleItem` & `BundleItemMixin`: `overrideStackedOnOther`, `overrideOtherStackedOnMe`, `getUseDuration`, `isBarVisible`, `getBarWidth`, `getBarColor`, `getTooltipImage`, `getFullnessDisplay`, `ARGB.colorFromFloat`, `Mth.mulAndTruncate`, and safe `DataResult<Fraction>` weight extraction from `BundleContents#weight()`.
    - `BundleContentsBuilderMixin`, `BundleContentsComponentAccessor`, `BundleContentsComponentMixin`, & `BundleStackHelper`: adapted for `BundleContents`, `BundleContents.Mutable`'s `26.3` `GrowableMutableContainer` / `SimpleMutableContainer` hierarchy (`items` inherited from `SimpleMutableContainer`), the `BundleContents.Mutable(List, Fraction, int)` constructor, `Fraction.ONE` occupancy redirects across `getMaxAmountToAdd`, `canInsertNewSlots`, and `getWeightWithAddedItems`, `ItemInstance`, and `ItemStackTemplate`.
    - `ShapedRecipeMixin`: adapted for `ShapedRecipe#assemble(CraftingInput)` and `CraftingInput#items()`.
    - `BundleTooltipDataMixin`, `TooltipComponentMixin`, & `BundleTooltipComponentMixin`: adapted for `BundleTooltip`, `ClientTooltipComponent#create(TooltipComponent)`, and `ClientBundleTooltip`'s static `getProgressBarFill(Fraction)`, `getProgressBarTexture(Fraction)`, and `getProgressBarFillText(Fraction)` methods with `extractImage` capacity context tracking.
    - `HandledScreenMixin`: adapted for `AbstractContainerScreen`, `MouseButtonEvent`, SDL3 `InputConstants.MOUSE_BUTTON_RIGHT` (`3`) mapped to container secondary click button `1` when calling `slotClicked`, `ContainerInput.PICKUP`, `AbstractContainerMenu`, and `ItemStackTemplate`.
- Standardized output artifact naming to `bundleupgrade-fabric-26.3-1.0.0.jar`.

### Fixed

- Resolved toolchain, compiler, and workspace warnings for Minecraft `26.3` and Java `25`:
  - Verified zero `-Xlint:all` compiler warnings across main, client, and tool sources and zero Gradle `--warning-mode all` build warnings.
  - Removed the unused `BundleCapacityContext#getCurrentMaxCapacity()` helper and redundant `cancellable = true` flag on `ShapedRecipeMixin`.
  - Switched nullable annotations to JSpecify (`org.jspecify.annotations.Nullable`) and aligned `.vscode/settings.json`, `CONTRIBUTING.md`, and CI workflows with Java `25` and Minecraft `26.3`.
- Preserved automatic stack-splitting protection when storing more than 64 identical items in an upgraded bundle:
  - Identical items inside `BundleContents.Mutable` are only merged into existing partial stacks up to `stack.getMaxStackSize()` (64), and any remainder is split into separate standard-sized `ItemStack` entries (`1 <= count <= 64`).
  - Decoded `BundleContents` instances containing legacy oversized `ItemStackTemplate` entries (`count > maxStackSize`) are split into standard-sized templates in `BundleContents.<init>` before `ItemStackTemplate#create()` validation.
- Scaled `getUseDuration` with tier capacity so holding right-click in the world drops every stored stack in a single hold.

### Features

- 5 sequential bundle tiers with cumulative capacity expansion:
  - Copper Bundle (+10 -> 74 units)
  - Iron Bundle (+20 -> 94 units)
  - Gold Bundle (+30 -> 124 units)
  - Emerald Bundle (+40 -> 164 units)
  - Diamond Bundle (+50 -> 214 units)
- Base-64 weight calculation system (stack of 64 = 1 unit, stack of 16 = 4 units, unstackable = 64 units).
- Unlocked the hardcoded `Fraction.ONE` occupancy ceiling via Mixins into `BundleContents$Mutable`.
- Dynamic and proportional item fullness bar scaling (`getBarWidth`, `getBarColor`) and tooltip progress bar scaling (`getProgressBarFill`, `getProgressBarTexture`).
- Real-time capacity tooltip label displaying `Current / Max` units.
- Full Vanilla bundle controls: left-click to insert, right-click to extract one stack, right-click and drag across empty slots to distribute stacks, scroll wheel to select which stack to extract, and hold right-click in the world to empty.
- 3x3 shaped crafting recipes with automatic preservation of stored items (`BUNDLE_CONTENTS`) and custom name (`CUSTOM_NAME`).
- Procedural pixel-art textures (closed, open front, open back), data-driven client item models (`assets/bundleupgrade/items/*_bundle.json`), `#minecraft:bundles` tag registration, and custom 128x128 mod icon.
- Bilingual localization in English (`en_us.json`) and Vietnamese (`vi_vn.json`).
