/*
 * Copyright (c) 2018 kulers
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
package net.runelite.client.plugins.nom.bpinskeinventorytags;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BpinskeInventoryTagsOverlay extends WidgetItemOverlay
{
	private final Client client;
	private final ItemManager itemManager;
	private final BpinskeInventoryTagsPlugin plugin;
	private final BpinskeInventoryTagsConfig config;
	private final Cache<Long, Image> fillCache;
	private final Cache<Integer, Tag> tagCache;
	private final Tag NONE = new Tag();

	@Inject
	private BpinskeInventoryTagsOverlay(Client client, ItemManager itemManager, BpinskeInventoryTagsPlugin plugin, BpinskeInventoryTagsConfig config)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.plugin = plugin;
		this.config = config;
		showOnEquipment();
		showOnInventory();
		showOnInterfaces(
			InterfaceID.RAIDS_STORAGE_SIDE,
			InterfaceID.RAIDS_STORAGE_PRIVATE,
			InterfaceID.RAIDS_STORAGE_SHARED,
			InterfaceID.GRAVESTONE_GENERIC
		);
		fillCache = CacheBuilder.newBuilder()
			.concurrencyLevel(1)
			.maximumSize(32)
			.build();
		tagCache = CacheBuilder.newBuilder()
			.concurrencyLevel(1)
			.maximumSize(39)
			.build();
	}

	Boolean highlightUntilEmpty = false;
	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		final Tag tag = getTag(itemId);
		if (tag == null || tag.color == null)
		{
			return;
		}

		final Color color = tag.color;

		Rectangle bounds = widgetItem.getCanvasBounds();
		if (SquareOverlay.shouldHide(client, bounds, plugin, config.delay())) return;

		// Brandon addition
		// Useful for fishing botting where you want to highlight inventory items
		ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);

		if (itemContainer.count() == config.inventory_activation()) {
			highlightUntilEmpty = true;
		}
		else if (itemContainer.count() <= config.inventory_deactivation()){
			highlightUntilEmpty = false;
		}
		// Brandon addition

		if (highlightUntilEmpty) {
			if (config.solidSquareSize() > 0) {
				if (config.randomDot())
					SquareOverlay.drawRandomBounds(graphics, widgetItem.getCanvasBounds(), config.solidSquareSize(), color);
				else
					SquareOverlay.drawCenterSquare(graphics, bounds, config.solidSquareSize(), color);
			} else {
				final BufferedImage outline = itemManager.getItemOutline(itemId, widgetItem.getQuantity(), color);

				if (config.fillSolidColor()) {
					final AsyncBufferedImage image = itemManager.getImage(itemId, widgetItem.getQuantity(), false);
					graphics.drawImage(drawColorImage(image, color), (int) bounds.getX(), (int) bounds.getY(), null);
				} else {
					graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
				}
			}
		}


		if (config.showTagOutline())
		{
			final BufferedImage outline = itemManager.getItemOutline(itemId, widgetItem.getQuantity(), color);
			graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
		}

		if (config.showTagFill())
		{
			final Image image = getFillImage(color, widgetItem.getId(), widgetItem.getQuantity());
			graphics.drawImage(image, (int) bounds.getX(), (int) bounds.getY(), null);
		}

		if (config.showTagUnderline())
		{
			int heightOffSet = (int) bounds.getY() + (int) bounds.getHeight() + 2;
			graphics.setColor(color);
			graphics.drawLine((int) bounds.getX(), heightOffSet, (int) bounds.getX() + (int) bounds.getWidth(), heightOffSet);
		}
	}

	private Tag getTag(int itemId)
	{
		Tag tag = tagCache.getIfPresent(itemId);
		if (tag == null)
		{
			tag = plugin.getTag(itemId);
			if (tag == null)
			{
				tagCache.put(itemId, NONE);
				return null;
			}

			if (tag == NONE)
			{
				return null;
			}

			tagCache.put(itemId, tag);
		}
		return tag;
	}


	private BufferedImage drawColorImage(AsyncBufferedImage img, Color color) {
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage solidVers = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) solidVers.getGraphics();
		g2.setColor(color);
		g2.fillRect(0, 0, width, height);

		g2.setComposite(AlphaComposite.DstIn);
		g2.drawImage(img, 0, 0, width, height, 0, 0, width, height, null);
		return solidVers;
	}

	private Image getFillImage(Color color, int itemId, int qty)
	{
		long key = (((long) itemId) << 32) | qty;
		Image image = fillCache.getIfPresent(key);
		if (image == null)
		{
			final Color fillColor = ColorUtil.colorWithAlpha(color, config.fillOpacity());
			image = ImageUtil.fillImage(itemManager.getImage(itemId, qty, false), fillColor);
			fillCache.put(key, image);
		}
		return image;
	}

	void invalidateCache()
	{
		fillCache.invalidateAll();
		tagCache.invalidateAll();
	}

}

