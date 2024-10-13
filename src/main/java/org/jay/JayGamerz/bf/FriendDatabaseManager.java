package org.jay.JayGamerz.bf;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FriendDatabaseManager {

    final BestFriends plugin;
    private HikariDataSource dataSource;

    public FriendDatabaseManager(BestFriends plugin) {
        this.plugin = plugin;
        setupDatabase();
    }

    private void setupDatabase() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/friendplugin");
        config.setUsername("your_username"); // Replace with your MySQL username
        config.setPassword("your_password"); // Replace with your MySQL password
        config.setMaximumPoolSize(10);
        this.dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS friends (id INT PRIMARY KEY AUTO_INCREMENT, player_uuid VARCHAR(36), friend_uuid VARCHAR(36), favorite BOOLEAN DEFAULT false, blocked BOOLEAN DEFAULT false)"
             )) {
            ps.execute();
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to initialize database", e);
        }
    }

    public boolean addFriend(UUID playerUUID, UUID friendUUID) {
        // Check if they are already friends
        if (areFriends(playerUUID, friendUUID)) {
            return false; // Already friends
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO friends (player_uuid, friend_uuid) VALUES (?, ?)"
             )) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, friendUUID.toString());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to add friend", e);
            return false;
        }
    }

    public boolean removeFriend(UUID playerUUID, UUID friendUUID) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM friends WHERE player_uuid = ? AND friend_uuid = ?"
             )) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, friendUUID.toString());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to remove friend", e);
            return false;
        }
    }

    public Set<UUID> getFriends(UUID playerUUID) {
        Set<UUID> friends = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT friend_uuid FROM friends WHERE player_uuid = ? AND blocked = false"
             )) {
            ps.setString(1, playerUUID.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                friends.add(UUID.fromString(rs.getString("friend_uuid")));
            }
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to retrieve friends", e);
        }
        return friends;
    }

    public boolean blockFriend(UUID playerUUID, UUID friendUUID) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE friends SET blocked = true WHERE player_uuid = ? AND friend_uuid = ?"
             )) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, friendUUID.toString());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to block friend", e);
            return false;
        }
    }

    public boolean unblockFriend(UUID playerUUID, UUID friendUUID) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE friends SET blocked = false WHERE player_uuid = ? AND friend_uuid = ?"
             )) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, friendUUID.toString());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to unblock friend", e);
            return false;
        }
    }

    public boolean areFriends(UUID playerUUID, UUID friendUUID) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT COUNT(*) FROM friends WHERE player_uuid = ? AND friend_uuid = ? AND blocked = false"
             )) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, friendUUID.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to check if friends", e);
        }
        return false;
    }

    public String getFriendStatus(UUID friendUUID) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT status FROM friends WHERE friend_uuid = ?"
             )) {
            ps.setString(1, friendUUID.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to retrieve friend status", e);
        }
        return "Unknown";
    }

    public void updateFriendStatus(UUID friendUUID, String status) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE friends SET status = ? WHERE friend_uuid = ?"
             )) {
            ps.setString(1, status);
            ps.setString(2, friendUUID.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to update friend status", e);
        }
    }
}
