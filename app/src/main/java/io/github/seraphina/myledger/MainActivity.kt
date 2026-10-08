package io.github.seraphina.myledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import io.github.seraphina.myledger.ui.LedgerApp
import io.github.seraphina.myledger.ui.theme.MyLedgerTheme
import io.github.seraphina.myledger.ui.viewmodel.LedgerViewModel

class MainActivity : ComponentActivity() {
    private val ledgerViewModel: LedgerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyLedgerTheme {
                LedgerApp(viewModel = ledgerViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ledgerViewModel.refreshSystemState()
    }
}
