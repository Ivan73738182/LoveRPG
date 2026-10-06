package com.ivangames.loverpg

data class Choice(
    val text: String,
    val next: String,
    // Карта "кому симпатия": {"anya": 5, "kira": 3}
    val affection: Map<String, Int> = emptyMap(),
    // Условие: id флага, который должен быть true, чтобы показать выбор
    val condition: String? = null,
    // Флаг, который установится при выборе
    val setFlag: String? = null
)

data class Scene(
    val id: String,
    val day: Int,
    val location: String,
    val text: String,          // итоговый текст (уже выбран boy/girl)
    val choices: List<Choice>
)
