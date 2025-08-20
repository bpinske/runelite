package net.runelite.client.plugins.nom.combatutility;

import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class CombatUtilityOverlay extends Overlay
{
    private final CombatUtilityPlugin plugin;

    @Inject
    private CombatUtilityOverlay(CombatUtilityPlugin plugin)
    {
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Loop through the list of squares prepared by the plugin and draw them
        for (SquareToDraw square : plugin.getSquaresToDraw())
        {
            SquareOverlay.drawCenterSquare(graphics, square.getBounds(), square.getSize(), square.getColor());
        }

        return null;
    }
}
