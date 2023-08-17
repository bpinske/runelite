package net.runelite.client.plugins.nom.nomautochatfilter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@PluginDescriptor(
        name = "Nom Auto Chat Filter",
        description = "Auto Chat Filter",
        tags = {"nomscripts","autochat"},
        enabledByDefault = false
)
@Slf4j
public class NomAutoChatFilter extends Plugin {
    @Inject
    private NomAutoChatFilterConfig config;
    @Inject
    private Client client;

    public NomAutoChatFilter() {
    }

    @Provides
    NomAutoChatFilterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(NomAutoChatFilterConfig.class);
    }

    @Override
    protected void startUp() {
    }

    @Override
    protected void shutDown() {
    }

    private ConcurrentHashMap<String, Instant> chatHistory = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> chatCount = new ConcurrentHashMap<>();
    private CopyOnWriteArraySet<String> spamBots = new CopyOnWriteArraySet<>();

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event)
    {
        if (!"chatFilterCheck".equals(event.getEventName()))
        {
            return;
        }

        int[] intStack = client.getIntStack();
        int intStackSize = client.getIntStackSize();
        String[] stringStack = client.getStringStack();
        int stringStackSize = client.getStringStackSize();

        final int messageType = intStack[intStackSize - 2];
        final int messageId = intStack[intStackSize - 1];
        String message = stringStack[stringStackSize - 1];

        ChatMessageType chatMessageType = ChatMessageType.of(messageType);
        final MessageNode messageNode = client.getMessages().get(messageId);
        final String name = messageNode.getName();
        boolean blockMessage = false;

        // Only filter autochat
        switch (chatMessageType)
        {
            case PUBLICCHAT:
            case MODCHAT:
            case AUTOTYPER:
                break;
            default:
                return;
        }

        if (config.filterAutochat() && chatMessageType == ChatMessageType.AUTOTYPER) {
            intStack[intStackSize - 3] = 0;
            return;
        }
        if (!config.filterBotSpam()) return;

        if (spamBots.contains(name)) {
            blockMessage = true;
        }


        if (chatCount.containsKey(name+message) && chatCount.get(name+message) >= config.repeatCount()) {
            blockMessage = true;
        }

        if (chatHistory.containsKey(name+message) && chatHistory.get(name+message).isBefore(Instant.now())) {
            blockMessage = true;
        }

        if (blockMessage)
        {
            // Block the message
            log.info("Filtering " + name+message);
            intStack[intStackSize - 3] = 0;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        MessageNode messageNode = chatMessage.getMessageNode();
        ChatMessageType chatMessageType = chatMessage.getType();
        //Only filter public chat
        switch (chatMessageType)
        {
            case PUBLICCHAT:
            case MODCHAT:
            case AUTOTYPER:
                break;
            default:
                return;
        }

        String message = messageNode.getValue();
        String name = messageNode.getName();
//        log.info(name+message+"chatmessageevent FUKKKKKKKKK");

        String combined = name+message;

        String jagexName = Text.toJagexName(name);
        if (jagexName.equals("8ald")) {
            log.info(name + " " + message);
        }
        chatCount.merge(combined,1,Integer::sum);
        if (chatHistory.containsKey(combined) && chatCount.containsKey(combined)) {
            Instant lastTime = chatHistory.get(combined);
            int count = chatCount.get(combined);
            if (jagexName.equals("8ald")) {
                log.info(combined + " " + lastTime.toString());
            }

            if (lastTime.plusSeconds(config.duplicateDelay()).isBefore(Instant.now())) {
//                chatHistory.remove(combined);
                chatCount.put(combined,0);
            } else if (count > config.repeatCount()) {
                chatHistory.put(combined, Instant.now());
            }
        } else {
            chatHistory.put(combined, Instant.now().plusSeconds(config.duplicateDelay()));
        }
    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event)
    {
        if (event == null) return;
        if (!(event.getActor() instanceof Player))
        {
            return;
        }

        Player p = (Player)event.getActor();

        String name = p.getName();
        String message = event.getOverheadText();
        if (name == null || message == null) return;

        if (p.getCombatLevel() < config.combatLevelMin() || p.getCombatLevel() > config.combatLevelMax()) {
            spamBots.add(name);
        }
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged e)
    {
        if (e.getGroup().equals("autochatfilter")) {
            if (e.getKey().contains("combatLevel")) {
                spamBots.clear();
            }
        }
    }

}
