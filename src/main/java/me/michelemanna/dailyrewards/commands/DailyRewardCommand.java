package me.michelemanna.dailyrewards.commands;

import me.michelemanna.dailyrewards.DailyRewards;
import me.michelemanna.dailyrewards.gui.DailyRewardMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DailyRewardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (args.length == 0) {
            if (sender.hasPermission("dailyreward.claim")) {
                new DailyRewardMenu().openMenu((Player) sender);
            } else {
                sender.sendMessage("You don't have permission to execute this command.");
            }

            return true;
        }

        if (args.length == 2 && args[0].contains("reset")) {
            if (sender.hasPermission("dailyreward.reset")) {
                Player player = Bukkit.getPlayer(args[1]);

                if (player == null) {
                    sender.sendMessage("Player not found.");
                    return true;
                }

                DailyRewards.getInstance().getDatabase().resetStreak(player.getUniqueId().toString());

                sender.sendMessage("Player's streak has been reset.");
            } else {
                sender.sendMessage("You don't have permission to execute this command.");
            }
            return true;
        }

        return false;
    }
}