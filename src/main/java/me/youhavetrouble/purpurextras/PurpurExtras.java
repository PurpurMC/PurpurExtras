package me.youhavetrouble.purpurextras;

import me.youhavetrouble.purpurextras.command.FancyCommand;
import me.youhavetrouble.purpurextras.config.PurpurConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public final class PurpurExtras extends JavaPlugin {

    private static PurpurConfig config;
    private static PurpurExtras instance;

    public final MiniMessage miniMessage = MiniMessage.builder().tags(
            TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.decorations())
                    .resolver(StandardTags.gradient())
                    .resolver(StandardTags.font())
                    .resolver(StandardTags.reset())
                    .resolver(StandardTags.rainbow())
                    .resolver(StandardTags.translatable())
                    .build()
    ).build();

    @Override
    public void onEnable() {
        try {
            Class.forName("org.purpurmc.purpur.PurpurConfig");
        } catch (ClassNotFoundException e) {
            getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0PurpurExtras was created to compliment Purpur, and it appears you're not using it!"));
            getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0Purpur is a drop-in replacement for " + getServer().getName() + "."));
            getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&x&8&0&5&2&8&0You can get Purpur on https://purpurmc.org/downloads"));
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        instance = this;
        config = new PurpurConfig();
        PluginCommand command = getCommand("purpurextras");
        if (command != null) {
            command.setExecutor(new PurpurExtrasCommand());
            getServer().getPluginManager().registerEvents(new FancyCommand(), this);
        }

    }

    public static PurpurConfig getPurpurConfig() {
        return config;
    }

    public static PurpurExtras getInstance() {
        return instance;
    }

    protected void reloadPurpurExtrasConfig(CommandSender commandSender) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            config = new PurpurConfig();
            commandSender.sendMessage(Component.text("PurpurExtras configuration reloaded!"));
        });
    }

    public void registerListener(Class<?> clazz) {
        try {
            Listener listener = (org.bukkit.event.Listener) clazz.getConstructor().newInstance();
            getServer().getPluginManager().registerEvents(listener, this);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
