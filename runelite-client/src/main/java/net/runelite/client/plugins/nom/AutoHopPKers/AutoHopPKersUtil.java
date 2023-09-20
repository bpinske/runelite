package net.runelite.client.plugins.nom.AutoHopPKers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.game.WorldService;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;

import javax.inject.Inject;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class AutoHopPKersUtil {
    private static final int MAX_PLAYER_COUNT = 1950;

    @Inject
    private WorldService worldService;
    @Inject
    private Client client;
    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private ClientThread clientThread;

    private HashSet<Integer> blacklist = new HashSet<>();

    public void Hop() {
        clientThread.invoke(() -> hop(true));
    }

    private void hop(boolean previous)
    {
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult == null || client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        World currentWorld = worldResult.findWorld(client.getWorld());
        if (currentWorld == null) {
            return;
        }

        List<World> worlds = worldResult.getWorlds();

        int worldIdx = worlds.indexOf(currentWorld);
        int totalLevel = client.getTotalLevel();
        boolean currIsMembers = currentWorld.getTypes().contains(WorldType.MEMBERS);
        World world;
        do
        {
            // Large hop to dodge pkers going one world at a time.
            worldIdx+=10;
            if (worldIdx >= worlds.size())
            {
                worldIdx = 0;
            }


            world = worlds.get(worldIdx);
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
        client.openWorldHopper();
        client.hopToWorld(rsWorld);
    }
}
