package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppId
import com.example.model.AppInstallState
import com.example.model.CalculationHistoryItem
import com.example.model.DownloadProgressState
import com.example.model.LauncherAppInfo
import com.example.model.NoteItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LauncherRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("hoyo_launcher_prefs", Context.MODE_PRIVATE)

    val availableApps: List<LauncherAppInfo> = listOf(
        LauncherAppInfo(
            id = AppId.CALCULATOR,
            name = "Calculadora Neo",
            codeName = "NEO-CALC v2",
            subtitle = "Aritmética e Precisão Quântica",
            description = "Uma ferramenta de cálculo futurista com interface holográfica. Na v1.0 básica, ela realiza apenas operações essenciais. Atualize para a v2.0 para desbloquear multiplicação, divisão, casas decimais e histórico completo!",
            category = "Ferramentas",
            baseSizeMb = 18.4,
            patchSizeMb = 7.2,
            v1VersionName = "v1.0.0 (Básica)",
            v2VersionName = "v2.0.0 (Completa)",
            bannerTag = "NOVA ATUALIZAÇÃO DISPONÍVEL",
            v1Limitations = listOf(
                "Sem multiplicação: Operação (×) bloqueada",
                "Sem divisão: Operação (÷) bloqueada",
                "Sem decimais: Apenas números inteiros",
                "Sem cálculo de porcentagem (%)",
                "Sem histórico de operações recentes",
                "Sem botão de apagar dígito (Backspace)"
            ),
            v2Changelog = listOf(
                "★ [NOVO] Multiplicação (×) desbloqueada para todas as contas!",
                "★ [NOVO] Divisão precisa (÷) com proteção contra divisão por zero",
                "★ [NOVO] Suporte total a ponto e casas decimais (.)",
                "★ [NOVO] Função de porcentagem (%) e inversão de sinal (±)",
                "★ [NOVO] Botão de apagar último dígito (Backspace ⌫)",
                "★ [NOVO] Painel com histórico completo dos cálculos realizados",
                "★ [VISUAL] Efeitos luminosos e feedback háptico futurista"
            )
        ),
        LauncherAppInfo(
            id = AppId.NOTEPAD,
            name = "Bloco de Notas Quântico",
            codeName = "CHRONO-NOTE v2",
            subtitle = "Registro de Pensamentos e Dados",
            description = "Seu caderno de registros neural. Na versão v1.0, o teclado é propositalmente restrito: apenas letras de 'A' a 'M' são aceitas e nenhum número é permitido. Atualize no launcher para desbloquear todas as letras, números e cores!",
            category = "Produtividade",
            baseSizeMb = 14.1,
            patchSizeMb = 8.6,
            v1VersionName = "v1.0.0 (Restrita)",
            v2VersionName = "v2.0.0 (Completa)",
            bannerTag = "EXPANSÃO DE CARACTERES",
            v1Limitations = listOf(
                "Teclado restrito: Apenas letras de 'A' até 'M' (sem N-Z)",
                "NÚMEROS PROIBIDOS: Bloqueio total dos dígitos 0 a 9",
                "Limite máximo rígido de 50 caracteres por nota",
                "Sem opções de cores (apenas tema monocromático)",
                "Sem sistema de busca ou filtro de notas",
                "Sem opção de fixar anotações no topo"
            ),
            v2Changelog = listOf(
                "★ [LIBERAÇÃO] Teclado irrestrito: Todas as letras (A-Z, a-z) desbloqueadas!",
                "★ [LIBERAÇÃO] Números liberados: Digite 0, 1, 2, 3, 4, 5, 6, 7, 8 e 9 à vontade!",
                "★ [NOVO] Suporte a pontuação, acentuação e emojis modernos",
                "★ [NOVO] 5 Temas de cores radiantes (Ciano, Esmeralda, Âmbar, Rosa, Violeta)",
                "★ [NOVO] Barra de busca instantânea em tempo real",
                "★ [NOVO] Fixar anotações prioritárias no topo",
                "★ [NOVO] Copiar texto da anotação para área de transferência",
                "★ [EXPANSÃO] Limite de 50 caracteres removido (notas ilimitadas)"
            )
        )
    )

    private val _appStates = MutableStateFlow<Map<AppId, AppInstallState>>(loadAppStates())
    val appStates: StateFlow<Map<AppId, AppInstallState>> = _appStates.asStateFlow()

    private val _downloadProgress = MutableStateFlow(DownloadProgressState())
    val downloadProgress: StateFlow<DownloadProgressState> = _downloadProgress.asStateFlow()

    private val _notes = MutableStateFlow<List<NoteItem>>(loadNotes())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private val _calcHistory = MutableStateFlow<List<CalculationHistoryItem>>(loadCalcHistory())
    val calcHistory: StateFlow<List<CalculationHistoryItem>> = _calcHistory.asStateFlow()

    private fun loadAppStates(): Map<AppId, AppInstallState> {
        val calcState = when (prefs.getString("state_calc", AppInstallState.NOT_INSTALLED.name)) {
            AppInstallState.INSTALLED_V1.name -> AppInstallState.INSTALLED_V1
            AppInstallState.INSTALLED_V2.name -> AppInstallState.INSTALLED_V2
            else -> AppInstallState.NOT_INSTALLED
        }
        val noteState = when (prefs.getString("state_note", AppInstallState.NOT_INSTALLED.name)) {
            AppInstallState.INSTALLED_V1.name -> AppInstallState.INSTALLED_V1
            AppInstallState.INSTALLED_V2.name -> AppInstallState.INSTALLED_V2
            else -> AppInstallState.NOT_INSTALLED
        }
        return mapOf(
            AppId.CALCULATOR to calcState,
            AppId.NOTEPAD to noteState
        )
    }

    private fun saveAppState(appId: AppId, state: AppInstallState) {
        val key = if (appId == AppId.CALCULATOR) "state_calc" else "state_note"
        prefs.edit().putString(key, state.name).apply()
        val current = _appStates.value.toMutableMap()
        current[appId] = state
        _appStates.value = current
    }

    suspend fun downloadOrInstall(appId: AppId) {
        val appInfo = availableApps.first { it.id == appId }
        _downloadProgress.value = DownloadProgressState(
            isProcessing = true,
            isUpdate = false,
            progress = 0f,
            statusText = "Conectando aos servidores MiHoYo...",
            speedText = "0 KB/s",
            appId = appId
        )

        val totalMb = appInfo.baseSizeMb
        for (step in 1..20) {
            delay(120)
            val currentProgress = step / 20f
            val downloaded = String.format(Locale.US, "%.1f", totalMb * currentProgress)
            val speed = String.format(Locale.US, "%.1f", 4.5 + (step % 4) * 0.8)
            val status = when {
                step < 6 -> "Baixando pacote base (${downloaded}/${totalMb} MB)..."
                step < 14 -> "Descompactando recursos do aplicativo..."
                step < 18 -> "Verificando integridade dos módulos SHA-256..."
                else -> "Finalizando instalação da versão 1.0..."
            }
            _downloadProgress.value = DownloadProgressState(
                isProcessing = true,
                isUpdate = false,
                progress = currentProgress,
                statusText = status,
                speedText = "$speed MB/s",
                appId = appId
            )
        }
        delay(200)
        saveAppState(appId, AppInstallState.INSTALLED_V1)
        _downloadProgress.value = DownloadProgressState()
    }

    suspend fun updateApp(appId: AppId) {
        val appInfo = availableApps.first { it.id == appId }
        _downloadProgress.value = DownloadProgressState(
            isProcessing = true,
            isUpdate = true,
            progress = 0f,
            statusText = "Localizando pacote delta v2.0...",
            speedText = "0 KB/s",
            appId = appId
        )

        val patchMb = appInfo.patchSizeMb
        for (step in 1..20) {
            delay(130)
            val currentProgress = step / 20f
            val downloaded = String.format(Locale.US, "%.1f", patchMb * currentProgress)
            val speed = String.format(Locale.US, "%.1f", 5.2 + (step % 5) * 0.6)
            val status = when {
                step < 7 -> "Baixando patch de atualização (${downloaded}/${patchMb} MB)..."
                step < 13 -> "Substituindo binários e compilando novos módulos..."
                step < 18 -> "Desbloqueando novos recursos da versão 2.0..."
                else -> "Otimizando cache do sistema..."
            }
            _downloadProgress.value = DownloadProgressState(
                isProcessing = true,
                isUpdate = true,
                progress = currentProgress,
                statusText = status,
                speedText = "$speed MB/s",
                appId = appId
            )
        }
        delay(250)
        saveAppState(appId, AppInstallState.INSTALLED_V2)
        _downloadProgress.value = DownloadProgressState()
    }

    fun revertToV1(appId: AppId) {
        saveAppState(appId, AppInstallState.INSTALLED_V1)
    }

    fun uninstallApp(appId: AppId) {
        saveAppState(appId, AppInstallState.NOT_INSTALLED)
    }

    fun resetAllApps() {
        saveAppState(AppId.CALCULATOR, AppInstallState.NOT_INSTALLED)
        saveAppState(AppId.NOTEPAD, AppInstallState.NOT_INSTALLED)
    }

    // Notes Data Management
    private fun loadNotes(): List<NoteItem> {
        val raw = prefs.getString("user_notes", null)
        if (raw.isNullOrEmpty()) {
            return listOf(
                NoteItem(
                    id = "initial_1",
                    title = "BEM VINDO",
                    content = "ESSE E O BLOCO DE NOTAS. NA V1 SO ACEITA LETRAS A ATE M.",
                    colorIndex = 0,
                    isPinned = true,
                    timestamp = "Hoje 10:00"
                )
            )
        }
        val list = mutableListOf<NoteItem>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    NoteItem(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        title = obj.optString("title", ""),
                        content = obj.optString("content", ""),
                        colorIndex = obj.optInt("colorIndex", 0),
                        isPinned = obj.optBoolean("isPinned", false),
                        timestamp = obj.optString("timestamp", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun persistNotes(list: List<NoteItem>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("content", item.content)
                put("colorIndex", item.colorIndex)
                put("isPinned", item.isPinned)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("user_notes", array.toString()).apply()
        _notes.value = list
    }

    fun addNote(title: String, content: String, colorIndex: Int) {
        val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        val newItem = NoteItem(
            id = System.currentTimeMillis().toString(),
            title = title,
            content = content,
            colorIndex = colorIndex,
            isPinned = false,
            timestamp = sdf.format(Date())
        )
        val updated = listOf(newItem) + _notes.value
        persistNotes(updated)
    }

    fun deleteNote(id: String) {
        val updated = _notes.value.filter { it.id != id }
        persistNotes(updated)
    }

    fun togglePinNote(id: String) {
        val updated = _notes.value.map {
            if (it.id == id) it.copy(isPinned = !it.isPinned) else it
        }
        persistNotes(updated)
    }

    // Calculator History Management
    private fun loadCalcHistory(): List<CalculationHistoryItem> {
        val raw = prefs.getString("calc_history", null) ?: return emptyList()
        val list = mutableListOf<CalculationHistoryItem>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CalculationHistoryItem(
                        id = obj.optString("id", "$i"),
                        expression = obj.optString("expression", ""),
                        result = obj.optString("result", ""),
                        timestamp = obj.optString("timestamp", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun persistCalcHistory(list: List<CalculationHistoryItem>) {
        val array = JSONArray()
        for (item in list.take(30)) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("expression", item.expression)
                put("result", item.result)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("calc_history", array.toString()).apply()
        _calcHistory.value = list
    }

    fun addCalculation(expression: String, result: String) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val newItem = CalculationHistoryItem(
            id = System.currentTimeMillis().toString(),
            expression = expression,
            result = result,
            timestamp = sdf.format(Date())
        )
        val updated = listOf(newItem) + _calcHistory.value
        persistCalcHistory(updated)
    }

    fun clearCalcHistory() {
        persistCalcHistory(emptyList())
    }
}
