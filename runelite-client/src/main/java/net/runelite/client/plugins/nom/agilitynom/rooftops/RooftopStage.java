package net.runelite.client.plugins.nom.agilitynom.rooftops;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class RooftopStage {
    protected String name;
    protected String action;
    @Getter
    protected WorldPoint tile;
    @Getter
    protected int id;
    protected List<WorldPoint> area;
    public RooftopStage(String name, String action, WorldPoint tile, int id, WorldPoint a, WorldPoint b) {
        this.name = name;
        this.action = action;
        this.tile = tile;
        this.id = id;
        this.area = new ArrayList<>();
        for (int x = a.getX(); x <= b.getX(); x++) {
            for (int y = a.getY(); y <= b.getY(); y++) {
                area.add(new WorldPoint(x,y,a.getPlane()));
            }
        }
    }

    public boolean onArea(Client client) {
        Player player = client.getLocalPlayer();
        if (player == null) return false;
        if (player.getWorldLocation() == null) return false;
        if (area == null) return player.getWorldLocation().getPlane() == 0;
        return area.stream().anyMatch(wp -> player.getWorldLocation().equals(wp));
    }

    public boolean onArea(WorldPoint worldPoint) {
        if (worldPoint == null) return false;
        if (area == null) return false;
        return area.stream().anyMatch(worldPoint::equals);
    }
}
