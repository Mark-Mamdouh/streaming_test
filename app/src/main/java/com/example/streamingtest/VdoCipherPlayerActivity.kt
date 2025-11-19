package com.example.streamingtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.streamingtest.databinding.ActivityVdocipherBinding
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.player.VdoPlayerSupportFragment
import com.vdocipher.aegis.player.PlayerHost
import com.vdocipher.aegis.player.VdoInitParams
import com.vdocipher.aegis.player.VdoPlayer

class VdoCipherPlayerActivity : AppCompatActivity(), PlayerHost.InitializationListener {

    private lateinit var binding: ActivityVdocipherBinding
    private var videoPlayer: VdoPlayer? = null
    private var vdoParams: VdoInitParams? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVdocipherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val otp = "YOUR_OTP"
        val playbackInfo = "YOUR_PLAYBACK_INFO"

        vdoParams = VdoInitParams.Builder()
            .setOtp(otp)
            .setPlaybackInfo(playbackInfo)
            .build()


        val fragment = VdoPlayerSupportFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.vdocipher_container, fragment)
            .commit()

        fragment.initialize(this)
    }

    override fun onInitializationSuccess(p0: PlayerHost?, p1: VdoPlayer?, p2: Boolean) {
        videoPlayer = p1
        p1?.load(vdoParams)
    }

    override fun onInitializationFailure(p0: PlayerHost?, p1: ErrorDescription?) {
        /* no-op */
    }
}
