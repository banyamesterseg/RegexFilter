package hu.banyamesterseg.regexfilter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

  private final RegexFilterPlugin plugin;

  public AsyncPlayerChatListener(RegexFilterPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
    if (!event.isCancelled() && !event.getPlayer().hasPermission("regexfilter.exempt")) {
      for (ChatFilter filter : plugin.getFilters()) {
        filter.onAsyncPlayerChat(event);
      }
    }
  }
}
// vim: ts=2 sw=2 et
