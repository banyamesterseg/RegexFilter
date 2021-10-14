package hu.banyamesterseg.regexfilter;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilter {

  private final RegexFilterPlugin plugin;

  Pattern pattern;
  boolean deny;
  String warn;
  String replacement;
  String command;
  String alertGroup;
  String alertMessage;
  String exemptGroup;

  public ChatFilter(RegexFilterPlugin plugin, Map<?, ?> config) {
    this.plugin = plugin;
    //match
    this.pattern = Pattern.compile((String) config.get("pattern"));
    Bukkit.getLogger().info("Filter added: /" + this.pattern.toString().replaceAll("§([0-9a-fk-or])", "§§$1$1") + "§r/");
    //texts
    this.alertMessage = ColorFormatter.addColor((String) config.getOrDefault("notify-text", null));
    if (this.alertMessage == null) {
      this.alertMessage = ColorFormatter.addColor((String) config.getOrDefault("alert-text", null));
    }
    this.warn         = ColorFormatter.addColor((String) config.getOrDefault("warn-text", null));
    this.command      = (String) config.getOrDefault("exec", null);
    this.replacement  = (String) config.getOrDefault("replace", null);
    //behavior
    this.deny = config.containsKey("deny") && (boolean) config.get("deny");
    this.alertGroup = (String) config.getOrDefault("notify-group", null);
    if (this.alertGroup == null ) {
      this.alertGroup = (String) config.getOrDefault("alert-group", null);
    }
    if (this.alertGroup != null ) {
      this.alertGroup = "regexfilter.notify."+this.alertGroup;
    }
    this.exemptGroup = (String) config.getOrDefault("exempt-group", null);
    if (this.exemptGroup != null ) {
      this.exemptGroup = "regexfilter.exempt."+this.exemptGroup;
    }
  }

  public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
    Player sender = event.getPlayer();
    String originalMessage = event.getMessage();
    Matcher matcher = pattern.matcher(originalMessage);
    //MATCH
    if (matcher.find()) {
      if (plugin.isDebugOn()) {
        Bukkit.getLogger().info("MATCH: /" + pattern.toString().replaceAll("§([0-9a-fk-or])", "§§$1$1") + "§r/");
      }
      //EXEMPT
      if (exemptGroup != null && permissionPredicate(exemptGroup).test(sender)) {
        if (plugin.isDebugOn()) {
          Bukkit.getLogger().info("  EXEMPTED by "+exemptGroup+", stopped processing rule");
        }
        return;
      }
      //NOTIFY
      if (alertGroup != null) {
        String alert;
        if (alertMessage != null) {
          alert = PlaceholderAPI.setPlaceholders(sender, alertMessage)
                                .replace("{MESSAGE}", originalMessage)
                                .replace("{PATTERN}", pattern.pattern())
                                .replace("{MATCH}", matcher.group());
        } else {
          alert = ColorFormatter.addColor("&cA message has been filtered");
        }
        if (plugin.isDebugOn()) {
          Bukkit.getLogger().info("  NOTIF "+alertGroup+" with \""+alert+"\"");
        }
        final String falert = alert;
        try {
          Bukkit.getOnlinePlayers().stream().filter(permissionPredicate(alertGroup)).forEach(player -> player.sendMessage(plugin.getPrefix()+falert));
        } catch(NullPointerException e) {
          Bukkit.getLogger().info("Notification recipient group "+alertGroup+" is empty, no messages sent");
        }
        Bukkit.getLogger().info("RegexFilter notification: "+falert);
      }
      //WARN
      if (warn != null) {
        String replacedWarn = PlaceholderAPI.setPlaceholders(sender, warn)
                                            .replace("{MESSAGE}", originalMessage)
                                            .replace("{PATTERN}", pattern.pattern())
                                            .replace("{MATCH}", matcher.group());
        if (plugin.isDebugOn()) {
          Bukkit.getLogger().info("  WARN "+sender.getName()+" with \""+replacedWarn+"§r\"");
        }
        sender.sendMessage(plugin.getPrefix() + ChatColor.RED + ChatColor.translateAlternateColorCodes('&', replacedWarn));
      }
      //EXEC
      if (command != null) {
        String replacedCommand = PlaceholderAPI.setPlaceholders(sender, this.command)
                                               .replace("{MESSAGE}", originalMessage)
                                               .replace("{PATTERN}", pattern.pattern())
                                               .replace("{MATCH}", matcher.group());
        if (plugin.isDebugOn()) {
          Bukkit.getLogger().info("  EXEC \""+replacedCommand+"§r\"");
        }
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replacedCommand));
      }
      //DENY
      if (deny) {
        if (plugin.isDebugOn()) {
          Bukkit.getLogger().info("  DENY \""+originalMessage+"§r\"");
        }
        event.setCancelled(true);
        //REPLACE
      } else if (replacement != null) {
        if (plugin.isDebugOn()) {
          Bukkit.getLogger().info("  REPLACING");
        }
        String replacedReplacement = PlaceholderAPI.setPlaceholders(sender, replacement)
                                                   .replace("{MESSAGE}", originalMessage)
                                                   .replace("{PATTERN}", pattern.pattern())
                                                   .replace("{MATCH}", matcher.group());
        event.setMessage(matcher.replaceAll(replacedReplacement));
      }
    }
  }

  public static Predicate<Player> permissionPredicate(String permission) {
    return player -> player.hasPermission(permission);
  }
}
//vim: sw=2 ts=2 et
