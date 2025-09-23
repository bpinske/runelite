/*
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
 * Copyright (c) 2019, Jordan Atwood <nightfirecat@protonmail.com>
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
 * ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import java.util.*;

@Slf4j
public class AutoHopPKersUtil {
    private static final int MAX_PLAYER_COUNT = 1950;

    // This set now includes SKILL_TOTAL because we handle it separately and explicitly.
    private static final Set<WorldType> SKIPPABLE_WORLD_TYPES = EnumSet.of(
            WorldType.PVP,
            WorldType.BOUNTY,
            WorldType.PVP_ARENA,
            WorldType.SKILL_TOTAL,
            WorldType.QUEST_SPEEDRUNNING,
            WorldType.HIGH_RISK,
            WorldType.LAST_MAN_STANDING,
            WorldType.BETA_WORLD,
            WorldType.LEGACY_ONLY,
            WorldType.EOC_ONLY,
            WorldType.NOSAVE_MODE,
            WorldType.TOURNAMENT,
            WorldType.FRESH_START_WORLD,
            WorldType.DEADMAN,
            WorldType.SEASONAL
    );

    @Inject
    private WorldService worldService;
    @Inject
    private Client client;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private ClientThread clientThread;

    /**
     * Public entry point to initiate the hop. Schedules the hop logic to run on the client thread.
     */
    public void Hop() {
        clientThread.invoke(this::findAndHopToNextWorld);
    }

    /**
     * Finds the next suitable world from a pre-filtered list of valid worlds and hops to it.
     */
    private void findAndHopToNextWorld() {
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult == null || client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        World currentWorld = worldResult.findWorld(client.getWorld());
        if (currentWorld == null) {
            return;
        }

        List<World> allWorlds = worldResult.getWorlds();
        boolean isMembersWorld = currentWorld.getTypes().contains(WorldType.MEMBERS);
        List<World> validWorlds = buildValidWorldList(allWorlds, isMembersWorld);

        if (validWorlds.size() <= 1) {
            sendCantHopMessage();
            return;
        }

        int currentWorldIndexInValidList = validWorlds.indexOf(currentWorld);
        if (currentWorldIndexInValidList == -1) {
            // This case is unlikely but could happen if the current world is full or special.
            // We can just hop to the first valid world in the list.
            hopToWorld(validWorlds.get(0));
            return;
        }

        // Get the next world in the list, wrapping around to the start if we're at the end.
        int nextWorldIndex = (currentWorldIndexInValidList + 1) % validWorlds.size();
        World nextWorld = validWorlds.get(nextWorldIndex);

        hopToWorld(nextWorld);
    }

    /**
     * Filters the complete world list to find all worlds that are valid hop targets.
     *
     * @param allWorlds    The complete list of all worlds.
     * @param isMembers    True if the player is currently on a members world.
     * @return A filtered list of valid worlds to hop to.
     */
    private List<World> buildValidWorldList(List<World> allWorlds, boolean isMembers) {
        List<World> validWorlds = new ArrayList<>();
        int totalLevel = client.getTotalLevel();

        for (World world : allWorlds) {
            EnumSet<WorldType> types = world.getTypes();

            if (world.getPlayers() >= MAX_PLAYER_COUNT) {
                continue;
            }

            if (types.contains(WorldType.MEMBERS) != isMembers) {
                continue;
            }

            if (!Collections.disjoint(types, SKIPPABLE_WORLD_TYPES)) {
                continue;
            }

            // This is a redundant check since SKILL_TOTAL is in the skippable set, but it's good practice
            // in case that set is modified later. This logic correctly handles total level worlds.
            if (types.contains(WorldType.SKILL_TOTAL)) {
                try {
                    String activity = world.getActivity();
                    int spaceIndex = activity.indexOf(" ");
                    if (spaceIndex != -1) {
                        int totalRequirement = Integer.parseInt(activity.substring(0, spaceIndex));
                        if (totalLevel < totalRequirement) {
                            continue; // Skip if player doesn't meet the requirement
                        }
                    }
                } catch (NumberFormatException ex) {
                    log.warn("Failed to parse total level requirement for world {}", world.getId(), ex);
                    continue; // Skip if parsing fails
                }
            }

            validWorlds.add(world);
        }
        return validWorlds;
    }

    /**
     * Executes the hop to a specific world object.
     *
     * @param world The RuneLite API world object to hop to.
     */
    private void hopToWorld(World world) {
        assert client.isClientThread();

        final net.runelite.api.World rsWorld = client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

        if (client.getGameState() == GameState.LOGIN_SCREEN) {
            client.changeWorld(rsWorld);
        } else {
            client.openWorldHopper();
            client.hopToWorld(rsWorld);
        }
    }

    /**
     * Sends a CONSOLE message to the player indicating no suitable world was found.
     */
    private void sendCantHopMessage() {
        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.NORMAL)
                .append("Couldn't find a world to quick-hop to.")
                .build();

        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(chatMessage)
                .build());
    }
}