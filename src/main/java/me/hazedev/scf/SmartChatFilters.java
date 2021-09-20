package me.hazedev.scf;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SmartChatFilters extends JavaPlugin implements CommandExecutor {

    private List<ChatFilter> filters;
    public String prefix;
    public boolean debug;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(this), this);
        getCommand("scfreload").setExecutor(this);
        saveDefaultConfig();
        reload();
    }

    public void reload() {
        filters = new ArrayList<>();
        reloadConfig();
        Configuration config = getConfig();
        prefix = CCUtils.addColor(config.getString("prefix", "&c[ChatFilters] "));
        debug = config.getBoolean("debug", false);
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

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (label.equals("scfreload")) {
        if (sender.hasPermission("scf.reload")) {
          getLogger().info("Filterset reload requested");
          this.reload();
          return true;
        } else {
          sender.sendMessage("no you don't");
          return true;
        }
      } else {
        return false;
      }
    }

    public Iterable<ChatFilter> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isDebugOn() {
        return debug;
    }
}
