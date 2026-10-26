package pl.fancychat;

import org.bukkit.ChatColor;

final class ChatColorCompat {

    private ChatColorCompat() {
    }

    /** Legacy tylko - zamienia kody & na znaki paragrafu. Bez Adventure/Components. */
    static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
