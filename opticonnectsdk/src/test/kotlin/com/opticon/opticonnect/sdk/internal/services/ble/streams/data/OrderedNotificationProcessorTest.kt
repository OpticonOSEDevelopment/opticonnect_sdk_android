package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class OrderedNotificationProcessorTest {

    @Test
    fun processesRapidNotificationsInArrivalOrder() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Default + Job())
        val notificationCount = 1_000
        val processedValues = mutableListOf<Int>()
        val allProcessed = CompletableDeferred<Unit>()
        val processor = OrderedNotificationProcessor(TEST_DEVICE_ID, scope) { data ->
            processedValues += data.toSequenceNumber()
            if (processedValues.size == notificationCount) {
                allProcessed.complete(Unit)
            }
        }

        try {
            processor.start()
            repeat(notificationCount) { sequenceNumber ->
                processor.enqueue(sequenceNumber.toNotificationData())
            }

            withTimeout(5_000) { allProcessed.await() }

            assertEquals((0 until notificationCount).toList(), processedValues)
        } finally {
            processor.close()
            scope.coroutineContext[Job]?.cancelAndJoin()
        }
    }

    @Test
    fun processesRapidNotificationsOneAtATime() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Default + Job())
        val notificationCount = 200
        val activeProcessors = AtomicInteger(0)
        val maxActiveProcessors = AtomicInteger(0)
        val processedCount = AtomicInteger(0)
        val allProcessed = CompletableDeferred<Unit>()
        val processor = OrderedNotificationProcessor(TEST_DEVICE_ID, scope) {
            val active = activeProcessors.incrementAndGet()
            maxActiveProcessors.updateAndGet { current -> maxOf(current, active) }
            delay(2)
            activeProcessors.decrementAndGet()

            if (processedCount.incrementAndGet() == notificationCount) {
                allProcessed.complete(Unit)
            }
        }

        try {
            processor.start()
            repeat(notificationCount) { sequenceNumber ->
                processor.enqueue(sequenceNumber.toNotificationData())
            }

            withTimeout(5_000) { allProcessed.await() }

            assertEquals(1, maxActiveProcessors.get())
            assertEquals(notificationCount, processedCount.get())
        } finally {
            processor.close()
            scope.coroutineContext[Job]?.cancelAndJoin()
        }
    }

    @Test
    fun copiesNotificationDataBeforeProcessing() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Default + Job())
        val processedValue = CompletableDeferred<Int>()
        val processor = OrderedNotificationProcessor(TEST_DEVICE_ID, scope) { data ->
            processedValue.complete(data.toSequenceNumber())
        }
        val notification = 42.toNotificationData()

        try {
            processor.start()
            processor.enqueue(notification)
            notification[0] = 0x7F
            notification[1] = 0x7F

            assertEquals(42, withTimeout(1_000) { processedValue.await() })
        } finally {
            processor.close()
            scope.coroutineContext[Job]?.cancelAndJoin()
        }
    }

    private fun Int.toNotificationData(): ByteArray {
        return byteArrayOf(
            ((this shr 8) and 0xFF).toByte(),
            (this and 0xFF).toByte()
        )
    }

    private fun ByteArray.toSequenceNumber(): Int {
        return ((this[0].toInt() and 0xFF) shl 8) or (this[1].toInt() and 0xFF)
    }

    private companion object {
        const val TEST_DEVICE_ID = "38:89:DC:0E:00:0F"
    }
}
