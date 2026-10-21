package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LauncherRepository
import com.example.model.AppId
import com.example.model.AppInstallState
import com.example.model.CalculationHistoryItem
import com.example.model.DownloadProgressState
import com.example.model.LauncherAppInfo
import com.example.model.NoteItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Launcher : ScreenDestination()
    data class CalculatorApp(val state: AppInstallState) : ScreenDestination()
    data class NotepadApp(val state: AppInstallState) : ScreenDestination()
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LauncherRepository(application.applicationContext)

    val availableApps: List<LauncherAppInfo> = repository.availableApps

    val appStates: StateFlow<Map<AppId, AppInstallState>> = repository.appStates

    val downloadProgress: StateFlow<DownloadProgressState> = repository.downloadProgress

    val notes: StateFlow<List<NoteItem>> = repository.notes

    val calcHistory: StateFlow<List<CalculationHistoryItem>> = repository.calcHistory

    private val _selectedAppId = MutableStateFlow(AppId.CALCULATOR)
    val selectedAppId: StateFlow<AppId> = _selectedAppId.asStateFlow()

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Launcher)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage.asStateFlow()

    fun selectApp(appId: AppId) {
        _selectedAppId.value = appId
    }

    fun startDownloadOrInstall(appId: AppId) {
        viewModelScope.launch {
            repository.downloadOrInstall(appId)
        }
    }

    fun startUpdate(appId: AppId) {
        viewModelScope.launch {
            repository.updateApp(appId)
        }
    }

    fun launchApp(appId: AppId) {
        val state = appStates.value[appId] ?: AppInstallState.NOT_INSTALLED
        if (state == AppInstallState.NOT_INSTALLED) {
            _infoMessage.value = "O aplicativo precisa ser baixado primeiro no Launcher!"
            return
        }
        when (appId) {
            AppId.CALCULATOR -> _currentScreen.value = ScreenDestination.CalculatorApp(state)
            AppId.NOTEPAD -> _currentScreen.value = ScreenDestination.NotepadApp(state)
        }
    }

    fun navigateBackToLauncher() {
        _currentScreen.value = ScreenDestination.Launcher
    }

    fun revertToV1(appId: AppId) {
        repository.revertToV1(appId)
        _infoMessage.value = "${appId.name}: Revertido para v1.0 básica para novos testes."
    }

    fun uninstallApp(appId: AppId) {
        repository.uninstallApp(appId)
        _infoMessage.value = "Aplicativo desinstalado do Launcher."
    }

    fun resetAll() {
        repository.resetAllApps()
        _infoMessage.value = "Todos os aplicativos foram redefinidos para o estado inicial!"
    }

    fun clearInfoMessage() {
        _infoMessage.value = null
    }

    // Notepad Actions
    fun addNote(title: String, content: String, colorIndex: Int) {
        repository.addNote(title, content, colorIndex)
    }

    fun deleteNote(id: String) {
        repository.deleteNote(id)
    }

    fun togglePinNote(id: String) {
        repository.togglePinNote(id)
    }

    // Calculator Actions
    fun saveCalculation(expression: String, result: String) {
        repository.addCalculation(expression, result)
    }

    fun clearCalcHistory() {
        repository.clearCalcHistory()
    }
}
