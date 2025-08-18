package net.runelite.client.plugins.nom.nomobjectindicators;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("nomobjectindicators")
public interface NomObjectIndicatorsConfig extends Config
{
	@ConfigSection(
			name = "Render Style",
			description = "General settings for how the markers are rendered.",
			position = 0,
			closedByDefault = false
	)
	String renderStyleSection = "renderStyleSection";

	@ConfigItem(
			position = 0,
			keyName = "solidSquare",
			name = "Solid square size",
			description = "The size of the solid square. Set to 0 to disable.",
			section = renderStyleSection
	)
	default int solidSquare() { return 10; }

	@ConfigItem(
			position = 1,
			keyName = "randomDot",
			name = "Random dot",
			description = "Dot moves randomly inside the object's click bounds.",
			section = renderStyleSection
	)
	default boolean randomDot() { return false; }

	// --- BUCKET 1 ---
	@ConfigSection(name = "Bucket 1", description = "Settings for object bucket 1", position = 10)
	String bucket1Section = "bucket1Section";
	@ConfigItem(keyName = "bucket1Enabled", name = "Enable Rendering", description = "Enable this bucket's markers to be rendered.", position = 0, section = bucket1Section)
	default boolean bucket1Enabled() { return true; }
	@ConfigItem(keyName = "bucket1Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket1Section)
	default Color bucket1Color() { return Color.YELLOW; }
	@ConfigItem(keyName = "bucket1Activation", name = "Min Inventory Count (Inclusive)", description = "Markers will show if inventory count is at or above this value.", position = 2, section = bucket1Section)
	default int bucket1ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket1Deactivation", name = "Max Inventory Count (Exclusive)", description = "Markers will show if inventory count is below this value.", position = 3, section = bucket1Section)
	default int bucket1DeactivationCount() { return 28; }
	@ConfigItem(keyName = "bucket1DisableOnInteract", name = "Disable while Interacting", description = "Hide markers while interacting with NPCs or players.", position = 4, section = bucket1Section)
	default boolean bucket1DisableWhileInteracting() { return false; }
	@ConfigItem(keyName = "bucket1DisableOnMove", name = "Disable while Moving", description = "Hide markers while your character is moving.", position = 5, section = bucket1Section)
	default boolean bucket1DisableWhileMoving() { return false; }

	// --- BUCKET 2 ---
	@ConfigSection(name = "Bucket 2", description = "Settings for object bucket 2", position = 20, closedByDefault = true)
	String bucket2Section = "bucket2Section";
	@ConfigItem(keyName = "bucket2Enabled", name = "Enable Rendering", description = "Enable this bucket's markers to be rendered.", position = 0, section = bucket2Section)
	default boolean bucket2Enabled() { return false; }
	@ConfigItem(keyName = "bucket2Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket2Section)
	default Color bucket2Color() { return Color.GREEN; }
	@ConfigItem(keyName = "bucket2Activation", name = "Min Inventory Count (Inclusive)", description = "Markers will show if inventory count is at or above this value.", position = 2, section = bucket2Section)
	default int bucket2ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket2Deactivation", name = "Max Inventory Count (Exclusive)", description = "Markers will show if inventory count is below this value.", position = 3, section = bucket2Section)
	default int bucket2DeactivationCount() { return 28; }
	@ConfigItem(keyName = "bucket2DisableOnInteract", name = "Disable while Interacting", description = "Hide markers while interacting with NPCs or players.", position = 4, section = bucket2Section)
	default boolean bucket2DisableWhileInteracting() { return false; }
	@ConfigItem(keyName = "bucket2DisableOnMove", name = "Disable while Moving", description = "Hide markers while your character is moving.", position = 5, section = bucket2Section)
	default boolean bucket2DisableWhileMoving() { return false; }

	// --- BUCKET 3 ---
	@ConfigSection(name = "Bucket 3", description = "Settings for object bucket 3", position = 30, closedByDefault = true)
	String bucket3Section = "bucket3Section";
	@ConfigItem(keyName = "bucket3Enabled", name = "Enable Rendering", description = "Enable this bucket's markers to be rendered.", position = 0, section = bucket3Section)
	default boolean bucket3Enabled() { return false; }
	@ConfigItem(keyName = "bucket3Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket3Section)
	default Color bucket3Color() { return Color.RED; }
	@ConfigItem(keyName = "bucket3Activation", name = "Min Inventory Count (Inclusive)", description = "Markers will show if inventory count is at or above this value.", position = 2, section = bucket3Section)
	default int bucket3ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket3Deactivation", name = "Max Inventory Count (Exclusive)", description = "Markers will show if inventory count is below this value.", position = 3, section = bucket3Section)
	default int bucket3DeactivationCount() { return 28; }
	@ConfigItem(keyName = "bucket3DisableOnInteract", name = "Disable while Interacting", description = "Hide markers while interacting with NPCs or players.", position = 4, section = bucket3Section)
	default boolean bucket3DisableWhileInteracting() { return false; }
	@ConfigItem(keyName = "bucket3DisableOnMove", name = "Disable while Moving", description = "Hide markers while your character is moving.", position = 5, section = bucket3Section)
	default boolean bucket3DisableWhileMoving() { return false; }

	// --- BUCKET 4 ---
	@ConfigSection(name = "Bucket 4", description = "Settings for object bucket 4", position = 40, closedByDefault = true)
	String bucket4Section = "bucket4Section";
	@ConfigItem(keyName = "bucket4Enabled", name = "Enable Rendering", description = "Enable this bucket's markers to be rendered.", position = 0, section = bucket4Section)
	default boolean bucket4Enabled() { return false; }
	@ConfigItem(keyName = "bucket4Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket4Section)
	default Color bucket4Color() { return Color.BLUE; }
	@ConfigItem(keyName = "bucket4Activation", name = "Min Inventory Count (Inclusive)", description = "Markers will show if inventory count is at or above this value.", position = 2, section = bucket4Section)
	default int bucket4ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket4Deactivation", name = "Max Inventory Count (Exclusive)", description = "Markers will show if inventory count is below this value.", position = 3, section = bucket4Section)
	default int bucket4DeactivationCount() { return 28; }
	@ConfigItem(keyName = "bucket4DisableOnInteract", name = "Disable while Interacting", description = "Hide markers while interacting with NPCs or players.", position = 4, section = bucket4Section)
	default boolean bucket4DisableWhileInteracting() { return false; }
	@ConfigItem(keyName = "bucket4DisableOnMove", name = "Disable while Moving", description = "Hide markers while your character is moving.", position = 5, section = bucket4Section)
	default boolean bucket4DisableWhileMoving() { return false; }

	// --- BUCKET 5 ---
	@ConfigSection(name = "Bucket 5", description = "Settings for object bucket 5", position = 50, closedByDefault = true)
	String bucket5Section = "bucket5Section";
	@ConfigItem(keyName = "bucket5Enabled", name = "Enable Rendering", description = "Enable this bucket's markers to be rendered.", position = 0, section = bucket5Section)
	default boolean bucket5Enabled() { return false; }
	@ConfigItem(keyName = "bucket5Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket5Section)
	default Color bucket5Color() { return Color.MAGENTA; }
	@ConfigItem(keyName = "bucket5Activation", name = "Min Inventory Count (Inclusive)", description = "Markers will show if inventory count is at or above this value.", position = 2, section = bucket5Section)
	default int bucket5ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket5Deactivation", name = "Max Inventory Count (Exclusive)", description = "Markers will show if inventory count is below this value.", position = 3, section = bucket5Section)
	default int bucket5DeactivationCount() { return 28; }
	@ConfigItem(keyName = "bucket5DisableOnInteract", name = "Disable while Interacting", description = "Hide markers while interacting with NPCs or players.", position = 4, section = bucket5Section)
	default boolean bucket5DisableWhileInteracting() { return false; }
	@ConfigItem(keyName = "bucket5DisableOnMove", name = "Disable while Moving", description = "Hide markers while your character is moving.", position = 5, section = bucket5Section)
	default boolean bucket5DisableWhileMoving() { return false; }

	// --- BUCKET 6 ---
	@ConfigSection(name = "Bucket 6", description = "Settings for object bucket 6", position = 60, closedByDefault = true)
	String bucket6Section = "bucket6Section";
	@ConfigItem(keyName = "bucket6Enabled", name = "Enable Rendering", description = "Enable this bucket's markers to be rendered.", position = 0, section = bucket6Section)
	default boolean bucket6Enabled() { return false; }
	@ConfigItem(keyName = "bucket6Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket6Section)
	default Color bucket6Color() { return Color.ORANGE; }
	@ConfigItem(keyName = "bucket6Activation", name = "Min Inventory Count (Inclusive)", description = "Markers will show if inventory count is at or above this value.", position = 2, section = bucket6Section)
	default int bucket6ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket6Deactivation", name = "Max Inventory Count (Exclusive)", description = "Markers will show if inventory count is below this value.", position = 3, section = bucket6Section)
	default int bucket6DeactivationCount() { return 28; }
	@ConfigItem(keyName = "bucket6DisableOnInteract", name = "Disable while Interacting", description = "Hide markers while interacting with NPCs or players.", position = 4, section = bucket6Section)
	default boolean bucket6DisableWhileInteracting() { return false; }
	@ConfigItem(keyName = "bucket6DisableOnMove", name = "Disable while Moving", description = "Hide markers while your character is moving.", position = 5, section = bucket6Section)
	default boolean bucket6DisableWhileMoving() { return false; }

	// --- BUCKET 7 ---
	@ConfigSection(name = "Bucket 7", description = "Settings for object bucket 7", position = 70, closedByDefault = true)
	String bucket7Section = "bucket7Section";
	@ConfigItem(keyName = "bucket7Enabled", name = "Enable Rendering", description = "Enable this bucket's markers to be rendered.", position = 0, section = bucket7Section)
	default boolean bucket7Enabled() { return false; }
	@ConfigItem(keyName = "bucket7Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket7Section)
	default Color bucket7Color() { return Color.CYAN; }
	@ConfigItem(keyName = "bucket7Activation", name = "Min Inventory Count (Inclusive)", description = "Markers will show if inventory count is at or above this value.", position = 2, section = bucket7Section)
	default int bucket7ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket7Deactivation", name = "Max Inventory Count (Exclusive)", description = "Markers will show if inventory count is below this value.", position = 3, section = bucket7Section)
	default int bucket7DeactivationCount() { return 28; }
	@ConfigItem(keyName = "bucket7DisableOnInteract", name = "Disable while Interacting", description = "Hide markers while interacting with NPCs or players.", position = 4, section = bucket7Section)
	default boolean bucket7DisableWhileInteracting() { return false; }
	@ConfigItem(keyName = "bucket7DisableOnMove", name = "Disable while Moving", description = "Hide markers while your character is moving.", position = 5, section = bucket7Section)
	default boolean bucket7DisableWhileMoving() { return false; }

	// --- BUCKET 8 ---
	@ConfigSection(name = "Bucket 8", description = "Settings for object bucket 8", position = 80, closedByDefault = true)
	String bucket8Section = "bucket8Section";
	@ConfigItem(keyName = "bucket8Enabled", name = "Enable Rendering", description = "Enable this bucket's markers to be rendered.", position = 0, section = bucket8Section)
	default boolean bucket8Enabled() { return false; }
	@ConfigItem(keyName = "bucket8Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket8Section)
	default Color bucket8Color() { return Color.PINK; }
	@ConfigItem(keyName = "bucket8Activation", name = "Min Inventory Count (Inclusive)", description = "Markers will show if inventory count is at or above this value.", position = 2, section = bucket8Section)
	default int bucket8ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket8Deactivation", name = "Max Inventory Count (Exclusive)", description = "Markers will show if inventory count is below this value.", position = 3, section = bucket8Section)
	default int bucket8DeactivationCount() { return 28; }
	@ConfigItem(keyName = "bucket8DisableOnInteract", name = "Disable while Interacting", description = "Hide markers while interacting with NPCs or players.", position = 4, section = bucket8Section)
	default boolean bucket8DisableWhileInteracting() { return false; }
	@ConfigItem(keyName = "bucket8DisableOnMove", name = "Disable while Moving", description = "Hide markers while your character is moving.", position = 5, section = bucket8Section)
	default boolean bucket8DisableWhileMoving() { return false; }

	// --- BUCKET 9 ---
	@ConfigSection(name = "Bucket 9", description = "Settings for object bucket 9", position = 90, closedByDefault = true)
	String bucket9Section = "bucket9Section";
	@ConfigItem(keyName = "bucket9Enabled", name = "Enable Rendering", description = "Enable this bucket's markers to be rendered.", position = 0, section = bucket9Section)
	default boolean bucket9Enabled() { return false; }
	@ConfigItem(keyName = "bucket9Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket9Section)
	default Color bucket9Color() { return Color.WHITE; }
	@ConfigItem(keyName = "bucket9Activation", name = "Min Inventory Count (Inclusive)", description = "Markers will show if inventory count is at or above this value.", position = 2, section = bucket9Section)
	default int bucket9ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket9Deactivation", name = "Max Inventory Count (Exclusive)", description = "Markers will show if inventory count is below this value.", position = 3, section = bucket9Section)
	default int bucket9DeactivationCount() { return 28; }
	@ConfigItem(keyName = "bucket9DisableOnInteract", name = "Disable while Interacting", description = "Hide markers while interacting with NPCs or players.", position = 4, section = bucket9Section)
	default boolean bucket9DisableWhileInteracting() { return false; }
	@ConfigItem(keyName = "bucket9DisableOnMove", name = "Disable while Moving", description = "Hide markers while your character is moving.", position = 5, section = bucket9Section)
	default boolean bucket9DisableWhileMoving() { return false; }
}