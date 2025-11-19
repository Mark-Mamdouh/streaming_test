package com.example.streamingtest

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.streamingtest.databinding.ActivityBunny2Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bunny.api.BunnyStreamApi
import org.openapitools.client.models.PaginationListOfVideoModel
import java.security.MessageDigest

class BunnyPlayerActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityBunny2Binding
    val accessKey = "271afca6-aac0-4bfb-9dabbf1dce18-198a-4905"
    val signingKey = "b4798594-1b4f-4bd8-85ad-b705db83e87d"
    val libraryId = 545553L

    fun generateSignedUrl(videoId: String, signingKey: String): String {
        val expires = (System.currentTimeMillis() / 1000) + 3600
        val signatureInput = "$videoId$expires$signingKey"

        val signature = MessageDigest.getInstance("SHA-256")
            .digest(signatureInput.toByteArray())
            .joinToString("") { "%02x".format(it) }

        return "?token=$signature&expires=$expires"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBunny2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        BunnyStreamApi.initialize(applicationContext, accessKey, libraryId)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response: PaginationListOfVideoModel =
                    BunnyStreamApi.getInstance().videosApi.videoList(
                        libraryId = libraryId
                    )

                withContext(Dispatchers.Main) {
                    println("response=${response}")
                    val guid = response.items?.get(0)?.guid ?: return@withContext

                    val signedSuffix = generateSignedUrl(guid, signingKey)

                    val playbackUrl =
                        "https://vz-${libraryId}-${guid}.edge.bunnyvideo.com/play${signedSuffix}"

//                    val videoUri = Uri.parse("https://$libraryId.b-cdn.net/${guid}/play_720p.mp4")
//
//
//                    val player = ExoPlayer.Builder(this@BunnyPlayerActivity2).build()
//                    binding.bunnyPlayer.player = player
//
//                    // Construct the video URL
//
//                    val mediaItem = MediaItem.fromUri(videoUri)
//                    player.setMediaItem(mediaItem)
//                    player.prepare()
//                    player.play()



//                    binding.bunnyPlayer.play(playbackUrl)
                    binding.bunnyPlayer.playVideo(guid, libraryId, response.items?.get(0)?.title ?: "")
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("responseError=${e.message}")
                }
            }
        }
    }
}
