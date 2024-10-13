package org.jay.JayGamerz.bf;


import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class MsgCommandMan implements SimpleCommand {

    private final BestFriends plugin;
    private final FriendDatabaseManager friendManager;

 MsgCommandMan(BestFriends plugin, FriendDatabaseManager friendManager) {
        this.plugin = plugin;
        this.friendManager = friendManager;
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
        UUID playerUUID = player.getUniqueId();

        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /msg <player> <message>"));
            return;
        }

        String friendName = args[0];
        UUID friendUUID = plugin.getServer().getPlayer(friendName).map(Player::getUniqueId).orElse(null);

        if (friendUUID == null || !friendManager.getFriends(playerUUID).contains(friendUUID)) {
            player.sendMessage(Component.text("You are not friends with " + friendName + "."));
            return;
        }

        String message = String.join(" ", args);
        plugin.getServer().getPlayer(friendUUID).ifPresent(friend -> friend.sendMessage(Component.text("Message from " + player.getUsername() + ": " + message)));
        player.sendMessage(Component.text("Message sent to " + friendName + ": " + message));
    }
}
