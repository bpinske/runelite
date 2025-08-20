package net.runelite.client.plugins.nom.combatutility;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@PluginDescriptor(
        name = "Combat Utility",
        description = "Provides various combat-related overlays and highlights.",
        tags = {"combat", "utility", "highlight", "spec", "boost", "absorption"}
)
public class CombatUtilityPlugin extends Plugin
{
    private static final int FULL_SPEC_ENERGY = 1000;
    private static final Set<Integer> BOOSTING_POTIONS = ImmutableSet.of(
            ItemID.OVERLOAD_1, ItemID.OVERLOAD_2, ItemID.OVERLOAD_3, ItemID.OVERLOAD_4,
            ItemID.SUPER_RANGING_1, ItemID.SUPER_RANGING_2, ItemID.SUPER_RANGING_3, ItemID.SUPER_RANGING_4,
            ItemID.SUPER_MAGIC_POTION_1, ItemID.SUPER_MAGIC_POTION_2, ItemID.SUPER_MAGIC_POTION_3, ItemID.SUPER_MAGIC_POTION_4
    );
    private static final Set<Integer> ABSORPTION_POTIONS = ImmutableSet.of(
            ItemID.ABSORPTION_1, ItemID.ABSORPTION_2, ItemID.ABSORPTION_3, ItemID.ABSORPTION_4
    );

    @Inject
    private Client client;

    @Inject
    private CombatUtilityConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CombatUtilityOverlay overlay;

    @Getter
    private final List<SquareToDraw> squaresToDraw = new ArrayList<>();

    @Provides
    CombatUtilityConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CombatUtilityConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        squaresToDraw.clear();
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick)
    {
        squaresToDraw.clear();
        checkFullSpec();
        checkBoostedStats();
        checkAbsorption();
        checkPrayerOrb();
    }

    private void checkFullSpec()
    {
        if (!config.highlightFullSpec())
        {
            return;
        }

        // Using the correct VarPlayer ID as you requested.
        if (client.getVarpValue(VarPlayerID.SA_ENERGY) == FULL_SPEC_ENERGY)
        {
            Widget specOrb = client.getWidget(WidgetInfo.MINIMAP_SPEC_ORB);
            if (specOrb != null && !specOrb.isHidden())
            {
                squaresToDraw.add(new SquareToDraw(specOrb.getBounds(), config.getSpecSquareSize(), config.getSpecHighlightColor()));
            }
        }
    }

    private boolean areStatsBoosted()
    {
        return client.getBoostedSkillLevel(Skill.ATTACK) > client.getRealSkillLevel(Skill.ATTACK)
                || client.getBoostedSkillLevel(Skill.STRENGTH) > client.getRealSkillLevel(Skill.STRENGTH)
                || client.getBoostedSkillLevel(Skill.DEFENCE) > client.getRealSkillLevel(Skill.DEFENCE)
                || client.getBoostedSkillLevel(Skill.RANGED) > client.getRealSkillLevel(Skill.RANGED)
                || client.getBoostedSkillLevel(Skill.MAGIC) > client.getRealSkillLevel(Skill.MAGIC);
    }

    private void checkBoostedStats()
    {
        if (!config.boostStatsReminder() || areStatsBoosted())
        {
            return;
        }

        highlightPotions(BOOSTING_POTIONS, config.getBoostPotionColor(), config.getBoostPotionSquareSize());
    }

    private void checkAbsorption()
    {
        if (!config.maintainAbsorption())
        {
            return;
        }

        // Using the correct Varbit ID as you requested.
        int currentAbsorption = client.getVarbitValue(Varbits.NMZ_ABSORPTION);
        if (currentAbsorption < config.getAbsorptionThreshold())
        {
            highlightPotions(ABSORPTION_POTIONS, config.getAbsorptionPotionColor(), config.getAbsorptionPotionSquareSize());
        }
    }

    // TODO run the agility scripts through gemini to see if there are improvement that can be made
    // TODO Make ZMI rc plugin
    // Maybe MLM?
    // Understand the pathfinder/how the agility minimap works
    // Recreate the Tithe farm plugin
    // Recreate the mastering mixology plugin
    // Make plugin for blast furnace crafting
    // Figure out dialogue boxes?

    private void highlightPotions(Set<Integer> potionIds, Color color, int size)
    {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        Item[] items = inventory.getItems();

        for (int slot = 0; slot < items.length; slot++)
        {
            Item item = items[slot];
            if (potionIds.contains(item.getId()))
            {
                Widget itemWidget = inventoryWidget.getChild(slot);
                if (itemWidget != null)
                {
                    squaresToDraw.add(new SquareToDraw(itemWidget.getBounds(), size, color));
                }
            }
        }
    }

    private void checkPrayerOrb()
    {
        if (!config.highlightPrayerOrb())
        {
            return;
        }

        Widget prayerOrb = client.getWidget(WidgetInfo.MINIMAP_PRAYER_ORB);
        if (prayerOrb != null && !prayerOrb.isHidden())
        {
            squaresToDraw.add(new SquareToDraw(prayerOrb.getBounds(), config.getPrayerOrbSquareSize(), config.getPrayerOrbColor()));
        }
    }

}
