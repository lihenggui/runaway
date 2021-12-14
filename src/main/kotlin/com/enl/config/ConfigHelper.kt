package com.enl.config

import com.charleskorn.kaml.Yaml
import com.enl.Config
import java.io.File

object ConfigHelper {
    fun getConfig(): Config {
        val filename = "config.yaml"
        val configPath = if (File(filename).exists()) {
            File(filename)
        } else {
            File(ClassLoader.getSystemResource("config.yaml").file)
        }
        val text = configPath.readText()
        return Yaml.default.decodeFromString(Config.serializer(), text)
    }
}