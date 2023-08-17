package net.runelite.client.plugins.nom.agilitynom.rooftops;


import java.util.Arrays;
import net.runelite.api.coords.WorldPoint;

public class Ardougne extends Base {
    public Ardougne() {
        init();
    }

    @Override
    public String toString() {
        return "Ardougne";
    }

    @Override
    public void init() {
        stageList.add(new RooftopStage("Wooden Beams", "Climb-up", new WorldPoint(2673, 3298, 0), 15608,  null));
        stageList.add(new RooftopStage("Gap", "Jump", new WorldPoint(2671, 3310, 3), 15609,  Arrays.asList(new WorldPoint(2671, 3299, 3), new WorldPoint(2671, 3300, 3), new WorldPoint(2671, 3301, 3), new WorldPoint(2671, 3302, 3), new WorldPoint(2671, 3303, 3), new WorldPoint(2671, 3304, 3), new WorldPoint(2671, 3305, 3), new WorldPoint(2671, 3306, 3), new WorldPoint(2671, 3307, 3), new WorldPoint(2671, 3308, 3), new WorldPoint(2671, 3309, 3))));
        stageList.add(new RooftopStage("Plank", "Walk-on", new WorldPoint(2661, 3318, 3), 26635,  Arrays.asList(new WorldPoint(2662, 3318, 3), new WorldPoint(2663, 3318, 3), new WorldPoint(2664, 3318, 3), new WorldPoint(2665, 3318, 3))));
        stageList.add(new RooftopStage("Gap", "Jump",
			new WorldPoint(2653, 3318, 3), 15610,

			Arrays.asList(new WorldPoint(2654, 3318, 3), new WorldPoint(2655, 3318, 3),
				new WorldPoint(2656, 3318, 3),

				new WorldPoint(2657, 3318, 3),// Ardy mark of grace tile
				new WorldPoint(2653, 3318, 3) // Ardy mark of grace tile
				)));
        stageList.add(new RooftopStage("Gap", "Jump", new WorldPoint(2653, 3309, 3), 15611,  Arrays.asList(new WorldPoint(2653, 3310, 3), new WorldPoint(2653, 3311, 3), new WorldPoint(2653, 3312, 3), new WorldPoint(2653, 3313, 3), new WorldPoint(2653, 3314, 3))));


        stageList.add(new RooftopStage("Steep roof", "Balance-across", new WorldPoint(2654, 3300, 3), 28912,  Arrays.asList(new WorldPoint(2651, 3304, 3), new WorldPoint(2651, 3305, 3), new WorldPoint(2651, 3306, 3), new WorldPoint(2651, 3307, 3), new WorldPoint(2651, 3308, 3), new WorldPoint(2651, 3309, 3), new WorldPoint(2652, 3304, 3), new WorldPoint(2653, 3300, 3), new WorldPoint(2653, 3301, 3), new WorldPoint(2653, 3302, 3), new WorldPoint(2653, 3303, 3), new WorldPoint(2653, 3304, 3))));

        stageList.add(new RooftopStage("Gap", "Jump", new WorldPoint(2657, 3296, 3), 15612,  Arrays.asList(new WorldPoint(2656, 3297, 3))));
    }

    @Override
    public int getLevel() {
        return 90;
    }

    @Override
    public WorldPoint getLocation() {
        return new WorldPoint(2673, 3298, 0);
    }
}
