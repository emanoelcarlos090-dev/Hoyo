package com.example.ui.launcher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppId
import com.example.model.AppInstallState
import com.example.model.LauncherAppInfo
import com.example.ui.components.HoyoDownloadProgressCard
import com.example.ui.components.HoyoGlassCard
import com.example.ui.components.HoyoPrimaryButton
import com.example.ui.components.HoyoSecondaryButton
import com.example.ui.components.HoyoStatusBadge
import com.example.ui.theme.HoyoCardBorder
import com.example.ui.theme.HoyoCardDark
import com.example.ui.theme.HoyoCardHover
import com.example.ui.theme.HoyoCyan
import com.example.ui.theme.HoyoCyanBright
import com.example.ui.theme.HoyoDeepDark
import com.example.ui.theme.HoyoGold
import com.example.ui.theme.HoyoGoldBright
import com.example.ui.theme.HoyoGreen
import com.example.ui.theme.HoyoRed
import com.example.ui.theme.HoyoSurfaceDark
import com.example.ui.theme.HoyoTextMuted
import com.example.ui.theme.HoyoTextPrimary
import com.example.ui.theme.HoyoTextSecondary
import com.example.viewmodel.LauncherViewModel

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val selectedAppId by viewModel.selectedAppId.collectAsState()
    val appStates by viewModel.appStates.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val availableApps = viewModel.availableApps

    val currentAppInfo = availableApps.first { it.id == selectedAppId }
    val currentInstallState = appStates[selectedAppId] ?: AppInstallState.NOT_INSTALLED

    var showResetDialog by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var selectedNotesTab by remember { mutableIntStateOf(0) } // 0: Changelog v2.0, 1: Limitações v1.0

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HoyoDeepDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // miHoYo top brand header
            LauncherTopBar(
                onResetClick = { showResetDialog = true }
            )

            // Game / App Switcher Tabs (Inspired by HoYoPlay PC Launcher)
            AppSwitcherBar(
                apps = availableApps,
                selectedAppId = selectedAppId,
                appStates = appStates,
                onSelectApp = { viewModel.selectApp(it) }
            )

            // Main scrollable content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Banner with Graphic & Title
                item {
                    HeroBannerSection(
                        appInfo = currentAppInfo,
                        installState = currentInstallState
                    )
                }

                // Resumo da Atualização (miHoYo Notice Board / Patch Notes)
                item {
                    UpdateChangelogCard(
                        appInfo = currentAppInfo,
                        installState = currentInstallState,
                        selectedTab = selectedNotesTab,
                        onTabChange = { selectedNotesTab = it }
                    )
                }

                // Tech Specs & Package Details
                item {
                    TechnicalInfoCard(
                        appInfo = currentAppInfo,
                        installState = currentInstallState
                    )
                }

                // Extra padding for bottom dock
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Floating Action Dock at the bottom (miHoYo launcher signature button)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            HoyoDeepDark.copy(alpha = 0.95f),
                            HoyoDeepDark
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // In-progress download bar
            HoyoDownloadProgressCard(
                downloadProgress = downloadProgress,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (!downloadProgress.isProcessing) {
                ActionDock(
                    installState = currentInstallState,
                    appInfo = currentAppInfo,
                    onDownload = { viewModel.startDownloadOrInstall(selectedAppId) },
                    onUpdate = { viewModel.startUpdate(selectedAppId) },
                    onLaunch = { viewModel.launchApp(selectedAppId) },
                    onOptionsClick = { showOptionsMenu = true }
                )
            }
        }

        // Quick Options Dropdown (Revert / Uninstall)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        ) {
            DropdownMenu(
                expanded = showOptionsMenu,
                onDismissRequest = { showOptionsMenu = false },
                modifier = Modifier.background(HoyoCardDark)
            ) {
                if (currentInstallState == AppInstallState.INSTALLED_V2) {
                    DropdownMenuItem(
                        text = {
                            Text("Reverter para v1.0 (Testar de novo)", color = HoyoGold)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = HoyoGold)
                        },
                        onClick = {
                            showOptionsMenu = false
                            viewModel.revertToV1(selectedAppId)
                        }
                    )
                }
                if (currentInstallState != AppInstallState.NOT_INSTALLED) {
                    DropdownMenuItem(
                        text = {
                            Text("Desinstalar aplicativo", color = HoyoRed)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = HoyoRed)
                        },
                        onClick = {
                            showOptionsMenu = false
                            viewModel.uninstallApp(selectedAppId)
                        }
                    )
                }
            }
        }
    }

    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text("Redefinir Launcher", color = HoyoTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Deseja resetar todos os aplicativos para o estado 'Não Baixado' para simular todo o fluxo novamente do zero?",
                    color = HoyoTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetAll()
                    }
                ) {
                    Text("Redefinir", color = HoyoRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar", color = HoyoTextSecondary)
                }
            },
            containerColor = HoyoCardDark,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun LauncherTopBar(
    onResetClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HoyoSurfaceDark)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CutCornerShape(6.dp)),
                color = HoyoGold
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "miHoYo logo",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "HOYOPLAY",
                        color = HoyoTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HUB",
                        color = HoyoGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = "DISTRIBUIÇÃO DE APPS v3.4",
                    color = HoyoTextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            HoyoStatusBadge(
                text = "SERVIDOR ONLINE",
                statusColor = HoyoGreen,
                modifier = Modifier.padding(end = 4.dp)
            )

            IconButton(
                onClick = onResetClick,
                modifier = Modifier.testTag("reset_launcher_button")
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Resetar",
                    tint = HoyoTextSecondary
                )
            }
        }
    }
}

