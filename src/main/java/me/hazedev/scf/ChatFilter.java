package me.hazedev.scf;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilter implements Listener {

    Pattern pattern;
    boolean deny;
    String warn;
    String replace;

    public ChatFilter(Map<?, ?> map) {
        this.pattern = Pattern.compile((String) map.get("pattern"));
        this.deny = map.containsKey("deny") && (boolean) map.get("deny");
        this.warn = (String) map.getOrDefault("warn", null);
        this.replace = (String) map.getOrDefault("replace", null);
    }

    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Matcher matcher = pattern.matcher(event.getMessage());
        if (matcher.matches()) {
            if (deny) {
                event.setCancelled(true);
            } else if (replace != null) {
                event.setMessage(matcher.replaceAll(replace));
            }
            if (warn != null) {
                event.getPlayer().sendMessage(ChatColor.RED + ChatColor.translateAlternateColorCodes('&', warn));
            }
        }
    }



}
