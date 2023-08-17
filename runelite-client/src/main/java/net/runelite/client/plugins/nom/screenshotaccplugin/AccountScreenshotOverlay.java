/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.nom.screenshotaccplugin;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.VarClientInt;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.nom.widgetfiller.UsefulWidgets;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

class AccountScreenshotOverlay extends Overlay
{
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM. dd, yyyy");
	private static final int REPORT_BUTTON_X_OFFSET = 404;

	private final Client client;
	private final DrawManager drawManager;
	private final AccountScreenshotPlugin plugin;
	private final AccountScreenshotConfig config;

	private final Queue<Consumer<Image>> consumers = new ConcurrentLinkedQueue<>();

	@Inject
	private AccountScreenshotOverlay(Client client, DrawManager drawManager, AccountScreenshotPlugin plugin, AccountScreenshotConfig config)
	{
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		this.client = client;
		this.drawManager = drawManager;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showQuestOverlay() || !isOnQuestTab() || plugin.getEmptyQuest() == null) return null;

		Widget quest = client.getWidget(UsefulWidgets.QUESTLIST_BOX.getGroupId(),UsefulWidgets.QUESTLIST_BOX.getChildId());
		if (quest == null) return null;
		Point p = quest.getCanvasLocation();

		graphics.drawImage(plugin.getEmptyQuest(),p.getX()+config.offsetXImage(),p.getY()+config.offsetYImage()-24, null);

		graphics.drawString(config.questText(),p.getX()+config.offsetXText(),p.getY()+config.offsetYText()-24);

		return null;
	}

	private boolean isOnQuestTab()
	{
		return client.getVar(Varbits.QUEST_TAB) == 0 && client.getVar(VarClientInt.INVENTORY_TAB) == 2;
	}
}