@Composable
fun AppSwitcherBar(
    apps: List<LauncherAppInfo>,
    selectedAppId: AppId,
    appStates: Map<AppId, AppInstallState>,
    onSelectApp: (AppId) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = HoyoDeepDark
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            apps.forEach { app ->
                val isSelected = app.id == selectedAppId
                val state = appStates[app.id] ?: AppInstallState.NOT_INSTALLED

                val statusLabel = when (state) {
                    AppInstallState.NOT_INSTALLED -> "Não Instalado"
                    AppInstallState.INSTALLED_V1 -> "v1.0 (Update!)"
                    AppInstallState.INSTALLED_V2 -> "v2.0 (Atualizado)"
                }
                val statusColor = when (state) {
                    AppInstallState.NOT_INSTALLED -> HoyoTextMuted
                    AppInstallState.INSTALLED_V1 -> HoyoGold
                    AppInstallState.INSTALLED_V2 -> HoyoCyan
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                        .border(
                            BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) HoyoGold else HoyoCardBorder
                            ),
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                        )
                        .clickable { onSelectApp(app.id) }
                        .testTag("app_tab_${app.id.key}"),
                    color = if (isSelected) HoyoCardHover else HoyoCardDark.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (app.id == AppId.CALCULATOR) Icons.Default.Calculate else Icons.Default.EditNote,
                            contentDescription = null,
                            tint = if (isSelected) HoyoGold else HoyoTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = app.name,
                                color = if (isSelected) HoyoTextPrimary else HoyoTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroBannerSection(
    appInfo: LauncherAppInfo,
    installState: AppInstallState
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(BorderStroke(1.dp, HoyoCardBorder), shape = RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp)),
        color = HoyoCardDark
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.img_launcher_hero),
                contentDescription = "Banner de jogo/app",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                HoyoDeepDark.copy(alpha = 0.4f),
                                HoyoDeepDark.copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HoyoStatusBadge(
                        text = appInfo.category.uppercase(),
                        statusColor = HoyoCyan
                    )

                    when (installState) {
                        AppInstallState.NOT_INSTALLED -> {
                            HoyoStatusBadge(
                                text = "NÃO INSTALADO",
                                statusColor = HoyoTextMuted
                            )
                        }
                        AppInstallState.INSTALLED_V1 -> {
                            HoyoStatusBadge(
                                text = "NOVO PATCH DISPONÍVEL!",
                                statusColor = HoyoGold,
                                showGlow = true
                            )
                        }
                        AppInstallState.INSTALLED_V2 -> {
                            HoyoStatusBadge(
                                text = "VERSÃO MAIS RECENTE",
                                statusColor = HoyoGreen
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = appInfo.name,
                        color = HoyoTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = appInfo.subtitle,
                        color = HoyoCyanBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = appInfo.description,
                        color = HoyoTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun UpdateChangelogCard(
    appInfo: LauncherAppInfo,
    installState: AppInstallState,
    selectedTab: Int,
    onTabChange: (Int) -> Unit
) {
    HoyoGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (installState == AppInstallState.INSTALLED_V1) HoyoGold.copy(alpha = 0.6f) else HoyoCardBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = HoyoGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RESUMO DA ATUALIZAÇÃO",
                        color = HoyoGoldBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "v2.0 vs v1.0",
                    color = HoyoCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub tabs to compare v2.0 news with v1.0 limitations
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = HoyoCardHover,
                contentColor = HoyoGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = if (selectedTab == 0) HoyoGold else HoyoCyan
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .height(38.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { onTabChange(0) },
                    text = {
                        Text(
                            "★ O que adicionou na v2.0",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) HoyoGold else HoyoTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { onTabChange(1) },
                    text = {
                        Text(
                            "⚠️ Como era na v1.0 básica",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) HoyoCyan else HoyoTextSecondary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // v2.0 Changelog items
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Novas funções desbloqueadas após atualizar via Launcher:",
                        color = HoyoTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    appInfo.v2Changelog.forEach { change ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(HoyoCardDark.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "•",
                                color = HoyoGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = change,
                                color = HoyoTextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            } else {
                // v1.0 Limitations items
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Restrições ativas na versão 1.0 (antes do update):",
                        color = HoyoRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    appInfo.v1Limitations.forEach { limitation ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(HoyoRed.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                .border(BorderStroke(0.5.dp, HoyoRed.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "✕",
                                color = HoyoRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = limitation,
                                color = HoyoTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TechnicalInfoCard(
    appInfo: LauncherAppInfo,
    installState: AppInstallState
) {
    HoyoGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DADOS DE PACOTE",
                    color = HoyoTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "ID: ${appInfo.codeName}",
                    color = HoyoCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Tamanho Base", color = HoyoTextSecondary, fontSize = 11.sp)
                    Text(
                        text = "${appInfo.baseSizeMb} MB",
                        color = HoyoTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(text = "Patch v2.0", color = HoyoTextSecondary, fontSize = 11.sp)
                    Text(
                        text = "${appInfo.patchSizeMb} MB",
                        color = HoyoGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(text = "Estado Local", color = HoyoTextSecondary, fontSize = 11.sp)
                    Text(
                        text = when (installState) {
                            AppInstallState.NOT_INSTALLED -> "Não Baixado"
                            AppInstallState.INSTALLED_V1 -> appInfo.v1VersionName
                            AppInstallState.INSTALLED_V2 -> appInfo.v2VersionName
                        },
                        color = when (installState) {
                            AppInstallState.NOT_INSTALLED -> HoyoTextMuted
                            AppInstallState.INSTALLED_V1 -> HoyoGold
                            AppInstallState.INSTALLED_V2 -> HoyoGreen
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ActionDock(
    installState: AppInstallState,
    appInfo: LauncherAppInfo,
    onDownload: () -> Unit,
    onUpdate: () -> Unit,
    onLaunch: () -> Unit,
    onOptionsClick: () -> Unit
) {
    when (installState) {
        AppInstallState.NOT_INSTALLED -> {
            HoyoPrimaryButton(
                text = "BAIXAR E INSTALAR",
                subtext = "Tamanho total: ${appInfo.baseSizeMb} MB • Instala v1.0",
                icon = Icons.Default.Download,
                onClick = onDownload,
                isHighlighted = false,
                modifier = Modifier.fillMaxWidth(),
                testTag = "download_app_button"
            )
        }
        AppInstallState.INSTALLED_V1 -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Secondary button to test the limited v1.0
                HoyoSecondaryButton(
                    text = "ABRIR v1.0",
                    subtext = "Testar versão básica",
                    icon = Icons.Default.PlayArrow,
                    onClick = onLaunch,
                    modifier = Modifier.weight(1f),
                    testTag = "launch_v1_button"
                )

                // Primary glowing button to update to v2.0
                HoyoPrimaryButton(
                    text = "ATUALIZAR (v2.0)",
                    subtext = "Patch: ${appInfo.patchSizeMb} MB • Novas funções",
                    icon = Icons.Default.SystemUpdate,
                    onClick = onUpdate,
                    isHighlighted = true,
                    modifier = Modifier.weight(1.3f),
                    testTag = "update_to_v2_button"
                )
            }
        }
        AppInstallState.INSTALLED_V2 -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HoyoPrimaryButton(
                    text = "INICIAR APLICATIVO",
                    subtext = "Versão v2.0 Completa Instalada",
                    icon = Icons.Default.PlayArrow,
                    onClick = onLaunch,
                    isHighlighted = false,
                    modifier = Modifier.weight(1f),
                    testTag = "launch_v2_button"
                )

                IconButton(
                    onClick = onOptionsClick,
                    modifier = Modifier
                        .size(56.dp)
                        .background(HoyoCardDark, CutCornerShape(8.dp))
                        .border(BorderStroke(1.dp, HoyoCardBorder), CutCornerShape(8.dp))
                        .testTag("app_options_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opções",
                        tint = HoyoTextSecondary
                    )
                }
            }
        }
    }
}
