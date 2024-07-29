package de.blazemcworld.fireflow

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

class Config(json: JsonObject) {
    companion object {
        val store = Config(JsonParser.parseReader(File("config.json").bufferedReader()).asJsonObject)
    }

    val database = Database(json.getAsJsonObject("database"))
    val limits = Limits(json.getAsJsonObject("limits") ?: JsonObject())

    class Database(json: JsonObject) {
        val url = json.get("jdbcUrl")?.asString ?: throw IllegalArgumentException("Missing database.jdbcUrl in config!")
        val driver = json.get("driver")?.asString ?: "org.mariadb.jdbc.Driver"
        val user = json.get("user")?.asString ?: throw IllegalArgumentException("Missing database.user in config!")
        val password = json.get("password")?.asString ?: throw IllegalArgumentException("Missing database.password in config!")
    }

    class Limits(json: JsonObject) {
        val spacesPerPlayer = json.get("spacesPerPlayer")?.asInt ?: 5
    }
}