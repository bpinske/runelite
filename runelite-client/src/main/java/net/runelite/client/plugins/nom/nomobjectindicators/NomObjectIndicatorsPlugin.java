package net.runelite.client.plugins.nom.nomobjectindicators;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@PluginDescriptor(
		name = "Nom Object Markers",
		description = "Enable marking of objects in different buckets with inventory-based activation",
		tags = {"nom", "overlay", "objects", "mark", "marker", "bucket"}
)
@Slf4j
public class NomObjectIndicatorsPlugin extends Plugin
{
	private static final String CONFIG_GROUP = "nomobjectindicators";
	private static final int NUM_BUCKETS = 9;

	@Getter(AccessLevel.PACKAGE)
	private final List<ColorTileObject> objects = new ArrayList<>();
	private final Map<Integer, Set<ObjectPoint>> points = new HashMap<>();
	private final boolean[] bucketActive = new boolean[NUM_BUCKETS];

	// Track last known interaction state for cooldowns
	private boolean wasInteracting = false;
	private final Instant[] lastInteractionStop = new Instant[NUM_BUCKETS]; // Cooldown start time per bucket

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NomObjectIndicatorsOverlay overlay;

	@Inject
	private NomObjectIndicatorsConfig config;

	@Inject
	private Gson gson;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ColorPickerManager colorPickerManager;

