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
                    body {
                        margin: 0;
                        background: #000;
                        overflow: hidden;
                    }
                    video {
                        width: 100%;
                        height: 75%;
                        background: #000;
                    }
                    .controls {
                        width: 100%;
                        height: 25%;
                        color: white;
                        padding: 10px;
                        box-sizing: border-box;
                        font-size: 18px;
                    }
                    .bar {
                        width: 100%;
                        height: 10px;
                        border-radius: 5px;
                        margin-top: 10px;
                        background: #333;
                        overflow: hidden;
                    }
                    .progress { height: 10px; width: 0%; background: #4caf50; }
                    .buffer   { height: 10px; width: 0%; background: #888; }

                    #quality {
                        padding: 5px;
                        margin-top: 10px;
                        background: #222;
                        color: white;
                        border: 1px solid #555;
                        border-radius: 5px;
                    }
                </style>
            </head>

            <body>

                <video id="video" controls autoplay></video>

                <div class="controls">
                    <span class="progress-text">0%</span>

                    <div class="bar">
                        <div class="buffer"></div>
                    </div>

                    <div class="bar">
                        <div class="progress"></div>
                    </div>

                    <select id="quality">
                        <option value="-1">Auto</option>
                    </select>
                </div>

                <script>
                    var video = document.getElementById('video');
                    var url = "$hlsUrl";

                    // Load HLS
                    if (Hls.isSupported()) {
                        var hls = new Hls();
                        hls.loadSource(url);
                        hls.attachMedia(video);
                        // Load available qualities
                        hls.on(Hls.Events.MANIFEST_PARSED, function (event, data) {
                            const levels = hls.levels;
                            const qualitySelect = document.getElementById("quality");

                            // Auto option
                            qualitySelect.innerHTML = "<option value='-1'>Auto</option>";

                            levels.forEach((lvl, index) => {
                                const option = document.createElement("option");
                                option.value = index;
                                option.text = lvl.height + "p";  // e.g. 360p, 720p, 1080p
                                qualitySelect.appendChild(option);
                            });
                        });
                        
                        // Change quality manually
                        document.getElementById("quality").addEventListener("change", function () {
                            const level = parseInt(this.value);

                            if (level === -1) {
                                hls.currentLevel = -1; // Auto
                            } else {
                                hls.currentLevel = level; // Manual fixed quality
                            }
                        });
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
                            document.querySelector(".buffer").style.width = percent + "%";

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
