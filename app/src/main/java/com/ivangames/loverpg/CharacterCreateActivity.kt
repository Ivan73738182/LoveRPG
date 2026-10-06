package com.ivangames.loverpg

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class CharacterCreateActivity : AppCompatActivity() {

    private var selectedGender: String = ""
    private lateinit var cardBoy: LinearLayout
    private lateinit var cardGirl: LinearLayout
    private lateinit var editName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_create)

        cardBoy = findViewById(R.id.cardBoy)
        cardGirl = findViewById(R.id.cardGirl)
        editName = findViewById(R.id.editName)
        val btnStart = findViewById<AppCompatButton>(R.id.btnStart)

        cardBoy.setOnClickListener {
            selectedGender = "boy"
            updateSelection()
        }

        cardGirl.setOnClickListener {
            selectedGender = "girl"
            updateSelection()
        }

        btnStart.setOnClickListener {
            val name = editName.text.toString().trim()

            if (selectedGender.isEmpty()) {
                Toast.makeText(this, "Выбери, кто ты 💕", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                Toast.makeText(this, "Введи своё имя", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("gender", selectedGender)
            intent.putExtra("name", name)
            startActivity(intent)
        }
    }

    private fun updateSelection() {
        if (selectedGender == "boy") {
            cardBoy.setBackgroundResource(R.drawable.bg_gender_selected)
            cardGirl.setBackgroundResource(R.drawable.bg_gender_normal)
        } else {
            cardBoy.setBackgroundResource(R.drawable.bg_gender_normal)
            cardGirl.setBackgroundResource(R.drawable.bg_gender_selected)
        }
    }
}
