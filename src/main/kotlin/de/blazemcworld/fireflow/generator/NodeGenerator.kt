package de.blazemcworld.fireflow.generator

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import java.io.File
import java.lang.reflect.Modifier
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Only meant as an ide utility
fun main() {
    val scope = Player::class.java
    val pkg = "de.blazemcworld.fireflow.node.impl.player"

    File("generated").mkdirs()
    val llmCache = mutableMapOf<String, MutableMap<String, String>>()

    if (File("llmCache.json").exists()) {
        val json = JsonParser.parseReader(File("llmCache.json").reader()).asJsonObject
        for ((system, cached) in json.entrySet()) {
            val map = mutableMapOf<String, String>()
            llmCache[system] = map
            for ((question, answer) in cached.asJsonObject.entrySet()) {
                map[question] = answer.asString
            }
        }
    }

    val client = HttpClient.newHttpClient()
    fun llmQuestion(system: String, history: List<Pair<String, String>>, question: String): String {
        synchronized(llmCache) {
            llmCache[system]?.get(question)?.let { return it }
        }

        val json = JsonObject()
        val options = JsonObject()
        options.addProperty("num_predict", 16)
        options.addProperty("temperature", 0)
        val messages = JsonArray()
        messages.add(JsonObject().also {
            it.addProperty("role", "system")
            it.addProperty("content", system)
        })
        for ((user, assistant) in history) {
            messages.add(JsonObject().also {
                it.addProperty("role", "user")
                it.addProperty("content", user)
            })
            messages.add(JsonObject().also {
                it.addProperty("role", "assistant")
                it.addProperty("content", assistant)
            })
        }
        messages.add(JsonObject().also {
            it.addProperty("role", "user")
            it.addProperty("content", question)
        })
        json.addProperty("stream", false)
        json.add("options", options)
        json.addProperty("model", "phi3")
        json.add("messages", messages)
        val req = HttpRequest.newBuilder(URI.create("http://localhost:11434/api/chat"))
            .POST(BodyPublishers.ofString(json.toString()))
            .header("Content-Type", "application/json")
            .build()
        val res = JsonParser.parseString(client.send(req, BodyHandlers.ofString()).body())
        val answer = res.asJsonObject.getAsJsonObject("message").get("content").asString
        synchronized(llmCache) {
            llmCache.computeIfAbsent(system) { mutableMapOf() }[question] = answer
        }
        return answer
    }

    val methodList = scope.methods.filter { m ->
        if (m.declaringClass == Class.forName("java.lang.Object")) return@filter false
        fun understands(type: Class<*>): Boolean {
            if (type.simpleName == "void") return true
            if (type.simpleName == "int") return true
            if (type.simpleName == "float") return true
            if (type.simpleName == "double") return true
            if (type.simpleName == "byte") return true
            if (type.simpleName == "long") return true
            if (type.simpleName == "short") return true
            return when (type) {
                Player::class.java -> true
                Pos::class.java -> true
                Vec::class.java -> true
                Component::class.java -> true
                TextComponent::class.java -> true
                else -> false
            }
        }
        understands(m.returnType) && m.parameterTypes.all { p -> understands(p) }
    }

    val pool = Executors.newFixedThreadPool(8)
    for ((i, method) in methodList.withIndex()) {
        pool.submit {
            println("Generating for $method (At ${i + 1}/${methodList.size})")

            val imports = mutableSetOf<String>()
            val content = StringBuilder("object ")
            val name = llmQuestion(
                "Provide a simpler name, for the given method.", listOf(
                    "public void net.minestom.server.entity.Entity.setVelocity(net.minestom.server.coordinate.Vec)" to "Set Velocity",
                    "public void net.minestom.server.entity.Entity.setBoundingBox(double,double,double)" to "Set Bounding Box",
                    "public void net.minestom.server.entity.Player.kill()" to "Kill Player",
                    "public void net.minestom.server.entity.Player.setRespawnPoint(net.minestom.server.coordinate.Pos)" to "Set Respawn Point",
                    "public void net.minestom.server.entity.Player.kick(net.kyori.adventure.text.Component)" to "Kick",
                    "public boolean net.minestom.server.entity.Player.isUsingItem()" to "Is Using Item",
                    "public boolean net.minestom.server.entity.Player.isFlying()" to "Is Flying"
                ), method.toString()
            ).replace(Regex("[(:].+"), "")

            val icon = llmQuestion(
                "Provide a related Minecraft item, for the user provided action, as an icon.", listOf(
                    "Set Velocity" to "minecraft:diamond_boots",
                    "Get Username" to "minecraft:name_tag",
                    "Get Position" to "minecraft:compass",
                    "Clear Effects" to "minecraft:milk_bucket",
                    "Set Player Level" to "minecraft:experience_bottle",
                    "Set Respawn Point" to "minecraft:red_bed",
                    "Get Respawn Point" to "minecraft:green_bed",
                    "Is Using Item" to "Bow",
                    "Get Current Use Time" to "minecraft:clock",
                    "Close Inventory" to "minecraft:chest",
                    "Kick" to "minecraft:barrier",
                    "Set Held Item" to "minecraft:iron_sword"
                ), name
            ).replace("minecraft:", "").uppercase().replace(Regex(" .+"), "")

            val signal = method.returnType.simpleName == "void" || (llmQuestion(
                "Answer if the method mutates some state.", listOf(
                    "Get Name" to "no",
                    "Teleport" to "yes",
                    "Set Velocity" to "yes",
                    "Damage" to "yes",
                    "Get Health" to "no"
                ), name
            ) == "yes")

            imports.add("de.blazemcworld.fireflow.node.BaseNode")
            imports.add("net.minestom.server.item.Material")
            content.append("${name.replace(" ", "")} : BaseNode(\"$name\", Material.$icon) {\n")

            if (signal) {
                imports.add("de.blazemcworld.fireflow.node.SignalType")
                content.append("    private val signal = input(\"Signal\", SignalType)\n")
            }

            fun getType(clazz: Class<*>): String {
                val type = when (clazz.simpleName) {
                    "boolean" -> "ConditionType"
                    "long", "double", "int", "float", "short", "byte" -> "NumberType"
                    "Player" -> "PlayerType"
                    "Component", "TextComponent" -> "MessageType"
                    "String" -> "TextType"
                    "Pos" -> "PositionType"
                    "Vec" -> "VectorType"
                    else -> {
                        System.err.println("Missing type for " + clazz.simpleName)
                        "UnknownType"
                    }
                }
                imports.add("de.blazemcworld.fireflow.node.$type")
                return type
            }

            val selfName = scope.simpleName.replace(Regex("^.")) { it.value.lowercase() }.replace(" ", "")
            if (!Modifier.isStatic(method.modifiers)) {
                content.append(
                    "    private val " + selfName + " = input(\"" + scope.simpleName + "\", " + getType(
                        scope
                    ) + ")\n"
                )
            }

            val parameters = mutableListOf<String>()
            for ((index, param) in method.parameters.withIndex()) {
                val inName = llmQuestion(
                    "For the method, how should the parameter be named?", listOf(
                        "public void net.minestom.server.entity.Player.setLevel(int)\nParam 1, of NumberType" to "Level",
                        "public void net.minestom.server.entity.Player.setRespawnPoint(net.minestom.server.coordinate.Pos)\nParam 1, of PositionType" to "Position"
                    ), "$method\nParam ${index + 1}, of " + getType(param.type)
                ).replace(Regex("[(:].+"), "")
                val varName = inName.lowercase().replace(Regex(" .")) { it.value.uppercase() }.replace(" ", "")
                content.append("    private val " + varName + " = input(\"" + inName + "\", " + getType(param.type) + ")\n")

                if (getType(param.type) != "NumberType" && getType(param.type) != "TextType") {
                    parameters.add("eval[ctx[$varName]] ?: return@run")
                    continue
                }

                val default = llmQuestion(
                    "For the method, is the parameter required, or can it have a default?", listOf(
                        "public void net.minestom.server.entity.Player.setLevel(int)\nParam 1, of NumberType" to "required",
                        "public void net.minestom.server.entity.Player.setFood(int)\nParam 1, of NumberType" to "optional"
                    ), "$method\nParam ${index + 1}, of " + getType(param.type)
                ) == "optional"
                val convert = if (param.type.simpleName == "double") "" else when (param.type.simpleName) {
                    "float" -> "?.toFloat()"
                    "long" -> "?.toLong()"
                    "byte" -> "?.toInt()?.toByte()"
                    "short" -> "?.toShort()"
                    "int" -> "?.toInt()"
                    else -> ""
                }
                if (!default) {
                    parameters.add("eval[ctx[$varName]]$convert ?: return@run")
                    continue
                }
                val defaultValue = llmQuestion(
                    "For the method, what should be the default value for the parameter?", listOf(

                    ), "$method\nParam ${index + 1}, of " + getType(param.type)
                )
                if (getType(param.type) == "TextType") {
                    parameters.add("eval[ctx[$varName]] ?: \"$defaultValue\"")
                } else {
                    parameters.add(
                        "eval[ctx[$varName]]$convert ?: $defaultValue" + when (param.type.simpleName) {
                            "float" -> "f"
                            "long" -> "L"
                            "double" -> if (defaultValue.indexOf('.') == -1) ".0" else ""
                            "byte" -> ".toByte()"
                            "short" -> ".toShort()"
                            "int" -> ".toInt()"
                            else -> ""
                        }
                    )
                }
            }
            var outName = ""
            if (signal) {
                content.append("    private val next = output(\"Next\", SignalType)\n")
            } else {
                val displayName = llmQuestion(
                    "Provide a simple name for the return value of the provided method.", listOf(
                        "public int net.minestom.server.entity.Player.getLevel()" to "Level",
                        "public long net.minestom.server.entity.Player.getCurrentItemUseTime()" to "Time",
                        "public boolean net.minestom.server.entity.Player.isUsingItem()" to "Case",
                        "public long net.minestom.server.entity.Player.getCurrentItemUseTime()" to "Time",
                        "public int net.minestom.server.entity.Player.getFood()" to "Food"
                    ), method.toString()
                )
                outName = displayName.lowercase().replace(Regex(" .")) { it.value.uppercase() }.replace(" ", "")
                content.append("    private val $outName = output(\"$displayName\", " + getType(method.returnType) + ")\n")
            }
            imports.add("de.blazemcworld.fireflow.node.NodeContext")
            content.append("\n    override fun setup(ctx: NodeContext) {\n")
            if (signal) {
                content.append("        ctx[signal].signalListener = { eval ->\n            run {\n                ")
            } else {
                content.append("        ctx[$outName].defaultHandler = eval@{ eval ->\n            run {\n                return@eval ")
            }
            if (Modifier.isStatic(method.modifiers)) content.append(scope.simpleName) else
                content.append("(eval[ctx[$selfName]]?.resolve() ?: return@run)")

            if (method.name.startsWith("get") && parameters.size == 0) {
                content.append("." + method.name.substring(3).replace(Regex("^.")) { it.value.lowercase() })
                if (!signal && getType(method.returnType) == "NumberType" && method.returnType.simpleName != "double") content.append(".toDouble()")
                content.append("\n            }\n")
            } else {
                content.append("." + method.name + "(")
                content.append(parameters.joinToString(", "))
                content.append(")")
                if (!signal && getType(method.returnType) == "NumberType" && method.returnType.simpleName != "double") content.append(".toDouble()")
                content.append("\n            }\n")
            }
            if (signal) {
                content.append("            eval.emit(ctx[next])\n")
            } else {
                content.append("            null\n")
            }
            content.append("        }\n    }\n}")

            val finalContent = "package $pkg\n\n" + imports.joinToString("\n") { "import $it" } + "\n\n$content"
            File("generated").resolve("${name.replace(" ", "")}.kt").writeText(finalContent)
        }
    }

    pool.shutdown()
    pool.awaitTermination(10, TimeUnit.MINUTES)

    val json = JsonObject()
    for ((system, cached) in llmCache.entries) {
        val map = JsonObject()
        for ((question, answer) in cached.entries) {
            map.addProperty(question, answer)
        }
        json.add(system, map)
    }
    File("llmCache.json").writeText(json.toString())
}