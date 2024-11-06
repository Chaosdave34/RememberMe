package io.github.chaosdave34.rememberme

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import net.gamerhub.rememberme.BuildConstants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.slf4j.Logger
import java.util.UUID

@Suppress("unused")
@Plugin(
    id = "rememberme",
    name = "RememberMe",
    version = BuildConstants.VERSION,
    description = "Remembers the last server you joined",
    authors = ["Chaosdave34"]
)
class RememberMe{
    private val lastServer = mutableMapOf<UUID, RegisteredServer>()

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
    }

    @Subscribe
    fun onPlayerSwitchServer(event: ServerConnectedEvent) {
        val player = event.player
        val server = event.server

        lastServer[player.uniqueId] = server
        player.sendMessage(Component.text("[Velocity] Sending you to server ${server.serverInfo.name}...", NamedTextColor.GRAY))
    }

    @Subscribe
    fun onPlayerJoinProxy(event: PlayerChooseInitialServerEvent) {
        lastServer[event.player.uniqueId]?.let { event.setInitialServer(it) }
    }
}