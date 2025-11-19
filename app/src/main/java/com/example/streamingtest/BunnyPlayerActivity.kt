package com.example.streamingtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.streamingtest.databinding.ActivityBunnyBinding


class BunnyPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBunnyBinding

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBunnyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.bunnyPlayer.player = player

        // Example test video (replace later with Bunny secured URL or VdoCipher)
        val mediaItem = MediaItem.fromUri(
            "https://player.mediadelivery.net/embed/545553/98f94d01-2805-449e-8bfe-433473c88988"
        )

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    override fun onStop() {
        player?.release()
        player = null
        super.onStop()
    }
}
