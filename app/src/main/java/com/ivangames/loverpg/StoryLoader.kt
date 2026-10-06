package com.ivangames.loverpg

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object StoryLoader {

    private var scenes: Map<String, Scene> = emptyMap()
    private var startSceneId: String = "prologue"

    fun load(context: Context): Map<String, Scene> {
        val json = context.assets.open("story.json").bufferedReader().use { it.readText() }
        val root = JSONObject(json)

        startSceneId = root.optString("start", "prologue")

        val array: JSONArray = root.getJSONArray("scenes")
        val result = LinkedHashMap<String, Scene>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val scene = parseScene(obj)
            result[scene.id] = scene
        }

        scenes = result
        return result
    }

    private fun parseScene(obj: JSONObject): Scene {
        val id = obj.getString("id")
        val day = obj.optInt("day", 1)
        val location = obj.optString("location", "")

        // Текст: пробуем text_boy/text_girl, если нет — общий text
        val textBoy = obj.optString("text_boy", "")
        val textGirl = obj.optString("text_girl", "")
        val textCommon = obj.optString("text", "")

        // Здесь мы возвращаем оба варианта, а выбор сделает GameActivity
        // Пока кладём "сырой" вариант через спец. маркер
        val rawText = when {
            textBoy.isNotEmpty() || textGirl.isNotEmpty() -> "§BOY§$textBoy§GIRL§$textGirl"
            else -> textCommon
        }

        val choicesArray = obj.optJSONArray("choices") ?: JSONArray()
        val choices = mutableListOf<Choice>()

        for (j in 0 until choicesArray.length()) {
            val c = choicesArray.getJSONObject(j)
            val text = c.getString("text")
            val next = c.getString("next")

            // Парсим affection: {"anya": 5, "kira": 3}
            val affMap = mutableMapOf<String, Int>()
            val affObj = c.optJSONObject("affection")
            if (affObj != null) {
                val keys = affObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    affMap[k] = affObj.getInt(k)
                }
            }

            val condition = if (c.has("condition")) c.getString("condition") else null
            val setFlag = if (c.has("setFlag")) c.getString("setFlag") else null

            choices.add(Choice(text, next, affMap, condition, setFlag))
        }

        return Scene(
            id = id,
            day = day,
            location = location,
            text = rawText,
            choices = choices
        )
    }

    fun getStartSceneId(): String = startSceneId
}
