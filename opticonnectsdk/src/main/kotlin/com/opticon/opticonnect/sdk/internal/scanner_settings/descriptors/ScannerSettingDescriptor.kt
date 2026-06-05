package com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors

internal data class EnumCommandSetting<T>(
    val group: String,
    val defaultValue: T,
    val commandsByValue: Map<T, String>
) {
    private val valuesByCommand = commandsByValue.entries.associate { entry ->
        normalizeCode(entry.value) to entry.key
    }

    fun commandFor(value: T): String? = commandsByValue[value]

    fun valueForCommand(command: String): T? = valuesByCommand[normalizeCode(command)]

    fun valueFrom(settings: Map<String, List<String>>): T {
        return settings.keys.firstNotNullOfOrNull { command -> valueForCommand(command) }
            ?: defaultValue
    }
}

internal data class BooleanCommandSetting(
    val group: String,
    val defaultValue: Boolean,
    val enabledCommand: String,
    val disabledCommand: String
) {
    fun commandFor(value: Boolean): String {
        return if (value) enabledCommand else disabledCommand
    }

    fun valueForCommand(command: String): Boolean? {
        return when (normalizeCode(command)) {
            normalizeCode(enabledCommand) -> true
            normalizeCode(disabledCommand) -> false
            else -> null
        }
    }

    fun valueFrom(settings: Map<String, List<String>>): Boolean {
        return settings.keys.firstNotNullOfOrNull { command -> valueForCommand(command) }
            ?: defaultValue
    }
}

internal data class ParameterSetting<T>(
    val group: String,
    val command: String,
    val defaultValue: T,
    val encode: (T) -> List<String>,
    val decode: (List<String>) -> T?
) {
    fun parametersFor(value: T): List<String> = encode(value)

    fun valueFrom(settings: Map<String, List<String>>): T {
        return settings[normalizeCode(command)]?.let(decode) ?: defaultValue
    }
}

internal data class SymbologyCommandSetting<T>(
    val enableCommandsByValue: Map<T, String>,
    val disableCommandsByValue: Map<T, String>,
    val enableOnlyCommandsByValue: Map<T, String>
) {
    private val valuesByEnableCommand = enableCommandsByValue.entries.associate { entry ->
        normalizeCode(entry.value) to entry.key
    }
    private val valuesByDisableCommand = disableCommandsByValue.entries.associate { entry ->
        normalizeCode(entry.value) to entry.key
    }
    private val valuesByEnableOnlyCommand = enableOnlyCommandsByValue.entries.associate { entry ->
        normalizeCode(entry.value) to entry.key
    }

    fun commandFor(value: T, enabled: Boolean): String? {
        return if (enabled) {
            enableCommandsByValue[value]
        } else {
            disableCommandsByValue[value]
        }
    }

    fun enableOnlyCommandFor(value: T): String? = enableOnlyCommandsByValue[value]

    fun valueForCommand(command: String): Pair<T, Boolean>? {
        val normalizedCommand = normalizeCode(command)
        valuesByEnableCommand[normalizedCommand]?.let { return it to true }
        valuesByDisableCommand[normalizedCommand]?.let { return it to false }
        valuesByEnableOnlyCommand[normalizedCommand]?.let { return it to true }
        return null
    }

    fun isEnabledFrom(
        value: T,
        settings: Map<String, List<String>>,
        isDefaultCommand: (String) -> Boolean
    ): Boolean {
        val normalizedSettings = settings.keys.map(::normalizeCode).toSet()

        enableCommandsByValue[value]?.let { command ->
            if (normalizeCode(command) in normalizedSettings) return true
        }
        disableCommandsByValue[value]?.let { command ->
            if (normalizeCode(command) in normalizedSettings) return false
        }
        enableOnlyCommandsByValue[value]?.let { command ->
            if (normalizeCode(command) in normalizedSettings) return true
        }

        val enabledOnlyCommandWasUsed = enableOnlyCommandsByValue.values.any { command ->
            normalizeCode(command) in normalizedSettings
        }
        if (enabledOnlyCommandWasUsed) return false

        enableCommandsByValue[value]?.let { command ->
            if (isDefaultCommand(command)) return true
        }
        disableCommandsByValue[value]?.let { command ->
            if (isDefaultCommand(command)) return false
        }

        return false
    }
}

internal data class SymbologyFormattingCommandSetting<T>(
    val commandsBySymbology: Map<T, String>
) {
    fun commandFor(symbology: T): String? = commandsBySymbology[symbology]

    fun symbologyForCommand(command: String): T? {
        val normalizedCommand = normalizeCode(command)
        return commandsBySymbology.entries.firstOrNull { entry ->
            normalizeCode(entry.value) == normalizedCommand
        }?.key
    }
}

private fun normalizeCode(code: String): String {
    return if (code.startsWith("[") || code.startsWith("]")) {
        code.substring(1)
    } else {
        code
    }
}
