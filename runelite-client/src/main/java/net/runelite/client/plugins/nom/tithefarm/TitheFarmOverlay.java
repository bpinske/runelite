package net.runelite.client.plugins.nom.tithefarm;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.Perspective;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class TitheFarmOverlay extends Overlay
{
    private final Client client;
    private final TitheFarmPlugin plugin;
    private final TitheFarmConfig config;

    @Inject
    private TitheFarmOverlay(Client client, TitheFarmPlugin plugin, TitheFarmConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Draw highlights on GameObjects in the scene (patches, barrel, etc.)
        for (GameObject gameObject : plugin.getHighlights())
        {
            Polygon poly = Perspective.getCanvasTilePoly(client, gameObject.getLocalLocation());
            if (poly != null)
            {
                // Determine color based on what is being highlighted
                Color color = determineGameObjectColor(gameObject);
                OverlayUtil.renderPolygon(graphics, poly, color);
            }
        }

        // Draw highlights on items in the inventory
//        for (WidgetItem item : client.getWidget(WidgetInfo.INVENTORY).getWidgetItems())
//        {
//            if (plugin.getInventoryHighlights().contains(item.getId()))
//            {
//                Color color = determineInventoryColor(item.getId());
//                OverlayUtil.renderImageLocation(graphics, item.getCanvasLocation(), getHighlightImage(color));
//            }
//        }

        return null;
    }

    private Color determineGameObjectColor(GameObject gameObject)
    {
        TitheFarmPatch patch = plugin.getPatches().get(gameObject.getWorldLocation());
        if (patch != null)
        {
            switch (patch.getState())
            {
                case EMPTY:
                    return config.getEmptyPatchColor();
                case HARVESTABLE:
                    return config.getHarvestColor();
                default:
                    // Any other growing state that needs water
                    return config.getUnwateredColor();
            }
        }
        // Check if it's the water barrel
//        if (gameObject.getId() == ObjectID.WATER_BARREL_27381)
//        {
//            return config.getWaterBarrelColor();
//        }
        return Color.MAGENTA; // Fallback
    }

    private Color determineInventoryColor(int itemId)
    {
        if (itemId == ItemID.GOLOVANOVA_SEED)
        {
            return config.getSeedColor();
        }
        if (itemId == ItemID.GRICOLLERS_CAN)
        {
            return config.getWateringCanColor();
        }
        return Color.MAGENTA; // Fallback
    }

    // This helper method would create the actual highlight image for inventory items
    // For simplicity, we are just returning a color, but a full implementation would
    // generate a BufferedImage here.
    private Color getHighlightImage(Color color)
    {
        return color;
    }
}