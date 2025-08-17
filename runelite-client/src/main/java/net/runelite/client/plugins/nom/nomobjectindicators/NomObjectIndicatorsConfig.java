/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED to, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED to, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.nom.nomobjectindicators;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("objectindicators")
public interface NomObjectIndicatorsConfig extends Config
{

	@ConfigItem(
			position = 1,
			keyName = "solidSquare",
			name = "Solid square size",
			description = "The size of the solid square. Set to 0 to disable."
	)
	default int solidSquare()
	{
		return 10;
	}

	@ConfigItem(
			position = 2,
			keyName = "randomDot",
			name = "Random dot",
			description = "Dot moves randomly inside click bounds."
	)
	default boolean randomDot() { return false; }


	// --- BUCKET 1 ---

	@ConfigSection(name = "Bucket 1", description = "Settings for object bucket 1", position = 10)
	String bucket1Section = "bucket1Section";
	@ConfigItem(keyName = "bucket1Enabled", name = "Enable", description = "Enable this bucket", position = 0, section = bucket1Section)
	default boolean bucket1Enabled() { return true; }
	@Alpha
	@ConfigItem(keyName = "bucket1Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket1Section)
	default Color bucket1Color() { return Color.YELLOW; }
	@ConfigItem(keyName = "bucket1Activation", name = "Activation Inventory Count", description = "Show markers if inventory has this many items or more.", position = 2, section = bucket1Section)
	default int bucket1ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket1Deactivation", name = "Deactivation Inventory Count", description = "Hide markers if inventory has fewer than this many items.", position = 3, section = bucket1Section)
	default int bucket1DeactivationCount() { return 1; }

	// --- BUCKET 2 ---
	@ConfigSection(name = "Bucket 2", description = "Settings for object bucket 2", position = 20, closedByDefault = true)
	String bucket2Section = "bucket2Section";
	@ConfigItem(keyName = "bucket2Enabled", name = "Enable", description = "Enable this bucket", position = 0, section = bucket2Section)
	default boolean bucket2Enabled() { return false; }
	@Alpha
	@ConfigItem(keyName = "bucket2Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket2Section)
	default Color bucket2Color() { return Color.GREEN; }
	@ConfigItem(keyName = "bucket2Activation", name = "Activation Inventory Count", description = "Show markers if inventory has this many items or more.", position = 2, section = bucket2Section)
	default int bucket2ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket2Deactivation", name = "Deactivation Inventory Count", description = "Hide markers if inventory has fewer than this many items.", position = 3, section = bucket2Section)
	default int bucket2DeactivationCount() { return 1; }

	// --- BUCKET 3 ---
	@ConfigSection(name = "Bucket 3", description = "Settings for object bucket 3", position = 30, closedByDefault = true)
	String bucket3Section = "bucket3Section";
	@ConfigItem(keyName = "bucket3Enabled", name = "Enable", description = "Enable this bucket", position = 0, section = bucket3Section)
	default boolean bucket3Enabled() { return false; }
	@Alpha
	@ConfigItem(keyName = "bucket3Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket3Section)
	default Color bucket3Color() { return Color.RED; }
	@ConfigItem(keyName = "bucket3Activation", name = "Activation Inventory Count", description = "Show markers if inventory has this many items or more.", position = 2, section = bucket3Section)
	default int bucket3ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket3Deactivation", name = "Deactivation Inventory Count", description = "Hide markers if inventory has fewer than this many items.", position = 3, section = bucket3Section)
	default int bucket3DeactivationCount() { return 1; }

	// --- BUCKET 4 ---
	@ConfigSection(name = "Bucket 4", description = "Settings for object bucket 4", position = 40, closedByDefault = true)
	String bucket4Section = "bucket4Section";
	@ConfigItem(keyName = "bucket4Enabled", name = "Enable", description = "Enable this bucket", position = 0, section = bucket4Section)
	default boolean bucket4Enabled() { return false; }
	@Alpha
	@ConfigItem(keyName = "bucket4Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket4Section)
	default Color bucket4Color() { return Color.BLUE; }
	@ConfigItem(keyName = "bucket4Activation", name = "Activation Inventory Count", description = "Show markers if inventory has this many items or more.", position = 2, section = bucket4Section)
	default int bucket4ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket4Deactivation", name = "Deactivation Inventory Count", description = "Hide markers if inventory has fewer than this many items.", position = 3, section = bucket4Section)
	default int bucket4DeactivationCount() { return 1; }

	// --- BUCKET 5 ---
	@ConfigSection(name = "Bucket 5", description = "Settings for object bucket 5", position = 50, closedByDefault = true)
	String bucket5Section = "bucket5Section";
	@ConfigItem(keyName = "bucket5Enabled", name = "Enable", description = "Enable this bucket", position = 0, section = bucket5Section)
	default boolean bucket5Enabled() { return false; }
	@Alpha
	@ConfigItem(keyName = "bucket5Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket5Section)
	default Color bucket5Color() { return Color.MAGENTA; }
	@ConfigItem(keyName = "bucket5Activation", name = "Activation Inventory Count", description = "Show markers if inventory has this many items or more.", position = 2, section = bucket5Section)
	default int bucket5ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket5Deactivation", name = "Deactivation Inventory Count", description = "Hide markers if inventory has fewer than this many items.", position = 3, section = bucket5Section)
	default int bucket5DeactivationCount() { return 1; }

	// --- BUCKET 6 ---
	@ConfigSection(name = "Bucket 6", description = "Settings for object bucket 6", position = 60, closedByDefault = true)
	String bucket6Section = "bucket6Section";
	@ConfigItem(keyName = "bucket6Enabled", name = "Enable", description = "Enable this bucket", position = 0, section = bucket6Section)
	default boolean bucket6Enabled() { return false; }
	@Alpha
	@ConfigItem(keyName = "bucket6Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket6Section)
	default Color bucket6Color() { return Color.ORANGE; }
	@ConfigItem(keyName = "bucket6Activation", name = "Activation Inventory Count", description = "Show markers if inventory has this many items or more.", position = 2, section = bucket6Section)
	default int bucket6ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket6Deactivation", name = "Deactivation Inventory Count", description = "Hide markers if inventory has fewer than this many items.", position = 3, section = bucket6Section)
	default int bucket6DeactivationCount() { return 1; }

	// --- BUCKET 7 ---
	@ConfigSection(name = "Bucket 7", description = "Settings for object bucket 7", position = 70, closedByDefault = true)
	String bucket7Section = "bucket7Section";
	@ConfigItem(keyName = "bucket7Enabled", name = "Enable", description = "Enable this bucket", position = 0, section = bucket7Section)
	default boolean bucket7Enabled() { return false; }
	@Alpha
	@ConfigItem(keyName = "bucket7Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket7Section)
	default Color bucket7Color() { return Color.CYAN; }
	@ConfigItem(keyName = "bucket7Activation", name = "Activation Inventory Count", description = "Show markers if inventory has this many items or more.", position = 2, section = bucket7Section)
	default int bucket7ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket7Deactivation", name = "Deactivation Inventory Count", description = "Hide markers if inventory has fewer than this many items.", position = 3, section = bucket7Section)
	default int bucket7DeactivationCount() { return 1; }

	// --- BUCKET 8 ---
	@ConfigSection(name = "Bucket 8", description = "Settings for object bucket 8", position = 80, closedByDefault = true)
	String bucket8Section = "bucket8Section";
	@ConfigItem(keyName = "bucket8Enabled", name = "Enable", description = "Enable this bucket", position = 0, section = bucket8Section)
	default boolean bucket8Enabled() { return false; }
	@Alpha
	@ConfigItem(keyName = "bucket8Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket8Section)
	default Color bucket8Color() { return Color.PINK; }
	@ConfigItem(keyName = "bucket8Activation", name = "Activation Inventory Count", description = "Show markers if inventory has this many items or more.", position = 2, section = bucket8Section)
	default int bucket8ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket8Deactivation", name = "Deactivation Inventory Count", description = "Hide markers if inventory has fewer than this many items.", position = 3, section = bucket8Section)
	default int bucket8DeactivationCount() { return 1; }

	// --- BUCKET 9 ---
	@ConfigSection(name = "Bucket 9", description = "Settings for object bucket 9", position = 90, closedByDefault = true)
	String bucket9Section = "bucket9Section";
	@ConfigItem(keyName = "bucket9Enabled", name = "Enable", description = "Enable this bucket", position = 0, section = bucket9Section)
	default boolean bucket9Enabled() { return false; }
	@Alpha
	@ConfigItem(keyName = "bucket9Color", name = "Color", description = "Color for this bucket", position = 1, section = bucket9Section)
	default Color bucket9Color() { return Color.WHITE; }
	@ConfigItem(keyName = "bucket9Activation", name = "Activation Inventory Count", description = "Show markers if inventory has this many items or more.", position = 2, section = bucket9Section)
	default int bucket9ActivationCount() { return 1; }
	@ConfigItem(keyName = "bucket9Deactivation", name = "Deactivation Inventory Count", description = "Hide markers if inventory has fewer than this many items.", position = 3, section = bucket9Section)
	default int bucket9DeactivationCount() { return 1; }

}
