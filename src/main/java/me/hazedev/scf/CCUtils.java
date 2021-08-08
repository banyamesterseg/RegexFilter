package me.hazedev.scf;

import org.bukkit.ChatColor;

public class CCUtils {

    public static String addColor(String text) {
        if (text == null) return null;
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
