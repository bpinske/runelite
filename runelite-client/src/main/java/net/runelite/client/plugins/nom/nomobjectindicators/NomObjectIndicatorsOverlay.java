/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED to, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED to, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.nom.nomobjectindicators;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.InstantTimer;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

class NomObjectIndicatorsOverlay extends Overlay
{
	private final Client client;
	private final NomObjectIndicatorsConfig config;
	private final NomObjectIndicatorsPlugin plugin;


	@Inject
	private NomObjectIndicatorsOverlay(Client client, NomObjectIndicatorsConfig config, NomObjectIndicatorsPlugin plugin, InstantTimer instantTimer)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;

        setPosition(OverlayPosition.DYNAMIC);
		setPriority(PRIORITY_LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	private WorldPoint lastPoint = null;

	@Override
	public Dimension render(Graphics2D graphics)
	{

//		if (config.bucket1DisableWhileBusy() ) {
//
//			WorldPoint currentPoint = client.getLocalPlayer().getWorldLocation();
//			if (!currentPoint.equals(lastPoint) ||
//					client.getLocalPlayer().getAnimation() != -1 || client.getLocalDestinationLocation() != null) {
//				lastPoint = currentPoint;
//				return null;
//			}
//		}


		// The plugin has already determined which objects should be visible based on
		// the active buckets and inventory counts. This overlay just renders them.
		List<ColorTileObject> objectsToRender = plugin.getObjects();

		if (objectsToRender.isEmpty() || config.solidSquare() <= 0)
		{
			return null;
		}

		for (ColorTileObject obj : objectsToRender)
		{
			TileObject object = obj.getTileObject();

			if (object.getPlane() != client.getPlane())
			{
				continue;
			}

			// The color is retrieved from the object itself, which was set by the plugin
			// based on the bucket's specific configuration.
			Color color = obj.getBorderColor();
			if (color == null)
			{
				// Fallback color in case of an issue, though it should not be needed.
				color = Color.MAGENTA;
			}

			int size = config.solidSquare();
			Shape shape = getConvexHull(object);

			if (shape == null)
			{
				continue;
			}

			if (config.randomDot())
			{
				SquareOverlay.drawRandomBounds(graphics, shape, size, color);
			}
			else
			{
				SquareOverlay.drawCenterSquare(graphics, shape.getBounds(), size, color);
			}
		}

		return null;
	}

	private Shape getConvexHull(TileObject object)
	{
		if (object instanceof GameObject)
		{
			return ((GameObject) object).getConvexHull();
		}
		else if (object instanceof WallObject)
		{
			return ((WallObject) object).getConvexHull();
		}
		else if (object instanceof DecorativeObject)
		{
			return ((DecorativeObject) object).getConvexHull();
		}
		else if (object instanceof GroundObject)
		{
			return ((GroundObject) object).getConvexHull();
		}

		return object.getCanvasTilePoly();
	}
}