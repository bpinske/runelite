/*
 * Copyright (c) 2019 Spudjb <https://github.com/spudjb>
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
package net.runelite.client.plugins.nom.questacclist;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.SoundEffectID;
import net.runelite.api.SpriteID;
import net.runelite.api.VarClientInt;
import net.runelite.api.Varbits;
import net.runelite.api.WorldType;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;
import static net.runelite.client.RuneLite.SCREENSHOT_DIR;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatClient;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.nom.widgetfiller.UsefulWidgets;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.StringUtils;

@PluginDescriptor(
	name = "Quest List Account",
	description = "Writes completed quests to file",
	enabledByDefault = false
)
public class AccountQuestListPlugin extends Plugin
{
	public static final int QUESTLIST_GROUP_ID = 399;

	private static final List<String> QUEST_HEADERS = ImmutableList.of("Free Quests", "Members' Quests", "Miniquests");

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private AccountQuestListConfig config;

	private EnumMap<QuestContainer, Collection<QuestWidget>> questSet;

	private File playerFolder;

	@Inject
	private ChatClient chatClient;

	@Inject
	private KeyManager keyManager;

	private final KeyListener searchHotkeyListener = new KeyListener()
	{
		@Override
		public void keyTyped(KeyEvent e)
		{
		}

		@Override
		public void keyPressed(KeyEvent e)
		{
			Keybind keybind = config.searchKeybind();
			if (keybind.matches(e))
			{
				String s = updateList();
				if (StringUtils.isEmpty(s)) return;
				clientThread.invokeLater(()-> {
					client.playSoundEffect(SoundEffectID.GE_COIN_TINKLE);
				});
				final StringSelection stringSelection = new StringSelection(updateList());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
				e.consume();
			}
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
		}
	};


	@Provides
	AccountQuestListConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AccountQuestListConfig.class);
	}

	@Override
	protected void startUp()
	{
		SCREENSHOT_DIR.mkdirs();
		keyManager.registerKeyListener(searchHotkeyListener);
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(searchHotkeyListener);
	}



	private boolean isOnQuestTab()
	{
		return client.getVar(Varbits.QUEST_TAB) == 0 && client.getVar(VarClientInt.INVENTORY_TAB) == 2;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!isOnQuestTab()) return;
		if (this.client.getLocalPlayer() != null && this.client.getLocalPlayer().getName() != null) {
			EnumSet<WorldType> worldTypes = this.client.getWorldType();
			String playerDir = this.client.getLocalPlayer().getName();
			if (worldTypes.contains(WorldType.DEADMAN)) {
				playerDir = playerDir + "-Deadman";
			}

			playerFolder = new File(RuneLite.SCREENSHOT_DIR, playerDir);
		} else {
			playerFolder = RuneLite.SCREENSHOT_DIR;
		}
		playerFolder.mkdirs();
		File file = new File(playerFolder, "quests.txt");
		if (file.exists()) return;
		addQuestButtons();
	}

	private void addQuestButtons()
	{
		try {
			File file = new File(playerFolder, "quests.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(updateList());

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String updateList()
	{
		Widget header = client.getWidget(UsefulWidgets.QUESTLIST_BOX.getGroupId(),UsefulWidgets.QUESTLIST_BOX.getChildId());

		if (header == null) return "";
		questSet = new EnumMap<>(QuestContainer.class);
		final Widget container = client.getWidget(UsefulWidgets.QUESTLIST_CONTAINER.getGroupId(),UsefulWidgets.QUESTLIST_CONTAINER.getChildId());

		final Widget freeList = client.getWidget(QuestContainer.FREE_QUESTS.widgetInfo.getGroupId(),QuestContainer.FREE_QUESTS.widgetInfo.getChildId());
		final Widget memberList = client.getWidget(QuestContainer.MEMBER_QUESTS.widgetInfo.getGroupId(),QuestContainer.MEMBER_QUESTS.widgetInfo.getChildId());
		final Widget miniList = client.getWidget(QuestContainer.MINI_QUESTS.widgetInfo.getGroupId(),QuestContainer.MINI_QUESTS.widgetInfo.getChildId());

		final Widget questListBox = client.getWidget(UsefulWidgets.QUESTLIST_BOX.getGroupId(),UsefulWidgets.QUESTLIST_BOX.getChildId());

		if (container == null || freeList == null || memberList == null || miniList == null || questListBox == null)
		{
			return "";
		}

		StringJoiner sj = new StringJoiner(config.delimiter());
		for (Widget staticChild : questListBox.getStaticChildren())
		{
			if (staticChild.getText().contains("Quest Points")) {
				sj.add(staticChild.getText());
				continue;
			}
		}
//		try
//		{
//			sj.add(chatClient.getQp(client.getLocalPlayer().getName())+"");
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}

		for (QuestContainer questContainer : QuestContainer.values()) {

			Widget list = client.getWidget(questContainer.widgetInfo.getGroupId(),questContainer.widgetInfo.getChildId());
			if (list == null)
			{
				return "";
			}

			Collection<QuestWidget> quests = questSet.get(questContainer);

			if (quests != null)
			{
				if (quests.stream().noneMatch(w ->
				{
					Widget codeWidget = w.getQuest();
					if (codeWidget == null)
					{
						return false;
					}
					return list.getChild(codeWidget.getIndex()) == codeWidget;
				}))
				{
					quests = null;
				}
			}

			if (quests == null)
			{
				// Find all of the widgets that we care about, sorting by their Y value
				quests = Arrays.stream(list.getDynamicChildren())
						.sorted(Comparator.comparing(Widget::getRelativeY))
						.filter(w -> !QUEST_HEADERS.contains(w.getText()))
						.map(w -> new QuestWidget(w, Text.removeTags(w.getText())))
						.collect(Collectors.toList());
				questSet.put(questContainer, quests);
			}

			for (QuestWidget questInfo : quests)
			{
				Widget quest = questInfo.getQuest();
				QuestState questState = QuestState.getByColor(quest.getTextColor());

				if (questState == QuestState.COMPLETE) {
					sj.add(questInfo.title);
				}
			}
		}
		return sj.toString() + config.delimiter();
	}

	@AllArgsConstructor
	@Getter
	private enum QuestContainer
	{
		FREE_QUESTS(UsefulWidgets.QUESTLIST_FREE_CONTAINER),
		MEMBER_QUESTS(UsefulWidgets.QUESTLIST_MEMBERS_CONTAINER),
		MINI_QUESTS(UsefulWidgets.QUESTLIST_MINIQUEST_CONTAINER);

		private final UsefulWidgets widgetInfo;
	}

	@AllArgsConstructor
	@Getter
	private enum QuestState
	{
		NOT_STARTED(0xff0000, "Not started", SpriteID.MINIMAP_ORB_HITPOINTS),
		IN_PROGRESS(0xffff00, "In progress", SpriteID.MINIMAP_ORB_HITPOINTS_DISEASE),
		COMPLETE(0xdc10d, "Completed", SpriteID.MINIMAP_ORB_HITPOINTS_POISON),
		ALL(0, "All", SpriteID.MINIMAP_ORB_PRAYER),
		NOT_COMPLETED(0, "Not Completed", SpriteID.MINIMAP_ORB_RUN);

		private final int color;
		private final String name;
		private final int spriteId;

		static QuestState getByColor(int color)
		{
			for (QuestState value : values())
			{
				if (value.getColor() == color)
				{
					return value;
				}
			}

			return null;
		}
	}

	@Data
	@AllArgsConstructor
	private static class QuestWidget
	{
		private Widget quest;
		private String title;
	}
}
