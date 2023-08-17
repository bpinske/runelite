package net.runelite.client.plugins.nom.worldhopp;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.game.WorldService;
import net.runelite.client.util.Text;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;

import javax.inject.Inject;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class NomWorldHopUtil {
    private static final int MAX_PLAYER_COUNT = 1950;
    private static final int DISPLAY_SWITCHER_MAX_ATTEMPTS = 3;


    @Inject
    private WorldService worldService;
    @Inject
    private Client client;
    @Inject
    private ChatMessageManager chatMessageManager;


    @Inject
    private ClientThread clientThread;

    private HashSet<Integer> blacklist = new HashSet<>();
    @Setter
    private int delaySeconds = 0;
    private int gameTicksSinceLastHop = 0;


    public void startHopping() {
        hopping.set(true);
        clientThread.invoke(() -> hop(true));
    }

    public void stopHopping() {
        hopping.set(false);
    }

    public void setBlacklist(String text) {
        blacklist.clear();
        Text.fromCSV(text).stream().mapToInt(Integer::parseInt).forEach(blacklist::add);
    }

    public void onGameTick(GameTick event)
    {
        gameTicksSinceLastHop++;
        if (gameTicksSinceLastHop * 0.6D < delaySeconds) return;
        if (quickHopTargetWorld == null)
        {
            return;
        }

        if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null)
        {
            client.openWorldHopper();

            if (++displaySwitcherAttempts >= DISPLAY_SWITCHER_MAX_ATTEMPTS)
            {
                String chatMessage = new ChatMessageBuilder()
                        .append(ChatColorType.NORMAL)
                        .append("Failed to quick-hop after ")
                        .append(ChatColorType.HIGHLIGHT)
                        .append(Integer.toString(displaySwitcherAttempts))
                        .append(ChatColorType.NORMAL)
                        .append(" attempts.")
                        .build();

                chatMessageManager
                        .queue(QueuedMessage.builder()
                                .type(ChatMessageType.CONSOLE)
                                .runeLiteFormattedMessage(chatMessage)
                                .build());

                resetQuickHopper();
            }
        }
        else
        {
            client.hopToWorld(quickHopTargetWorld);
            resetQuickHopper();
        }
    }

    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }

        if (event.getMessage().equals("Please finish what you're doing before using the World Switcher."))
        {
            resetQuickHopper();
        }
    }




    private net.runelite.api.World quickHopTargetWorld;
    private int displaySwitcherAttempts = 0;

    private void hop(boolean previous)
    {
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult == null || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        World currentWorld = worldResult.findWorld(client.getWorld());

        if (currentWorld == null)
        {
            return;
        }

        List<World> worlds = worldResult.getWorlds();

        int worldIdx = worlds.indexOf(currentWorld);
        int totalLevel = client.getTotalLevel();
        boolean currIsMembers = currentWorld.getTypes().contains(WorldType.MEMBERS);
        World world;
        do
        {
			/*
				Get the previous or next world in the list,
				starting over at the other end of the list
				if there are no more elements in the
				current direction of iteration.
			 */
            if (previous)
            {
                worldIdx--;

                if (worldIdx < 0)
                {
                    worldIdx = worlds.size() - 1;
                }
            }
            else
            {
                worldIdx++;

                if (worldIdx >= worlds.size())
                {
                    worldIdx = 0;
                }
            }

            world = worlds.get(worldIdx);
            if (blacklist.contains(worldIdx)) continue;

            EnumSet<WorldType> types = world.getTypes().clone();


            boolean tempIsMembers = types.contains(WorldType.MEMBERS);
            if (currIsMembers != tempIsMembers) continue;

            if (types.contains(WorldType.SKILL_TOTAL))
            {
                try
                {
                    int totalRequirement = Integer.parseInt(world.getActivity().substring(0, world.getActivity().indexOf(" ")));

                    if (totalLevel < totalRequirement)
                    {
                        continue;
                    }
                }
                catch (NumberFormatException ex)
                {
                    log.warn("Failed to parse total level requirement for target world", ex);
                }
            }

            // Avoid switching to near-max population worlds, as it will refuse to allow the hop if the world is full
            if (world.getPlayers() >= MAX_PLAYER_COUNT)
            {
                continue;
            }

            break;
        }
        while (world != currentWorld);

        if (world == currentWorld)
        {
            String chatMessage = new ChatMessageBuilder()
                    .append(ChatColorType.NORMAL)
                    .append("Couldn't find a world to quick-hop to.")
                    .build();

            chatMessageManager.queue(QueuedMessage.builder()
                    .type(ChatMessageType.CONSOLE)
                    .runeLiteFormattedMessage(chatMessage)
                    .build());
        }
        else
        {
            hop(world.getId());
        }
    }

    private void hop(int worldId)
    {
        assert client.isClientThread();

        WorldResult worldResult = worldService.getWorlds();
        // Don't try to hop if the world doesn't exist
        World world = worldResult.findWorld(worldId);
        if (world == null)
        {
            return;
        }

        final net.runelite.api.World rsWorld = client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

        if (client.getGameState() == GameState.LOGIN_SCREEN)
        {
            // on the login screen we can just change the world by ourselves
            client.changeWorld(rsWorld);
            return;
        }

        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.NORMAL)
                .append("Quick-hopping to World ")
                .append(ChatColorType.HIGHLIGHT)
                .append(Integer.toString(world.getId()))
                .append(ChatColorType.NORMAL)
                .append("..")
                .build();

        chatMessageManager
                .queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());

        quickHopTargetWorld = rsWorld;
        displaySwitcherAttempts = 0;
    }
    private AtomicBoolean hopping = new AtomicBoolean();
    private void resetQuickHopper()
    {
        displaySwitcherAttempts = 0;
        quickHopTargetWorld = null;
        gameTicksSinceLastHop = 0;

        if (hopping.get()) {
            hop(false);
        }
    }
}
