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
    public String prefix;

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
        prefix = CCUtils.addColor(config.getString("prefix", "&c[ChatFilters] "));
        for (Map<?, ?> filterMap: config.getMapList("filters")) {
            ChatFilter filter;
            try {
                filter = new ChatFilter(this, filterMap);
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

    public String getPrefix() {
        return prefix;
    }
}
