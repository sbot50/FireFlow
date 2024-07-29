package de.blazemcworld.fireflow.gui

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextDecoration

object TextWidth {

    private val info: JsonObject
    private val missingWidth: Double
    private val missingBoldOffset: Double

    init {
        val data = JsonParser.parseString(TextWidth::class.java.getResource("/fontwidth.json")!!.readText()).asJsonObject
        info = data.get("chars").asJsonObject
        val missing = data.get("missing_char").asJsonObject
        missingWidth = missing.get("width").asDouble
        missingBoldOffset = missing.get("bold_offset").asDouble
    }

    fun calculate(comp: TextComponent, parentBold: Boolean = false): Double {
        val state = comp.style().decoration(TextDecoration.BOLD)
        var bold = parentBold
        if (state == TextDecoration.State.TRUE) bold = true
        if (state == TextDecoration.State.FALSE) bold = false

        var width = calculate(comp.content(), bold)
        for (child in comp.children()) {
            if (child is TextComponent) {
                width += calculate(child, bold)
            }
        }

        return width
    }

    fun calculate(str: String, bold: Boolean = false): Double {
        var width = 0.0

        for (c in str) {
            if (info.has(c.toString())) {
                width += info.get(c.toString()).asJsonObject.get("width").asDouble
                if (bold) width += info.get(c.toString()).asJsonObject.get("bold_offset").asDouble
            } else {
                width += missingWidth
                if (bold) width += missingBoldOffset
            }
        }

        return width
    }

}