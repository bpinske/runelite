package net.runelite.client.plugins.nom.inventorymarkers;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.plugins.nom.inventorymarkers.ui.PlayerOddsPanel;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;

public class OddsOverlay extends Overlay
{
    private final Client client;
    private final OddsPlugin plugin;
    private final OddsConfig config;

    @Inject
    private OddsOverlay(Client client, OddsPlugin plugin, OddsConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
//        if (!config.nameOverlay()) return null;
        for (Player player : client.getPlayers()) {
            if (player == null || player.getName() == null) continue;
            final String name = player.getName();
            for (PlayerOddsPanel panel : plugin.getPanels()) {
                if (panel.getOpponentName() == null) {

//                    System.out.println("Null name");
                    continue;
                }
                if (panel.getOpponentName().equals(name)) {
//                    System.out.println("Render");
                    renderPlayerOverlay(graphics, player, Color.MAGENTA);
                }
            }
        }
        return null;
    }

    private void renderPlayerOverlay(Graphics2D graphics, Player actor, Color color)
    {
        final String name = Text.sanitize(actor.getName());
        net.runelite.api.Point textLocation = actor.getCanvasTextLocation(graphics, name, actor.getLogicalHeight()+40);

        if (textLocation == null)
        {
            return;
        }

        OverlayUtil.renderTextLocation(graphics, textLocation, name, color);
    }
}
