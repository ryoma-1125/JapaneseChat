package me.syari.jp_chat

import me.syari.jp_chat.util.ChatConv.convJapanese
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatEvent: Listener {
    @EventHandler(ignoreCancelled = true)
    fun on(e: AsyncPlayerChatEvent){
        e.isCancelled = true
        val m = e.message
        val p = e.player

        if (m.startsWith("$")) {
            val rawMessage = m.substring(1)
            broadcast("&f${p.displayName} &b≫ &f$rawMessage")
            return
        }

        val jp = convJapanese(m)
        broadcast(
            "&f${p.displayName} &b≫ &f" + if(m == jp) m else "$jp &7($m)"
        )
    }

    private val String.toColor get() = ChatColor.translateAlternateColorCodes('&', this)

    private fun broadcast(message: String){
        Bukkit.getServer().broadcastMessage(message.toColor)
    }
}