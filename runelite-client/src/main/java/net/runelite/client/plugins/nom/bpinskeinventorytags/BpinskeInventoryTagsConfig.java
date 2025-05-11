/*
 * Copyright (c) 2018 kulers
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
package net.runelite.client.plugins.nom.bpinskeinventorytags;

import net.runelite.client.config.*;

@ConfigGroup(BpinskeInventoryTagsConfig.GROUP)
public interface BpinskeInventoryTagsConfig extends Config
{
	String GROUP = "inventorytags";

	@ConfigSection(
		name = "Tag display mode",
		description = "How tags are displayed in the inventory.",
		position = 0
	)
	String tagStyleSection = "tagStyleSection";

	@ConfigSection(
			name = "Solid Square",
			description = "Solid square modification",
			position = 0,
			closedByDefault = false
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
			keyName = "solidSquareSize",
			name = "solidSquareSize",
			description = "solidSquareSize",
			section = solidSquare
	)
	default int solidSquareSize()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "fillSolidColor",
			name = "Color whole item",
			description = "Configures if fills whole item image with color, solidSquare must be 0",
			section = solidSquare
	)
	default boolean fillSolidColor()
	{
		return false;
	}

	@ConfigItem(
			keyName = "delay",
			name = "delay",
			description = "delay",
			section = solidSquare
	)
	default int delay()
	{
		return 3000;
	}

	@ConfigItem(
			keyName = "inventory_deactivation",
			name = "inventory_deactivation",
			description = "inventory_deactivation",
			section = solidSquare
	)
	default int inventory_deactivation()
	{
		return 0;
	}


	@ConfigItem(
			keyName = "inventory_activation",
			name = "inventory_activation",
			description = "inventory_activation",
			section = solidSquare
	)
	default int inventory_activation()
	{
		return 28;
	}



	@ConfigItem(
			keyName = "stacks",
			name = "Count item stacks",
			description = "Count item stacks",
			section = solidSquare
	)
	default boolean stacks()
	{
		return true;
	}


	@ConfigItem(
		position = 0,
		keyName = "showTagOutline",
		name = "Outline",
		description = "Configures whether or not item tags show be outlined.",
		section = tagStyleSection
	)
	default boolean showTagOutline()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "tagUnderline",
		name = "Underline",
		description = "Configures whether or not item tags should be underlined.",
		section = tagStyleSection
	)
	default boolean showTagUnderline()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "tagFill",
		name = "Fill",
		description = "Configures whether or not item tags should be filled.",
		section = tagStyleSection
	)
	default boolean showTagFill()
	{
		return false;
	}

	@Range(
		max = 255
	)
	@ConfigItem(
		position = 3,
		keyName = "fillOpacity",
		name = "Fill opacity",
		description = "Configures the opacity of the tag fill",
		section = tagStyleSection
	)
	default int fillOpacity()
	{
		return 50;
	}
}
