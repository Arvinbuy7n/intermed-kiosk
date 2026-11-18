package mn.intermed.kiosk.ui.diagnostics

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ScannerSection(state: DiagnosticsState) {
    SectionCard(title = "Сканнер") {
        val lastScan = state.lastScan
        if (lastScan == null) {
            Text(
                text = "QR эсвэл баркод уншуулна уу",
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            Text(
                text = "Уншсан тоо: ${state.scanCount} · Урт: ${lastScan.length} тэмдэгт",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(text = lastScan, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
