/*
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
 * Copyright (c) 2019, Jordan Atwood <nightfirecat@protonmail.com>
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
package net.runelite.client.plugins.nom.AutoHopPKers;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
		name = "AutoHopPKers",
		description = "PKers are cancer",
		tags = {"pk", "pkers", "wilderness", "attack", "range", "nomscripts"},
		enabledByDefault = false
)
public class AutoHopPkersPlugin extends Plugin
{
	private final Pattern WILDERNESS_LEVEL_PATTERN = Pattern.compile(".*?(\\d+)-(\\d+).*");

	private Instant lastPing;

	@Getter
	private String lastName = "";
	@Getter
	private int lower = -1;
	@Getter
	private int upper = -1;

	private final int PVPWORLD_TEXT = 52;

	@Inject
	private Client client;

	@Inject
	private ClientUI clientUI;

	@Inject
	private ClientThread clientThread;

@	Inject
	private AutoHopPKersUtil autoHopPKersUtil;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Notifier notifier;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Override
	protected void startUp() throws Exception
	{
		lastPing = Instant.now();
		lower = -1;
		upper = -1;
	}


	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		for (Player player : client.getPlayers()) {
			checkPlayer(player);
		}

		final Widget wildernessLevelWidget = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
		final Widget pvpWorldWidget = client.getWidget(90,PVPWORLD_TEXT);

		String wildernessLevelText = "";
		if (pvpWorldWidget != null && !pvpWorldWidget.isHidden()) {
			wildernessLevelText = pvpWorldWidget.getText();
		}
		if (wildernessLevelText.isEmpty() && (wildernessLevelWidget != null && !wildernessLevelWidget.isHidden())) {
			wildernessLevelText = wildernessLevelWidget.getText();
		}
		if (wildernessLevelText.isEmpty()) {
			lower = 0;
			upper = 0;
			return;
		}

		final Matcher m = WILDERNESS_LEVEL_PATTERN.matcher(wildernessLevelText);
		if (!m.matches()) {
			return;
		}
		lower = Integer.parseInt(m.group(1));
		upper = Integer.parseInt(m.group(2));
	}


	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event)
	{
		checkPlayer(event.getPlayer());
	}

	private boolean checkPlayer(Player p) {
		if (!inWilderness()) return false;
		if (p.equals(client.getLocalPlayer())) return false;
		if (p.getCombatLevel() >= lower && p.getCombatLevel() <= upper)
		{
			autoHopPKersUtil.Hop();
			System.out.println("criteria triggered");
			return true;
		}
		return false;
	}

	public boolean inWilderness() {
		final Widget wildy = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
		final Widget pvpWorldWidget = client.getWidget(90,PVPWORLD_TEXT);
		final Widget safeZone = client.getWidget(WidgetInfo.PVP_WORLD_SAFE_ZONE);
		if (safeZone != null && !safeZone.isHidden()) return false;
		return (wildy != null && !wildy.isHidden()) ||
				(pvpWorldWidget != null && !pvpWorldWidget.isHidden()) ;
	}
}
