package com.example.streamingtest

import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class BunnyPlayerActivity4 : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bunny4)

        webView = findViewById(R.id.bunnyWebView)

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.javaScriptCanOpenWindowsAutomatically = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = BunnyPlayerActivity3.MyChromeClient(this)

        webView.addJavascriptInterface(JSBridge(), "Android")

        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <script src="https://assets.mediadelivery.net/playerjs/player-0.1.0.min.js"></script>
                <style>
                    body { margin:0; padding:0; background:#000; }
                    iframe { width:100%; height:100%; border:0; }
                </style>
            </head>
            <body>

            <iframe
                id="bunny-frame"
                src="https://iframe.mediadelivery.net/embed/545553/f15fa728-046d-4927-ade5-60c2ce38bff1"
                allowfullscreen
                width="720" height="400" >
            </iframe>

            <script>
                const iframe = document.getElementById("bunny-frame");
                const player = new playerjs.Player(iframe);

                player.on("ready", () => Android.onPlayerEvent("ready"));
                player.on("play",  () => Android.onPlayerEvent("play"));
                player.on("pause", () => Android.onPlayerEvent("pause"));

                player.on("timeupdate", (data) => {
                    Android.onTimeUpdate(data.seconds.toString());
                });

                // --- Android-callable API ---
                function playVideo() { player.play(); }
                function pauseVideo() { player.pause(); }
                function muteVideo() { player.mute(); }
                function unmuteVideo() { player.unmute(); }
                function seekTo(s) { player.setCurrentTime(s); }
            </script>

            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(
            "https://example.com", // required for Player.js to work inside an iframe
            html,
            "text/html",
            "UTF-8",
            null
        )
    }


    inner class JSBridge {
        @JavascriptInterface
        fun onPlayerEvent(event: String) {
            Log.d("BUNNY", "Event: $event")
        }

        @JavascriptInterface
        fun onTimeUpdate(seconds: String) {
            Log.d("BUNNY", "Time: $seconds")
        }
    }

    // --- Android -> JS functions ---
    fun play() = webView.evaluateJavascript("playVideo()", null)
    fun pause() = webView.evaluateJavascript("pauseVideo()", null)
    fun mute() = webView.evaluateJavascript("muteVideo()", null)
    fun unmute() = webView.evaluateJavascript("unmuteVideo()", null)
    fun seekTo(sec: Int) = webView.evaluateJavascript("seekTo($sec)", null)
}
