package com.example.streamingtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.streamingtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVdoCipher.setOnClickListener {
            startActivity(Intent(this, VdoCipherPlayerActivity::class.java))
        }

        binding.btnBunny.setOnClickListener {
            startActivity(Intent(this, BunnyPlayerActivity::class.java))
        }

        binding.btnBunny2.setOnClickListener {
            startActivity(Intent(this, BunnyPlayerActivity2::class.java))
        }
    }
}