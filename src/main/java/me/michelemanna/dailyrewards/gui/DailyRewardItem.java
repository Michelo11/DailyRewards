package me.michelemanna.dailyrewards.gui;

import me.michelemanna.dailyrewards.DailyRewards;
import me.michelemanna.dailyrewards.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class DailyRewardItem extends AbstractItem {
    private final PlayerData data;
    private final int day;

    public DailyRewardItem(PlayerData data, int day) {
        this.data = data;
        this.day = day;
    }

    @Override
    public ItemProvider getItemProvider() {
        boolean claimed = data.getClaimedDays() >= day;
        boolean canClaim = data.getClaimedDays() == day - 1 && data.getLastClaim().getTime() + 86400000L <= System.currentTimeMillis();

        if (claimed) {
            return new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .setDisplayName("§aDay " + day)
                    .setLegacyLore(List.of(
                            "§7You have already claimed this reward."
                    ));
        } else if (canClaim) {
            return new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                    .setDisplayName("§eDay " + day)
                    .setLegacyLore(List.of(
                            "§7Click to claim your reward!"
                    ));
        }
        return new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("§cDay " + day)
                .setLegacyLore(List.of(
                        "§7You must claim the previous reward first."
                ));

    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        if (data.getClaimedDays() == day - 1 && data.getLastClaim().getTime() + 86400000L <= System.currentTimeMillis()) {
            player.closeInventory();

            DailyRewards.getInstance().getDatabase().claimReward(player.getUniqueId().toString(), day);

            player.sendMessage("§aYou have claimed your reward for day " + day + "!");

            if (day == 7) {
                DailyRewards.getInstance().getDatabase().resetStreak(player.getUniqueId().toString());

                player.sendMessage("§cYou have claimed your reward for 7 days in a row! Your streak has been reset.");
            }
        }
    }
}
