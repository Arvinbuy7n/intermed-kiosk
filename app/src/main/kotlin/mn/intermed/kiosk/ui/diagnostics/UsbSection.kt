package mn.intermed.kiosk.ui.diagnostics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UsbSection(state: DiagnosticsState, viewModel: DiagnosticsViewModel) {
    SectionCard(title = "USB төхөөрөмжүүд") {
        OutlinedButton(onClick = { viewModel.refreshDevices() }) {
            Text("Шинэчлэх")
        }
        if (state.usbDevices.isEmpty()) {
            Text(text = "USB төхөөрөмж олдсонгүй", style = MaterialTheme.typography.bodyMedium)
        }
        state.usbDevices.forEach { info ->
            val permission = if (info.hasPermission) "зөвшөөрөлтэй" else "зөвшөөрөлгүй"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = info.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${info.kind} · VID ${info.vendorHex} · PID ${info.productHex} · $permission",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                OutlinedButton(
                    onClick = { viewModel.connectUsb(info) },
                    enabled = !state.printerBusy,
                ) {
                    Text("Принтер болгож холбох")
                }
            }
        }
    }
}
