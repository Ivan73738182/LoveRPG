package com.ivangames.loverpg

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Template(
    val id: String,
    val location: String,
    val with: String,
    val minAffection: Int,
    val maxAffection: Int,
    val weight: Int,
    val textBoy: String,
    val textGirl: String,
    val rawChoices: List<RawChoice>
)

data class RawChoice(
    val text: String,
    val affection: Map<String, Int>,
    val next: String? = null
)

object ContentGenerator {

    private var templates: List<Template> = emptyList()

    fun load(context: Context) {
        val json = context.assets.open("templates.json").bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        val array: JSONArray = root.getJSONArray("templates")

        val list = mutableListOf<Template>()
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            val rawChoices = mutableListOf<RawChoice>()
            val choicesArr = o.optJSONArray("choices") ?: JSONArray()
            for (j in 0 until choicesArr.length()) {
                val c = choicesArr.getJSONObject(j)
                val text = c.getString("text")
                val next = if (c.has("next")) c.getString("next") else null
                val affMap = mutableMapOf<String, Int>()
                val affObj = c.optJSONObject("affection")
                if (affObj != null) {
                    val keys = affObj.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        affMap[k] = affObj.getInt(k)
                    }
                }
                rawChoices.add(RawChoice(text, affMap, next))
            }

            list.add(
                Template(
                    id = o.getString("id"),
                    location = o.optString("location", ""),
                    with = o.optString("with", ""),
                    minAffection = o.optInt("minAffection", 0),
                    maxAffection = o.optInt("maxAffection", 100),
                    weight = o.optInt("weight", 1),
                    textBoy = o.optString("text_boy", o.optString("text", "")),
                    textGirl = o.optString("text_girl", o.optString("text", "")),
                    rawChoices = rawChoices
                )
            )
        }
        templates = list
    }

    fun generateScene(
        usedTemplateIds: Set<String>,
        affection: Map<String, Int>,
        playerName: String,
        playerGender: String,
        day: Int
    ): Scene? {

        val candidates = templates.filter { t ->
            !usedTemplateIds.contains(t.id) && checkAffection(t, affection)
        }

        if (candidates.isEmpty()) return null

        val picked = pickWeighted(candidates) ?: return null

        val rawText = if (playerGender == "boy") picked.textBoy else picked.textGirl
        val text = rawText.replace("[name]", playerName)

        val choices = picked.rawChoices.map { rc ->
            Choice(
                text = rc.text.replace("[name]", playerName),
                next = rc.next ?: "__GENERATED__",
                affection = rc.affection
            )
        }

        return Scene(
            id = "generated_${picked.id}",
            day = day,
            location = picked.location,
            text = text,
            choices = choices
        )
    }

    private fun checkAffection(t: Template, affection: Map<String, Int>): Boolean {
        if (t.with.isEmpty()) return true
        val value = affection[t.with] ?: 0
        return value in t.minAffection..t.maxAffection
    }

    private fun pickWeighted(list: List<Template>): Template? {
        if (list.isEmpty()) return null
        val total = list.sumOf { it.weight }
        if (total <= 0) return list.random()
        var r = (0 until total).random()
        for (t in list) {
            r -= t.weight
            if (r < 0) return t
        }
        return list.last()
    }

    fun extractTemplateId(sceneId: String): String? {
        return if (sceneId.startsWith("generated_")) sceneId.removePrefix("generated_") else null
    }
}
