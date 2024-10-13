package org.jay.JayGamerz.bf;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(id = "bestfriends", name = "BestFriends", version = "1.0-SNAPSHOT", description = "This MInecraft Plugin to add Party System to Velocity Servers.", url = "https://www.youtube.com/@jaygamerz", authors = {"Jay Gamerz"})
public class BestFriends {
    private final ProxyServer server;
    private final Logger logger;
    private FriendDatabaseManager friendDatabaseManager;

    @Inject
    public BestFriends(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.friendDatabaseManager = new FriendDatabaseManager(this);
        server.getCommandManager().register("friends", new FriendCommand(this, friendDatabaseManager));
        this.server.getCommandManager().register("msg", new MsgCommandMan(this, friendDatabaseManager));
        server.getEventManager().register(this, new FriendListener(friendDatabaseManager));
    }

    public ProxyServer getServer() {
        return server;
    }

    public FriendDatabaseManager getFriendManager() {
        return friendDatabaseManager;
    }

    public Logger getLogger() {
        return logger;
    }
}