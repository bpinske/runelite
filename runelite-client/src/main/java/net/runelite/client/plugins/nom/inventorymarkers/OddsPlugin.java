package net.runelite.client.plugins.nom.inventorymarkers;


import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.nom.inventorymarkers.math.DuelSimulator;
import net.runelite.client.plugins.nom.inventorymarkers.math.DuelType;
import net.runelite.client.plugins.nom.inventorymarkers.math.RSPlayer;
import net.runelite.client.plugins.nom.inventorymarkers.ui.OddsPluginPanel;
import net.runelite.client.plugins.nom.inventorymarkers.ui.PlayerOddsPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(name = "Staking Odds", description = "Overlays to provide staking odds", enabledByDefault = false,
        tags = {"nomscripts","nomscripts"})
public class OddsPlugin extends Plugin
{
   @Inject
   private Client client;
   @Inject
   private OverlayManager overlayManager;
//   @Inject
//   private OddsOverlay overlay;
   @Inject
   private OddsConfig config;
   @Getter
   @Inject
   private ScheduledExecutorService executorService;

   @Inject
   private ClientThread clientThread;
   @Inject
   private ClientToolbar clientToolbar;
   private NavigationButton navigationButton;
   private OddsPluginPanel pluginPanel;
   @Getter
   private CopyOnWriteArrayList<PlayerOddsPanel> panels = new CopyOnWriteArrayList<>();

   @Inject
   private HiscoreClient hiscoreClient;

   @Inject
   private Provider<MenuManager> menuManager;
   private static final String LOOKUP = "Stake Odds";

   @Inject
   private Notifier notifier;

   @Inject
   private OddsOverlay overlay;


   public OddsPlugin() {
   }

   @Provides
   OddsConfig getConfig(final ConfigManager configManager) {
      return (OddsConfig)configManager.getConfig((Class)OddsConfig.class);
   }

   protected void startUp() {
//      overlayManager.add(overlay);

      pluginPanel = new OddsPluginPanel(this, config);
      final String ICON_FILE = "panel_icon.png";
      final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), ICON_FILE);
//
      navigationButton = NavigationButton.builder()
              .tooltip("Odds calc")
              .icon(icon)
              .priority(5)
              .panel(pluginPanel)
              .build();

      clientToolbar.addNavigation(navigationButton);
      lastPing = Instant.now();

      if (config.playerOption() && client != null)
      {
         menuManager.get().addPlayerMenuItem(LOOKUP);
      }
   }

   protected void shutDown() {
//      this.overlayManager.remove(overlay);
      clientToolbar.removeNavigation(navigationButton);

      if (client != null)
      {
         menuManager.get().removePlayerMenuItem(LOOKUP);
      }
   }

   Pattern pattern = Pattern.compile("^(.* {2})\\(.*");
   @Subscribe
   public void onMenuOptionClicked(MenuOptionClicked event)
   {
		Player[] players = client.getCachedPlayers();
		if (players.length <= event.getId()) return;
		Player player = players[event.getId()];
		if (player == null)
		{
		return;
		}

		if (Text.removeTags(event.getMenuOption()).equals("Challenge") || event.getMenuOption().equals(LOOKUP))
		{
		addPanel(player.getName());
		}
   }

   @Subscribe
   public void onConfigChanged(ConfigChanged event)
   {
      if (event.getGroup().equals("odds"))
      {
         if (client != null)
         {
            menuManager.get().removePlayerMenuItem(LOOKUP);

            if (config.playerOption())
            {
               menuManager.get().addPlayerMenuItem(LOOKUP);
            }
         }
      }
   }

   @Subscribe
   public void onChatMessage(final ChatMessage event) {
      if (!config.duelRequest()) return;
      if (event.getType() == ChatMessageType.CHALREQ_TRADE && event.getMessage().toLowerCase().endsWith("duel with you.")) {
         final Pattern p = Pattern.compile(".*?(?=\\s+wishes)");
         final Matcher m = p.matcher(event.getMessage());
         if (m.find()) {
            final String user = m.group(0);
            if (this.config.challengeSound()) {
               this.client.playSoundEffect(3925);
            }
            addPanel(user);
         }
      }
   }


