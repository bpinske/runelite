/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
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
package net.runelite.client.plugins.nom.screenshotaccplugin;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("accountscreenshot")
public interface AccountScreenshotConfig extends Config
{

	@ConfigItem(
			keyName = "TTL/Combat level",
			name = "TTL/Combat level",
			description = "<br> for new line, ?ttl? for total level, ?cb? for combat level e.g. Total lvl: ?ttl?<br>Combat: ?cb?",
			position = 0
	)
	default String totalLevelText()
	{
		return "Total lvl: ?ttl?<br>Combat: ?cb?";
	}

	@ConfigSection(
			name = "Border options",
			description = "Border options",
			position = 0
	)
	String border = "border";

	@ConfigSection(
			name = "Quest options",
			description = "Quest options",
			position = 0
	)
	String quest = "quest";

	@ConfigItem(
			keyName = "showQuestOverlay",
			name = "showQuestOverlay",
			description = "showQuestOverlay",
			position = 0,
			section = quest
	)
	default boolean showQuestOverlay()
	{
		return false;
	}

	@ConfigItem(
			keyName = "questText",
			name = "questText",
			description = "questText",
			position = 1,
			section = quest
	)
	default String questText()
	{
		return "Example text";
	}


	@ConfigItem(
			keyName = "offsetXImage",
			name = "offsetXImage",
			description = "offsetXImage",
			position = 2,
			section = quest
	)
	default int offsetXImage()
	{
		return 45;
	}

	@ConfigItem(
			keyName = "offsetYImage",
			name = "offsetYImage",
			description = "offsetYImage",
			position = 3,
			section = quest
	)
	default int offsetYImage()
	{
		return 1;
	}


	@ConfigItem(
			keyName = "offsetXText",
			name = "offsetXText",
			description = "offsetXText",
			position = 4,
			section = quest
	)
	default int offsetXText()
	{
		return 80;
	}

	@ConfigItem(
			keyName = "offsetYText",
			name = "offsetYText",
			description = "offsetYText",
			position = 5,
			section = quest
	)
	default int offsetYText()
	{
		return 20;
	}

	@ConfigItem(
			keyName = "borderThickness",
			name = "borderThickness",
			description = "borderThickness",
			section = border
	)
	default int borderThickness()
	{
		return 3;
	}

	@ConfigItem(
			keyName = "borderColor",
			name = "borderColor",
			description = "borderColor",
			section = border
	)
	default Color borderColor()
	{
		return new Color(255, 255, 255);
	}


	@ConfigItem(
			keyName = "trimTop",
			name = "trimTop",
			description = "trimTop",
			section = border
	)
	default int trimTop()
	{
		return 1;
	}


	@ConfigItem(
			keyName = "trimBot",
			name = "trimBot",
			description = "trimBot",
			section = border
	)
	default int trimBot()
	{
		return 4;
	}


	@ConfigItem(
			keyName = "trimLeft",
			name = "trimLeft",
			description = "trimLeft",
			section = border
	)
	default int trimLeft()
	{
		return 1;
	}


	@ConfigItem(
			keyName = "trimRight",
			name = "trimRight",
			description = "trimRight",
			section = border
	)
	default int trimRight()
	{
		return 1;
	}

	@ConfigSection(
			name = "Hide stats",
			description = "Hide stats",
			position = 0
	)
	String stats = "timedSection";


	@ConfigItem(
			keyName = "statsToHide",
			name = "statsToHide",
			description = "Comma separated stats to hide, Overall to hide total level",
			position = 0,
			section = stats
	)
	default String statsToHide()
	{
		return "";
	}


	@ConfigItem(
			keyName = "alwaysHide",
			name = "alwaysHide",
			description = "alwaysHide",
			position = 0,
			section = stats
	)
	default boolean alwaysHide()
	{
		return false;
	}


	@ConfigItem(
			keyName = "removeMemberFade",
			name = "removeMemberFade",
			description = "removeMemberFade",
			position = 0,
			section = stats
	)
	default boolean removeMemberFade()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hotkey",
		name = "Screenshot hotkey",
		description = "When you press this key a screenshot will be taken",
		position = 17
	)
	default Keybind hotkey()
	{
		return Keybind.NOT_SET;
	}
}
