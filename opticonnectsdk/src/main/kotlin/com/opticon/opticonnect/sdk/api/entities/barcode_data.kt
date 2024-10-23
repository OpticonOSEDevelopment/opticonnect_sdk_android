package com.opticon.opticonnect.sdk.api.entities

/**
 * This class holds detailed information about a scanned barcode, including:
 * - The processed barcode data ([data]), which may have undergone transformations
 *   based on scanner settings (e.g., preamble, prefix, suffix, postamble).
 * - The raw byte representation ([dataBytes]) of the processed barcode data.
 * - The quantity of the scanned item ([quantity]), which can be negative to indicate removal.
 * - Information about the barcode symbology ([symbology] and [symbologyId]).
 * - The time of the scan ([timeOfScan]).
 * - The ID of the scanning device ([deviceId]).
 */
data class BarcodeData(
    /**
     * The processed barcode data retrieved from the scan.
     *
     * The barcode data may have undergone transformations or additions such as preamble,
     * prefix, suffix, or postamble based on scanner settings. Therefore, this is not necessarily
     * the raw data directly from the barcode, but rather the processed output.
     *
     * This data is decoded from the raw bytes using UTF-8 encoding.
     */
    val data: String,

    /**
     * The byte representation of the processed barcode data retrieved from the scan.
     *
     * Like the `data` field, this data may have undergone transformations or additions
     * such as preamble, prefix, suffix, or postamble based on scanner settings. This field
     * provides the byte-level representation (e.g., UTF-8 or Unicode) of the processed data.
     */
    val dataBytes: ByteArray,

    /**
     * The quantity of the scanned item.
     *
     * This represents the number of items scanned. A quantity of -1 signifies the removal of
     * a scanned item.
     */
    val quantity: Int,

    /**
     * The ID of the barcode symbology used for the scan.
     *
     * This is a numeric identifier for the barcode symbology (e.g., Code 39, QR Code),
     * allowing applications to identify the type of barcode scanned.
     */
    val symbologyId: Int,

    /**
     * The name of the barcode symbology used for the scan.
     *
     * This is a human-readable description of the symbology (e.g., "Code 39", "QR Code")
     * that was used to encode the barcode.
     */
    val symbology: String,

    /**
     * The time the barcode was scanned.
     *
     * This timestamp indicates when the scan occurred and can be useful for logging
     * or real-time tracking of scans.
     */
    val timeOfScan: String,

    /**
     * The device ID of the scanner that scanned the barcode.
     */
    val deviceId: String
)