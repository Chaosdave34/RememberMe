package net.gamerhub.rememberme;

import com.google.inject.Inject;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Plugin(
        id = "rememberme",
        name = "RememberMe",
        version = BuildConstants.VERSION,
        description = "Remembers the last server you joined",
        authors = {"Chaosdave34"}
)
public class RememberMe {
    private static final Map<UUID, RegisteredServer> lastServer = new HashMap<>();

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }

    @Subscribe
    public void onPlayerSwitchServer(ServerConnectedEvent e) {
        lastServer.put(e.getPlayer().getUniqueId(), e.getServer());
    }

    @Subscribe
    public void onPlayerChooseServer(PlayerChooseInitialServerEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (lastServer.containsKey(uuid)) {
            e.setInitialServer(lastServer.get(uuid));
        }
    }
}
