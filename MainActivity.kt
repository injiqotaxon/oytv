
package com.oysha.tv

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.oysha.tv.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var player: ExoPlayer? = null
    private val channels = mutableListOf<Channel>()
    private lateinit var adapter: ChannelAdapter
    private var prefs: Prefs? = null

    data class Channel(val name: String, val url: String, val group: String = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = Prefs(this)

        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player

        adapter = ChannelAdapter(channels) { ch -> playChannel(ch) }
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        // Load saved playlists
        val savedLinks = prefs!!.getPlaylists()
        if (savedLinks.isNotEmpty()) {
            binding.editLink.setText(savedLinks.last())
            loadM3U(savedLinks.last())
        }

        binding.btnAdd.setOnClickListener {
            val link = binding.editLink.text.toString().trim()
            if (link.isEmpty()) {
                Toast.makeText(this, "Линк ёзинг!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefs!!.savePlaylist(link)
            loadM3U(link)
        }
        binding.btnClear.setOnClickListener {
            channels.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadM3U(m3uUrl: String) {
        binding.txtStatus.text = "Юкланмоқда..."
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val text = URL(m3uUrl).readText()
                val parsed = parseM3U(text)
                withContext(Dispatchers.Main) {
                    channels.clear()
                    channels.addAll(parsed)
                    adapter.notifyDataSetChanged()
                    binding.txtStatus.text = "${parsed.size} та канал топилди"
                    Toast.makeText(this@MainActivity, "${parsed.size} канал", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.txtStatus.text = "Хатолик: ${e.message}"
                }
            }
        }
    }

    private fun parseM3U(content: String): List<Channel> {
        val list = mutableListOf<Channel>()
        val lines = content.lines()
        var currentName = ""
        var currentGroup = ""
        for (line in lines) {
            if (line.startsWith("#EXTINF")) {
                currentName = line.substringAfterLast(",").trim()
                if (line.contains("group-title=")) {
                    currentGroup = line.substringAfter("group-title="").substringBefore(""")
                }
            } else if (line.startsWith("http")) {
                if (currentName.isNotEmpty()) {
                    list.add(Channel(currentName, line.trim(), currentGroup))
                    currentName = ""
                }
            }
        }
        return list
    }

    private fun playChannel(ch: Channel) {
        binding.txtNow.text = ch.name
        val mediaItem = MediaItem.fromUri(ch.url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
