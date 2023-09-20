/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
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
package net.runelite.client.plugins.nom.screenshotaccplugin;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.*;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import static net.runelite.client.RuneLite.SCREENSHOT_DIR;

@PluginDescriptor(
	name = "Account Screenshot",
	description = "Take images for account info",
	tags = {"nomscripts","external", "images", "imgur", "integration", "notifications"},
	enabledByDefault = false
)
@Slf4j
public class AccountScreenshotPlugin extends Plugin
{
	private boolean shouldTakeScreenshot;

	@Inject
	private AccountScreenshotConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AccountScreenshotOverlay accountScreenshotOverlay;

	@Inject
	private Client client;

	@Inject
	private ClientUI clientUi;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private DrawManager drawManager;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private KeyManager keyManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private ImageCapture imageCapture;

	@Getter(AccessLevel.PACKAGE)
	private BufferedImage reportButton;

	private NavigationButton titleBarButton;

	@Getter
	private BufferedImage emptyQuest;

	private final HotkeyListener hotkeyListener = new HotkeyListener(() -> config.hotkey())
	{
		@Override
		public void hotkeyPressed()
		{
			manualScreenshot();
		}
	};

	@Provides
	AccountScreenshotConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AccountScreenshotConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(accountScreenshotOverlay);
		SCREENSHOT_DIR.mkdirs();
		keyManager.registerKeyListener(hotkeyListener);

		emptyQuest = ImageUtil.getResourceStreamFromClass(getClass(), "empty.png");

		final BufferedImage iconImage = ImageUtil.getResourceStreamFromClass(getClass(), "screenshot.png");

		titleBarButton = NavigationButton.builder()
			.tooltip("Take account screenshot")
			.icon(iconImage)
			.onClick(this::manualScreenshot)
			.popup(ImmutableMap
				.<String, Runnable>builder()
				.put("Open screenshot folder...", () ->
				{
					LinkBrowser.open(SCREENSHOT_DIR.toString());
				})
				.build())
			.build();

		clientToolbar.addNavigation(titleBarButton);

		spriteManager.getSpriteAsync(SpriteID.CHATBOX_REPORT_BUTTON, 0, s -> reportButton = s);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(accountScreenshotOverlay);
		clientToolbar.removeNavigation(titleBarButton);
		keyManager.unregisterKeyListener(hotkeyListener);
	}

	private void manualScreenshot()
	{
		takeScreenshot("", null);
	}


	/**
	 * Saves a screenshot of the client window to the screenshot folder as a PNG,
	 * and optionally uploads it to an image-hosting service.
	 *
	 * @param fileName Filename to use, without file extension.
	 * @param subDir   Subdirectory to store the captured screenshot in.
	 */
	private void takeScreenshot(String fileName, String subDir)
	{
		if (client.getGameState() == GameState.LOGIN_SCREEN)
		{
			// Prevent the screenshot from being captured
			log.info("Login screenshot prevented");
			return;
		}

		Consumer<Image> imageCallback = (img) ->
		{
			// This callback is on the game thread, move to executor thread
			executor.submit(() -> takeScreenshot(fileName, subDir, img));
		};

//		if (config.displayDate())
//		{
//			accountScreenshotOverlay.queueForTimestamp(imageCallback);
//		}
//		else
		{
			drawManager.requestNextFrameListener(imageCallback);
		}
	}

	private void takeScreenshot(String fileName, String subDir, Image image)
	{

		Widget skillContainer = client.getWidget(WidgetInfo.SKILLS_CONTAINER);
		if (skillContainer == null) return;
		int height = skillContainer.getHeight()+config.borderThickness()*2 - config.trimTop() - config.trimBot();
		int width = skillContainer.getWidth()+config.borderThickness()*2 - config.trimRight() - config.trimLeft();
		BufferedImage screenshot = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics graphics = screenshot.getGraphics();

		Point p = skillContainer.getCanvasLocation();
		int gameOffsetX = -p.getX() + config.borderThickness() - config.trimLeft();
		int gameOffsetY = -p.getY() + config.borderThickness() - config.trimTop();

		System.out.println(p.getX() +" " + skillContainer.getRelativeX() + " " + skillContainer.getOriginalX() + " " + clientUi.getCanvasOffset().getX());

		// Draw the game onto the screenshot
		graphics.drawImage(image, gameOffsetX, gameOffsetY, null);
		graphics.setColor(config.borderColor());
		graphics.fillRect(0, 0, config.borderThickness(), height);
		graphics.fillRect(0, 0, width, config.borderThickness());
		graphics.fillRect(0, height-config.borderThickness(), width, config.borderThickness());
		graphics.fillRect(width-config.borderThickness(), 0, config.borderThickness(), height);
		imageCapture.takeScreenshot(screenshot, fileName, subDir, false, ImageUploadStyle.NEITHER);
	}

	private void hideStats() {
		Widget statsContainer = client.getWidget(WidgetInfo.SKILLS_CONTAINER);
		if (statsContainer == null || statsContainer.getStaticChildren() == null || statsContainer.isHidden()) return;

		List<String> list = Text.fromCSV(config.statsToHide());
//		System.out.println(list);
//		System.out.println(statsContainer.getStaticChildren().length);
		int i = -1;
		for (Widget staticChild : statsContainer.getStaticChildren()) {
			i++;
//			System.out.println(StatToIndex.name(i));
			if (staticChild == null || (staticChild.getChildren() == null && staticChild.getStaticChildren() == null)) continue;
//			System.out.println("not null");
			if ("Overall".equals(StatToIndex.name(i))) {
				if (!config.totalLevelText().isEmpty()) {
					Player player = client.getLocalPlayer();
					if (player == null) return;
					for (Widget childChild : staticChild.getStaticChildren()) {
						if (childChild != null && childChild.getText() != null) {

							String text = config.totalLevelText().replace("?ttl?",client.getTotalLevel()+"").replace("?cb?",player.getCombatLevel()+"");
							childChild.setText(text);
						}
					}
				}
				if (!list.contains(StatToIndex.name(i))) continue;
				for (Widget childChild : staticChild.getStaticChildren()) {
					if (childChild != null && childChild.getText() != null) {
						childChild.setText(" ");
					}
				}
				continue;
			}

			if (!list.contains(StatToIndex.name(i))) continue;
			for (Widget childChild : staticChild.getChildren()) {
				if (childChild != null && childChild.getText() != null) {
					childChild.setText(" ");
				}
			}
		}

	}


	@Subscribe
	private void onBeforeRender(BeforeRender event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (config.alwaysHide())
		{
			hideStats();
		}

		if (config.removeMemberFade()) removeMemberFade();
	}

	private void removeMemberFade() {
		Widget statsContainer = client.getWidget(WidgetInfo.SKILLS_CONTAINER);
		if (statsContainer == null || statsContainer.getStaticChildren() == null || statsContainer.isHidden()) return;

		for (Widget staticChild : statsContainer.getStaticChildren()) {
			if (staticChild != null && staticChild.getChildren() != null)
			for (Widget childChild : staticChild.getChildren()) {
				if (childChild != null && childChild.getOpacity() != 0) {
					childChild.setHidden(true);
				}
			}
		}

	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("accountscreenshot"))
		{
			hideStats();
		}
	}
}
