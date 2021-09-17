package me.hazedev.scf;

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

    private final SmartChatFilters plugin;

    Pattern pattern;
    boolean deny;
    String warn;
    String replacement;
    String command;
    String alertGroup;
    String alertMessage;
    String exemptGroup;

    public ChatFilter(SmartChatFilters plugin, Map<?, ?> map) {
        this.plugin = plugin;
        this.pattern = Pattern.compile((String) map.get("pattern"));
        this.deny = map.containsKey("deny") && (boolean) map.get("deny");
        this.warn = CCUtils.addColor((String) map.getOrDefault("warn", null));
        this.replacement = (String) map.getOrDefault("replace", null);
        this.command = (String) map.getOrDefault("exec", null);
        this.alertGroup = (String) map.getOrDefault("alert-group", null);
        this.alertMessage = CCUtils.addColor((String) map.getOrDefault("alert-message", null));
        this.exemptGroup = (String) map.getOrDefault("exempt", null);
    }

    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String originalMessage = event.getMessage();
        Matcher matcher = pattern.matcher(originalMessage);
        if (matcher.matches()) {
            if (exemptGroup != null && groupPredicate(exemptGroup).test(sender)) {
                return;
            }
            if (deny) {
                event.setCancelled(true);
            } else if (replacement != null) {
                event.setMessage(matcher.replaceAll(replacement));
            }
            if (warn != null) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + ChatColor.translateAlternateColorCodes('&', warn));
            }
            if (command != null) {
                String replacedCommand = PlaceholderAPI.setPlaceholders(sender, this.command);
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replacedCommand));
            }
            if (alertGroup != null) {
                String alert = plugin.getPrefix();
                if (alertMessage != null) {
                    alert += PlaceholderAPI.setPlaceholders(sender, alertMessage)
                            .replace("{PATTERN}", pattern.pattern())
                            .replace("{MATCH}", matcher.group())
                            .replace("{MESSAGE}", originalMessage);
                } else {
                    alert += CCUtils.addColor("&cA message has been filtered");
                }
                String finalAlert = alert;
                Bukkit.getOnlinePlayers().stream().filter(groupPredicate(alertGroup)).forEach(player -> player.sendMessage(finalAlert));
            }
        }
    }

    public static String toGroupPermission(String group) {
        return "scf.group." + group;
    }

    public static Predicate<Player> permissionPredicate(String permission) {
        return player -> player.hasPermission(permission);
    }

    public static Predicate<Player> groupPredicate(String group) {
        return permissionPredicate(toGroupPermission(group));
    }

}
