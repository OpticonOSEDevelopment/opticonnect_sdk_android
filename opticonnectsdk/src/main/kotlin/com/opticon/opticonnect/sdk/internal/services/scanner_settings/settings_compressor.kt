package com.opticon.opticonnect.sdk.internal.services.scanner_settings

import com.opticon.opticonnect.sdk.api.entities.CommandData
import com.opticon.opticonnect.sdk.api.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.internal.entities.Command
import com.opticon.opticonnect.sdk.internal.entities.RawCommand
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SettingsCompressor @Inject constructor(
    private val settingsHandler: SettingsHandler,
    private val dataWizardHelper: DataWizardHelper
) {
    suspend fun getCompressedSettingsCommand(settings: String): Command {
        val compressedSettingsList = getCompressedSettingsList(settings)

        val compressedCommandData = StringBuffer()
        for (commandData in compressedSettingsList) {
            compressedCommandData.append(commandData.command)
            for (parameter in commandData.parameters) {
                compressedCommandData.append(parameter)
            }
        }
        return RawCommand(compressedCommandData.toString(), sendFeedback = false)
    }

    private fun isDirectInputCommand(command: String): Boolean {
        return settingsHandler.isDirectInputKey(command) || command.startsWith("$")
    }

    private fun addCommandToCompressedList(
        commandData: CommandData,
        compressedList: MutableList<CommandData>
    ) {
        val groupsToDisable = settingsHandler.getGroupsToDisableForCode(commandData.command)

        compressedList.removeAll {
            it.command == commandData.command || (groupsToDisable.isNotEmpty() &&
                    settingsHandler.getGroupsForCode(it.command).any { group ->
                        groupsToDisable.contains(group)
                    })
        }

        compressedList.add(commandData)
    }

    private fun compressCommandList(commandList: List<CommandData>): List<CommandData> {
        val compressedList = mutableListOf<CommandData>()
        for (commandData in commandList) {
            addCommandToCompressedList(commandData, compressedList)
        }
        return compressedList
    }

    suspend fun getCompressedSettingsList(settings: String): List<CommandData> {
        val commandsList = mutableListOf<CommandData>()
        var i = 0
        while (i < settings.length) {
            val currentChar = settings[i]
            when (currentChar) {
                '[' -> {
                    commandsList.add(CommandData(settings.substring(i, i + 4)))
                    i += 3
                }
                ']' -> {
                    commandsList.add(CommandData(settings.substring(i, i + 5)))
                    i += 4
                }
                '\'' -> {
                    val start = i
                    i++ // Move past the opening quote
                    while (i < settings.length && settings[i] != '\'') {
                        i++
                    }
                    // Include the closing quote
                    val parameter = settings.substring(start, i + 1)
                    commandsList.lastOrNull()?.parameters?.add(parameter)
                }
                else -> {
                    commandsList.add(CommandData(settings.substring(i, i + 2)))
                    i++
                }
            }

            if (commandsList.isNotEmpty() &&
                (isDirectInputCommand(commandsList.last().command) ||
                        dataWizardHelper.isDataWizardParameter(commandsList.last().command))
            ) {
                val parameter = commandsList.last().command
                if (commandsList.isNotEmpty()) {
                    commandsList.removeAt(commandsList.size - 1)
                }
                commandsList.lastOrNull()?.parameters?.add(parameter)
            }

            i++
        }

        return compressCommandList(commandsList)
    }
}