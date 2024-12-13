package me.michelemanna.dailyrewards;

import me.michelemanna.dailyrewards.commands.DailyRewardCommand;
import me.michelemanna.dailyrewards.managers.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DailyRewards extends JavaPlugin {
    private static DailyRewards instance;
    private DatabaseManager database;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        getCommand("dailyreward").setExecutor(new DailyRewardCommand());

        try {
            this.database = new DatabaseManager();
            this.database.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        this.database.close();
    }

    public static DailyRewards getInstance() {
        return instance;
    }

    public DatabaseManager getDatabase() {
        return database;
    }
}