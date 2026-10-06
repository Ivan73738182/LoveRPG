package com.ivangames.loverpg

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class GameActivity : AppCompatActivity() {

    private lateinit var textDay: TextView
    private lateinit var textLocation: TextView
    private lateinit var textAffection: TextView
    private lateinit var textScene: TextView
    private lateinit var scrollText: ScrollView
    private lateinit var layoutChoices: LinearLayout

    private var playerName: String = "Герой"
    private var playerGender: String = "boy"

    // Симпатия к персонажам
    private val affection = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        playerName = intent.getStringExtra("name") ?: "Герой"
        playerGender = intent.getStringExtra("gender") ?: "boy"

        textDay = findViewById(R.id.textDay)
        textLocation = findViewById(R.id.textLocation)
        textAffection = findViewById(R.id.textAffection)
        textScene = findViewById(R.id.textScene)
        scrollText = findViewById(R.id.scrollText)
        layoutChoices = findViewById(R.id.layoutChoices)

        // Стартуем с пролога
        showScene("prologue")
    }

    private fun showScene(sceneId: String) {
        val scene = StoryData.scenes[sceneId] ?: run {
            finish()
            return
        }

        // Верхняя панель
        textDay.text = "День ${scene.day}"
        textLocation.text = scene.location
        updateAffectionText()

        // Текст сцены — подставляем имя игрока
        val personalText = scene.text
            .replace("[name]", playerName)
            .replace("[он_она]", if (playerGender == "boy") "он" else "она")
        textScene.text = personalText

        // Прокрутка наверх
        scrollText.post { scrollText.scrollTo(0, 0) }

        // Кнопки вариантов
        layoutChoices.removeAllViews()
        for (choice in scene.choices) {
            val btn = AppCompatButton(this).apply {
                text = choice.text
                setTextColor(resources.getColor(R.color.text_light, null))
                textSize = 16f
                isAllCaps = false
                background = resources.getDrawable(R.drawable.bg_btn_pink, null)
                stateListAnimator = null
                elevation = 0f

                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 6, 0, 6)
                layoutParams = lp

                setOnClickListener {
                    handleChoice(choice)
                }
            }
            layoutChoices.addView(btn)
        }
    }

    private fun handleChoice(choice: Choice) {
        // Обновляем симпатию
        if (choice.affectionTarget.isNotEmpty() && choice.affectionDelta != 0) {
            val current = affection[choice.affectionTarget] ?: 0
            affection[choice.affectionTarget] = (current + choice.affectionDelta).coerceIn(0, 100)
        }

        if (choice.nextSceneId == "EXIT_TO_MENU") {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
            return
        }

        showScene(choice.nextSceneId)
    }

    private fun updateAffectionText() {
        val anya = affection["anya"] ?: 0
        textAffection.text = if (anya > 0) "💗 Аня: $anya" else ""
    }
}
