package org.app.vimate.files.ui.component

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Empty
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Svg
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.files_back
import org.app.vimate.appres.resources.files_confirm_password
import org.app.vimate.appres.resources.files_delete
import org.app.vimate.appres.resources.files_enter_password
import org.app.vimate.appres.resources.files_pin_mismatch
import org.app.vimate.appres.resources.files_private_empty
import org.app.vimate.appres.resources.files_private_note
import org.app.vimate.appres.resources.files_set_password
import org.app.vimate.appres.resources.files_set_password_btn
import org.app.vimate.appres.resources.files_set_password_note
import org.app.vimate.appres.resources.files_set_the_password
import org.app.vimate.appres.resources.files_wrong_password
import org.app.vimate.appres.resources.ic_backspace
import org.app.vimate.appres.resources.ic_help_circle
import org.app.vimate.appres.resources.ic_undo
import org.app.vimate.files.usecase.FilesUseCase
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "undo", "0", "del")

/**
 * POC PrivateGate: white "Set Password" intro (250x200dp lock, 20sp title, 2-line note, red 140x40dp button),
 * then the PIN pad: 22sp prompt + help icon, four 48dp boxes 16dp apart, grey keypad of 48dp keys.
 */
@Composable
internal fun PrivateGate() {
    val files = koinInject<FilesUseCase>()
    var unlocked by remember { mutableStateOf(false) }
    if (unlocked) { PrivateList(); return }
    val existing = remember { files.privatePin }
    var entering by remember { mutableStateOf(existing.isNotEmpty()) }
    if (!entering) {
        Column(Modifier.fillMaxSize().background(ViColors.surface), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(42.dp))
            Svg("private_lock", Modifier.size(250.dp, 200.dp))
            Spacer(Modifier.height(8.dp))
            Text(stringResource(Res.string.files_set_password), fontSize = 20.sp, lineHeight = 27.sp, fontWeight = FontWeight.Bold, color = ViColors.ink)
            Text(stringResource(Res.string.files_set_password_note), Modifier.padding(top = 8.dp).width(288.dp),
                fontSize = 14.5.sp, lineHeight = 17.7.sp, color = ViColors.muted, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            Box(Modifier.size(140.dp, 40.dp).clip(RoundedCornerShape(2.dp)).background(ViColors.vRed).clickable { entering = true }, contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.files_set_password_btn), color = ViColors.white, fontSize = 16.sp, lineHeight = 19.sp, fontWeight = FontWeight.Bold)
            }
        }
        return
    }
    var pin by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<StringResource?>(null) }
    fun press(k: String) {
        error = null
        if (pin.length < 4) pin += k
        if (pin.length == 4) when {
            existing.isNotEmpty() -> if (pin == existing) unlocked = true else { error = Res.string.files_wrong_password; pin = "" }
            confirm == null -> { confirm = pin; pin = "" }
            pin == confirm -> { files.privatePin = pin; unlocked = true }
            else -> { error = Res.string.files_pin_mismatch; confirm = null; pin = "" }
        }
    }
    Column(Modifier.fillMaxSize().background(ViColors.surface)) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            Row(Modifier.align(Alignment.TopCenter).padding(top = 120.dp).height(27.dp), verticalAlignment = Alignment.CenterVertically) {
                val prompt = error ?: when { existing.isNotEmpty() -> Res.string.files_enter_password; confirm == null -> Res.string.files_set_the_password; else -> Res.string.files_confirm_password }
                Text(stringResource(prompt), fontSize = 19.2.sp, lineHeight = 27.sp, color = if (error != null) VRed else ViColors.ink)
                Ic(Res.drawable.ic_help_circle, Modifier.padding(start = 8.dp).size(16.dp), ViColors.radioOff)
            }
            LazyRow(Modifier.align(Alignment.TopCenter).padding(top = 183.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), userScrollEnabled = false) {
                items(4, key = { it }) { i ->
                    val current = i == pin.length
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)).background(if (current) ViColors.pinCurrent else ViColors.gap)
                        .then(if (current) Modifier.border(2.dp, ViColors.pinBorder, RoundedCornerShape(6.dp)) else Modifier), contentAlignment = Alignment.Center) {
                        if (i < pin.length) Box(Modifier.size(12.dp).clip(CircleShape).background(ViColors.ink))
                    }
                }
            }
        }
        // Keypad: #EEEEEE panel, 3 cols of 48dp white keys with 4dp gaps.
        LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxWidth().background(ViColors.pinKeypad), userScrollEnabled = false,
            contentPadding = PaddingValues(start = 5.dp, end = 5.dp, top = 10.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(keys, key = { it }) { k ->
                Box(Modifier.height(48.dp).shadow(1.dp, RoundedCornerShape(2.dp)).background(ViColors.surface, RoundedCornerShape(2.dp)).clickable {
                    when (k) { "del" -> pin = pin.dropLast(1); "undo" -> { pin = ""; if (existing.isEmpty() && confirm == null) entering = false else confirm = null }; else -> press(k) }
                }, contentAlignment = Alignment.Center) {
                    when (k) {
                        "undo" -> Ic(Res.drawable.ic_undo, Modifier.size(24.dp), ViColors.ink, stringResource(Res.string.files_back))
                        "del" -> Ic(Res.drawable.ic_backspace, Modifier.size(28.dp), ViColors.ink, stringResource(Res.string.files_delete))
                        else -> Text(k, fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, color = ViColors.ink)
                    }
                }
            }
        }
        Spacer(Modifier.height(94.dp).fillMaxWidth().background(ViColors.surface))
    }
}

/** POC PrivateList: hidden downloads. */
@Composable
private fun PrivateList() {
    val files = koinInject<FilesUseCase>()
    val all by files.tasks.collectAsState()
    val hidden = all.filter { it.hidden }
    LazyColumn(Modifier.fillMaxSize()) {
        item(key = "note") { Text(stringResource(Res.string.files_private_note), color = ViColors.metaGrey, fontSize = 13.sp, modifier = Modifier.padding(16.dp)) }
        if (hidden.isEmpty()) item(key = "empty") { Empty(stringResource(Res.string.files_private_empty)) }
        items(hidden, key = { it.id }) { t -> DoneRow(t, hidden) }
    }
}
