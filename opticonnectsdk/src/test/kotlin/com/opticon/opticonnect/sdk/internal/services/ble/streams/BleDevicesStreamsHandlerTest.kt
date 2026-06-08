package com.opticon.opticonnect.sdk.internal.services.ble.streams

import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BleDevicesStreamsHandlerTest {

    @Test
    fun latestBatteryValuesAreNullUntilAValueHasBeenReceived() {
        val handler = BleDevicesStreamsHandler()

        assertNull(handler.getLatestBatteryPercentage(TEST_DEVICE_ID))
        assertNull(handler.getLatestBatteryStatus(TEST_DEVICE_ID))

        handler.getOrCreateBatteryPercentageStream(TEST_DEVICE_ID)
        handler.getOrCreateBatteryStatusStream(TEST_DEVICE_ID)

        assertNull(handler.getLatestBatteryPercentage(TEST_DEVICE_ID))
        assertNull(handler.getLatestBatteryStatus(TEST_DEVICE_ID))
    }

    @Test
    fun latestBatteryValuesReturnMostRecentStreamValues() = runBlocking {
        val handler = BleDevicesStreamsHandler()
        val batteryStatus = BatteryLevelStatus(
            isBatteryPresent = true,
            isWirelessCharging = false,
            isWiredCharging = true,
            isCharging = true,
            isBatteryFaulty = false,
            percentage = 87
        )

        handler.getOrCreateBatteryPercentageStream(TEST_DEVICE_ID).emit(87)
        handler.getOrCreateBatteryStatusStream(TEST_DEVICE_ID).emit(batteryStatus)

        assertEquals(87, handler.getLatestBatteryPercentage(TEST_DEVICE_ID))
        assertEquals(batteryStatus, handler.getLatestBatteryStatus(TEST_DEVICE_ID))
    }

    private companion object {
        const val TEST_DEVICE_ID = "38:89:DC:0E:00:0F"
    }
}
