package me.michelemanna.dailyrewards.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.michelemanna.dailyrewards.DailyRewards;
import me.michelemanna.dailyrewards.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    private HikariDataSource dataSource;

    public void connect() throws SQLException {
        ConfigurationSection cs = DailyRewards.getInstance().getConfig().getConfigurationSection("mysql");
        Objects.requireNonNull(cs, "Unable to find the following key: mysql");
        HikariConfig config = new HikariConfig();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }

        config.setJdbcUrl("jdbc:mysql://" + cs.getString("host") + ":" + cs.getString("port") + "/" + cs.getString("database"));
        config.setUsername(cs.getString("username"));
        config.setPassword(cs.getString("password"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setConnectionTimeout(10000);
        config.setLeakDetectionThreshold(10000);
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(60000);
        config.setPoolName("PhonePool");
        config.addDataSourceProperty("useSSL", cs.getBoolean("ssl"));
        config.addDataSourceProperty("allowPublicKeyRetrieval", true);

        this.dataSource = new HikariDataSource(config);

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();

        statement.execute(
                "CREATE TABLE IF NOT EXISTS dailyRewards(" +
                        "uuid VARCHAR(36) PRIMARY KEY," +
                        "lastClaim Date," +
                        "claimedDays INT" +
                        ")"
        );

        statement.close();
        connection.close();
    }

    public void close() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }

    public CompletableFuture<PlayerData> getData(String uuid) {
        CompletableFuture<PlayerData> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(DailyRewards.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM dailyRewards WHERE uuid = ?");

                statement.setString(1, uuid);

                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    PlayerData data = new PlayerData();
                    data.setClaimedDays(set.getInt("claimedDays"));
                    data.setLastClaim(set.getDate("lastClaim"));

                    future.complete(data);
                } else {
                    future.complete(null);
                }

                set.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public void claimReward(String uuid, int claimedDays) {
        Bukkit.getScheduler().runTaskAsynchronously(DailyRewards.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO dailyRewards(uuid, lastClaim, claimedDays) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE lastClaim = ?, claimedDays = ?");

                statement.setString(1, uuid);
                statement.setDate(2, new Date(System.currentTimeMillis()));
                statement.setInt(3, claimedDays);
                statement.setDate(4, new Date(System.currentTimeMillis()));
                statement.setInt(5, claimedDays);

                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void resetStreak(String uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(DailyRewards.getInstance(), () -> {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE dailyRewards SET lastClaim = ?, claimedDays = ? WHERE uuid = ?");

                statement.setDate(1, new Date(System.currentTimeMillis()));
                statement.setInt(2, 0);
                statement.setString(3, uuid);

                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}