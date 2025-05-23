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
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.npchighlight;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.ui.overlay.OverlayLayer;

@ConfigGroup("nomnpcindicators")
public interface NomNpcIndicatorsConfig extends Config
{
	@ConfigSection(
		name = "Render style",
		description = "The render style of NPC highlighting",
		position = 0
	)
    String renderStyleSection = "renderStyleSection";

	@ConfigItem(
		position = 0,
		keyName = "highlightHull",
		name = "Highlight hull",
		description = "Configures whether or not NPC should be highlighted by hull",
		section = renderStyleSection
	)
	default boolean highlightHull()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "highlightTile",
		name = "Highlight tile",
		description = "Configures whether or not NPC should be highlighted by tile",
		section = renderStyleSection
	)
	default boolean highlightTile()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "highlightSouthWestTile",
		name = "Highlight south west tile",
		description = "Configures whether or not NPC should be highlighted by south western tile",
		section = renderStyleSection
	)
	default boolean highlightSouthWestTile()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "npcToHighlight",
		name = "NPCs to Highlight",
		description = "List of NPC names to highlight"
	)
	default String getNpcToHighlight()
	{
		return "";
	}

	@ConfigItem(
		keyName = "npcToHighlight",
		name = "",
		description = ""
	)
	void setNpcToHighlight(String npcsToHighlight);

	@ConfigItem(
		position = 4,
		keyName = "npcColor",
		name = "Highlight Color",
		description = "Color of the NPC highlight"
	)
	default Color getHighlightColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		position = 5,
		keyName = "drawNames",
		name = "Draw names above NPC",
		description = "Configures whether or not NPC names should be drawn above the NPC"
	)
	default boolean drawNames()
	{
		return false;
	}

	@ConfigItem(
		position = 6,
		keyName = "drawMinimapNames",
		name = "Draw names on minimap",
		description = "Configures whether or not NPC names should be drawn on the minimap"
	)
	default boolean drawMinimapNames()
	{
		return false;
	}

	@ConfigItem(
		position = 7,
		keyName = "highlightMenuNames",
		name = "Highlight menu names",
		description = "Highlight NPC names in right click menu"
	)
	default boolean highlightMenuNames()
	{
		return false;
	}

	@ConfigItem(
		position = 8,
		keyName = "ignoreDeadNpcs",
		name = "Ignore dead NPCs",
		description = "Prevents highlighting NPCs after they are dead"
	)
	default boolean ignoreDeadNpcs()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "deadNpcMenuColor",
		name = "Dead NPC menu color",
		description = "Color of the NPC menus for dead NPCs"
	)
    Color deadNpcMenuColor();

	@ConfigItem(
		position = 10,
		keyName = "showRespawnTimer",
		name = "Show respawn timer",
		description = "Show respawn timer of tagged NPCs")
	default boolean showRespawnTimer()
	{
		return false;
	}


	@ConfigSection(
			name = "Solid square",
			description = "The render style of NPC highlighting",
			position = 0
	)
	String solidSquare = "solidSquare";

	@ConfigItem(
			keyName = "randomDot",
			name = "Random dot",
			description = "Dot moves randomly inside click bounds",
			section = solidSquare
	)
	default boolean randomDot()
	{
		return false;
	}


	@ConfigItem(
			keyName = "solidSquare",
			name = "solidSquare",
			description = "solidSquare",
			section = solidSquare
	)
	default int solidSquare()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "checkCombat",
			name = "Disable on npcs when in combat",
			description = "Disable when in combat",
			section = solidSquare
	)
	default boolean checkCombat()
	{
		return false;
	}

	@ConfigItem(
			keyName = "interacting",
			name = "Show on current target",
			description = "Enable on entity you are interacting with",
			section = solidSquare
	)
	default boolean interacting()
	{
		return false;
	}

	@ConfigItem(
			keyName = "interactingDelay",
			name = "Current target fade delay",
			description = "interactingDelay",
			section = solidSquare
	)
	default int interactingDelay()
	{
		return 2000;
	}


	@ConfigItem(
			keyName = "randomColor",
			name = "Randoms Color",
			description = "Color of the Randoms highlight",
			section = solidSquare
	)
	default Color getRandomColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
			keyName = "highlightRandoms",
			name = "Should highlight randoms",
			description = "Configures whether or not highlight randoms",
			section = solidSquare
	)
	default boolean shouldHighlightRandoms()
	{
		return true;
	}

	@ConfigItem(
			keyName = "layer",
			name = "Layer",
			description = "The layer of the overlay",
			section = solidSquare
	)
	default OverlayLayer overlayLayer()
	{
		return SquareOverlay.OVERLAY_LAYER2;
	}
}