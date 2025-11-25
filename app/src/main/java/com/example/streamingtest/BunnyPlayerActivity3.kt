package com.example.streamingtest

import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class BunnyPlayerActivity3 : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bunny3)

        webView = findViewById(R.id.bunnyWebView)

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = MyChromeClient(this)

        val hlsUrl = "https://vz-4e01e6b4-279.b-cdn.net/f15fa728-046d-4927-ade5-60c2ce38bff1/playlist.m3u8"

        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">

                <script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>

                <style>
                    html, body {
                        margin: 0;
                        padding: 0;
                        height: 100%;
                        background: #000;
                        overflow: hidden;
                    }
                    video {
                        width: 100%;
                        height: 80%;
                        background: #000;
                    }
                    .progress-container {
                        width: 100%;
                        height: 20%;
                        padding: 10px;
                        box-sizing: border-box;
                        color: white;
                        font-size: 18px;
                    }
                    .progress-bar {
                        width: 100%;
                        height: 10px;
                        background: #333;
                        margin-top: 10px;
                        border-radius: 5px;
                        overflow: hidden;
                    }
                    .progress {
                        height: 10px;
                        width: 0%;
                        background: #4caf50;
                    }
                </style>
            </head>

            <body>

                <video id="video" controls autoplay></video>

                <div class="progress-container">
                    <span class="progress-text">0%</span>
                    <div class="progress-bar">
                        <div class="progress"></div>
                    </div>
                </div>

                <script>
                    var video = document.getElementById('video');
                    var url = "$hlsUrl";

                    // Load HLS
                    if (Hls.isSupported()) {
                        var hls = new Hls();
                        hls.loadSource(url);
                        hls.attachMedia(video);
                    } else if (video.canPlayType("application/vnd.apple.mpegurl")) {
                        video.src = url;
                    }

                    // === Progress Tracking Variables ===
                    let totalDuration = 0;
                    let lastProgress = 0;

                    // Ready event
                    video.addEventListener("loadedmetadata", () => {
                        totalDuration = video.duration;
                        console.log("MARK, duration =", totalDuration);
                    });

                    video.addEventListener("play", () => {
                        console.log("Video is playing");
                    });

                    // Time update listener
                    video.addEventListener("timeupdate", () => {
                        const currentTime = video.currentTime;
                        const duration = video.duration;

                        if (!duration || duration === Infinity) return;

                        const progressPercentage = (currentTime / duration) * 100;
                        const progressRounded = Math.floor(progressPercentage / 25) * 25;

                        console.log("MARK Progress Percentage:", Math.floor(progressPercentage) + "%");

                        const progressText = document.querySelector(".progress-text");
                        progressText.textContent = Math.floor(progressPercentage) + "%";

                        if (progressRounded > lastProgress) {
                            console.log("MARK Reached:", progressRounded + "%");
                            lastProgress = progressRounded;

                            const progressBar = document.querySelector(".progress");
                            progressBar.style.width = progressRounded + "%";
                        }
                         const buffered = video.buffered;

                        if (buffered.length > 0) {
                            const end = buffered.end(buffered.length - 1);
                            const bufferPercentage = (end / duration) * 100;
                    
                            console.log("Mark Buffer Percentage:", Math.floor(bufferPercentage) + "%");
                        }
                    });
                </script>

            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    class MyChromeClient(private val activity: AppCompatActivity) : WebChromeClient() {

        private var customView: View? = null
        private lateinit var callback: CustomViewCallback
        private var originalSystemUi: Int = 0

        override fun onShowCustomView(view: View, viewCallback: CustomViewCallback) {
            if (customView != null) {
                viewCallback.onCustomViewHidden()
                return
            }

            customView = view
            callback = viewCallback

            val decor = activity.window.decorView as FrameLayout
            originalSystemUi = decor.systemUiVisibility

            decor.addView(
                customView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )

            decor.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        override fun onHideCustomView() {
            val decor = activity.window.decorView as FrameLayout
            decor.systemUiVisibility = originalSystemUi

            customView?.let {
                decor.removeView(it)
                callback.onCustomViewHidden()
            }
            customView = null
        }
    }
}
