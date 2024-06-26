package org.purpurmc.purpurextras.modules;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.Locale;

/**
 * Adds a sound for when the leash snaps
 */
public class LeashSnapSoundModule implements PurpurExtrasModule, Listener {

    private Key sound;
    private double volume;
    private double pitch;

    protected LeashSnapSoundModule() {
        String soundId = PurpurExtras.getPurpurConfig().getString("settings.leash-snap.sound", "block.bamboo.break");

        try {
            sound = org.bukkit.Sound.valueOf(soundId.replace('.', '_').toUpperCase(Locale.ROOT)).key();
        } catch (IllegalArgumentException e) {
            System.out.println("Could not set sound to '" + soundId + "', using default value 'block.bamboo.break'");
            sound = Key.key(Key.MINECRAFT_NAMESPACE, "block.bamboo.break");
        }

        volume = PurpurExtras.getPurpurConfig().getDouble("settings.leash-snap.volume", 1f);
        pitch = PurpurExtras.getPurpurConfig().getDouble("settings.leash-snap.pitch", 1.25f);
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.leash-snap.enabled", false) && sound != null;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeashBreak(EntityUnleashEvent event){

        if (event.getReason() != EntityUnleashEvent.UnleashReason.DISTANCE) return;

        event.getEntity().getWorld().playSound(Sound.sound(sound, Sound.Source.PLAYER, (float) volume, (float) pitch), event.getEntity());
    }
}