	@Provides
	NomObjectIndicatorsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NomObjectIndicatorsConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		// Initialize interaction cooldowns to a distant past time
		for (int i = 0; i < NUM_BUCKETS; i++)
		{
			lastInteractionStop[i] = Instant.MIN;
		}
		clientThread.invokeLater(this::reloadPointsFromConfig);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		points.clear();
		objects.clear();
		Arrays.fill(bucketActive, false);
		Arrays.fill(lastInteractionStop, Instant.MIN);
		wasInteracting = false;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN || gameStateChanged.getGameState() == GameState.LOADING)
		{
			clientThread.invokeLater(this::reloadPointsFromConfig);
		}
	}


	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		checkBucketActivationAndUpdateRenderList();
	}

	private boolean isPlayerInteracting()
	{
		Player localPlayer = client.getLocalPlayer();
		return localPlayer != null && (localPlayer.getAnimation() != -1 || localPlayer.getInteracting() != null);
	}

	private boolean isPlayerMoving()
	{
		// A player is moving if they have a walk destination. This is a stable check.
		return client.getLocalDestinationLocation() != null;
	}

	private void checkBucketActivationAndUpdateRenderList()
	{
		final boolean isCurrentlyInteracting = isPlayerInteracting();
		final boolean isMoving = isPlayerMoving();
		final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		final int inventoryCount = (inventory == null) ? 0 : (int) Arrays.stream(inventory.getItems())
				.filter(item -> item.getId() != -1 && item.getQuantity() > 0)
				.count();

		boolean renderListNeedsRebuild = false;

		for (int i = 0; i < NUM_BUCKETS; i++)
		{
			final int bucketId = i + 1;
			int minCount = getBucketActivationCount(bucketId);
			int maxCount = getBucketDeactivationCount(bucketId);
			int cooldownSeconds = getBucketInteractionCooldown(bucketId);

			boolean bucketEnabled = getBucketEnabled(bucketId);
			boolean inventoryConditionMet = (inventoryCount >= minCount && inventoryCount < maxCount);

			// Check for interacting block with cooldown
			boolean interactingBlocks = false;
			if (getBucketDisableWhileInteracting(bucketId))
			{
				if (isCurrentlyInteracting)
				{
					// Player is currently interacting, so it blocks
					interactingBlocks = true;
					// Do not update lastInteractionStop yet, it updates when interaction *stops*
				}
				else
				{
					// Player is NOT interacting. Check if we're within the cooldown period.
					// If the last stop was within cooldownSeconds, it still blocks.
					if (Instant.now().isBefore(lastInteractionStop[i].plus(Duration.ofSeconds(cooldownSeconds))))
					{
						interactingBlocks = true;
					}
				}
			}

			boolean movingBlocks = isMoving && getBucketDisableWhileMoving(bucketId);

			boolean newBucketActiveState = bucketEnabled && inventoryConditionMet && !interactingBlocks && !movingBlocks;

			if (newBucketActiveState != bucketActive[i])
			{
				bucketActive[i] = newBucketActiveState;
				renderListNeedsRebuild = true;
			}
		}

		// Update lastInteractionStop for relevant buckets AFTER checking all buckets for the current tick
		if (wasInteracting && !isCurrentlyInteracting)
		{
			// Player just stopped interacting, update cooldown for all relevant buckets
			for (int i = 0; i < NUM_BUCKETS; i++)
			{
				if (getBucketDisableWhileInteracting(i + 1))
				{
					lastInteractionStop[i] = Instant.now();
				}
			}
			renderListNeedsRebuild = true; // State change due to cooldowns, might need rebuild
		}
		wasInteracting = isCurrentlyInteracting;


		if (renderListNeedsRebuild)
		{
			rebuildRenderableObjects();
		}
	}


	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (event.getType() != MenuAction.EXAMINE_OBJECT.getId() || !client.isKeyPressed(KeyCode.KC_SHIFT))
		{
			return;
		}

		final TileObject tileObject = findTileObject(client.getPlane(), event.getActionParam0(), event.getActionParam1(), event.getIdentifier());
		if (tileObject == null)
		{
			return;
		}

		ObjectPoint markedPoint = findMarkedObjectPoint(tileObject);
		if (markedPoint != null)
		{
			client.createMenuEntry(-1)
					.setOption("Unmark object")
					.setTarget(event.getTarget())
					.setParam0(event.getActionParam0())
					.setParam1(event.getActionParam1())
					.setIdentifier(event.getIdentifier())
					.setType(MenuAction.RUNELITE)
					.onClick(this::unmarkObject);

			client.createMenuEntry(-1)
					.setOption("Set color")
					.setTarget(event.getTarget())
					.setParam0(event.getActionParam0())
					.setParam1(event.getActionParam1())
					.setIdentifier(event.getIdentifier())
					.setType(MenuAction.RUNELITE)
					.onClick(this::setColor);

		}

		for (int i = 1; i <= NUM_BUCKETS; i++)
		{
			final int bucketId = i;
			client.createMenuEntry(-1)
					.setOption("Mark object (Bucket " + bucketId + ")")
					.setTarget(event.getTarget())
					.setParam0(event.getActionParam0())
					.setParam1(event.getActionParam1())
					.setIdentifier(event.getIdentifier())
					.setType(MenuAction.RUNELITE)
					.onClick(e -> markObject(e, bucketId));
		}
	}

	private void markObject(MenuEntry entry, int bucketId)
	{
		unmarkObject(entry);

		final TileObject object = findTileObject(client.getPlane(), entry.getParam0(), entry.getParam1(), entry.getIdentifier());
		if (object == null)
		{
			return;
		}

		ObjectComposition objectComposition = getObjectComposition(entry.getIdentifier());
		if (objectComposition == null)
		{
			return;
		}

		String name = objectComposition.getName();
		if (Strings.isNullOrEmpty(name) || name.equals("null"))
		{
			return;
		}

		final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, object.getLocalLocation());
		final int regionId = worldPoint.getRegionID();

		final ObjectPoint point = new ObjectPoint(object.getId(), name, regionId, worldPoint.getRegionX(), worldPoint.getRegionY(), worldPoint.getPlane());
		point.setBucket(bucketId);
		point.setBorderColor(getBucketColor(bucketId));

		Set<ObjectPoint> objectPoints = points.computeIfAbsent(regionId, k -> new HashSet<>());
		objectPoints.add(point);
		savePoints(regionId, objectPoints);
		rebuildRenderableObjects(); // Rebuild when markings change
	}

	private void unmarkObject(MenuEntry entry)
	{
		final TileObject object = findTileObject(client.getPlane(), entry.getParam0(), entry.getParam1(), entry.getIdentifier());
		if (object == null)
		{
			return;
		}

		final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, object.getLocalLocation());
		final int regionId = worldPoint.getRegionID();
		Set<ObjectPoint> objectPoints = points.get(regionId);
		if (objectPoints == null)
		{
			return;
		}

		final ObjectComposition objectComposition = getObjectComposition(entry.getIdentifier());
		if (objectComposition != null && objectPoints.removeIf(findObjectPredicate(objectComposition, object, worldPoint)))
		{
			savePoints(regionId, objectPoints);
			rebuildRenderableObjects(); // Rebuild when markings change
		}
	}

	private void rebuildRenderableObjects()
	{
		objects.clear();
		if (client.getGameState() != GameState.LOGGED_IN && client.getGameState() != GameState.LOADING)
		{
			return;
		}

		Scene scene = client.getScene();
		if (scene == null)
		{
			return;
		}

		Tile[][][] tiles = scene.getTiles();
		for (int z = 0; z < 4; z++)
		{
			for (int x = 0; x < 104; x++)
			{
				for (int y = 0; y < 104; y++)
				{
					Tile tile = tiles[z][x][y];
					if (tile == null)
					{
						continue;
					}

					for (GameObject gameObject : tile.getGameObjects())
					{
						checkObjectPoints(gameObject);
					}
					checkObjectPoints(tile.getWallObject());
					checkObjectPoints(tile.getDecorativeObject());
					checkObjectPoints(tile.getGroundObject());
				}
			}
		}
	}

	private void setColor(MenuEntry entry)
	{
		final TileObject object = findTileObject(client.getPlane(), entry.getParam0(), entry.getParam1(), entry.getIdentifier());
		if (object == null)
		{
			return;
		}

		ObjectPoint point = findMarkedObjectPoint(object);
		if (point == null)
		{
			return;
		}

		Color color = point.getBorderColor();
		if (color == null)
		{
			// Fallback to the bucket's default color if no specific color is set
			color = getBucketColor(point.getBucket());
		}

		final Color finalColor = color;
		SwingUtilities.invokeLater(() ->
		{
			RuneliteColorPicker colorPicker = colorPickerManager.create(SwingUtilities.windowForComponent(client.getCanvas()),
					finalColor, "Set Object Marker Color", false); // false = no alpha
			colorPicker.setOnClose(c -> clientThread.invokeLater(() ->
			{
				updateObjectPoint(object, p -> p.setBorderColor(c));
				rebuildRenderableObjects(); // Rebuild after color change
			}));
			colorPicker.setVisible(true);
		});
	}

	private void updateObjectPoint(TileObject object, Consumer<ObjectPoint> consumer)
	{
		ObjectPoint point = findMarkedObjectPoint(object);
		if (point == null)
		{
			return;
		}

		consumer.accept(point);

		// Save the change to the config
		final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, object.getLocalLocation());
		final int regionId = worldPoint.getRegionID();
		Set<ObjectPoint> regionPoints = points.get(regionId);
		if (regionPoints != null)
		{
			savePoints(regionId, regionPoints);
		}
	}


	private void checkObjectPoints(TileObject object)
	{
		if (object == null)
		{
			return;
		}

		ObjectPoint point = findMarkedObjectPoint(object);
		if (point != null && point.getBucket() > 0 && point.getBucket() <= NUM_BUCKETS && bucketActive[point.getBucket() - 1])
		{
			ObjectComposition objectComposition = getObjectComposition(object.getId());
			if (objectComposition != null)
			{
				Color color = point.getBorderColor();
				if (color == null)
				{
					// Fallback if no color is stored (shouldn't happen with new marking logic)
					color = getBucketColor(point.getBucket());
				}
				objects.add(new ColorTileObject(object, objectComposition, point.getName(), color, null, (byte) 0));

			}
		}
	}

	@Nullable
	private ObjectPoint findMarkedObjectPoint(TileObject object)
	{
		if (object == null)
		{
			return null;
		}

		final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, object.getLocalLocation());
		final Set<ObjectPoint> regionPoints = points.get(worldPoint.getRegionID());
		if (regionPoints == null || regionPoints.isEmpty())
		{
			return null;
		}

		final ObjectComposition objectComposition = getObjectComposition(object.getId());
		if (objectComposition == null)
		{
			return null;
		}

		return regionPoints.stream()
				.filter(findObjectPredicate(objectComposition, object, worldPoint))
				.findFirst()
				.orElse(null);
	}

	private Predicate<ObjectPoint> findObjectPredicate(ObjectComposition objectComposition, TileObject object, WorldPoint worldPoint)
	{
		return op -> (op.getId() == object.getId() || op.getName().equals(objectComposition.getName()))
				&& op.getRegionX() == worldPoint.getRegionX()
				&& op.getRegionY() == worldPoint.getRegionY()
				&& op.getZ() == worldPoint.getPlane();
	}

	private void savePoints(final int regionId, final Set<ObjectPoint> points)
	{
		if (points.isEmpty())
		{
			configManager.unsetConfiguration(CONFIG_GROUP, "region_" + regionId);
		}
		else
		{
			final String json = gson.toJson(points);
			configManager.setConfiguration(CONFIG_GROUP, "region_" + regionId, json);
		}
	}

	private void reloadPointsFromConfig()
	{
		points.clear();
		if (client.getMapRegions() == null)
		{
			return;
		}
		for (int regionId : client.getMapRegions())
		{
			final String json = configManager.getConfiguration(CONFIG_GROUP, "region_" + regionId);
			if (!Strings.isNullOrEmpty(json))
			{
				try
				{
					Set<ObjectPoint> regionPoints = gson.fromJson(json, new TypeToken<Set<ObjectPoint>>() {}.getType());
					points.put(regionId, regionPoints.stream().filter(p -> !p.getName().equals("null")).collect(Collectors.toSet()));
				}
				catch (Exception e)
				{
					log.warn("Could not parse object points for region {}: {}", regionId, e.getMessage());
				}
			}
		}
		rebuildRenderableObjects(); // Rebuild after config reload
	}

	@Nullable
	private TileObject findTileObject(int z, int x, int y, int id)
	{
		Scene scene = client.getScene();
		if (scene == null)
		{
			return null;
		}
		Tile[][][] tiles = scene.getTiles();
		final Tile tile = tiles[z][x][y];
		if (tile == null)
		{
			return null;
		}

		for (GameObject obj : tile.getGameObjects())
		{
			if (obj != null && obj.getId() == id) return obj;
		}
		if (tile.getWallObject() != null && tile.getWallObject().getId() == id) return tile.getWallObject();
		if (tile.getDecorativeObject() != null && tile.getDecorativeObject().getId() == id) return tile.getDecorativeObject();
		if (tile.getGroundObject() != null && tile.getGroundObject().getId() == id) return tile.getGroundObject();

		// Second loop to check object composition ID if direct ID match fails for GameObjects
		// This handles cases where object.getID() might be the base object ID but the target identifier is the transformed ID.
		for (GameObject obj : tile.getGameObjects())
		{
			if (obj != null) {
				ObjectComposition comp = getObjectComposition(obj.getId());
				if (comp != null && comp.getId() == id) return obj;
			}
		}

		return null;
	}

	@Nullable
	private ObjectComposition getObjectComposition(int id)
	{
		ObjectComposition objectComposition = client.getObjectDefinition(id);
		return objectComposition == null || objectComposition.getImpostorIds() == null ? objectComposition : objectComposition.getImpostor();
	}

	// Dynamic Config Getters
	private boolean getBucketEnabled(int bucketId)
	{
		switch (bucketId)
		{
			case 1: return config.bucket1Enabled();
			case 2: return config.bucket2Enabled();
			case 3: return config.bucket3Enabled();
			case 4: return config.bucket4Enabled();
			case 5: return config.bucket5Enabled();
			case 6: return config.bucket6Enabled();
			case 7: return config.bucket7Enabled();
			case 8: return config.bucket8Enabled();
			case 9: return config.bucket9Enabled();
			default: return false;
		}
	}

	private Color getBucketColor(int bucketId)
	{
		switch (bucketId)
		{
			case 1: return config.bucket1Color();
			case 2: return config.bucket2Color();
			case 3: return config.bucket3Color();
			case 4: return config.bucket4Color();
			case 5: return config.bucket5Color();
			case 6: return config.bucket6Color();
			case 7: return config.bucket7Color();
			case 8: return config.bucket8Color();
			case 9: return config.bucket9Color();
			default: return Color.WHITE;
		}
	}

	private int getBucketActivationCount(int bucketId)
	{
		switch (bucketId)
		{
			case 1: return config.bucket1ActivationCount();
			case 2: return config.bucket2ActivationCount();
			case 3: return config.bucket3ActivationCount();
			case 4: return config.bucket4ActivationCount();
			case 5: return config.bucket5ActivationCount();
			case 6: return config.bucket6ActivationCount();
			case 7: return config.bucket7ActivationCount();
			case 8: return config.bucket8ActivationCount();
			case 9: return config.bucket9ActivationCount();
			default: return Integer.MAX_VALUE;
		}
	}

	private int getBucketDeactivationCount(int bucketId)
	{
		switch (bucketId)
		{
			case 1: return config.bucket1DeactivationCount();
			case 2: return config.bucket2DeactivationCount();
			case 3: return config.bucket3DeactivationCount();
			case 4: return config.bucket4DeactivationCount();
			case 5: return config.bucket5DeactivationCount();
			case 6: return config.bucket6DeactivationCount();
			case 7: return config.bucket7DeactivationCount();
			case 8: return config.bucket8DeactivationCount();
			case 9: return config.bucket9DeactivationCount();
			default: return Integer.MAX_VALUE;
		}
	}

	private boolean getBucketDisableWhileInteracting(int bucketId)
	{
		switch (bucketId)
		{
			case 1: return config.bucket1DisableWhileInteracting();
			case 2: return config.bucket2DisableWhileInteracting();
			case 3: return config.bucket3DisableWhileInteracting();
			case 4: return config.bucket4DisableWhileInteracting();
			case 5: return config.bucket5DisableWhileInteracting();
			case 6: return config.bucket6DisableWhileInteracting();
			case 7: return config.bucket7DisableWhileInteracting();
			case 8: return config.bucket8DisableWhileInteracting();
			case 9: return config.bucket9DisableWhileInteracting();
			default: return false;
		}
	}

	private int getBucketInteractionCooldown(int bucketId)
	{
		switch (bucketId)
		{
			case 1: return config.bucket1InteractionCooldown();
			case 2: return config.bucket2InteractionCooldown();
			case 3: return config.bucket3InteractionCooldown();
			case 4: return config.bucket4InteractionCooldown();
			case 5: return config.bucket5InteractionCooldown();
			case 6: return config.bucket6InteractionCooldown();
			case 7: return config.bucket7InteractionCooldown();
			case 8: return config.bucket8InteractionCooldown();
			case 9: return config.bucket9InteractionCooldown();
			default: return 0;
		}
	}

	private boolean getBucketDisableWhileMoving(int bucketId)
	{
		switch (bucketId)
		{
			case 1: return config.bucket1DisableWhileMoving();
			case 2: return config.bucket2DisableWhileMoving();
			case 3: return config.bucket3DisableWhileMoving();
			case 4: return config.bucket4DisableWhileMoving();
			case 5: return config.bucket5DisableWhileMoving();
			case 6: return config.bucket6DisableWhileMoving();
			case 7: return config.bucket7DisableWhileMoving();
			case 8: return config.bucket8DisableWhileMoving();
			case 9: return config.bucket9DisableWhileMoving();
			default: return false;
		}
	}
}