/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.runelite.api.Skill.AGILITY;

@PluginDescriptor(
	name = "Nom Agility",
	description = "Highlight what to click",
	tags = {"nomscripts","grace", "marks", "overlay", "shortcuts", "skilling", "traps", "sepulchre", "nom"},
	enabledByDefault = false
)
@Slf4j
public class NomAgilityPlugin extends Plugin
{
	@Getter
	private final Map<TileObject, NomObstacle> obstacles = new HashMap<>();

	@Getter
	private final List<Tile> marksOfGrace = new ArrayList<>();

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NomAgilityOverlay nomAgilityOverlay;

	@Inject
	private NomAgilityMinimapOverlay nomAgilityMinimapOverlay;


	@Inject
	private NomAgilityFoodOverlay nomAgilityFoodOverlay;

	@Inject
	private Notifier notifier;

	@Inject
	private Client client;

	@Inject
	private NomAgilityConfig config;

	private int lastAgilityXp;

	@Getter
	private int agilityLevel;

	@Provides
	NomAgilityConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NomAgilityConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(nomAgilityOverlay);
		overlayManager.add(nomAgilityMinimapOverlay);
		overlayManager.add(nomAgilityFoodOverlay);
		agilityLevel = client.getBoostedSkillLevel(AGILITY);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(nomAgilityOverlay);
		overlayManager.remove(nomAgilityMinimapOverlay);
		overlayManager.remove(nomAgilityFoodOverlay);
		marksOfGrace.clear();
		obstacles.clear();
		agilityLevel = 0;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{

	}

	@Getter
	private NomCourses course = null;

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		if (statChanged.getSkill() != AGILITY)
		{
			return;
		}

		// Get course
		setCourse();
		nomAgilityOverlay.statTimer();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case HOPPING:
			case LOGIN_SCREEN:
				course = null;
				break;
			case LOADING:
				marksOfGrace.clear();
				obstacles.clear();
				setCourse();
				break;
		}
	}

	private void setCourse() {
		double distance = Double.MAX_VALUE;
		for (NomCourses value : NomCourses.values()) {
			double courseDistance = value.getCourseData().getLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation());
			if (courseDistance < distance) {
				distance = courseDistance;
				course = value;
			}
		}
		if (distance > 120) {
			log.info("No course");
			course = null;
		} else {
			log.info("Setting course " + course);
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		onTileObject(event.getTile(), null, event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		onTileObject(event.getTile(), event.getGameObject(), null);
	}



	private void onTileObject(Tile tile, TileObject oldObject, TileObject newObject)
	{
		obstacles.remove(oldObject);

		if (newObject == null)
		{
			return;
		}

		if (NomObstacles.COURSE_OBSTACLE_IDS.contains(newObject.getId()) ||
			NomObstacles.PORTAL_OBSTACLE_IDS.contains(newObject.getId()) ||
			(NomObstacles.TRAP_OBSTACLE_IDS.contains(newObject.getId())
				&& NomObstacles.TRAP_OBSTACLE_REGIONS.contains(newObject.getWorldLocation().getRegionID())) ||
			NomObstacles.SEPULCHRE_OBSTACLE_IDS.contains(newObject.getId()) ||
			NomObstacles.SEPULCHRE_SKILL_OBSTACLE_IDS.contains(newObject.getId()))
		{
			obstacles.put(newObject, new NomObstacle(tile));
			System.out.println("Add "+newObject.getId());
		}
	}
}
