package hu.banyamesterseg.regexfilter;

import org.bukkit.ChatColor;

public class ColorFormatter {

  public static String addColor(String text) {
    if (text == null) return null;
    return ChatColor.translateAlternateColorCodes('&', text);
  }
}
//vim: ts=2 sw=2 et
