package io.github.chaosdave34.rememberme.seriazlizer

import com.velocitypowered.api.proxy.server.RegisteredServer
import io.github.chaosdave34.rememberme.RememberMe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.optionals.getOrNull

object RegisteredServerSerializer : KSerializer<RegisteredServer?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RegisteredServer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: RegisteredServer?) {
        value?.let { encoder.encodeString(it.serverInfo.name) }
    }

    override fun deserialize(decoder: Decoder): RegisteredServer? = RememberMe.INSTANCE.proxyServer.getServer(decoder.decodeString()).getOrNull()
}