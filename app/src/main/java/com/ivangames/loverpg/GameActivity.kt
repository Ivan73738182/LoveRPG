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
    private var currentDay: Int = 1

    private val affection = mutableMapOf<String, Int>()
    private val flags = mutableSetOf<String>()
    private val usedTemplates = mutableSetOf<String>()

    private var story: Map<String, Scene> = emptyMap()
    private var currentSceneId: String = ""

    private val characterNames = mapOf(
        "anya" to "Аня",
        "kira" to "Кира"
    )

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

        story = StoryLoader.load(this)
        ContentGenerator.load(this)

        currentSceneId = StoryLoader.getStartSceneId()
        showScene(currentSceneId)
    }

    private fun showScene(sceneId: String) {
        val scene = story[sceneId] ?: run {
            finish()
            return
        }
        renderScene(scene)
    }

    private fun renderScene(scene: Scene) {
        currentSceneId = scene.id
        currentDay = scene.day

        textDay.text = "День ${scene.day}"
        textLocation.text = scene.location
        updateAffectionText()

        textScene.text = resolveText(scene.text)
        scrollText.post { scrollText.scrollTo(0, 0) }

        layoutChoices.removeAllViews()
        for (choice in scene.choices) {
            if (choice.condition != null && !flags.contains(choice.condition)) continue
            addChoiceButton(choice)
        }
    }

    private fun resolveText(raw: String): String {
        if (raw.startsWith("§BOY§")) {
            val boyStart = "§BOY§".length
            val girlMarker = raw.indexOf("§GIRL§")
            val boyText = raw.substring(boyStart, girlMarker)
            val girlText = raw.substring(girlMarker + "§GIRL§".length)
            return if (playerGender == "boy") boyText else girlText
        }
        return raw
    }

    private fun addChoiceButton(choice: Choice) {
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
            lp.setMargins(0, 8, 0, 8)
            layoutParams = lp

            setOnClickListener { handleChoice(choice) }
        }
        layoutChoices.addView(btn)
    }

    private fun handleChoice(choice: Choice) {
        // Симпатия
        for ((target, delta) in choice.affection) {
            val current = affection[target] ?: 0
            affection[target] = (current + delta).coerceIn(0, 100)
        }

        // Флаг
        if (choice.setFlag != null) flags.add(choice.setFlag)

        // Выход в меню
        if (choice.next == "EXIT_TO_MENU") {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
            return
        }

        // Генерация следующей сцены
        if (choice.next == "__GENERATED__") {
            generateNextScene()
            return
        }

        // Обычная сцена из story.json
        showScene(choice.next)
    }

    /** Генерируем случайную сцену через ContentGenerator */
    private fun generateNextScene() {
        val generated = ContentGenerator.generateScene(
            usedTemplateIds = usedTemplates,
            affection = affection,
            playerName = playerName,
            playerGender = playerGender,
            day = currentDay + 1
        )

        if (generated == null) {
            // Все шаблоны израсходованы — идём в конец дня
            showScene("day1_end_sleep")
            return
        }

        // Запоминаем id шаблона (чтобы не повторялся)
        ContentGenerator.extractTemplateId(generated.id)?.let { usedTemplates.add(it) }

        renderScene(generated)
    }

    private fun updateAffectionText() {
        val parts = mutableListOf<String>()
        for ((key, value) in affection) {
            if (value > 0) {
                val name = characterNames[key] ?: key
                parts.add("💗 $name: $value")
            }
        }
        textAffection.text = parts.joinToString("  ")
    }
}
