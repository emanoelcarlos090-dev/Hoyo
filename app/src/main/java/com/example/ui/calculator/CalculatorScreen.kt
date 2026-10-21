package com.example.ui.calculator

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.unit.sp
import com.example.model.AppInstallState
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
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: LauncherViewModel,
    installState: AppInstallState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBackToLauncher()
    }

    val isV2 = installState == AppInstallState.INSTALLED_V2
    val historyList by viewModel.calcHistory.collectAsState()

    var displayText by remember { mutableStateOf("0") }
    var currentExpression by remember { mutableStateOf("") }
    var pendingOperator by remember { mutableStateOf<String?>(null) }
    var operand1 by remember { mutableStateOf<Double?>(null) }
    var resetInputOnNextDigit by remember { mutableStateOf(false) }

    var lockedFeatureDialogMessage by remember { mutableStateOf<String?>(null) }
    var showHistorySheet by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (resetInputOnNextDigit || displayText == "0" || displayText == "Erro") {
            displayText = d
            resetInputOnNextDigit = false
        } else {
            if (displayText.length < 12) {
                displayText += d
            }
        }
    }

    fun onOperator(op: String) {
        // v1.0 only allows + and -
        if (!isV2 && (op == "×" || op == "÷")) {
            lockedFeatureDialogMessage = "A operação de Multiplicação (×) e Divisão (÷) só foi adicionada na Versão 2.0 do aplicativo!\n\nVolte ao Launcher para baixar a atualização e desbloquear a multiplicação."
            return
        }

        val currentVal = displayText.toDoubleOrNull() ?: 0.0
        if (operand1 == null) {
            operand1 = currentVal
            pendingOperator = op
            currentExpression = "$displayText $op"
            resetInputOnNextDigit = true
        } else {
            if (pendingOperator != null && !resetInputOnNextDigit) {
                // compute partial
                val result = when (pendingOperator) {
                    "+" -> operand1!! + currentVal
                    "-" -> operand1!! - currentVal
                    "×" -> operand1!! * currentVal
                    "÷" -> if (currentVal == 0.0) null else operand1!! / currentVal
                    else -> currentVal
                }
                if (result == null) {
                    displayText = "Erro: Divisão por 0"
                    operand1 = null
                    pendingOperator = null
                    currentExpression = ""
                    resetInputOnNextDigit = true
                    return
                }
                operand1 = result
                val formatted = if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
                displayText = formatted
                currentExpression = "$formatted $op"
            } else {
                currentExpression = "$displayText $op"
            }
            pendingOperator = op
            resetInputOnNextDigit = true
        }
    }

    fun onEquals() {
        if (operand1 != null && pendingOperator != null) {
            val currentVal = displayText.toDoubleOrNull() ?: 0.0
            val fullExpr = "$currentExpression $displayText"
            val result = when (pendingOperator) {
                "+" -> operand1!! + currentVal
                "-" -> operand1!! - currentVal
                "×" -> operand1!! * currentVal
                "÷" -> if (currentVal == 0.0) null else operand1!! / currentVal
                else -> currentVal
            }

            if (result == null) {
                displayText = "Erro: Divisão por 0"
                currentExpression = ""
                operand1 = null
                pendingOperator = null
                resetInputOnNextDigit = true
                return
            }

            val formatted = if (result % 1.0 == 0.0) result.toLong().toString() else DecimalFormat("#.######").format(result)
            displayText = formatted
            currentExpression = "$fullExpr = $formatted"
            viewModel.saveCalculation(fullExpr, formatted)
            operand1 = null
            pendingOperator = null
            resetInputOnNextDigit = true
        }
    }

    fun onClear() {
        displayText = "0"
        currentExpression = ""
        operand1 = null
        pendingOperator = null
        resetInputOnNextDigit = false
    }

    fun onBackspace() {
        if (!isV2) {
            lockedFeatureDialogMessage = "O botão de apagar dígito (Backspace ⌫) é exclusivo da versão 2.0!\n\nAtualize o aplicativo no Launcher para liberar."
            return
        }
        if (displayText.length > 1 && displayText != "Erro") {
            displayText = displayText.dropLast(1)
        } else {
            displayText = "0"
        }
    }

    fun onDecimal() {
        if (!isV2) {
            lockedFeatureDialogMessage = "O suporte a casas decimais (.) não existe na v1.0 básica!\n\nAtualize no Launcher para a v2.0 para fazer cálculos com números quebrados."
            return
        }
        if (!displayText.contains(".")) {
            displayText += "."
        }
    }

    fun onPercent() {
        if (!isV2) {
            lockedFeatureDialogMessage = "Cálculo de porcentagem (%) disponível apenas na versão 2.0 atualizada!"
            return
        }
        val currentVal = displayText.toDoubleOrNull() ?: 0.0
        val res = currentVal / 100.0
        displayText = res.toString()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HoyoDeepDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // App Navigation Top Bar
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
                    modifier = Modifier.testTag("back_to_launcher_btn")
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
                        text = "Calculadora Neo",
                        color = HoyoTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isV2) "v2.0 (Completa)" else "v1.0 (Básica)",
                        color = if (isV2) HoyoGreen else HoyoGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isV2) {
                    IconButton(
                        onClick = { showHistorySheet = true },
                        modifier = Modifier.testTag("calc_history_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Histórico",
                            tint = HoyoCyan
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            lockedFeatureDialogMessage = "O Histórico de cálculos só é ativado na atualização v2.0 do Launcher!"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Histórico Bloqueado",
                            tint = HoyoTextMuted
                        )
                    }
                }

                HoyoStatusBadge(
                    text = if (isV2) "v2.0 COMPLETA" else "v1.0 LIMITADA",
                    statusColor = if (isV2) HoyoCyan else HoyoGold
                )
            }
        }

        // Notification / Version Banner
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
                            "✨ Multiplicação (×), Divisão (÷), Decimais e Histórico desbloqueados!"
                        } else {
                            "⚠️ Multiplicação e Divisão BLOQUEADOS! Atualize no Launcher para v2.0."
                        },
                        color = if (isV2) HoyoCyanBright else HoyoGoldBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (!isV2) {
                    TextButton(
                        onClick = { viewModel.navigateBackToLauncher() }
                    ) {
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

        // Calculator Display Area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(BorderStroke(1.5.dp, HoyoCardBorder), shape = RoundedCornerShape(12.dp)),
            color = HoyoCardDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = currentExpression.ifEmpty { " " },
                    color = HoyoTextMuted,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayText,
                    color = HoyoTextPrimary,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("calc_display_text")
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Calculator Keypad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: C, Backspace, %, ÷
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CalcKey(
                    label = "C",
                    onClick = { onClear() },
                    isAction = true,
                    textColor = HoyoRed,
                    modifier = Modifier.weight(1f),
                    testTag = "calc_key_clear"
                )
                CalcKey(
                    label = "⌫",
                    onClick = { onBackspace() },
                    isAction = true,
                    isLocked = !isV2,
                    modifier = Modifier.weight(1f),
                    testTag = "calc_key_backspace"
                )
                CalcKey(
                    label = "%",
                    onClick = { onPercent() },
                    isAction = true,
                    isLocked = !isV2,
                    modifier = Modifier.weight(1f),
                    testTag = "calc_key_percent"
                )
                CalcKey(
                    label = "÷",
                    onClick = { onOperator("÷") },
                    isOperator = true,
                    isLocked = !isV2,
                    modifier = Modifier.weight(1f),
                    testTag = "calc_key_divide"
                )
            }

            // Row 2: 7, 8, 9, ×
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CalcKey(label = "7", onClick = { onDigit("7") }, modifier = Modifier.weight(1f))
                CalcKey(label = "8", onClick = { onDigit("8") }, modifier = Modifier.weight(1f))
                CalcKey(label = "9", onClick = { onDigit("9") }, modifier = Modifier.weight(1f))
                CalcKey(
                    label = "×",
                    onClick = { onOperator("×") },
                    isOperator = true,
                    isLocked = !isV2,
                    modifier = Modifier.weight(1f),
                    testTag = "calc_key_multiply"
                )
            }

            // Row 3: 4, 5, 6, -
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CalcKey(label = "4", onClick = { onDigit("4") }, modifier = Modifier.weight(1f))
                CalcKey(label = "5", onClick = { onDigit("5") }, modifier = Modifier.weight(1f))
                CalcKey(label = "6", onClick = { onDigit("6") }, modifier = Modifier.weight(1f))
                CalcKey(
                    label = "-",
                    onClick = { onOperator("-") },
                    isOperator = true,
                    modifier = Modifier.weight(1f),
                    testTag = "calc_key_minus"
                )
            }

            // Row 4: 1, 2, 3, +
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CalcKey(label = "1", onClick = { onDigit("1") }, modifier = Modifier.weight(1f))
                CalcKey(label = "2", onClick = { onDigit("2") }, modifier = Modifier.weight(1f))
                CalcKey(label = "3", onClick = { onDigit("3") }, modifier = Modifier.weight(1f))
                CalcKey(
                    label = "+",
                    onClick = { onOperator("+") },
                    isOperator = true,
                    modifier = Modifier.weight(1f),
                    testTag = "calc_key_plus"
                )
            }

            // Row 5: 0, ., =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CalcKey(
                    label = "0",
                    onClick = { onDigit("0") },
                    modifier = Modifier.weight(2.1f)
                )
                CalcKey(
                    label = ".",
                    onClick = { onDecimal() },
                    isLocked = !isV2,
                    modifier = Modifier.weight(1f),
                    testTag = "calc_key_decimal"
                )
                CalcKey(
                    label = "=",
                    onClick = { onEquals() },
                    isEquals = true,
                    modifier = Modifier.weight(1f),
                    testTag = "calc_key_equals"
                )
            }
        }
    }

    // Modal Sheet: History of Calculations (v2 only)
    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            containerColor = HoyoCardDark,
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HISTÓRICO DE CÁLCULOS (v2.0)",
                        color = HoyoGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (historyList.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCalcHistory() }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Limpar Histórico",
                                tint = HoyoRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (historyList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum cálculo registrado ainda.",
                            color = HoyoTextMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historyList) { item ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = HoyoSurfaceDark,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = item.expression,
                                            color = HoyoTextSecondary,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "= ${item.result}",
                                            color = HoyoCyan,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Text(
                                        text = item.timestamp,
                                        color = HoyoTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Locked Feature Dialog for v1
    lockedFeatureDialogMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { lockedFeatureDialogMessage = null },
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
                        lockedFeatureDialogMessage = null
                        viewModel.navigateBackToLauncher()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HoyoGold)
                ) {
                    Text("Ir ao Launcher Atualizar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { lockedFeatureDialogMessage = null }) {
                    Text("Entendido", color = HoyoTextSecondary)
                }
            },
            containerColor = HoyoCardDark,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun CalcKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false,
    isAction: Boolean = false,
    isEquals: Boolean = false,
    isLocked: Boolean = false,
    textColor: Color? = null,
    testTag: String = "calc_key_$label"
) {
    val bgColor = when {
        isEquals -> HoyoGold
        isOperator -> HoyoCyan.copy(alpha = 0.15f)
        isAction -> HoyoCardDark
        else -> HoyoCardHover
    }

    val contentColor = when {
        textColor != null -> textColor
        isEquals -> Color.Black
        isLocked -> HoyoTextMuted
        isOperator -> HoyoCyan
        isAction -> HoyoGold
        else -> HoyoTextPrimary
    }

    Surface(
        modifier = modifier
            .height(58.dp)
            .border(
                BorderStroke(
                    1.dp,
                    if (isLocked) HoyoCardBorder.copy(alpha = 0.5f) else if (isOperator) HoyoCyan.copy(alpha = 0.4f) else HoyoCardBorder
                ),
                shape = CutCornerShape(8.dp)
            )
            .clip(CutCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = if (isLocked) HoyoSurfaceDark.copy(alpha = 0.5f) else bgColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    color = contentColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isLocked) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = HoyoGold.copy(alpha = 0.8f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}
