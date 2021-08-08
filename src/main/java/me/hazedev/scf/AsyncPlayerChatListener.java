package me.hazedev.scf;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

    private final SmartChatFilters plugin;

    public AsyncPlayerChatListener(SmartChatFilters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled() && !event.getPlayer().hasPermission("scf.exempt")) {
            for (ChatFilter filter : plugin.getFilters()) {
                if (!event.isCancelled()) {
                    filter.onAsyncPlayerChat(event);
                } else {
                    break;
                }
            }
        }
    }

}
