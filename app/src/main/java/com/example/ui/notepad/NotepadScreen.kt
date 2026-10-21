package com.example.ui.notepad

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInstallState
import com.example.model.NoteItem
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
import com.example.ui.theme.NoteColorAmber
import com.example.ui.theme.NoteColorCyan
import com.example.ui.theme.NoteColorEmerald
import com.example.ui.theme.NoteColorRose
import com.example.ui.theme.NoteColorViolet
import com.example.viewmodel.LauncherViewModel

@Composable
fun NotepadScreen(
    viewModel: LauncherViewModel,
    installState: AppInstallState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBackToLauncher()
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isV2 = installState == AppInstallState.INSTALLED_V2
    val notes by viewModel.notes.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showNewNoteDialog by remember { mutableStateOf(false) }
    var lockedDialogMessage by remember { mutableStateOf<String?>(null) }
    var blockedCharWarning by remember { mutableStateOf<String?>(null) }

    val noteColorPalette = listOf(
        NoteColorCyan to HoyoCyan,
        NoteColorEmerald to HoyoGreen,
        NoteColorAmber to HoyoGold,
        NoteColorRose to HoyoRed,
        NoteColorViolet to Color(0xFFCE93D8)
    )

    val filteredNotes = notes
        .filter {
            if (searchQuery.isBlank() || !isV2) true
            else it.title.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true)
        }
        .sortedByDescending { if (isV2) it.isPinned else false }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HoyoDeepDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(HoyoSurfaceDark)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateBackToLauncher() },
                    modifier = Modifier.testTag("back_to_launcher_notepad_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar ao Launcher",
                        tint = HoyoCyan
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Bloco de Notas",
                        color = HoyoTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isV2) "v2.0 (Teclado Completo)" else "v1.0 (Teclado Restrito)",
                        color = if (isV2) HoyoGreen else HoyoGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HoyoStatusBadge(
                text = if (isV2) "v2.0 ILIMITADA" else "v1.0 RESTRITA",
                statusColor = if (isV2) HoyoCyan else HoyoGold
            )
        }

        // Restriction/Feature Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(
                    BorderStroke(1.dp, if (isV2) HoyoCyan.copy(alpha = 0.5f) else HoyoGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ),
            color = if (isV2) HoyoCyan.copy(alpha = 0.1f) else HoyoGold.copy(alpha = 0.1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isV2) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isV2) HoyoCyan else HoyoGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isV2) {
                            "✨ Teclado completo liberado: Todas as letras (A-Z), números (0-9), cores e busca!"
                        } else {
                            "⚠️ Versão 1.0 Restrita: NÚMEROS (0-9) e letras de 'N' a 'Z' BLOQUEADOS! Apenas A até M. Limite 50 caracteres."
                        },
                        color = if (isV2) HoyoCyanBright else HoyoGoldBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (!isV2) {
                    TextButton(onClick = { viewModel.navigateBackToLauncher() }) {
                        Text(
                            text = "Atualizar",
                            color = HoyoGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Search Bar (v2 feature, locked in v1)
        if (isV2) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = HoyoCardDark,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, HoyoCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = HoyoCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Pesquisar anotações...", color = HoyoTextMuted, fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("notepad_search_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = HoyoTextPrimary,
                            unfocusedTextColor = HoyoTextPrimary
                        )
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar", tint = HoyoTextMuted)
                        }
                    }
                }
            }
        } else {
            // Disabled Search Placeholder for v1
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable {
                        lockedDialogMessage = "A barra de pesquisa e filtros só existe na Versão 2.0 do Bloco de Notas!\n\nAtualize o aplicativo no Launcher para liberar a busca instantânea."
                    },
                color = HoyoSurfaceDark.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, HoyoCardBorder.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = HoyoTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pesquisa bloqueada na v1.0",
                            color = HoyoTextMuted,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = HoyoGold.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Notes List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Nenhuma anotação encontrada.",
                            color = HoyoTextMuted,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Toque no botão '+' abaixo para criar uma nota.",
                            color = HoyoTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        val cardBg = if (isV2 && note.colorIndex in noteColorPalette.indices) {
                            noteColorPalette[note.colorIndex].first
                        } else {
                            HoyoCardDark
                        }
                        val cardAccent = if (isV2 && note.colorIndex in noteColorPalette.indices) {
                            noteColorPalette[note.colorIndex].second
                        } else {
                            HoyoCyan
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    BorderStroke(
                                        if (isV2 && note.isPinned) 1.5.dp else 1.dp,
                                        if (isV2 && note.isPinned) HoyoGold else cardAccent.copy(alpha = 0.4f)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clip(RoundedCornerShape(10.dp))
                                .testTag("note_card_${note.id}"),
                            color = cardBg
                        ) {
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (isV2 && note.isPinned) {
                                            Icon(
                                                imageVector = Icons.Default.PushPin,
                                                contentDescription = "Fixado",
                                                tint = HoyoGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = note.title,
                                            color = HoyoTextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isV2) {
                                            IconButton(
                                                onClick = { viewModel.togglePinNote(note.id) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (note.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                                    contentDescription = "Fixar",
                                                    tint = if (note.isPinned) HoyoGold else HoyoTextMuted,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString("${note.title}\n${note.content}"))
                                                    Toast.makeText(context, "Copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copiar",
                                                    tint = HoyoCyan,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteNote(note.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Excluir",
                                                tint = HoyoRed.copy(alpha = 0.8f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = note.content,
                                    color = HoyoTextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = note.timestamp,
                                        color = HoyoTextMuted,
                                        fontSize = 10.sp
                                    )
                                    if (!isV2) {
                                        Text(
                                            text = "v1.0 • ${note.content.length}/50 chars",
                                            color = HoyoGold.copy(alpha = 0.7f),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Floating Action Button to add note
            FloatingActionButton(
                onClick = { showNewNoteDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("add_note_fab"),
                containerColor = if (isV2) HoyoCyan else HoyoGold,
                contentColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Nota",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    // Dialog: Create New Note
    if (showNewNoteDialog) {
        var inputTitle by remember { mutableStateOf("") }
        var inputContent by remember { mutableStateOf("") }
        var selectedColorIdx by remember { mutableIntStateOf(0) }

        fun validateAndFilter(text: String, isTitle: Boolean): String {
            if (isV2) return text // Full freedom in v2.0!

            // In v1.0, user requested: "um bloco de notas que não de pra colocar todas as letras número"
            // We block all digits (0-9) and all letters beyond 'M' (N-Z, n-z)!
            val sb = StringBuilder()
            var hadBlocked = false
            for (ch in text) {
                if (ch.isDigit()) {
                    hadBlocked = true
                } else if (ch.isLetter()) {
                    val upper = ch.uppercaseChar()
                    if (upper in 'A'..'M') {
                        sb.append(ch)
                    } else {
                        hadBlocked = true
                    }
                } else if (ch == ' ' || ch == '\n') {
                    sb.append(ch)
                } else {
                    hadBlocked = true
                }
            }
            if (hadBlocked) {
                blockedCharWarning = "⛔ Restrição v1.0: Números (0-9) e letras de 'N' a 'Z' são PROIBIDOS na versão básica! Atualize para v2.0 no Launcher para liberar tudo."
            }
            // Length restriction on v1.0:
            val maxLen = if (isTitle) 20 else 50
            return sb.toString().take(maxLen)
        }

        AlertDialog(
            onDismissRequest = { showNewNoteDialog = false },
            title = {
                Text(
                    text = if (isV2) "Nova Anotação (v2.0 Completa)" else "Nova Anotação (v1.0 Restrita)",
                    color = HoyoTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!isV2) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = HoyoGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "⚠️ v1.0: Apenas letras A até M. NÚMEROS NÃO PERMITIDOS. Máximo 50 caracteres.",
                                color = HoyoGoldBright,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    blockedCharWarning?.let { warning ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = HoyoRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = warning,
                                color = HoyoRed,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = {
                            inputTitle = validateAndFilter(it, isTitle = true)
                        },
                        label = { Text("Título da Nota", color = HoyoTextSecondary, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_title_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isV2) HoyoCyan else HoyoGold,
                            unfocusedBorderColor = HoyoCardBorder,
                            focusedTextColor = HoyoTextPrimary,
                            unfocusedTextColor = HoyoTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = inputContent,
                        onValueChange = {
                            inputContent = validateAndFilter(it, isTitle = false)
                        },
                        label = {
                            Text(
                                text = if (isV2) "Conteúdo (Ilimitado)" else "Conteúdo (Máx 50 chars, A-M)",
                                color = HoyoTextSecondary,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("note_content_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isV2) HoyoCyan else HoyoGold,
                            unfocusedBorderColor = HoyoCardBorder,
                            focusedTextColor = HoyoTextPrimary,
                            unfocusedTextColor = HoyoTextPrimary
                        )
                    )

                    // Color palette selector (v2 only!)
                    if (isV2) {
                        Column {
                            Text("Cor da Nota:", color = HoyoTextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(noteColorPalette.indices.toList()) { idx ->
                                    val isSelected = selectedColorIdx == idx
                                    val color = noteColorPalette[idx].second
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(color, CircleShape)
                                            .border(
                                                BorderStroke(if (isSelected) 2.5.dp else 1.dp, if (isSelected) Color.White else Color.Transparent),
                                                CircleShape
                                            )
                                            .clickable { selectedColorIdx = idx }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputTitle.isNotBlank() || inputContent.isNotBlank()) {
                            viewModel.addNote(
                                title = inputTitle.ifBlank { "Sem Título" },
                                content = inputContent,
                                colorIndex = if (isV2) selectedColorIdx else 0
                            )
                            showNewNoteDialog = false
                            blockedCharWarning = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isV2) HoyoCyan else HoyoGold)
                ) {
                    Text("Salvar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNewNoteDialog = false
                    blockedCharWarning = null
                }) {
                    Text("Cancelar", color = HoyoTextSecondary)
                }
            },
            containerColor = HoyoCardDark,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Locked Feature Dialog for v1
    lockedDialogMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { lockedDialogMessage = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = HoyoGold,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text("Recurso Bloqueado na v1.0", color = HoyoTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(msg, color = HoyoTextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        lockedDialogMessage = null
                        viewModel.navigateBackToLauncher()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HoyoGold)
                ) {
                    Text("Ir ao Launcher Atualizar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { lockedDialogMessage = null }) {
                    Text("Entendido", color = HoyoTextSecondary)
                }
            },
            containerColor = HoyoCardDark,
            shape = RoundedCornerShape(12.dp)
        )
    }
}
