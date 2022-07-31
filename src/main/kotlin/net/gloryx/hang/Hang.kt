package net.gloryx.hang

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueType

class Hang(val modID: String, val loader: ClassLoader) {
    constructor(modID: String, cls: Class<*>) : this(modID, cls.classLoader)

    /**
     * 
     */
    constructor(modID: String) : this(modID, Thread.currentThread().contextClassLoader)
    private lateinit var initialMap: Map<String, String>
    private var preferJson = false

    /**
     * Initialize Hang with an initial language map from ClientLanguageMap.
     * @return the modified instance, for chaining convenience
     */
    fun init(initialMap: Map<String, String>) = apply {
        this.initialMap = initialMap
    }

    /**
     * Set whether to prefer translation entries from JSON (existing ones) or the ones from HOCON,
     * in case of a conflict.
     * @param preferJson Whether to prefer translation entries from JSON or from HOCON
     * @return the modified instance, for chaining convenience
     */
    fun preferJson(preferJson: Boolean) = apply {
        this.preferJson = preferJson
    }

    fun get(): Map<String, String> {
        if (!this::initialMap.isInitialized) throw IllegalArgumentException("#get called before #init(Map), follow README.md")
        val lang = initialMap["hang"] ?: "en_us"
        val root = ConfigFactory.load(loader, "assets/$modID/lang/$lang.conf").root()
        val newMap = mutableMapOf<String, String>()
        for ((key, value) in root) {
            recObj(key, value, newMap)
        }
        return polish(initialMap, newMap)
    }

    private fun polish(initial: Map<String, String>, hc: Map<String, String>): Map<String, String> =
        if (preferJson) {
            val i = initial.toList()
            val h = hc.toList()
            (i + h).distinct().toMap()
        } else (hc.toList() + initial.toList()).distinct().toMap()

    private fun recObj(key: String, obj: ConfigValue, map: MutableMap<String, String>) {
        if (obj.valueType() == ConfigValueType.STRING) map[key] = obj.unwrapped() as String
        else {
            for (o in (obj as ConfigObject)) {
                if (o.value.valueType() == ConfigValueType.OBJECT) recObj("$key.${o.key}", o.value, map)
                else if (o.value.valueType() == ConfigValueType.STRING) map["$key.${o.key}"] =
                    o.value.unwrapped() as String
            }
        }
    }
}