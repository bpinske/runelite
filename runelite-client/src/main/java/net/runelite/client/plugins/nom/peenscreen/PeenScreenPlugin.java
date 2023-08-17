package net.runelite.client.plugins.nom.peenscreen;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.discord.DiscordService;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Green Screen",
	tags = "nomscripts",
	enabledByDefault = false
)
public class PeenScreenPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private PeenScreenConfig config;

	@Inject
	private PeenScreenOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	DiscordService discordService;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Provides
	PeenScreenConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PeenScreenConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		overlay.setLayer(config.overlayLayer());
	}
}
