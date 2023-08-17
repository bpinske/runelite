package net.runelite.client.plugins.nom.rsnhider;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("rsnhidernom")
public interface RsnHiderNomConfig extends Config
{
	@ConfigItem(
		name = "Hide in widgets (Lag warning)",
		keyName = "hideWidgets",
		description = "Hides your RSN everywhere. Might lag your game."
	)
	default boolean hideWidgets()
	{
		return false;
	}


	@ConfigItem(
			name = "contentCreatorNames",
			keyName = "contentCreatorNames",
			description = "contentCreatorNames"
	)
	default boolean contentCreatorNames()
	{
		return false;
	}
}
