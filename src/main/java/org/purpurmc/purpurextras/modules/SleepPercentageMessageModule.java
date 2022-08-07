package org.purpurmc.purpurextras.modules;

import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.List;

public class SleepPercentageMessageModule implements PurpurExtrasModule, Listener {
    private final String playerSleepMessage;
    private final String nightSkipMessage;
    private final String sleepMessageBypass = "purpurextras.sleepmessagebypass";

    protected SleepPercentageMessageModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        this.playerSleepMessage = config.getString("settings.chat.send-sleep-percentage-message.player-sleeping", "<grey><playername> has fallen asleep. <sleeping> out of <needed> required players in <worldname> are sleeping.");
        this.nightSkipMessage  = config.getString("settings.chat.send-sleep-percentage-message.skipping-night", "<grey>Enough players have slept! Skipping through the night in <worldname>.");
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        DefaultPermissions.registerPermission(sleepMessageBypass, "Allows player to not display a message in chat when they sleep", PermissionDefault.OP);
        return PurpurExtras.getPurpurConfig().getBoolean("settings.chat.send-sleep-percentage-message.enabled", false);
    }

    @EventHandler
    public void onPlayerDeepSleep(PlayerDeepSleepEvent event){
        if (playerSleepMessage == null || playerSleepMessage.isEmpty()) return;
        if (event.getPlayer().hasPermission(sleepMessageBypass)) return;
        World world = event.getPlayer().getWorld();
        String playerName = event.getPlayer().getName();
        String worldName = world.getName();
        int currentSleepCount = 0;
        int worldOnlineTotal = world.getPlayerCount();
        Integer worldSleepPercent = world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
        Integer neededSleepers = (int) Math.ceil((worldSleepPercent / 100.0) * worldOnlineTotal);
        List<Player> playerList = world.getPlayers();
        for (Player player : playerList) {
            if (player.isDeeplySleeping()) currentSleepCount += 1;
        }
        world.sendMessage(MiniMessage.miniMessage().deserialize(playerSleepMessage,
                Placeholder.unparsed("playername", playerName),
                Placeholder.unparsed("sleeping", String.valueOf(currentSleepCount)),
                Placeholder.unparsed("needed", String.valueOf(neededSleepers)),
                Placeholder.unparsed("worldname", worldName)));
    }

    @EventHandler
    public void nightSkip(TimeSkipEvent event) {
        if (!event.getSkipReason().equals(TimeSkipEvent.SkipReason.NIGHT_SKIP)) return;
        if (nightSkipMessage == null || nightSkipMessage.isEmpty()) return;
        String worldName = event.getWorld().getName();
        event.getWorld().sendMessage(MiniMessage.miniMessage().deserialize(nightSkipMessage, Placeholder.unparsed("worldname", worldName)));
    }
}
