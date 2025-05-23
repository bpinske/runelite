/*Nomnom
 * Copyright (c) 2017, Aria <aria@ar1as.space>
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
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
 nomnom */
package net.runelite.client.plugins.nom.zulrah.overlays;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.nom.zulrah.ZulrahInstance;
import net.runelite.client.plugins.nom.zulrah.ZulrahPlugin;
import net.runelite.client.plugins.nom.zulrah.phase.ZulrahPhase;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class ZulrahOverlay extends Overlay
{
	private static final Color TILE_BORDER_COLOR = new Color(0, 0, 0, 100);
	private static final Color NEXT_TEXT_COLOR = new Color(255, 255, 255, 100);

	private final Client client;
	private final ZulrahPlugin plugin;

	@Inject
	ZulrahOverlay(@Nullable Client client, ZulrahPlugin plugin)
	{
		setPosition(OverlayPosition.DYNAMIC);
		this.client = client;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		ZulrahInstance instance = plugin.getInstance();

		if (instance == null)
		{
			return null;
		}

		ZulrahPhase currentPhase = instance.getPhase();
		ZulrahPhase nextPhase = instance.getNextPhase();
		if (currentPhase == null)
		{
			return null;
		}

		LocalPoint startTile = instance.getStartLocation();
		if (nextPhase != null && currentPhase.getStandLocation() == nextPhase.getStandLocation())
		{
			drawStandTiles(graphics, startTile, currentPhase, nextPhase);
		}
		else
		{
			drawStandTile(graphics, startTile, currentPhase, false);
			drawStandTile(graphics, startTile, nextPhase, true);
		}
		drawZulrahTileMinimap(graphics, startTile, currentPhase, false);
		drawZulrahTileMinimap(graphics, startTile, nextPhase, true);

		return null;
	}

	private void drawStandTiles(Graphics2D graphics, LocalPoint startTile, ZulrahPhase currentPhase, ZulrahPhase nextPhase)
	{
		LocalPoint localTile = currentPhase.getStandTile(startTile);
		localTile = new LocalPoint(localTile.getX(), localTile.getY());
		Polygon poly = Perspective.getCanvasTilePoly(client, localTile);
		Point textLoc = Perspective.getCanvasTextLocation(client, graphics, localTile, "Next", 0);
		if (poly != null && textLoc != null)
		{
			Color northColor = currentPhase.getColor();
			renderTile(graphics, client.getLocalDestinationLocation(), northColor);
			graphics.setColor(TILE_BORDER_COLOR);
			graphics.setStroke(new BasicStroke(2));
			graphics.drawPolygon(poly);
			graphics.setColor(NEXT_TEXT_COLOR);
			graphics.drawString("Next", textLoc.getX(), textLoc.getY());
		}
		if (nextPhase.isJad())
		{
			Image jadPrayerImg = ZulrahImageManager.getProtectionPrayerBufferedImage(nextPhase.getPrayer());
			if (jadPrayerImg != null)
			{
				Point imageLoc = Perspective.getCanvasImageLocation(client, localTile, (BufferedImage) jadPrayerImg, 0);
				if (imageLoc != null)
				{
					graphics.drawImage(jadPrayerImg, imageLoc.getX(), imageLoc.getY(), null);
				}
			}
		}
	}

	private void drawStandTile(Graphics2D graphics, LocalPoint startTile, ZulrahPhase phase, boolean next)
	{
		if (phase == null)
		{
//			log.info("phase null");
			return;
		}

		LocalPoint localTile = phase.getStandTile(startTile);
		localTile = new LocalPoint(localTile.getX(), localTile.getY());
		Polygon poly = Perspective.getCanvasTilePoly(client, localTile);
		Color color = phase.getColor();
		if (poly != null)
		{
			graphics.setColor(TILE_BORDER_COLOR);
			graphics.setStroke(new BasicStroke(2));
			graphics.drawPolygon(poly);
			graphics.setColor(color);
			graphics.fillPolygon(poly);
			Point textLoc = Perspective.getCanvasTextLocation(client, graphics, localTile, "Stand", 0);
			if (textLoc != null)
			{
				graphics.setColor(NEXT_TEXT_COLOR);
				graphics.drawString("Stand", textLoc.getX(), textLoc.getY());
			}
		} else
		{
			log.info("poly null");
		}
		if (next)
		{
			Point textLoc = Perspective.getCanvasTextLocation(client, graphics, localTile, "Next", 0);
			if (textLoc != null)
			{
				graphics.setColor(NEXT_TEXT_COLOR);
				graphics.drawString("Next", textLoc.getX(), textLoc.getY());
			}
			if (phase.isJad())
			{
				Image jadPrayerImg = ZulrahImageManager.getProtectionPrayerBufferedImage(phase.getPrayer());
				if (jadPrayerImg != null)
				{
					Point imageLoc = Perspective.getCanvasImageLocation(client, localTile, (BufferedImage) jadPrayerImg, 0);
					if (imageLoc != null)
					{
						graphics.drawImage(jadPrayerImg, imageLoc.getX(), imageLoc.getY(), null);
					}
				}
			}
		}
	}

	private void drawZulrahTileMinimap(Graphics2D graphics, LocalPoint startTile, ZulrahPhase phase, boolean next)
	{
		if (phase == null)
		{
			return;
		}
		LocalPoint zulrahLocalTile = phase.getZulrahTile(startTile);
		Point zulrahMinimapPoint = Perspective.localToCanvas(client, zulrahLocalTile, 0);
		if (zulrahMinimapPoint == null)
		{
			return;
		}
		Color color = phase.getColor();
		graphics.setColor(color);
		graphics.fillOval(zulrahMinimapPoint.getX(), zulrahMinimapPoint.getY(), 5, 5);
		graphics.setColor(TILE_BORDER_COLOR);
		graphics.setStroke(new BasicStroke(1));
		graphics.drawOval(zulrahMinimapPoint.getX(), zulrahMinimapPoint.getY(), 5, 5);
		if (next)
		{
			graphics.setColor(NEXT_TEXT_COLOR);
			FontMetrics fm = graphics.getFontMetrics();
			graphics.drawString("Next", zulrahMinimapPoint.getX() - fm.stringWidth("Next") / 2, zulrahMinimapPoint.getY() - 2);
		}
	}

	public WorldPoint toWorldPoint(Point point)
	{
		return toWorldPoint(point, 0);
	}

	public WorldPoint toWorldPoint(Point point, int plane) { return new WorldPoint(point.getX(), point.getY(), plane); }

	private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color)
	{
		if (dest == null)
		{
			return;
		}

		final Polygon poly = Perspective.getCanvasTilePoly(client, dest);

		if (poly == null)
		{
			return;
		}

		OverlayUtil.renderPolygon(graphics, poly, color);
	}
}
