/*
 * Copyright (c) 2020, ThatGamerBlue <thatgamerblue@gmail.com>
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
package net.runelite.client.plugins.nom.playerhider;

import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.events.StatChanged;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Player Hider",
	description = "Hides your player for streamers.",
	tags = {"nomscripts","twitch, nomscripts"},
	enabledByDefault = false
)
public class PlayerHiderPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private PlayerHiderConfig config;

	@Inject
	private EventBus eventBus;


	@Provides
	private PlayerHiderConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlayerHiderConfig.class);
	}

	@Override
	public void startUp()
	{
//		changeLook();

	}

	@Override
	public void shutDown()
	{

	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("playerhider"))
		{
//			changeLook();
			if (event.getKey().equals("thumbnail")) {
				thumbnailStats();
			}
			if (event.getKey().equals("statsString")) {
				changeStats(config.statsString());
			}
			if (event.getKey().equals("isDupeLook")) {
				dupeLook();
			}
		}
	}

	private void dupeLook() {
		Player player = client.getLocalPlayer();
		if (player == null || player.getPlayerComposition() == null) return;

	}

	private int bodyColor = 4;
	private void changeLook() {
		Player player = client.getLocalPlayer();
		if (player == null || player.getPlayerComposition() == null) return;

		int[] kits = player.getPlayerComposition().getEquipmentIds();

		if (config.isPlayerModel() && config.playerModel() > 0) {
			player.getPlayerComposition().setTransformedNpcId(config.playerModel());
		}
		if (config.weaponId() > 0) {
			kits[KitType.WEAPON.getIndex()] = config.weaponId();
			player.getPlayerComposition().setHash();
		}
		if (config.offhandId() > 0) {
			kits[KitType.SHIELD.getIndex()] = config.offhandId();
			player.getPlayerComposition().setHash();
		}


		if (config.bodyColor()) {
			for (int i = 0; i < 5; i++) {
				player.getPlayerComposition().getColors()[i] = config.playerColor();
			}
			player.getPlayerComposition().setHash();
		}
		if (config.fixedLook()) {

//			kits[KitType.HAIR.getIndex()] = config.weaponId();
//			kits[KitType.TORSO.getIndex()] = config.weaponId();
//			kits[KitType.HAIR.getIndex()] = config.weaponId();
//			kits[KitType.HAIR.getIndex()] = config.weaponId();
		}
	}

	private HashMap<Skill, Integer> changedStats = new HashMap<>();
	private void changeStats(String s) {
		List<String> statLevels = Text.fromCSV(s);
		if (statLevels.size() % 2 != 0) return;

		for (int i = 0; i < statLevels.size(); i+=2) {
			String stat = statLevels.get(i);
			int level = Integer.parseInt(statLevels.get(i+1));
			if (level == 0) continue;
			Skill skill = Skill.valueOf(stat);
			changedStats.put(skill, level);
			changeStat(skill, level);
		}
	}

	private void changeStat(Skill skill, int level) {
		level = Ints.constrainToRange(level, 1, Experience.MAX_REAL_LEVEL);
		int xp = Experience.getXpForLevel(level);

		client.getBoostedSkillLevels()[skill.ordinal()] = level;
		client.getRealSkillLevels()[skill.ordinal()] = level;
		client.getSkillExperiences()[skill.ordinal()] = xp;

		client.queueChangedSkill(skill);
	}


	private void thumbnailStats() {
		Widget statsContainer = client.getWidget(WidgetInfo.SKILLS_CONTAINER);
		if (statsContainer == null || statsContainer.getStaticChildren() == null) return;
		for (Widget child : statsContainer.getStaticChildren()) {
			if (child != null && child.getChildren() != null) {
				for (Widget childChild : child.getChildren()) {
					if (childChild != null && child.getText() != null) {
						childChild.setText("9?");
					}
				}
			}
		}
	}


	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
//		changeLook();
//		bodyColor = ThreadLocalRandom.current().nextInt(9)+4;
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event)
	{
//		final Player local = client.getLocalPlayer();
//		final Player player = event.getPlayer();
//
//		if (player.equals(local))
//		{
//			changeLook();
//		}
	}

	@Subscribe
	private void onBeforeRender(BeforeRender event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		changeLook();
		changeOrbStats();
		changeStats(config.statsString());
		changeBankCount();
	}

	private void changeBankCount() {

		Widget bankTop = client.getWidget(WidgetInfo.BANK_ITEM_COUNT_TOP);
		Widget bankBottom = client.getWidget(WidgetInfo.BANK_ITEM_COUNT_BOTTOM);

		if (bankTop == null) return;
		int bankNum = config.bankSlotsFilled() + LocalDateTime.now().getDayOfYear()%80;


		setTextWidget(bankTop,""+ bankNum);
		setTextWidget(bankBottom,""+800);
	}

	private void setTextWidget(Widget widget, String text) {
		if (widget == null) return;
		if (!widget.getText().isEmpty()) {
			widget.setText(text);
		} else {
			List<Widget> widgets = new ArrayList<>();
			widgets.addAll(Arrays.asList(widget.getStaticChildren()));
			widgets.addAll(Arrays.asList(widget.getDynamicChildren()));
			widgets.addAll(Arrays.asList(widget.getNestedChildren()));
			for (Widget widget1 : widgets) {
				if (widget1 != null && !widget1.getText().isEmpty()) {
					widget1.setText(text);
				}
			}
		}
	}

	private void changeOrbStats() {
		int hpLevel = changedStats.getOrDefault(Skill.HITPOINTS,0);
		int prayerLevel = changedStats.getOrDefault(Skill.PRAYER,0);

		Widget health = client.getWidget(WidgetInfo.MINIMAP_HEALTH_ORB);
		Widget prayer = client.getWidget(WidgetInfo.MINIMAP_PRAYER_ORB_TEXT);
		if (prayer == null || health == null|| health.getStaticChildren() == null) return;
		for (Widget staticChild : health.getStaticChildren()) {
			if (staticChild.getText().isEmpty()) continue;
			if (hpLevel > 0) staticChild.setText(hpLevel+"");
		}

		if (prayerLevel > 0) prayer.setText(prayerLevel+"");
	}
}
