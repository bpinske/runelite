package net.runelite.client.plugins.nom.agilitynom.rooftops;


import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;

public class Draynor extends Base {
    public Draynor() {
        init();
    }

    @Override
    public String toString() {
        return "Draynor";
    }

    @Override
    public void init() {
        stageList.add(new RooftopStage("Rough wall","Climb",new WorldPoint(3103,3279,0),11404,null));

        stageList.add(new RooftopStage("Tightrope","Cross",new WorldPoint(3098,3277,3),11405, Arrays.asList(new WorldPoint(3097,3281,3),new WorldPoint(3098,3281,3),new WorldPoint(3099,3277,3),new WorldPoint(3099,3278,3),new WorldPoint(3099,3279,3),new WorldPoint(3099,3280,3),new WorldPoint(3099,3281,3),new WorldPoint(3100,3277,3),new WorldPoint(3100,3278,3),new WorldPoint(3100,3279,3),new WorldPoint(3100,3280,3),new WorldPoint(3100,3281,3),new WorldPoint(3101,3277,3),new WorldPoint(3101,3278,3),new WorldPoint(3101,3279,3),new WorldPoint(3101,3280,3),new WorldPoint(3101,3281,3),new WorldPoint(3102,3277,3),new WorldPoint(3102,3278,3),new WorldPoint(3102,3279,3),new WorldPoint(3102,3280,3),new WorldPoint(3102,3281,3))));

        stageList.add(new RooftopStage("Tightrope","Cross",new WorldPoint(3092,3276,3),11406, Arrays.asList(new WorldPoint(3088,3274,3),new WorldPoint(3088,3275,3),new WorldPoint(3089,3273,3),new WorldPoint(3089,3274,3),new WorldPoint(3089,3275,3),new WorldPoint(3089,3276,3),new WorldPoint(3090,3273,3),new WorldPoint(3090,3274,3),new WorldPoint(3090,3275,3),new WorldPoint(3090,3276,3),new WorldPoint(3091,3276,3))));

        stageList.add(new RooftopStage("Narrow wall","Balance",new WorldPoint(3089,3264,3),11430, Arrays.asList(new WorldPoint(3089,3265,3),new WorldPoint(3090,3265,3),new WorldPoint(3091,3265,3),new WorldPoint(3092,3265,3),new WorldPoint(3092,3266,3),new WorldPoint(3093,3265,3),new WorldPoint(3093,3266,3),new WorldPoint(3093,3267,3),new WorldPoint(3094,3265,3),new WorldPoint(3094,3266,3),new WorldPoint(3094,3267,3))));

        stageList.add(new RooftopStage("Wall","Jump-up",new WorldPoint(3088,3256,3),11630, Arrays.asList(new WorldPoint(3088,3257,3),new WorldPoint(3088,3258,3),new WorldPoint(3088,3259,3),new WorldPoint(3088,3260,3),new WorldPoint(3088,3261,3))));

        stageList.add(new RooftopStage("Gap","Jump",new WorldPoint(3095,3255,3),11631, Arrays.asList(new WorldPoint(3087,3255,3),new WorldPoint(3088,3255,3),new WorldPoint(3089,3255,3),new WorldPoint(3090,3255,3),new WorldPoint(3091,3255,3),new WorldPoint(3092,3255,3),new WorldPoint(3093,3255,3),new WorldPoint(3094,3255,3))));

        stageList.add(new RooftopStage("Crate","Climb-down",new WorldPoint(3102,3261,3),11632, Arrays.asList(new WorldPoint(3096,3256,3),new WorldPoint(3096,3257,3),new WorldPoint(3096,3258,3),new WorldPoint(3096,3259,3),new WorldPoint(3096,3260,3),new WorldPoint(3096,3261,3),new WorldPoint(3097,3256,3),new WorldPoint(3097,3257,3),new WorldPoint(3097,3258,3),new WorldPoint(3097,3259,3),new WorldPoint(3097,3260,3),new WorldPoint(3097,3261,3),new WorldPoint(3098,3256,3),new WorldPoint(3098,3257,3),new WorldPoint(3098,3258,3),new WorldPoint(3098,3259,3),new WorldPoint(3098,3260,3),new WorldPoint(3098,3261,3),new WorldPoint(3099,3256,3),new WorldPoint(3099,3257,3),new WorldPoint(3099,3258,3),new WorldPoint(3099,3259,3),new WorldPoint(3099,3260,3),new WorldPoint(3099,3261,3),new WorldPoint(3100,3257,3),new WorldPoint(3100,3258,3),new WorldPoint(3100,3259,3),new WorldPoint(3100,3260,3),new WorldPoint(3100,3261,3),new WorldPoint(3101,3256,3),new WorldPoint(3101,3257,3),new WorldPoint(3101,3258,3),new WorldPoint(3101,3259,3),new WorldPoint(3101,3260,3),new WorldPoint(3101,3261,3))));
    }

    @Override
    public int getLevel() {
        return 10;
    }

    @Override
    public WorldPoint getLocation() {
        return new WorldPoint(3103,3279,0);
    }
}
