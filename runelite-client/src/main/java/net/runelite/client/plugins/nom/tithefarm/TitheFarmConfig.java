package net.runelite.client.plugins.nom.tithefarm;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("tithefarm")
public interface TitheFarmConfig extends Config
{
    @Alpha
    @ConfigItem(
            keyName = "seedColor",
            name = "Seed Highlight",
            description = "Color to highlight Lovakengj seeds when planting is needed.",
            position = 1
    )
    default Color getSeedColor()
    {
        return new Color(0, 255, 0, 100);
    }

    @Alpha
    @ConfigItem(
            keyName = "emptyPatchColor",
            name = "Empty Patch Highlight",
            description = "Color to highlight empty patches that need seeds.",
            position = 2
    )
    default Color getEmptyPatchColor()
    {
        return new Color(0, 255, 0, 100);
    }

    @Alpha
    @ConfigItem(
            keyName = "unwateredColor",
            name = "Unwatered Plant Highlight",
            description = "Color to highlight plants that need to be watered.",
            position = 3
    )
    default Color getUnwateredColor()
    {
        return new Color(255, 200, 0, 150);
    }

    @Alpha
    @ConfigItem(
            keyName = "harvestColor",
            name = "Harvestable Plant Highlight",
            description = "Color to highlight fully grown plants ready for harvesting.",
            position = 4
    )
    default Color getHarvestColor()
    {
        return new Color(0, 255, 255, 150);
    }

    @Alpha
    @ConfigItem(
            keyName = "wateringCanColor",
            name = "Watering Can Highlight",
            description = "Color to highlight your watering can when it's time to refill.",
            position = 5
    )
    default Color getWateringCanColor()
    {
        return new Color(50, 150, 255, 150);
    }

    @Alpha
    @ConfigItem(
            keyName = "waterBarrelColor",
            name = "Water Barrel Highlight",
            description = "Color to highlight the water barrel when you need to refill your watering can.",
            position = 6
    )
    default Color getWaterBarrelColor()
    {
        return new Color(50, 150, 255, 150);
    }
}