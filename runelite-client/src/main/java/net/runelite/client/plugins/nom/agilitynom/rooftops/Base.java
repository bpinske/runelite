package net.runelite.client.plugins.nom.agilitynom.rooftops;


import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;


public abstract class Base {
    @Getter
    protected List<RooftopStage> stageList = new ArrayList<>();
    public Base() {
        init();
    }

    public abstract void init();
    public abstract int getLevel();
    public abstract WorldPoint getLocation();
}
