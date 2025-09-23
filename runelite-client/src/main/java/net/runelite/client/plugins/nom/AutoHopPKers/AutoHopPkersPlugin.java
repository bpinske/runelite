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

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
		name = "AutoHopPKers",
		description = "Automatically hops and plays a sound when a PKer appears.",
		tags = {"pk", "pkers", "wilderness", "attack", "range", "nomscripts"},
		enabledByDefault = false
)
public class AutoHopPkersPlugin extends Plugin
{
	private final Pattern WILDERNESS_LEVEL_PATTERN = Pattern.compile(".*?(\\d+)-(\\d+).*");

	private Instant lastSoundPlayed;

	@Getter
	private int lower = -1;
	@Getter
	private int upper = -1;

	private static final int PVPWORLD_TEXT_WIDGET_ID = 52;

	@Inject
	private Client client;

	@Inject
	private AutoHopPKersUtil autoHopPKersUtil;

	@Inject
	private AutoHopPkersConfig config;

	@Provides
	AutoHopPkersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AutoHopPkersConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		lastSoundPlayed = Instant.now().minus(Duration.ofSeconds(config.frequency())); // Allow sound on first encounter
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

		updateWildernessLevel();

		if (config.checkEveryTick()) {
			for (Player player : client.getPlayers()) {
				checkPlayer(player);
			}
		}
	}


	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event)
	{
		checkPlayer(event.getPlayer());
	}

	private void updateWildernessLevel()
	{
		final Widget wildernessLevelWidget = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
		final Widget pvpWorldWidget = client.getWidget(90, PVPWORLD_TEXT_WIDGET_ID);

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
		if (m.matches())
		{
			lower = Integer.parseInt(m.group(1));
			upper = Integer.parseInt(m.group(2));
		}
	}

	private void checkPlayer(Player p) {
		if (p == null || p.equals(client.getLocalPlayer()) || !inWilderness())
		{
			return;
		}

		boolean isAttackable = p.getCombatLevel() >= lower && p.getCombatLevel() <= upper;
		if (!isAttackable)
		{
			return;
		}

		boolean isSkulled = p.getSkullIcon() != SkullIcon.NONE;

		// --- Sound Alert Logic ---
		if (config.pkerPing())
		{
			boolean shouldPlaySound;
			if (config.soundOnlyOnSkulled()) {
				shouldPlaySound = isSkulled; // Only play sound if they are skulled
			} else {
				shouldPlaySound = true; // Play sound for anyone in combat range
			}

			if (shouldPlaySound && Instant.now().isAfter(lastSoundPlayed.plus(Duration.ofSeconds(config.frequency()))))
			{
				client.playSoundEffect(SoundEffectID.TOWN_CRIER_BELL_DING, SoundEffectVolume.HIGH);
				lastSoundPlayed = Instant.now();
			}
		}

		// --- Auto Hop Logic ---
		if (config.enableAutoHop())
		{
			boolean shouldHop;
			if (config.hopOnlyOnSkulled()) {
				shouldHop = isSkulled; // Only hop if they are skulled
			} else {
				shouldHop = true; // Hop for anyone in combat range
			}

			if (shouldHop)
			{
				autoHopPKersUtil.Hop();
				System.out.println("Hop triggered by player: " + p.getName());
			}
		}
	}

	public boolean inWilderness() {
		final Widget wildyWidget = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
		final Widget pvpWorldWidget = client.getWidget(90, PVPWORLD_TEXT_WIDGET_ID);
		final Widget safeZoneWidget = client.getWidget(WidgetInfo.PVP_WORLD_SAFE_ZONE);

		if (safeZoneWidget != null && !safeZoneWidget.isHidden())
		{
			return false;
		}
		return (wildyWidget != null && !wildyWidget.isHidden()) ||
				(pvpWorldWidget != null && !pvpWorldWidget.isHidden());
	}
}