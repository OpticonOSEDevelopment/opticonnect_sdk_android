package com.opticon.opticonnect.sdk.internal.services.scanner_settings

import com.opticon.opticonnect.sdk.api.constants.commands.datawizard.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataWizardHelper @Inject constructor() {

    private val dataWizardCommands: Set<String> = setOf(
        DATA_WIZARD_CONFIGURATION_0,
        DATA_WIZARD_CONFIGURATION_1,
        DATA_WIZARD_CONFIGURATION_2,
        DATA_WIZARD_CONFIGURATION_3,
        DATA_WIZARD_CONFIGURATION_4,
        DATA_WIZARD_CONFIGURATION_5,
        DATA_WIZARD_CONFIGURATION_6,
        DATA_WIZARD_CONFIGURATION_7,
        DATA_WIZARD_ENABLE_CONFIGURATION_0,
        DATA_WIZARD_ENABLE_CONFIGURATION_1,
        DATA_WIZARD_ENABLE_CONFIGURATION_2,
        DATA_WIZARD_ENABLE_CONFIGURATION_3,
        DATA_WIZARD_ENABLE_CONFIGURATION_4,
        DATA_WIZARD_ENABLE_CONFIGURATION_5,
        DATA_WIZARD_ENABLE_CONFIGURATION_6,
        DATA_WIZARD_ENABLE_CONFIGURATION_7,
        DATA_WIZARD_DISABLE_CONFIGURATION_0,
        DATA_WIZARD_DISABLE_CONFIGURATION_1,
        DATA_WIZARD_DISABLE_CONFIGURATION_2,
        DATA_WIZARD_DISABLE_CONFIGURATION_3,
        DATA_WIZARD_DISABLE_CONFIGURATION_4,
        DATA_WIZARD_DISABLE_CONFIGURATION_5,
        DATA_WIZARD_DISABLE_CONFIGURATION_6,
        DATA_WIZARD_DISABLE_CONFIGURATION_7,
        DATA_WIZARD_ENABLE_UNPROCESSED_BEEP,
        DATA_WIZARD_DISABLE_UNPROCESSED_BEEP,
        DATA_WIZARD_RESET,
        DATA_WIZARD_TRANSMIT_CONFIGURATION_SETTINGS,
        DATA_WIZARD_TRANSMIT_ALL_BARCODES,
        DATA_WIZARD_TRANSMIT_VALIDATED_BARCODES
    )

    private fun isDataWizardCommand(command: String): Boolean {
        return dataWizardCommands.contains(command)
    }

    fun isDataWizardParameter(candidate: String): Boolean {
        return (candidate.startsWith(DATA_WIZARD_COMMAND_PREFIX_0) ||
                candidate.startsWith(DATA_WIZARD_COMMAND_PREFIX_1)) &&
                candidate.length >= 3 &&
                !isDataWizardCommand(candidate)
    }
}
