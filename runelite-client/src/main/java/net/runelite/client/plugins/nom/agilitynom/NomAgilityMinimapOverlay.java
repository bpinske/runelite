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
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.plugins.nom.agilitynom.rooftops.RooftopStage;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

class NomAgilityMinimapOverlay extends Overlay
{
	private final Client client;
	private final NomAgilityPlugin plugin;
	private final NomAgilityConfig config;

	@Inject
	private NomAgilityMinimapOverlay(Client client, NomAgilityPlugin plugin, NomAgilityConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		NomCourses course = plugin.getCourse();
		if (course == null) {
			return null;
		}

		for (RooftopStage rooftopStage : course.getCourseData().getStageList()) {
			if (!rooftopStage.onArea(client)) continue;

			if (client.getLocalPlayer().getWorldLocation().distanceTo2D(rooftopStage.getTile()) < config.onlyShowWhenNoBounds()) return null;

			SquareOverlay.drawOnMinimap(client, graphics, inbetweenTile(client.getLocalPlayer().getWorldLocation(),rooftopStage.getTile()), config.solidSquare(), config.getMinimapColor());
		}
		return null;
	}

	public WorldPoint inbetweenTile(WorldPoint al, WorldPoint bl) {
		int distance = Math.min(al.distanceTo(bl),12);
		double deltaX = bl.getX()-al.getX();
		double deltaY = bl.getY()-al.getY();
		double ratio = distance/Math.sqrt(Math.pow(deltaX,2)+Math.pow(deltaY,2));
		return new WorldPoint((int)(al.getX()+deltaX*ratio),(int)(al.getY()+deltaY*ratio),al.getPlane());
	}
}
