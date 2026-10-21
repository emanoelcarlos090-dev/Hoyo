package com.example.model

enum class AppId(val key: String) {
    CALCULATOR("calculator"),
    NOTEPAD("notepad")
}

enum class AppInstallState {
    NOT_INSTALLED,
    INSTALLED_V1,
    INSTALLED_V2
}

data class LauncherAppInfo(
    val id: AppId,
    val name: String,
    val codeName: String,
    val subtitle: String,
    val description: String,
    val category: String,
    val baseSizeMb: Double,
    val patchSizeMb: Double,
    val v1VersionName: String,
    val v2VersionName: String,
    val v1Limitations: List<String>,
    val v2Changelog: List<String>,
    val bannerTag: String
)

data class NoteItem(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val content: String,
    val colorIndex: Int = 0,
    val isPinned: Boolean = false,
    val timestamp: String
)

data class CalculationHistoryItem(
    val id: String = System.currentTimeMillis().toString(),
    val expression: String,
    val result: String,
    val timestamp: String
)

data class DownloadProgressState(
    val isProcessing: Boolean = false,
    val isUpdate: Boolean = false,
    val progress: Float = 0f,
    val statusText: String = "",
    val speedText: String = "",
    val appId: AppId? = null
)
