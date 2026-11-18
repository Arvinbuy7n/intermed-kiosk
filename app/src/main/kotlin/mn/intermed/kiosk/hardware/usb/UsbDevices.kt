package mn.intermed.kiosk.hardware.usb

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build

class UsbDevices(private val context: Context) {

    private val manager: UsbManager? = context.getSystemService(UsbManager::class.java)

    fun list(): List<UsbDeviceInfo> {
        val usbManager = manager ?: return emptyList()
        return usbManager.deviceList.values
            .map { device ->
                UsbDeviceInfo(
                    device = device,
                    name = device.productName ?: device.deviceName,
                    kind = kindOf(device),
                    hasPermission = usbManager.hasPermission(device),
                )
            }
            .sortedBy { it.name }
    }

    fun requestPermission(device: UsbDevice) {
        val intent = Intent(ACTION_USB_PERMISSION).setPackage(context.packageName)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        manager?.requestPermission(device, PendingIntent.getBroadcast(context, 0, intent, flags))
    }

    private fun kindOf(device: UsbDevice): String {
        val classes = (0 until device.interfaceCount).map { device.getInterface(it).interfaceClass }.toSet()
        return when {
            UsbConstants.USB_CLASS_PRINTER in classes -> "Принтер"
            UsbConstants.USB_CLASS_HID in classes -> "HID (сканнер, гар)"
            UsbConstants.USB_CLASS_COMM in classes || UsbConstants.USB_CLASS_CDC_DATA in classes -> "Serial (CDC)"
            UsbConstants.USB_CLASS_MASS_STORAGE in classes -> "Флаш"
            UsbConstants.USB_CLASS_HUB in classes -> "Hub"
            UsbConstants.USB_CLASS_VENDOR_SPEC in classes -> "Vendor specific"
            else -> "Бусад"
        }
    }

    private companion object {
        const val ACTION_USB_PERMISSION = "mn.intermed.kiosk.USB_PERMISSION"
    }
}
