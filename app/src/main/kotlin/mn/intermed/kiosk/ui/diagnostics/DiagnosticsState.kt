package mn.intermed.kiosk.ui.diagnostics

import mn.intermed.kiosk.hardware.usb.UsbDeviceInfo

data class DiagnosticsState(
    val usbDevices: List<UsbDeviceInfo> = emptyList(),
    val printerConnected: Boolean = false,
    val printerBusy: Boolean = false,
    val printerMessage: String = "Принтер холбогдоогүй",
    val lastScan: String? = null,
    val scanCount: Int = 0,
)
