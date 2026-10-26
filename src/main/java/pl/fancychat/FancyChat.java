package pl.fancychat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FancyChat extends JavaPlugin {

    private final Set<UUID> adminChatToggled = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("fancychat") || cmd.equals("fc")) {
            return handleFancyChat(sender, args);
        }
        if (cmd.equals("achat") || cmd.equals("a")) {
            return handleAChat(sender, args);
        }
        return false;
    }

    private boolean handleFancyChat(CommandSender sender, String[] args) {
        if (!sender.hasPermission("fancychat.admin")) {
            sender.sendMessage(colorize(getMessage("no-permission")));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage(colorize(getMessage("config-reloaded")));
        } else {
            sender.sendMessage(colorize("&eUzycie: /fancychat reload"));
        }
        return true;
    }

    private boolean handleAChat(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Komenda tylko dla graczy!");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("fancychat.adminchat")) {
            player.sendMessage(colorize(getMessage("no-permission")));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            UUID id = player.getUniqueId();
            if (adminChatToggled.remove(id)) {
                player.sendMessage(colorize(getMessage("adminchat-disabled")));
            } else {
                adminChatToggled.add(id);
                player.sendMessage(colorize(getMessage("adminchat-enabled")));
            }
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(colorize("&eUzycie: /achat <wiadomosc> lub /achat toggle"));
            return true;
        }
        String msg = String.join(" ", args);
        sendAdminChat(player, msg);
        return true;
    }

    void sendAdminChat(Player sender, String msg) {
        String formatted = colorize("&c[ADMIN-CHAT] &f" + sender.getName() + ": &c" + escapeIfNoPerm(msg, sender));
        getServer().getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("fancychat.adminchat"))
                .forEach(p -> p.sendMessage(formatted));
        getLogger().info("[ADMIN-CHAT] " + sender.getName() + ": " + msg);
    }

    boolean isAdminChatToggled(Player player) {
        return adminChatToggled.contains(player.getUniqueId());
    }

    /** Znajduje range gracza po uprawnieniu fancychat.rank.<nazwa>, inaczej range domyslna. */
    String getRank(Player player) {
        ConfigurationSection ranks = getConfig().getConfigurationSection("ranks");
        if (ranks == null) return null;
        for (String rank : ranks.getKeys(false)) {
            if (player.hasPermission("fancychat.rank." + rank.toLowerCase())) {
                return rank;
            }
        }
        return getConfig().getString("default-rank");
    }

    String getRankValue(String rank, String key) {
        return getConfig().getString("ranks." + rank + "." + key, "");
    }

    String getMessage(String key) {
        return getConfig().getString("messages." + key, "&cBrak komunikatu: " + key);
    }

    String colorize(String text) {
        return ChatColorCompat.translate(text);
    }

    /** Zostawia kody & tylko graczom z uprawnieniem fancychat.color, inaczej je ucieka. */
    String escapeIfNoPerm(String msg, Player player) {
        if (player.hasPermission("fancychat.color")) {
            return msg;
        }
        return msg.replace("&", "&\u00a7&\u00a7").replace("\u00a7&", "");
    }
}
