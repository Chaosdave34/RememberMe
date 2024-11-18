package io.github.chaosdave34.rememberme

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import io.github.chaosdave34.rememberme.seriazlizer.RegisteredServerSerializer
import io.github.chaosdave34.rememberme.seriazlizer.UUIDSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.slf4j.Logger
import java.io.File
import java.nio.file.Path
import java.util.UUID
import kotlin.collections.mutableMapOf
import kotlin.io.bufferedReader
import kotlin.io.bufferedWriter
import kotlin.io.path.createDirectory
import kotlin.io.path.notExists

@Suppress("unused")
@Plugin(
    id = "rememberme",
    name = "RememberMe",
    version = BuildConstants.VERSION,
    description = "Remembers the last server you joined",
    authors = ["Chaosdave34"]
)
class RememberMe @Inject constructor(val proxyServer: ProxyServer, val logger: Logger, @DataDirectory val dataDirectory: Path) {
    private val lastServer = mutableMapOf<UUID, RegisteredServer>()

    companion object {
        lateinit var INSTANCE: RememberMe
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        INSTANCE = this

        if (dataDirectory.notExists()) dataDirectory.createDirectory()

        val saveFile = File(dataDirectory.toFile(), "lastServer.json")

        try {
            if (saveFile.exists()) {
                val json = saveFile.bufferedReader().readLine() ?: "{}"
                Json.decodeFromString(MapSerializer(UUIDSerializer, RegisteredServerSerializer), json).forEach { (uuid, server) ->
                    server?.let { lastServer[uuid] = it }
                }
            }
        } catch (exception: Exception) {
            logger.error("Failed to load save file.", exception)
        }
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        val saveFile = File(dataDirectory.toFile(), "lastServer.json")
        if (!saveFile.exists()) saveFile.createNewFile()

        try {
            saveFile.bufferedWriter().use { it.write(Json.encodeToString(MapSerializer(UUIDSerializer, RegisteredServerSerializer), lastServer)) }
        } catch (exception: Exception) {
            logger.error("Failed to save save file.", exception)
        }
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
        event.setInitialServer(lastServer[event.player.uniqueId])
    }
}