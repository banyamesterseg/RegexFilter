package hu.banyamesterseg.regexfilter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RegexFilterPlugin extends JavaPlugin implements CommandExecutor {

  private List<ChatFilter> filters;
  public String prefix;
  public boolean debug;

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(this), this);
    this.getCommand("regexfilter").setExecutor(this);
    saveDefaultConfig();
    reload();
  }

  public void reload() {
    filters = new ArrayList<>();
    reloadConfig();
    Configuration config = getConfig();
    prefix = ColorFormatter.addColor(config.getString("prefix", "&7Regex&BFilter&8> "));
    debug = config.getBoolean("debug", false);
    addFilters(config.getMapList("filters"), filters);
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args[0].equals("reload")) {
      if (sender.hasPermission("regexfilter.reload")) {
        sender.sendMessage("reinitializing filterset");
        getLogger().info("reinitializing filterset");
        this.reload();
        return true;
      } else {
        sender.sendMessage("no you don't");
        return true;
      }
    } else {
      getLogger().info("wtf");
      return true;
    }
  }

  private void addFilters(List<Map<?, ?>> configList, List<ChatFilter> filters) {
    for (Map<?, ?> filterConfig: configList) {
      if (filterConfig.get("include") != null) {
        try {
          File incFile = new File("plugins"+File.separator+"RegexFilter"+File.separator+filterConfig.get("include"));
          getLogger().info(incFile.getCanonicalPath());
          Configuration include = YamlConfiguration.loadConfiguration(incFile);
          addFilters(include.getMapList("filters"), filters);
        } catch (Exception e) {
          getLogger().warning(e.getMessage());
          continue;
        }
      } else {
        ChatFilter filter;
        try {
          filter = new ChatFilter(this, filterConfig);
        } catch (NullPointerException e) {
          getLogger().warning("Filter found without pattern, ignoring");
          continue;
        } catch (Exception e) {
          getLogger().warning(e.getMessage());
          continue;
        }
        filters.add(filter);
      }
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
//vim: ts=2 sw=2 et
