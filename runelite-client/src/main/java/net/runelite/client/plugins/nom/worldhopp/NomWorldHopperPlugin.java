/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * Copyright (c) 2019, gregg1494 <https://github.com/gregg1494>
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
package net.runelite.client.plugins.nom.worldhopp;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;

import javax.inject.Inject;

@PluginDescriptor(
	name = "Nom World Hopper",
	description = "Allows you to quickly hop worlds",
	tags = {"nomscripts","ping", "switcher"},
	hidden = true
)
@Slf4j
public class NomWorldHopperPlugin extends Plugin
{
	@Inject
	private KeyManager keyManager;
	@Inject
	private NomWorldHopperConfig config;

	@Inject
	NomWorldHopUtil hopUtil;

	private final HotkeyListener previousKeyListener = new HotkeyListener(() -> config.previousKey())
	{
		@Override
		public void hotkeyPressed()
		{
			hopUtil.startHopping();
		}
	};
	private final HotkeyListener nextKeyListener = new HotkeyListener(() -> config.nextKey())
	{
		@Override
		public void hotkeyPressed()
		{
			hopUtil.stopHopping();
		}
	};

	@Provides
	NomWorldHopperConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NomWorldHopperConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		keyManager.registerKeyListener(previousKeyListener);
		keyManager.registerKeyListener(nextKeyListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		keyManager.unregisterKeyListener(previousKeyListener);
		keyManager.unregisterKeyListener(nextKeyListener);
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged event)
	{
		if (event.getGroup().equals(NomWorldHopperConfig.GROUP))
		{
			hopUtil.setBlacklist(config.worldBlackList());
			hopUtil.setDelaySeconds(config.hopDelay());
			if (config.toggleHopping()) {
				hopUtil.startHopping();
			} else {
				hopUtil.stopHopping();
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		hopUtil.onGameTick(event);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		hopUtil.onChatMessage(event);
	}
}
