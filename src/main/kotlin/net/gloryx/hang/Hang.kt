package net.gloryx.hang

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueType

/**
 * The heart of Hang.
 * @param modID The MOD ID, used in the [basePath] property to not interfere with Minecraft's normal language file schema.
 * @param loader The [ClassLoader] to load the language files from.
 * @see init
 * @see Hang.get
 */
class Hang(val modID: String, val loader: ClassLoader) {
    constructor(modID: String, cls: Class<*>) : this(modID, cls.classLoader)

    /**
     * This is not recommended, because it can lead to translation files not showing up.
     */
    constructor(modID: String) : this(modID, Thread.currentThread().contextClassLoader)

    /**
     * You can use this constructor to use your custom language-files' base path.
     * @param customBasePath
     * @constructor Creates a [Hang] instance with the [customBasePath]
     */
    constructor(modID: String, loader: ClassLoader, customBasePath: String = "assets/$modID/lang") : this(modID, loader) {
        this.basePath = customBasePath
    }

    private var basePath = "assets/$modID/lang"
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

    /**
     * @throws IllegalArgumentException if called before [init]
     * @return the final map to set the map property (in the mixin) to.
     */
    fun get(): Map<String, String> {
        if (!this::initialMap.isInitialized) throw IllegalArgumentException("#get called before #init(Map), follow README.md")
        val lang = initialMap["hang"] ?: "en_us"
        val root = ConfigFactory.load(loader, "$basePath/$lang.conf").root()
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