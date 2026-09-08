package org.purpurmc.purpurextras.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.papermc.paper.event.player.PlayerServerFullCheckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.purpurmc.purpurextras.PurpurExtras;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinFullServerModule implements PurpurExtrasModule, Listener {

    private static JoinFullServerModule instance;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private File file;
    private List<JFSUser> users = new ArrayList<>();

    public record JFSUser(String uuid, String name) {}

    @Override
    public void enable() {
        instance = this;
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadJSONFile(plugin);
    }

    public static JoinFullServerModule getInstance() {
        return instance;
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.join-full-server.enabled", false);
    }

    private void loadJSONFile(PurpurExtras plugin) {
        this.file = new File(plugin.getDataFolder(), "jfslist.json");

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                try {
                    Files.createDirectories(parent.toPath());
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not create directories for jfslist.json");
                }
            }
            saveJSON();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<JFSUser>>() {}.getType();
            List<JFSUser> loaded = GSON.fromJson(reader, listType);
            this.users = loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            plugin.getLogger().severe("Could not read jfslist.json");
            this.users = new ArrayList<>();
        }
    }

    public synchronized boolean addUser(UUID uuid, String name) {
        if (uuid == null) return false;
        String uuidStr = uuid.toString();

        for (JFSUser user : users) {
            if (user.uuid().equalsIgnoreCase(uuidStr)) {
                return false;
            }
        }

        users.add(new JFSUser(uuidStr, name != null ? name : "Unknown"));
        return saveJSON();
    }

    public synchronized boolean removeUser(UUID uuid) {
        if (uuid == null) return false;
        String uuidStr = uuid.toString();

        boolean removed = users.removeIf(user -> user.uuid().equalsIgnoreCase(uuidStr));
        if (removed) {
            saveJSON();
        }
        return removed;
    }

    public boolean isAllowed(UUID uuid) {
        if (uuid == null) return false;
        String uuidStr = uuid.toString();
        return users.stream().anyMatch(user -> user.uuid().equalsIgnoreCase(uuidStr));
    }

    public List<JFSUser> getUsers() {
        return new ArrayList<>(this.users);
    }

    public synchronized void reload() {
        loadJSONFile(PurpurExtras.getInstance());
    }

    private boolean saveJSON() {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(users, writer);
            return true;
        } catch (IOException e) {
            PurpurExtras.getInstance().getLogger().severe("Could not save jfslist.json");
            return false;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerFullCheck(PlayerServerFullCheckEvent event) {
        UUID uuid = event.getPlayerProfile().getId();
        if (isAllowed(uuid)) {
            event.allow(true);
        }
    }
}
