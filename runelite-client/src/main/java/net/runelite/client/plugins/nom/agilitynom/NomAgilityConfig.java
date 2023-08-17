/*
 * Copyright (c) 2018, Cas <https://github.com/casvandongen>
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
package net.runelite.client.plugins.nom.agilitynom;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("nomagility")
public interface NomAgilityConfig extends Config
{
	@ConfigItem(
			keyName = "squareSize",
			name = "Square size",
			description = "Square size",
			position = 0
	)
	default int solidSquare()
	{
		return 1;
	}

//	@ConfigItem(
//		keyName = "showClickboxes",
//		name = "Show Clickboxes",
//		description = "Show agility course obstacle clickboxes",
//		position = 0
//	)
//	default boolean showClickboxes()
//	{
//		return true;
//	}

	@ConfigItem(
			keyName = "overlayColor",
			name = "Overlay Color",
			description = "Color of Agility overlay",
			position = 4
	)
	default Color getOverlayColor()
	{
		return Color.PINK;
	}


	@ConfigItem(
			keyName = "disableAnimation",
			name = "Disable on action",
			description = "Disable while moving or animating",
			position = 5
	)
	default boolean disableWhenMovingOrAnimating()
	{
		return true;
	}

	@ConfigItem(
			keyName = "disableAnimationDelay",
			name = "Hide delay (ms)",
			description = "Will keep hiding overlay for X ms after moving or animating",
			position = 6
	)
	default int animatingDelay()
	{
		return 1500;
	}


	@ConfigItem(
			keyName = "forceOverlayExp",
			name = "Force show after exp (ms)",
			description = "Will force overlay to show after exp drop for X ms",
			position = 7
	)
	default int forceShow()
	{
		return 1500;
	}


	@ConfigItem(
			keyName = "minimapColor",
			name = "Minimap Color",
			description = "Color of Agility overlay",
			position = 8
	)
	default Color getMinimapColor()
	{
		return Color.MAGENTA;
	}

	@ConfigItem(
			keyName = "minimapDistance",
			name = "Show minimap distance",
			description = "Only shows minimap indicator if the obstacle is greater than X distance away",
			position = 9
	)
	default int onlyShowWhenNoBounds()
	{
		return 12;
	}

	@ConfigItem(
			keyName = "highlightFoodHp",
			name = "HP to highlight food",
			description = "Highlight food when below X HP",
			position = 10
	)
	default int highlightFoodHp()
	{
		return 9;
	}

	@ConfigItem(
			keyName = "highlightFoodFull",
			name = "Highlight food until full HP",
			description = "When food overlay shows, it will keep showing until you are full HP",
			position = 11
	)
	default boolean highlightUntilFull()
	{
		return true;
	}


	@ConfigItem(
		keyName = "highlightPieLevel",
		name = "HP to highlight pie",
		description = "Highlight summer pie when below X agility",
		position = 12
	)
	default int highlightAgilityLevel()
	{
		return 1;
	}

//	@ConfigItem(
//		keyName = "highlightMarks",
//		name = "Highlight Marks of Grace",
//		description = "Enable/disable the highlighting of retrievable Marks of Grace",
//		position = 6
//	)
//	default boolean highlightMarks()
//	{
//		return true;
//	}
//
//	@ConfigItem(
//		keyName = "markHighlight",
//		name = "Mark Highlight Color",
//		description = "Color of highlighted Marks of Grace",
//		position = 7
//	)
//	default Color getMarkColor()
//	{
//		return Color.RED;
//	}
}
