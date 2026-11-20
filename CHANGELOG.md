# Changelog

## [1.0.0] (Minecraft 26.1 - Fabric)

### Changed

- Upgraded **Bundle Upgrade** to Minecraft `26.1` (`fabric-api=0.145.1+26.1`, `fabric-loader=0.20.1`, `loom_version=1.19.4`, Gradle `9.9.2`) on Java 25.
- Migrated the build toolchain to unobfuscated Minecraft `26.1`:
  - Switched the Loom plugin from `net.fabricmc.fabric-loom-remap` to `net.fabricmc.fabric-loom` (`1.19.4`), removed `yarn_mappings`, replaced `modImplementation` with `implementation`, and updated the Java target and Mixin `compatibilityLevel` to Java `25` (`JAVA_25`).
  - Updated the Gradle Wrapper to `9.9.2`, workspace IDE settings (`.vscode/settings.json`) to `JavaSE-25`, and GitHub Actions workflows (`.github/workflows/build.yml`, `.github/workflows/release.yml`) to Java `25`.
- Migrated all common and client source files and Mixins from Yarn mappings to Minecraft `26.1` official Mojang names and APIs:
  - Updated `ModItems` to register items with `BuiltInRegistries.ITEM`, `Registries.ITEM`, `Identifier.fromNamespaceAndPath`, `Item.Properties#setId`, `Item.Properties#stacksTo`, `DataComponents.BUNDLE_CONTENTS`, and `BundleContents.EMPTY`, and migrated creative tab registration from `ItemGroupEvents` to `CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)` using `output.insertAfter(...)`.
  - Updated `UpgradedBundleItem` and `BundleItemMixin` to target `overrideStackedOnOther`, `overrideOtherStackedOnMe`, `getUseDuration`, `isBarVisible`, `getBarWidth`, `getBarColor`, `getTooltipImage`, and `getFullnessDisplay`, using `ARGB.colorFromFloat`, `Mth.mulAndTruncate`, and safe `DataResult<Fraction>` weight extraction from `BundleContents#weight()`.
  - Updated `BundleContentsBuilderMixin`, `BundleContentsComponentAccessor`, `BundleContentsComponentMixin`, and `BundleStackHelper` for `BundleContents`, `BundleContents.Mutable`, `ItemInstance`, and `ItemStackTemplate`.
  - Updated `ShapedRecipeMixin` for the Minecraft `26.1` `ShapedRecipe#assemble(CraftingInput)` signature and `CraftingInput#items()`.
  - Updated `BundleTooltipDataMixin`, `TooltipComponentMixin`, and `BundleTooltipComponentMixin` for `BundleTooltip`, `ClientTooltipComponent#create(TooltipComponent)`, and `ClientBundleTooltip`'s static `getProgressBarFill(Fraction)`, `getProgressBarTexture(Fraction)`, and `getProgressBarFillText(Fraction)` methods with `extractImage` capacity context tracking.
  - Updated `HandledScreenMixin` for `AbstractContainerScreen`, `MouseButtonEvent`, `InputConstants.MOUSE_BUTTON_RIGHT`, `ContainerInput.PICKUP`, `AbstractContainerMenu`, and `ItemStackTemplate`.
- Standardized output artifact naming to `bundleupgrade-fabric-26.1-1.0.0.jar`.

### Fixed

- Resolved toolchain, compiler, and workspace warnings for Minecraft `26.1` and Java `25`:
  - Verified zero `-Xlint:all` compiler warnings across main, client, and tool sources and zero Gradle `--warning-mode all` build warnings.
  - Removed the unused `BundleCapacityContext#getCurrentMaxCapacity()` helper and redundant `cancellable = true` flag on `ShapedRecipeMixin`.
  - Switched nullable annotations to JSpecify (`org.jspecify.annotations.Nullable`) and aligned `.vscode/settings.json`, `CONTRIBUTING.md`, and CI workflows with Java `25` and Minecraft `26.1`.
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
