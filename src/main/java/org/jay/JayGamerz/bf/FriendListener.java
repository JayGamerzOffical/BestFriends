package org.jay.JayGamerz.bf;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class FriendListener {

    private final FriendDatabaseManager friendManager;
    private ProxyServer server;

    public FriendListener(FriendDatabaseManager friendManager) {
        this.friendManager = friendManager;
        this.server = server;
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Update player's status when they join
        String status = "Online in " + player.getCurrentServer().get().getServerInfo().getName();
        friendManager.updateFriendStatus(playerUUID, status);

        // Notify friends about this player being online
        friendManager.getFriends(playerUUID).forEach(friendUUID -> {
            server.getPlayer(friendUUID).ifPresent(friend -> {
                friend.sendMessage(Component.text("§a" + player.getUsername() + " is now online in " + player.getCurrentServer().get().getServerInfo().getName() + "!"));
            });
        });
    }
}
