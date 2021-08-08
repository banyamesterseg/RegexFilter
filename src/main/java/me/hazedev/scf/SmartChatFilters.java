package me.hazedev.scf;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SmartChatFilters extends JavaPlugin {

    private List<ChatFilter> filters;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(this), this);
        saveDefaultConfig();
        reload();
    }

    public void reload() {
        filters = new ArrayList<>();
        reloadConfig();
        Configuration config = getConfig();
        for (Map<?, ?> filterMap: config.getMapList("filters")) {
            ChatFilter filter;
            try {
                filter = new ChatFilter(filterMap);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            filters.add(filter);
        }
    }

    public Iterable<ChatFilter> getFilters() {
        return Collections.unmodifiableList(filters);
    }

}
