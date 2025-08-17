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
	String renderStyleSection = "renderStyleSection";

	@Alpha
	@ConfigItem(
			position = 4,
			keyName = "markerColor",
			name = "Marker color",
			description = "Configures the color of newly created object markers.",
			section = renderStyleSection
	)
	default Color markerColor()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
			position = 5,
			keyName = "fillColor",
			name = "Fill color",
			description = "Configures the fill color of newly created object markers.",
			section = renderStyleSection
	)
	Color fillColor();


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
	default boolean randomDot()
	{
		return false;
	}

	@ConfigSection(
			name = "Inventory Trigger",
			description = "Settings to control rendering based on inventory state.",
			position = 3
	)
	String inventoryTriggerSection = "inventoryTriggerSection";

	@ConfigItem(
			position = 0,
			keyName = "renderOnInventoryFull",
			name = "Render based on inventory",
			description = "Only render the overlay when the inventory reaches a certain fullness.",
			section = inventoryTriggerSection
	)
	default boolean renderOnInventoryFull()
	{
		return false;
	}

	@Range(min = 1, max = 28)
	@ConfigItem(
			position = 1,
			keyName = "inventoryActivationCount",
			name = "Activation count",
			description = "The number of items in inventory to start rendering the overlay (e.g., 28 for full).",
			section = inventoryTriggerSection
	)
	default int inventoryActivationCount()
	{
		return 28;
	}

	@Range(min = 1, max = 28)
	@ConfigItem(
			position = 2,
			keyName = "inventoryDeactivationCount",
			name = "Deactivation count",
			description = "The overlay will stop rendering if the inventory count is at or below this number.",
			section = inventoryTriggerSection
	)
	default int inventoryDeactivationCount()
	{
		return 1;
	}

}
