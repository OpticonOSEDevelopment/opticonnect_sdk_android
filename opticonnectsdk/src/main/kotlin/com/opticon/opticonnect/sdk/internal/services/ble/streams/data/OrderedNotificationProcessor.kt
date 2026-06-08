package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.Closeable

internal class OrderedNotificationProcessor(
    private val deviceId: String,
    private val scope: CoroutineScope,
    private val processNotification: suspend (ByteArray) -> Unit
) : Closeable {
    private val notificationDataChannel = Channel<ByteArray>(Channel.UNLIMITED)
    private var processingJob: Job? = null

    fun start() {
        if (processingJob != null) return

        processingJob = scope.launch {
            try {
                for (data in notificationDataChannel) {
                    try {
                        processNotification(data)
                    } catch (e: Exception) {
                        Timber.e(e, "Error processing notification data for device: $deviceId")
                    }
                }
            } catch (_: CancellationException) {
                Timber.d("Notification processing cancelled for device: $deviceId")
            } catch (e: Exception) {
                Timber.e(e, "Error in notification processing queue for device: $deviceId")
            }
        }
    }

    fun enqueue(data: ByteArray) {
        try {
            notificationDataChannel.trySend(data.copyOf()).getOrThrow()
        } catch (_: ClosedSendChannelException) {
            Timber.d("Notification received after processing queue closed for device: $deviceId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to enqueue notification data for device: $deviceId")
        }
    }

    override fun close() {
        notificationDataChannel.close()
        processingJob?.cancel()
        processingJob = null
    }
}
