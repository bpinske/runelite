package net.runelite.client.plugins.nom.screenshotaccplugin;

import net.runelite.api.Skill;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StatToIndex {

    private static final HashMap<Skill, Integer> map = new HashMap<>();

    static {
        map.put(Skill.ATTACK, 0);
        map.put(Skill.STRENGTH, 1);
        map.put(Skill.DEFENCE, 2);
        map.put(Skill.RANGED, 3);
        map.put(Skill.PRAYER, 4);
        map.put(Skill.MAGIC, 5);
        map.put(Skill.RUNECRAFT, 6);
        map.put(Skill.CONSTRUCTION, 7);
        map.put(Skill.HITPOINTS, 8);
        map.put(Skill.AGILITY, 9);
        map.put(Skill.HERBLORE, 10);
        map.put(Skill.THIEVING, 11);
        map.put(Skill.CRAFTING, 12);
        map.put(Skill.FLETCHING, 13);
        map.put(Skill.SLAYER, 14);
        map.put(Skill.HUNTER, 15);
        map.put(Skill.MINING, 16);
        map.put(Skill.SMITHING, 17);
        map.put(Skill.FISHING, 18);
        map.put(Skill.COOKING, 19);
        map.put(Skill.FIREMAKING, 20);
        map.put(Skill.WOODCUTTING, 21);
        map.put(Skill.FARMING, 22);
        map.put(Skill.OVERALL, 23); //Total level:<br>1245
    }

    public static int index(Skill skill) {
        return map.getOrDefault(skill,-1);
    }
    public static String name(int index) {
        Optional<Map.Entry<Skill, Integer>> opt = map.entrySet().stream().filter(e -> e.getValue() == index).findFirst();

        if (opt.isPresent()) {
            return opt.get().getKey().getName();
        } else {
            return "asdf";
        }
    }
}