//   Actor lastActor = null;
   @Subscribe
   public void onGameTick(GameTick gameTick)
   {
      pluginPanel.rebuild();
//      Player p = client.getLocalPlayer();
//      if (p == null) return;
//      Actor p2 = p.getInteracting();
//      if ((!(p2 instanceof Player))) return;
//      if (lastActor != null && lastActor.equals(p2)) return;
//      lastActor = p2;
//      addPanel(p2.getName());
   }

   private void addPanel(String opponent) {
      if (panels.stream().anyMatch(p -> p.getOpponentName().equalsIgnoreCase(opponent))) return;
      log.info("Begin lookup " + opponent);
      executorService.execute(()->{
         RSPlayer self = RSPlayer.fromSelf(this.client);
         RSPlayer opp = RSPlayer.fromName(hiscoreClient, opponent);
         if (opp == null) {
            log.info("Opponent lookup failed " + opponent);
            return;
         }
         DuelSimulator ds = new DuelSimulator(config, self, opp);
         SwingUtilities.invokeLater(()-> {
            if (panels.stream().anyMatch(p -> p.getOpponentName().equalsIgnoreCase(opponent))) return;
            panels.add(0,new PlayerOddsPanel(this, config, ds));

            if (Instant.now().compareTo(lastPing.plus(Duration.ofSeconds(10))) >= 0) {
               DuelType niceOdds = notify(ds);
               if (niceOdds != null) {
                  lastPing = Instant.now();
                  notifier.notify(niceOdds.name() + " odds " + ds.getOdds(niceOdds) + " vs " + opponent, TrayIcon.MessageType.WARNING);
               }
            }
         });
      });
   }

   private Instant lastPing;
   private DuelType notify(DuelSimulator simulator) {
      if (simulator.getOdds(DuelType.TENT) < config.ignoreAbove() && simulator.getOdds(DuelType.TENT) >= config.tentacleNotif()) return DuelType.TENT;
      if (simulator.getOdds(DuelType.WHIP) < config.ignoreAbove() && simulator.getOdds(DuelType.WHIP) >= config.whipNotif()) return DuelType.WHIP;
      if (simulator.getOdds(DuelType.SCIM) < config.ignoreAbove() && simulator.getOdds(DuelType.SCIM) >= config.scimNotif()) return DuelType.SCIM;
      if (simulator.getOdds(DuelType.DDS) < config.ignoreAbove() && simulator.getOdds(DuelType.DDS) >= config.ddsNotif()) return DuelType.DDS;
      if (simulator.getOdds(DuelType.BOX) < config.ignoreAbove() && simulator.getOdds(DuelType.BOX) >= config.boxNotif()) return DuelType.BOX;
      if (simulator.getOdds(DuelType.R_KNIFE) < config.ignoreAbove() && simulator.getOdds(DuelType.R_KNIFE) >= config.rangedNotif()) return DuelType.R_KNIFE;
      if (simulator.getOdds(DuelType.D_KNIFE) < config.ignoreAbove() && simulator.getOdds(DuelType.D_KNIFE) >= config.rangedNotif()) return DuelType.D_KNIFE;

      return null;
   }

   @Subscribe
   private void onWidgetLoaded(WidgetLoaded widgetLoaded)
   {
      // "Dueling with: "
      if (widgetLoaded.getGroupId() == ChallengeWidgets.RULE_SCREEN_GROUP_ID) {
         executorService.execute(()->{
            log.info("Getting stats from duel screen");
            Widget nameWidget = client.getWidget(ChallengeWidgets.RULE_SCREEN_GROUP_ID, ChallengeWidgets.RuleScreen.OPPONENT_NAME_LABEL);
            RSPlayer self = RSPlayer.fromSelf(this.client);
            final double att = getStatFromWidget(ChallengeWidgets.RuleScreen.ATTACK);
            final double str = getStatFromWidget(ChallengeWidgets.RuleScreen.STRENGTH);
            final double def = getStatFromWidget(ChallengeWidgets.RuleScreen.DEFENCE);
            final double hit = getStatFromWidget(ChallengeWidgets.RuleScreen.HITPOINTS);
            final double rng = getStatFromWidget(ChallengeWidgets.RuleScreen.RANGE);
            String name;
            if (nameWidget == null || nameWidget.getText() == null) {
               name = "Opponent";
            } else {
               name = nameWidget.getText().replace("Dueling with: ","");
            }
            RSPlayer opp = new RSPlayer(name, att, str, def, hit, rng);
            DuelSimulator ds = new DuelSimulator(config, self, opp);
            SwingUtilities.invokeLater(()-> {
               if (panels.stream().anyMatch(p -> p.getOpponentName().equalsIgnoreCase(opp.getUserName()))) return;
               panels.add(0,new PlayerOddsPanel(this, config, ds));
            });
         });
      }
   }

   private double getStatFromWidget(int num) {
      Widget w = client.getWidget(ChallengeWidgets.RULE_SCREEN_GROUP_ID,num);
      if (w == null || w.getText() == null || w.getText().isEmpty()) return 69;
      log.info("Num " + num + " Text " + w.getText());
      int value = Integer.parseInt(w.getText().replaceAll("\\D",""));
      if (value <= 0) {
         try {
            log.info("Value is zero, retrying");
            Thread.sleep(600);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      value = Integer.parseInt(w.getText().replaceAll("\\D",""));
      return value;
   }
}
