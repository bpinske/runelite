package net.runelite.client.plugins.nom.playerhider;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("playerhider")
public interface PlayerHiderConfig extends Config
{


	@ConfigItem(
		name = "isDupeLook",
		keyName = "isDupeLook",
		description = "Changes isDupeLook",
		position = 2
	)
	default boolean isDupeLook()
	{
		return false;
	}

	@ConfigItem(
			name = "isPlayerModel",
			keyName = "isPlayerModel",
			description = "Changes isPlayerModel",
			position = 0
	)
	default boolean isPlayerModel()
	{
		return true;
	}

	@ConfigItem(
		name = "Player color",
		keyName = "playerColor",
		description = "Changes playerColor",
		position = 1
	)
	default int playerColor()
	{
		return 1;
	}


	@ConfigItem(
		name = "Player model",
		keyName = "playerModel",
		description = "Changes playerModel",
		position = 1
	)
	default int playerModel()
	{
		return -1;
	}


	@ConfigItem(
			name = "Weapon Id",
			keyName = "weaponId",
			description = "Changes weapon",
			position = 3
	)
	default int weaponId()
	{
		return 0;
	}

	@ConfigItem(
		name = "Offhand Id",
		keyName = "offhandId",
		description = "Changes weapon",
		position = 4
	)
	default int offhandId()
	{
		return 0;
	}


	@ConfigItem(
			name = "bodyColor",
			keyName = "bodyColor",
			description = "Changes bodyColor",
			position = 5
	)
	default boolean bodyColor()
	{
		return true;
	}

	@ConfigItem(
			name = "fixedLook",
			keyName = "fixedLook",
			description = "Changes fixedLook",
			position = 6
	)
	default boolean fixedLook()
	{
		return true;
	}

	@ConfigItem(
			name = "bankSlotsFilled",
			keyName = "bankSlotsFilled",
			description = "Changes bankSlotsFilled",
			position = 7
	)
	default int bankSlotsFilled()
	{
		return 75;
	}

	@ConfigItem(
			name = "Stats string",
			keyName = "statsString",
			description = "Changes stats"
	)
	default String statsString()
	{
		return "ATTACK,0,DEFENCE,0,STRENGTH,0,HITPOINTS,0,RANGED,0,PRAYER,0,MAGIC,0,COOKING,0,WOODCUTTING,0,FLETCHING,0,FISHING,0,FIREMAKING,0,CRAFTING,0,SMITHING,0,MINING,0,HERBLORE,0,AGILITY,0,THIEVING,0,SLAYER,0,FARMING,0,RUNECRAFT,0,HUNTER,0,CONSTRUCTION,0";
	}

	@ConfigItem(
			name = "Thumbnail stats",
			keyName = "thumbnail",
			description = "Changes playerStats"
	)
	default boolean thumbnail()
	{
		return false;
	}


	@ConfigItem(
			name = "No level",
			keyName = "copyPaste",
			description = "Changes stats copyPaste",
			position = 99
	)
	default String lalala()
	{
		return "ATTACK,0,DEFENCE,0,STRENGTH,0,HITPOINTS,0,RANGED,0,PRAYER,0,MAGIC,0,COOKING,0,WOODCUTTING,0,FLETCHING,0,FISHING,0,FIREMAKING,0,CRAFTING,0,SMITHING,0,MINING,0,HERBLORE,0,AGILITY,0,THIEVING,0,SLAYER,0,FARMING,0,RUNECRAFT,0,HUNTER,0,CONSTRUCTION,0";
	}

	@ConfigItem(
		name = "Mid level",
		keyName = "copyPastae",
		description = "Changes stats copyPaste",
		position = 99
	)
	default String lalalaa()
	{
		return "ATTACK,0,DEFENCE,0,STRENGTH,0,HITPOINTS,0,RANGED,0,PRAYER,0,MAGIC,0,COOKING,0,WOODCUTTING,0,FLETCHING,0,FISHING,0,FIREMAKING,0,CRAFTING,0,SMITHING,0,MINING,0,HERBLORE,0,AGILITY,0,THIEVING,0,SLAYER,0,FARMING,0,RUNECRAFT,0,HUNTER,0,CONSTRUCTION,0";
	}

	@ConfigItem(
		name = "High level",
		keyName = "copyPastee",
		description = "Changes stats copyPaste",
		position = 99
	)
	default String lalalea()
	{
		return "ATTACK,0,DEFENCE,0,STRENGTH,0,HITPOINTS,0,RANGED,0,PRAYER,0,MAGIC,0,COOKING,0,WOODCUTTING,0,FLETCHING,0,FISHING,0,FIREMAKING,0,CRAFTING,0,SMITHING,0,MINING,0,HERBLORE,0,AGILITY,0,THIEVING,0,SLAYER,0,FARMING,0,RUNECRAFT,0,HUNTER,0,CONSTRUCTION,0";
	}
}
