package mn.intermed.kiosk.ui.diagnostics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrinterSection(state: DiagnosticsState, viewModel: DiagnosticsViewModel) {
    val idle = !state.printerBusy
    val ready = idle && state.printerConnected

    SectionCard(title = "Принтер") {
        Text(
            text = if (state.printerConnected) "Холбогдсон" else "Холбогдоогүй",
            style = MaterialTheme.typography.titleMedium,
            color = if (state.printerConnected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
        )
        Text(text = state.printerMessage, style = MaterialTheme.typography.bodyLarge)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = { viewModel.connectUsbAuto() }, enabled = idle) {
                Text("USB-ээр холбох")
            }
            OutlinedButton(onClick = { viewModel.disconnect() }, enabled = ready) {
                Text("Салгах")
            }
        }

        HorizontalDivider()
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = { viewModel.readStatus() }, enabled = ready) {
                Text("Төлөв унших")
            }
            Button(onClick = { viewModel.printTestReceipt() }, enabled = ready) {
                Text("Туршилтын баримт хэвлэх")
            }
            OutlinedButton(onClick = { viewModel.printCyrillicText() }, enabled = ready) {
                Text("Кирилл текст шууд хэвлэх")
            }
        }
    }
}
