package pl.fancychat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final FancyChat plugin;

    public ChatListener(FancyChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Tryb admin chatu - wiadomosc trafia tylko do adminow.
        if (plugin.isAdminChatToggled(player)) {
            event.setCancelled(true);
            plugin.sendAdminChat(player, event.getMessage());
            return;
        }

        String rank = plugin.getRank(player);
        String prefix = plugin.getRankValue(rank, "prefix");
        String nameColor = plugin.getRankValue(rank, "name-color");
        String msgColor = plugin.getRankValue(rank, "message-color");

        String message = plugin.escapeIfNoPerm(event.getMessage(), player);
        String format;

        String gradStart = plugin.getRankValue(rank, "gradient-start");
        String gradMid = plugin.getRankValue(rank, "gradient-mid");
        String gradEnd = plugin.getRankValue(rank, "gradient-end");
        if (GradientUtil.isHexColor(gradStart) && GradientUtil.isHexColor(gradEnd)) {
            if (!GradientUtil.isHexColor(gradMid)) {
                gradMid = gradEnd;
            }
            String gradientName = GradientUtil.applyGradient(player.getName(), gradStart, gradMid, gradEnd);
            format = plugin.colorize(prefix) + gradientName
                    + plugin.colorize("&r: " + msgColor) + "%2$s";
        } else {
            format = plugin.colorize(prefix + nameColor + "%1$s" + "&r: " + msgColor + "%2$s");
        }

        event.setMessage(message);
        event.setFormat(format);
    }
}
