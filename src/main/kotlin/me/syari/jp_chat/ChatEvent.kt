package me.syari.jp_chat

import me.syari.jp_chat.util.ChatConv.convJapanese
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatEvent: Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(e: AsyncPlayerChatEvent) {
        val m = e.message

        if (m.any { it.toInt() > 0x7E }) return

        if (m.startsWith("/")) return

        val dollarCount = m.count { it == '$' }

        if (dollarCount == 1 && m.startsWith("$")) {
            e.message = m.substring(1)
            return
        }

        if (dollarCount >= 2) {
            val parts = m.split("$")
            val processedMessage = StringBuilder()
            val originalWithoutMarkers = StringBuilder()

            for (i in parts.indices) {
                val part = parts[i]
                if (i % 2 == 0) {
                    processedMessage.append(processWords(part))
                } else {
                    processedMessage.append(part)
                }
                originalWithoutMarkers.append(part)
            }
            val jp = processedMessage.toString()
            val raw = originalWithoutMarkers.toString()

            e.message = if (raw == jp) raw else "$jp §7($raw§7)"
            return
        }

        val jp = processWords(m)
        e.message = if (m == jp) m else "$jp §7($m§7)"
    }

    private fun processWords(message: String): String {
        if (message.isEmpty()) return ""
        val words = message.split(Regex("""\s+"""))

        val urlPattern = Regex("""^(https?://|www\.|[\w-]+\.(net|jp|com|org|me|info)).*""", RegexOption.IGNORE_CASE)

        val grassPattern = Regex("""^[w]+$""", RegexOption.IGNORE_CASE)

        val colorPrefixPattern = Regex("""^([&§][0-9a-fk-orx])+""", RegexOption.IGNORE_CASE)

        return words.joinToString(" ") { word ->
            val colorMatch = colorPrefixPattern.find(word)
            val prefix = colorMatch?.value ?: ""
            val body = if (prefix.isNotEmpty()) word.substring(prefix.length) else word

            when {
                urlPattern.matches(word) -> word
                grassPattern.matches(word) -> word
                prefix.isNotEmpty() -> prefix + convJapanese(body)
                else -> convJapanese(word)
            }
        }
    }
}