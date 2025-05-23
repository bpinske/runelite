/*
 * Copyright (c) 2018, James Swindle <wilingua@gmail.com>
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
package net.runelite.client.plugins.npchighlight;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;

public class NomNpcSceneOverlay extends Overlay
{
	// Anything but white text is quite hard to see since it is drawn on
	// a dark background
	private static final Color TEXT_COLOR = Color.WHITE;

	private static final NumberFormat TIME_LEFT_FORMATTER = DecimalFormat.getInstance(Locale.US);

	static
	{
		((DecimalFormat)TIME_LEFT_FORMATTER).applyPattern("#0.0");
	}

	private final Client client;
	private final NomNpcIndicatorsConfig config;
	private final NOmNpcIndicatorsPlugin plugin;

	@Inject
	NomNpcSceneOverlay(Client client, NomNpcIndicatorsConfig config, NOmNpcIndicatorsPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(SquareOverlay.OVERLAY_LAYER);
	}

	Instant interactTime = Instant.now();
	Actor lastActor = null;
	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.shouldHighlightRandoms()) {
			for (NPC npc : client.getNpcs()) {
				highlightRandoms(graphics, npc, config.getRandomColor());
			}
		}
		if (config.showRespawnTimer())
		{
			plugin.getDeadNpcsToDisplay().forEach((id, npc) -> renderNpcRespawn(npc, graphics));
		}


		Player local = client.getLocalPlayer();
		if (local == null) return null;
		Actor actor = local.getInteracting();

		if (config.checkCombat() && actor != null) {
			return null;
		}

		for (NPC npc : plugin.getHighlightedNpcs())
		{
			renderNpcOverlay(graphics, npc, config.getHighlightColor());
		}
		if (config.interacting()) {
			if (actor != null) {
				interactTime = Instant.now();
				lastActor = actor;
				if (config.randomDot())
					SquareOverlay.drawRandomBounds(graphics,actor.getConvexHull(),config.solidSquare(),config.getHighlightColor());
				else
				SquareOverlay.drawCenterSquare(graphics, actor, config.solidSquare(), config.getHighlightColor());
			} else if (lastActor != null && interactTime.plusMillis(config.interactingDelay()).isAfter(Instant.now())) {
				if (config.randomDot())
					SquareOverlay.drawRandomBounds(graphics,lastActor.getConvexHull(),config.solidSquare(),config.getHighlightColor());
				else
				SquareOverlay.drawCenterSquare(graphics, lastActor, config.solidSquare(), config.getHighlightColor());
			}
		}
		return null;
	}

	private void renderNpcRespawn(final NomMemorizedNpc npc, final Graphics2D graphics)
	{
		if (npc.getPossibleRespawnLocations().isEmpty())
		{
			return;
		}

		final WorldPoint respawnLocation = npc.getPossibleRespawnLocations().get(0);
		final LocalPoint lp = LocalPoint.fromWorld(client, respawnLocation.getX(), respawnLocation.getY());

		if (lp == null)
		{
			return;
		}

		final Color color = config.getHighlightColor();

		final LocalPoint centerLp = new LocalPoint(
			lp.getX() + Perspective.LOCAL_TILE_SIZE * (npc.getNpcSize() - 1) / 2,
			lp.getY() + Perspective.LOCAL_TILE_SIZE * (npc.getNpcSize() - 1) / 2);

		final Polygon poly = Perspective.getCanvasTileAreaPoly(client, centerLp, npc.getNpcSize());

		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, color);
		}

		final Instant now = Instant.now();
		final double baseTick = ((npc.getDiedOnTick() + npc.getRespawnTime()) - client.getTickCount()) * (Constants.GAME_TICK_LENGTH / 1000.0);
		final double sinceLast = (now.toEpochMilli() - plugin.getLastTickUpdate().toEpochMilli()) / 1000.0;
		final double timeLeft = Math.max(0.0, baseTick - sinceLast);
		final String timeLeftStr = TIME_LEFT_FORMATTER.format(timeLeft);

		final int textWidth = graphics.getFontMetrics().stringWidth(timeLeftStr);
		final int textHeight = graphics.getFontMetrics().getAscent();

		final Point canvasPoint = Perspective
			.localToCanvas(client, centerLp, respawnLocation.getPlane());

		if (canvasPoint != null)
		{
			final Point canvasCenterPoint = new Point(
				canvasPoint.getX() - textWidth / 2,
				canvasPoint.getY() + textHeight / 2);

			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, timeLeftStr, TEXT_COLOR);
		}
	}

	private void renderNpcOverlay(Graphics2D graphics, NPC actor, Color color)
	{
		NPCComposition npcComposition = actor.getTransformedComposition();
		if (npcComposition == null || !npcComposition.isInteractible()
			|| (actor.isDead() && config.ignoreDeadNpcs()))
		{
			return;
		}

		if (config.solidSquare() > 0) {
			if (config.randomDot()) SquareOverlay.drawRandomBounds(graphics,actor.getConvexHull(),config.solidSquare(),config.getHighlightColor());
			else
			SquareOverlay.drawCenterSquare(graphics, actor, config.solidSquare(), color);
			return;
		}

		if (config.highlightHull())
		{
			Shape objectClickbox = actor.getConvexHull();
			renderPoly(graphics, color, objectClickbox);
		}

		if (config.highlightTile())
		{
			int size = npcComposition.getSize();
			LocalPoint lp = actor.getLocalLocation();
			Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);

			renderPoly(graphics, color, tilePoly);
		}

		if (config.highlightSouthWestTile())
		{
			int size = npcComposition.getSize();
			LocalPoint lp = actor.getLocalLocation();

			int x = lp.getX() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);
			int y = lp.getY() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);

			Polygon southWestTilePoly = Perspective.getCanvasTilePoly(client, new LocalPoint(x, y));

			renderPoly(graphics, color, southWestTilePoly);
		}

		if (config.drawNames() && actor.getName() != null)
		{
			String npcName = Text.removeTags(actor.getName());
			Point textLocation = actor.getCanvasTextLocation(graphics, npcName, actor.getLogicalHeight() + 40);

			if (textLocation != null)
			{
				OverlayUtil.renderTextLocation(graphics, textLocation, npcName, color);
			}
		}
	}

	private void renderPoly(Graphics2D graphics, Color color, Shape polygon)
	{
		if (polygon != null)
		{
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(polygon);
		}
	}

	private void highlightRandoms(Graphics2D graphics, NPC actor, Color color) {
		Player local = client.getLocalPlayer();
		if (local == null) return;
		NPCComposition npcComposition = actor.getComposition();
		if (!local.equals(actor.getInteracting())) return;

		if (npcComposition == null || Arrays.stream(npcComposition.getActions()).noneMatch("Dismiss"::equals))
		{
			return;
		}

		if (config.solidSquare() > 0) {
			SquareOverlay.drawCenterSquare(graphics, actor, config.solidSquare(), color);
		} else {
			Shape objectClickbox = actor.getConvexHull();
			renderPoly(graphics, color, objectClickbox);
		}
	}
}
