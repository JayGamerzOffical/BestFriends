package org.jay.JayGamerz.bf;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class FriendCommand implements SimpleCommand {

    private final BestFriends plugin;
    private final FriendDatabaseManager friendDatabaseManager;

    public FriendCommand(BestFriends plugin, FriendDatabaseManager friendDatabaseManager) {
        this.plugin = plugin;
        this.friendDatabaseManager = friendDatabaseManager;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player)) {
            source.sendMessage(Component.text("Only players can use this command."));
            return;
        }

        Player player = (Player) source;

        if (args.length < 1) {
            player.sendMessage(Component.text("Usage: /friends <add/remove/block/unblock/status> <player>"));
            return;
        }

        String action = args[0];
        if (args.length == 2) {
            String friendName = args[1];
            UUID friendUUID = plugin.getServer().getPlayer(friendName).map(Player::getUniqueId).orElse(null);

            if (friendUUID == null) {
                player.sendMessage(Component.text("Player not found."));
                return;
            }

            switch (action.toLowerCase()) {
                case "add":
                    if (friendDatabaseManager.addFriend(player.getUniqueId(), friendUUID)) {
                        player.sendMessage(Component.text(friendName + " added as a friend."));
                    } else {
                        player.sendMessage(Component.text(friendName + " is already your friend."));
                    }
                    break;
                case "remove":
                    if (friendDatabaseManager.removeFriend(player.getUniqueId(), friendUUID)) {
                        player.sendMessage(Component.text(friendName + " removed from your friends."));
                    } else {
                        player.sendMessage(Component.text("Failed to remove friend."));
                    }
                    break;
                case "block":
                    if (friendDatabaseManager.blockFriend(player.getUniqueId(), friendUUID)) {
                        player.sendMessage(Component.text(friendName + " has been blocked."));
                    } else {
                        player.sendMessage(Component.text("Failed to block friend."));
                    }
                    break;
                case "unblock":
                    if (friendDatabaseManager.unblockFriend(player.getUniqueId(), friendUUID)) {
                        player.sendMessage(Component.text(friendName + " has been unblocked."));
                    } else {
                        player.sendMessage(Component.text("Failed to unblock friend."));
                    }
                    break;
                default:
                    player.sendMessage(Component.text("Unknown action."));
                    break;
            }
        } else {
            player.sendMessage(Component.text("Usage: /friend <add/remove/block/unblock> <player>"));
        }
    }
}
