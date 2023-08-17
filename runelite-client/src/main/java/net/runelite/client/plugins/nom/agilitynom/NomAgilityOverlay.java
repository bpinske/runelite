/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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

import net.runelite.api.Client;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.InstantTimer;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.plugins.nom.agilitynom.rooftops.RooftopStage;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

class NomAgilityOverlay extends Overlay
{
	private final Client client;
	private final NomAgilityPlugin plugin;
	private final NomAgilityConfig config;

	@Inject
	private NomAgilityOverlay(Client client, NomAgilityPlugin plugin, NomAgilityConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		instantTimer = new InstantTimer();
		statTimer = new InstantTimer();
	}

	public void statTimer() {
		statTimer.resetTimer();
	}

	private final InstantTimer instantTimer;
	private final InstantTimer statTimer;
	private WorldPoint lastPoint = null;
	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.disableWhenMovingOrAnimating() && statTimer.runningMoreThan(config.forceShow())) {
			if (config.animatingDelay() > 0 && !instantTimer.runningMoreThan(config.animatingDelay())) return null;

			WorldPoint currentPoint = client.getLocalPlayer().getWorldLocation();
			if (!currentPoint.equals(lastPoint) ||
					client.getLocalPlayer().getAnimation() != -1 || client.getLocalDestinationLocation() != null) {
				lastPoint = currentPoint;
				instantTimer.resetTimer();
				return null;
			}
		}
		final List<Tile> marksOfGrace = plugin.getMarksOfGrace();
		NomCourses course = plugin.getCourse();
		if (course == null) {
			return null;
		}

		for (RooftopStage rooftopStage : course.getCourseData().getStageList()) {
			if (!rooftopStage.onArea(client)) {
//				System.out.println("Not on " + rooftopStage.getId());
				continue;
			}
			if (!marksOfGrace.isEmpty()) {
				for (Tile markOfGraceTile : marksOfGrace)
				{
					if (rooftopStage.onArea(markOfGraceTile.getWorldLocation())) {
						SquareOverlay.drawTile(client,graphics,markOfGraceTile.getWorldLocation(),config.getOverlayColor(),config.solidSquare());
						return null;
					}
				}
			}

			for (TileObject object : plugin.getObstacles().keySet()) {
				if (object.getId() == rooftopStage.getId()) {
					Shape objectClickbox = object.getClickbox();

					if (objectClickbox == null || objectClickbox.getBounds() == null) {
						return null;
					}
					graphics.setColor(Color.WHITE);
					graphics.draw(objectClickbox);
					SquareOverlay.drawRandomBounds(graphics, object, config.solidSquare(), config.getOverlayColor());

					return null;
				}
			}
		}
		return null;
	}
}
