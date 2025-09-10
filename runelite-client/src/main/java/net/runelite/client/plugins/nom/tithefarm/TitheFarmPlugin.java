package net.runelite.client.plugins.nom.tithefarm;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@PluginDescriptor(
        name = "Tithe Farm Helper",
        description = "Provides assistance for the Tithe Farm minigame.",
        tags = {"tithe", "farm", "minigame", "skilling", "farming", "helper"}
)
public class TitheFarmPlugin extends Plugin
{
    private static final int TITHE_FARM_REGION_ID = 6966;
    private static final Set<Integer> TITHE_PATCH_OBJECTS = ImmutableSet.of(
            ObjectID.TITHE_PATCH, // Empty patch
            ObjectID.LOGAVANO_PLANT);


    @Inject
    private Client client;

    @Inject
    private TitheFarmConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private TitheFarmOverlay overlay;

    @Getter
    private final Map<WorldPoint, TitheFarmPatch> patches = new HashMap<>();
    @Getter
    private final Set<GameObject> highlights = new HashSet<>();
    @Getter
    private final Set<Integer> inventoryHighlights = new HashSet<>();

    private enum Phase
    {
        PLANTING,
        WATERING,
        HARVESTING,
        REFILLING,
        IDLE
    }

    private Phase currentPhase = Phase.IDLE;

    @Provides
    TitheFarmConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(TitheFarmConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        patches.clear();
        highlights.clear();
        inventoryHighlights.clear();
    }

    private boolean isInTitheFarm()
    {
        return client.getLocalPlayer() != null && client.getLocalPlayer().getWorldLocation().getRegionID() == TITHE_FARM_REGION_ID;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGGED_IN && isInTitheFarm())
        {
            // When logging in inside the farm, we need to assess the state
            // A full clear and rebuild is the safest option.
            patches.clear();
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (!isInTitheFarm())
        {
            return;
        }

        GameObject gameObject = event.getGameObject();
        if (TITHE_PATCH_OBJECTS.contains(gameObject.getId()))
        {
            TitheFarmPatch patch = new TitheFarmPatch(gameObject);
            updatePatchState(patch, gameObject.getId());
            patches.put(gameObject.getWorldLocation(), patch);
        }
        // When any game object spawns, we should re-evaluate our current phase
        determinePhase();
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        if (!isInTitheFarm())
        {
            return;
        }

        patches.remove(event.getGameObject().getWorldLocation());
        // When any game object despawns, we should re-evaluate our current phase
        determinePhase();
    }

    private void updatePatchState(TitheFarmPatch patch, int gameObjectId)
    {
        // This method would contain the full mapping of every plant object ID to its state.
        // For this example, we'll use a simplified version.
        switch (gameObjectId)
        {
            case ObjectID.TITHE_PATCH:
                patch.setState(TitheFarmPatch.State.EMPTY);
                break;
            case ObjectID.GOLOVANOVA_PLANT: // Example for a seedling
                patch.setState(TitheFarmPatch.State.SEEDLING);
                break;
            // Add cases for MIDLING, GROWN, HARVESTABLE, WATERED states based on their Object IDs
            default:
                // Assume other plant IDs are growing stages
                if (gameObjectId > ObjectID.TITHE_PATCH)
                {
                    patch.setState(TitheFarmPatch.State.GROWN); // Simplified placeholder
                }
                break;
        }
    }

    private void determinePhase()
    {
        // This is the main logic loop, which should be called whenever state changes.
        highlights.clear();
        inventoryHighlights.clear();

        long emptyPatches = patches.values().stream().filter(p -> p.getState() == TitheFarmPatch.State.EMPTY).count();
        long unwateredPatches = patches.values().stream().filter(p -> p.getState() != TitheFarmPatch.State.WATERED && p.getState() != TitheFarmPatch.State.EMPTY).count(); // Simplified
        long harvestablePatches = patches.values().stream().filter(p -> p.getState() == TitheFarmPatch.State.HARVESTABLE).count();

        if (emptyPatches > 0)
        {
            currentPhase = Phase.PLANTING;
            inventoryHighlights.add(ItemID.LOGAVANO_SEED);
            patches.values().stream()
                    .filter(p -> p.getState() == TitheFarmPatch.State.EMPTY)
                    .forEach(p -> highlights.add(p.getGameObject()));
        }
        else if (unwateredPatches > 0)
        {
            currentPhase = Phase.WATERING;
            patches.values().stream()
                    .filter(p -> p.getState() != TitheFarmPatch.State.WATERED) // simplified
                    .forEach(p -> highlights.add(p.getGameObject()));
        }
        else if (harvestablePatches > 0)
        {
            currentPhase = Phase.HARVESTING;
            patches.values().stream()
                    .filter(p -> p.getState() == TitheFarmPatch.State.HARVESTABLE)
                    .forEach(p -> highlights.add(p.getGameObject()));
        }
        else if (patches.size() > 0 && emptyPatches == 0 && unwateredPatches == 0 && harvestablePatches == 0)
        {
            // This case covers when everything is planted and watered, waiting to grow.
            currentPhase = Phase.IDLE;
        }

        else
        {
            // If all patches are gone (or we just finished harvesting), it's time to refill.
            currentPhase = Phase.REFILLING;
            inventoryHighlights.add(ItemID.GRICOLLERS_CAN);
            // We would need to find the water barrel GameObject here to highlight it.
        }
    }
}