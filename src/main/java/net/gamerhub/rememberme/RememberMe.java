package net.gamerhub.rememberme;

import com.google.inject.Inject;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.player.*;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.query.ProxyQueryEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import jdk.jfr.Registered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.security.Permission;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Plugin(
        id = "rememberme",
        name = "RememberMe",
        version = BuildConstants.VERSION,
        description = "Remembers the last server you joined",
        authors = {"Chaosdave34"}
)
public class RememberMe {
    private final ProxyServer server;
    private final Logger logger;
    private static final Map<UUID, RegisteredServer> lastServer = new HashMap<>();

    @Inject
    public RememberMe(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }

    @Subscribe
    public void onPlayerSwitchServer(ServerConnectedEvent e) {
        Player p = e.getPlayer();
        RegisteredServer server = e.getServer();
        lastServer.put(p.getUniqueId(), server);
        p.sendMessage(Component.text("[Velocity] Sending you to server " + server.getServerInfo().getName() + "...", NamedTextColor.GRAY));
    }

    @Subscribe
    public void onPlayerChooseServer(PlayerChooseInitialServerEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (lastServer.containsKey(uuid)) {
            e.setInitialServer(lastServer.get(uuid));
        }
    }
}
