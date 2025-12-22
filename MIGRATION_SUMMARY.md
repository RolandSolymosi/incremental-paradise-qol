# Migration Summary: Updating Incremental Paradise QoL to Minecraft 1.21.11

This document outlines the changes required to migrate the mod from its previous version to be compatible with Minecraft 1.21.11 (Fabric).

## 1. KeyBinding API Changes
The `KeyBinding` API in 1.21.11 requires `KeyBinding.Category` objects instead of simple strings for categorization.

*   **Centralized Category:** Created a public static `CATEGORY` in `OptionsModule.java` using `KeyBinding.Category.create()`.
*   **Updated Calls:** Updated all `KeyBindingHelper.registerKeyBinding` calls in modules like `AutoBank`, `LoadoutsModule`, `TaskTrackerModule`, `DepositHotkeyModule`, and `SellAllHotkeyModule` to use this new shared `CATEGORY` object.

## 2. Rendering Pipeline Overhaul (Internal Changes)
Minecraft 1.21.11 introduces a significant refactor to the rendering codebase, moving away from immediate-mode vertex consumers in many places to a deferred "Command Queue" system.

### `Glowing.java` (Entity Highlighting)
*   **EntityRenderer#render:** The signature for the `render` method changed drastically.
    *   **Old:** `render(Entity entity, ..., MatrixStack matrices, VertexConsumerProvider vertexConsumers, ...)`
    *   **New:** `render(EntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, CameraRenderState camera)`
*   **Implementation:** 
    *   The render method now submits a "Model Command" to the `OrderedRenderCommandQueue`.
    *   Deprecated usage of `RenderLayer.getOutline(Identifier)` was replaced with `RenderLayers.outlineNoCull(Identifier)`.
*   **EntityRenderState:** Introduced a `HighlightEntityRenderState` static class to hold rendering data (like `sneaking`), as the `render` method now consumes this state object instead of the raw entity.

### `DraggableScreen.java` & `HudWidget.java`
*   **Matrix Stack:** Updated to use `org.joml.Matrix3x2fStack` instead of `net.minecraft.client.util.math.MatrixStack` for 2D GUI transformations.
*   **Method Calls:** Replaced `push()`/`pop()` with `pushMatrix()`/`popMatrix()` and `scale()` to match the JOML API.

## 3. GUI Input Handling
The signature for mouse input events in screens has been updated.

*   **Click Object:** Methods like `mouseClicked`, `mouseDragged`, and `mouseReleased` now accept a `net.minecraft.client.gui.Click` record instead of raw `x` and `y` coordinates.
*   **Affected Files:**
    *   `DraggableScreen.java`: Updated `@Override` methods to match the new `(Click click, ...)` signatures.
    *   `HandledScreen` Mixin: Updated the injection target signature to `(Lnet/minecraft/client/gui/Click;Z)V`.

## 4. Entity Data Persistence
The methods for saving and loading custom entity data have been renamed and use new interfaces.

*   **Old:** `writeCustomDataToNbt(NbtCompound)` / `readCustomDataFromNbt(NbtCompound)`
*   **New:** `writeCustomData(WriteView)` / `readCustomData(ReadView)`
*   **Affected File:** `Glowing.java` (specifically the `HighlightEntity` class).
*   **DataTracker:** Updated `initDataTracker` to use the new `DataTracker.Builder` pattern.

## 5. Mixin Refactoring
*   **Class Renaming:** Renamed `com.incrementalqol.mixins.EntityRenderer` to `MixinEntityRenderer`. This prevents classloading collisions with the vanilla `net.minecraft.client.render.entity.EntityRenderer` class, which was causing a crash at startup.
*   **Configuration:** Updated `incremental-qol.client.mixins.json` to point to the renamed mixin class.

## 6. Dependency Updates
*   **Fabric/Yarn:** Updated `gradle.properties` to target Minecraft `1.21.11`, Yarn build `1`, and Fabric Loader `0.18.1`.
