package com.opticon.opticonnect.sdk.internal.services.scanner_settings

import com.opticon.opticonnect.sdk.api.constants.commands.datawizard.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DataWizardHelper @Inject constructor() {
    private val dataWizardCommands: Set<String> = setOf(
        DataWizardCommands.DATA_WIZARD_CONFIGURATION_0,
        DataWizardCommands.DATA_WIZARD_CONFIGURATION_1,
        DataWizardCommands.DATA_WIZARD_CONFIGURATION_2,
        DataWizardCommands.DATA_WIZARD_CONFIGURATION_3,
        DataWizardCommands.DATA_WIZARD_CONFIGURATION_4,
        DataWizardCommands.DATA_WIZARD_CONFIGURATION_5,
        DataWizardCommands.DATA_WIZARD_CONFIGURATION_6,
        DataWizardCommands.DATA_WIZARD_CONFIGURATION_7,
        DataWizardCommands.DATA_WIZARD_ENABLE_CONFIGURATION_0,
        DataWizardCommands.DATA_WIZARD_ENABLE_CONFIGURATION_1,
        DataWizardCommands.DATA_WIZARD_ENABLE_CONFIGURATION_2,
        DataWizardCommands.DATA_WIZARD_ENABLE_CONFIGURATION_3,
        DataWizardCommands.DATA_WIZARD_ENABLE_CONFIGURATION_4,
        DataWizardCommands.DATA_WIZARD_ENABLE_CONFIGURATION_5,
        DataWizardCommands.DATA_WIZARD_ENABLE_CONFIGURATION_6,
        DataWizardCommands.DATA_WIZARD_ENABLE_CONFIGURATION_7,
        DataWizardCommands.DATA_WIZARD_DISABLE_CONFIGURATION_0,
        DataWizardCommands.DATA_WIZARD_DISABLE_CONFIGURATION_1,
        DataWizardCommands.DATA_WIZARD_DISABLE_CONFIGURATION_2,
        DataWizardCommands.DATA_WIZARD_DISABLE_CONFIGURATION_3,
        DataWizardCommands.DATA_WIZARD_DISABLE_CONFIGURATION_4,
        DataWizardCommands.DATA_WIZARD_DISABLE_CONFIGURATION_5,
        DataWizardCommands.DATA_WIZARD_DISABLE_CONFIGURATION_6,
        DataWizardCommands.DATA_WIZARD_DISABLE_CONFIGURATION_7,
        DataWizardCommands.DATA_WIZARD_ENABLE_UNPROCESSED_BEEP,
        DataWizardCommands.DATA_WIZARD_DISABLE_UNPROCESSED_BEEP,
        DataWizardCommands.DATA_WIZARD_RESET,
        DataWizardCommands.DATA_WIZARD_TRANSMIT_CONFIGURATION_SETTINGS,
        DataWizardCommands.DATA_WIZARD_TRANSMIT_ALL_BARCODES,
        DataWizardCommands.DATA_WIZARD_TRANSMIT_VALIDATED_BARCODES
    )

    private fun isDataWizardCommand(command: String): Boolean {
        return dataWizardCommands.contains(command)
    }

    fun isDataWizardParameter(candidate: String): Boolean {
        return (candidate.startsWith(DataWizardCommands.DATA_WIZARD_COMMAND_PREFIX_0) ||
                candidate.startsWith(DataWizardCommands.DATA_WIZARD_COMMAND_PREFIX_1)) &&
                candidate.length >= 3 &&
                !isDataWizardCommand(candidate)
    }
}
