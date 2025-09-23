/*
 * Copyright (c) 2024, YourName <your@email.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.nom.AutoHopPKers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("autohop")
public interface AutoHopPkersConfig extends Config
{
    @ConfigSection(
            name = "Hop Settings",
            description = "Settings for automatically hopping worlds.",
            position = 0
    )
    String hopSection = "hopSection";

    @ConfigItem(
            keyName = "enableAutoHop",
            name = "Enable Auto Hop",
            description = "Master toggle to enable or disable the auto hopping feature.",
            position = 0,
            section = hopSection
    )
    default boolean enableAutoHop()
    {
        return true;
    }

    @ConfigItem(
            keyName = "hopOnlyOnSkulled",
            name = "Hop Only On Skulled",
            description = "If enabled, the plugin will only hop if the detected player is skulled.",
            position = 1,
            section = hopSection
    )
    default boolean hopOnlyOnSkulled()
    {
        return true;
    }

    @ConfigSection(
            name = "Sound Settings",
            description = "Settings for the sound alert.",
            position = 2
    )
    String soundSection = "soundSection";

    @ConfigItem(
            keyName = "pkerPing",
            name = "Play Sound on Player",
            description = "Make a sound when a potential PKer is detected.",
            position = 3,
            section = soundSection
    )
    default boolean pkerPing()
    {
        return true;
    }

    @ConfigItem(
            keyName = "soundOnlyOnSkulled",
            name = "Sound Only On Skulled",
            description = "If enabled, the sound will only play for skulled players.",
            position = 4,
            section = soundSection
    )
    default boolean soundOnlyOnSkulled()
    {
        return false; // Default to false to alert for any player in range
    }

    @ConfigItem(
            keyName = "frequency",
            name = "Sound Cooldown (seconds)",
            description = "The number of seconds to wait before playing the sound again.",
            position = 5,
            section = soundSection
    )
    default int frequency()
    {
        return 5;
    }

    @ConfigSection(
            name = "General Settings",
            description = "General plugin settings.",
            position = 6
    )
    String generalSection = "generalSection";

    @ConfigItem(
            keyName = "checkEveryTick",
            name = "Check Every Tick",
            description = "If enabled, checks for players every game tick. More responsive but uses more resources.",
            position = 7,
            section = generalSection
    )
    default boolean checkEveryTick()
    {
        return true;
    }
}