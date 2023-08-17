/*
 * Copyright (c) 2018, Brett Middle <https://github.com/bmiddle>
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
package net.runelite.client.plugins.nom.combatlevel;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("nomplayernotifier")
public interface NomPlayerNotifierConfig extends Config
{
	@ConfigItem(
			keyName = "frequency",
			name = "Seconds between each sound",
			description = "Seconds between each sound"
	)
	default int frequency()
	{
		return 5;
	}

	@ConfigItem(
		keyName = "pkerPing",
		name = "Make sound if pker shows",
		description = "Make sound if pker logs in"
	)
	default boolean pkerPing()
	{
		return true;
	}


	@ConfigItem(
			keyName = "notification",
			name = "Make notification if pker shows",
			description = "Make notification if pker shows"
	)
	default boolean notification()
	{
		return true;
	}


	@ConfigItem(
			keyName = "focus",
			name = "Request focus if pker shows",
			description = "Request focus if pker shows"
	)
	default boolean requestFocus()
	{
		return true;
	}


	@ConfigItem(
			keyName = "checkEveryTick",
			name = "checkEveryTick",
			description = "checkEveryTick"
	)
	default boolean checkEveryTick()
	{
		return false;
	}
}
