package me.michelemanna.dailyrewards.gui;

import me.michelemanna.dailyrewards.DailyRewards;
import me.michelemanna.dailyrewards.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.window.Window;

public class DailyRewardMenu {
    public void openMenu(Player player) {
        DailyRewards.getInstance().getDatabase().getData(player.getUniqueId().toString()).thenAccept(data -> {
            if (data == null) data = new PlayerData();

            Gui.Builder.Normal builder = Gui.normal()
                    .setStructure(new Structure(
                            "# # # # # # # # #",
                            "# . . . . . . . #",
                            "# 1 2 3 4 5 6 7 #",
                            "# . . . . . . . #",
                            "# # # # # # # # #"
                    ));

            for (int i = 1; i <= 7; i++) {
                builder.addIngredient(Character.forDigit(i, 10), new DailyRewardItem(data, i));
            }

            Gui gui = builder.build();

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle("Daily Rewards")
                    .setGui(gui)
                    .build();

            Bukkit.getScheduler().runTask(DailyRewards.getInstance(), window::open);
        });
    }
}
