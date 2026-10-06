package com.ivangames.loverpg

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val gender = intent.getStringExtra("gender") ?: "boy"
        val name = intent.getStringExtra("name") ?: "Герой"

        val textHello = findViewById<TextView>(R.id.textHello)
        val textRole = findViewById<TextView>(R.id.textRole)

        textHello.text = "Привет, $name! 💕"

        textRole.text = if (gender == "boy") {
            "Ты — парень в этой истории."
        } else {
            "Ты — девушка в этой истории."
        }
    }
}
