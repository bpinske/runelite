package net.runelite.client.plugins.nom.combatutility;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("combatutility")
public interface CombatUtilityConfig extends Config
{
    @ConfigSection(
            name = "Spec Bar Highlight",
            description = "Settings for the special attack bar highlight.",
            position = 0
    )
    String specBarSection = "specBarSection";

    @ConfigItem(
            keyName = "highlightFullSpec",
            name = "Highlight Full Spec",
            description = "Highlights the special attack bar when energy is at 100%.",
            position = 1,
            section = specBarSection
    )
    default boolean highlightFullSpec() { return true; }

    @ConfigItem(
            keyName = "specHighlightColor",
            name = "Highlight Color",
            description = "The color of the highlight for the spec bar.",
            position = 2,
            section = specBarSection
    )
    default Color getSpecHighlightColor()
    {
        return new Color(0, 255, 255, 150);
    }

    @ConfigItem(
            keyName = "specSquareSize",
            name = "Square Size",
            description = "The size of the square to draw on the spec bar.",
            position = 3,
            section = specBarSection
    )
    default int getSpecSquareSize()
    {
        return 15;
    }


    @ConfigSection(
            name = "Boost Stats",
            description = "Settings for the boost stats reminder.",
            position = 10
    )
    String boostStatsSection = "boostStatsSection";

    @ConfigItem(
            keyName = "boostStatsReminder",
            name = "Boost Stats Reminder",
            description = "Highlights boosting potions in your inventory when your stats are not boosted.",
            position = 11,
            section = boostStatsSection
    )
    default boolean boostStatsReminder() { return true; }

    @ConfigItem(
            keyName = "boostPotionColor",
            name = "Highlight Color",
            description = "The color to highlight boosting potions with.",
            position = 12,
            section = boostStatsSection
    )
    default Color getBoostPotionColor()
    {
        return new Color(255, 0, 255, 150);
    }

    @ConfigItem(
            keyName = "boostPotionSquareSize",
            name = "Square Size",
            description = "The size of the square to draw on boosting potions.",
            position = 13,
            section = boostStatsSection
    )
    default int getBoostPotionSquareSize()
    {
        return 15;
    }


    @ConfigSection(
            name = "Maintain Absorption",
            description = "Settings for the absorption reminder.",
            position = 20
    )
    String absorptionSection = "absorptionSection";

    @ConfigItem(
            keyName = "maintainAbsorption",
            name = "Maintain Absorption",
            description = "Highlights absorption potions in your inventory when your absorption is low.",
            position = 21,
            section = absorptionSection
    )
    default boolean maintainAbsorption() { return true; }

    @ConfigItem(
            keyName = "absorptionThreshold",
            name = "Absorption Threshold",
            description = "Highlights absorption potions when your absorption drops below this value.",
            position = 22,
            section = absorptionSection
    )
    default int getAbsorptionThreshold()
    {
        return 200;
    }

    @ConfigItem(
            keyName = "absorptionPotionColor",
            name = "Highlight Color",
            description = "The color to highlight absorption potions with.",
            position = 23,
            section = absorptionSection
    )
    default Color getAbsorptionPotionColor()
    {
        return new Color(255, 255, 0, 150);
    }

    @ConfigItem(
            keyName = "absorptionPotionSquareSize",
            name = "Square Size",
            description = "The size of the square to draw on absorption potions.",
            position = 24,
            section = absorptionSection
    )
    default int getAbsorptionPotionSquareSize()
    {
        return 15;
    }

    @ConfigSection(
            name = "Prayer Orb Highlight",
            description = "Settings for the prayer orb overlay.",
            position = 30
    )
    String prayerOrbSection = "prayerOrbSection";

    @ConfigItem(
            keyName = "highlightPrayerOrb",
            name = "Highlight Prayer Orb",
            description = "Adds a persistent highlight over the prayer orb.",
            position = 31,
            section = prayerOrbSection
    )
    default boolean highlightPrayerOrb() { return true; }

    @ConfigItem(
            keyName = "prayerOrbColor",
            name = "Highlight Color",
            description = "The color of the highlight for the prayer orb.",
            position = 32,
            section = prayerOrbSection
    )
    default Color getPrayerOrbColor()
    {
        return new Color(0, 150, 255, 150);
    }

    @ConfigItem(
            keyName = "prayerOrbSquareSize",
            name = "Square Size",
            description = "The size of the square to draw on the prayer orb.",
            position = 33,
            section = prayerOrbSection
    )
    default int getPrayerOrbSquareSize()
    {
        return 15;
    }

}