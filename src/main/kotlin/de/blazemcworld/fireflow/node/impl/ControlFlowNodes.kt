package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.*
import net.minestom.server.MinecraftServer
import net.minestom.server.item.Material
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule

object ScheduleNode : BaseNode("Schedule", Material.CLOCK) {
    private val signal = input("Signal", SignalType)
    private val delay = input("Delay", NumberType)
    private val sharedLocals = input("Shared Locals", ConditionType)
    private val next = output("Next", SignalType)
    private val schedule = output("Schedule", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = {
            var task: Task? = null
            val stop: () -> Unit = {
                task?.cancel()
            }
            ctx.global.onDestroy += stop
            val child = it.child(it[ctx[sharedLocals]] ?: false)
            task = MinecraftServer.getSchedulerManager().scheduleTask({
                ctx.global.onDestroy -= stop
                child.emit(ctx[schedule], now = true)
                return@scheduleTask TaskSchedule.stop()
            }, TaskSchedule.tick(it[ctx[delay]]?.toInt() ?: 1))

            it.emit(ctx[next])
        }
    }
}