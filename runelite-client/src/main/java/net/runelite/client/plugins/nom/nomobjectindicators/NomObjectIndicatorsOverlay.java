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

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import net.runelite.api.*;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

class NomObjectIndicatorsOverlay extends Overlay
{
	private final Client client;
	private final NomObjectIndicatorsConfig config;
	private final NomObjectIndicatorsPlugin plugin;
	private boolean highlightUntilEmpty = false;


	@Inject
	private NomObjectIndicatorsOverlay(Client client, NomObjectIndicatorsConfig config, NomObjectIndicatorsPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(PRIORITY_LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		if (config.renderOnInventoryFull())
		{
			final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			if (inventory != null)
			{
				final int currentCount = inventory.count();
				if (currentCount >= config.inventoryActivationCount())
				{
					this.highlightUntilEmpty = true;
				}
				else if (currentCount <= config.inventoryDeactivationCount())
				{
					this.highlightUntilEmpty = false;
				}
			}
		}
		else
		{
			// If the feature is disabled, ensure the highlighting state is always off.
			this.highlightUntilEmpty = false;
		}

		// Main rendering logic check
		if (config.renderOnInventoryFull() && !this.highlightUntilEmpty)
		{
			return null;
		}


		if (config.solidSquare() <= 0)
		{
			return null;
		}

		var objects = plugin.getObjects();
		if (objects.isEmpty())
		{
			return null;
		}

		for (ColorTileObject obj : objects)
		{
			TileObject object = obj.getTileObject();

			if (object.getPlane() != client.getPlane())
			{
				continue;
			}

			ObjectComposition composition = obj.getComposition();
			if (composition.getImpostorIds() != null)
			{
				// This is a multiloc
				composition = composition.getImpostor();
				// Only mark the object if the name still matches
				if (composition == null
						|| Strings.isNullOrEmpty(composition.getName())
						|| "null".equals(composition.getName())
						|| !composition.getName().equals(obj.getName()))
				{
					continue;
				}
			}

			Color color = MoreObjects.firstNonNull(obj.getBorderColor(), config.markerColor());
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