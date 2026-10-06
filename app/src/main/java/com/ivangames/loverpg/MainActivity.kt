package com.ivangames.loverpg

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNewGame = findViewById<AppCompatButton>(R.id.btnNewGame)
        val btnContinue = findViewById<AppCompatButton>(R.id.btnContinue)
        val btnExit = findViewById<AppCompatButton>(R.id.btnExit)

        btnNewGame.setOnClickListener {
            startActivity(Intent(this, CharacterCreateActivity::class.java))
        }

        btnContinue.setOnClickListener {
            Toast.makeText(this, "Сохранений пока нет", Toast.LENGTH_SHORT).show()
        }

        btnExit.setOnClickListener {
            finish()
        }
    }
}
