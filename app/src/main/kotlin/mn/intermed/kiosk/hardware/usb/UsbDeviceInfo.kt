package mn.intermed.kiosk.hardware.usb

import android.hardware.usb.UsbDevice

data class UsbDeviceInfo(
    val device: UsbDevice,
    val name: String,
    val kind: String,
    val hasPermission: Boolean,
) {
    val vendorHex: String
        get() = "%04X".format(device.vendorId)

    val productHex: String
        get() = "%04X".format(device.productId)
}
