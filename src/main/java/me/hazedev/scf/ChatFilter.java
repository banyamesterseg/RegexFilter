package me.hazedev.scf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilter implements Listener {

    private final SmartChatFilters plugin;

    Pattern pattern;
    boolean deny;
    String warn;
    String replacement;
    String command;
    String alertGroup;
    String alertMessage;

    public ChatFilter(SmartChatFilters plugin, Map<?, ?> map) {
        this.plugin = plugin;
        this.pattern = Pattern.compile((String) map.get("pattern"));
        this.deny = map.containsKey("deny") && (boolean) map.get("deny");
        this.warn = CCUtils.addColor((String) map.getOrDefault("warn", null));
        this.replacement = (String) map.getOrDefault("replace", null);
        this.command = (String) map.getOrDefault("exec", null);
        this.alertGroup = (String) map.getOrDefault("alert.group", null);
        this.alertMessage = CCUtils.addColor((String) map.getOrDefault("alert.message", null));
    }

    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String originalMessage = event.getMessage();
        Matcher matcher = pattern.matcher(event.getMessage());
        if (matcher.matches()) {
            if (deny) {
                event.setCancelled(true);
            } else if (replacement != null) {
                event.setMessage(matcher.replaceAll(replacement));
            }
            if (warn != null) {
                event.getPlayer().sendMessage(ChatColor.RED + ChatColor.translateAlternateColorCodes('&', warn));
            }
            if (command != null) {
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            }
            if (alertGroup != null) {
                String[] message = new String[2];
                message[0] = alertMessage != null ? alertMessage : CCUtils.addColor("&cA message has been filtered");
                message[1] = CCUtils.addColor("&r  " + event.getPlayer().getName() + "> " + originalMessage);
                Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("scf.group." + alertGroup)).forEach(player -> player.sendMessage(message));
            }
        }
    }



}
