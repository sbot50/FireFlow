package de.blazemcworld.fireflow.gui

import net.minestom.server.instance.Instance
import kotlin.math.max
import kotlin.math.min

private const val PADDING = 0.1
private const val DOUBLE_PADDING = PADDING * 2
private const val CENTER_SPACING = 0.2

class NodeComponent {

    var pos = Pos2d.ZERO
    var isBeingMoved = false
    val title = TextComponent()
    val inputs = mutableListOf<IOComponent.Input>()
    val outputs = mutableListOf<IOComponent.Output>()
    val outline = RectangleComponent()

    fun update(inst: Instance) {
        val baseY = pos.y
        var inputY = 0.0
        var outputY = 0.0
        var inputWidth = 0.0
        var outputWidth = 0.0

        for (i in inputs) {
            inputWidth = max(inputWidth, i.text.width())
        }
        for (o in outputs) {
            outputWidth = max(outputWidth, o.text.width())
        }
        if (inputWidth + outputWidth < title.width()) {
            val diff = (title.width() - inputWidth - outputWidth) * 0.5
            inputWidth += diff
            outputWidth += diff
        }
        inputWidth += CENTER_SPACING
        for (i in inputs) {
            i.pos = Pos2d(pos.x + inputWidth - i.text.width(), inputY + baseY)
            inputY -= i.text.height()
            i.update(inst)
        }
        for (o in outputs) {
            o.pos = Pos2d(pos.x - outputWidth, outputY + baseY)
            outputY -= o.text.height()
            o.update(inst)
        }
        title.pos = Pos2d(pos.x - title.width() * 0.5 + (inputWidth - outputWidth) * 0.5, pos.y + title.height())
        title.update(inst)
        outline.pos = Pos2d(pos.x - outputWidth - PADDING, pos.y + min(inputY, outputY) + title.height() - PADDING)
        outline.size = Pos2d(inputWidth + outputWidth + DOUBLE_PADDING, -min(inputY, outputY) + title.height() + DOUBLE_PADDING)
        outline.update(inst)
    }

    fun remove() {
        title.remove()
        outline.remove()
        inputs.forEach(IOComponent::remove)
        outputs.forEach(IOComponent::remove)
    }

    fun includes(pos: Pos2d) = outline.includes(pos)

}