package org.app.vimate.settings.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.jetbrains.compose.resources.stringResource

/** 56dp white header with a soft shadow; back at x=16, 20sp bold title at x=60. */
@Composable
internal fun SettingsHeader(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(56.dp).shadow(3.dp).background(ViColors.white), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.padding(start = 16.dp).size(32.dp).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_back, Modifier.size(30.dp), ViColors.ink, stringResource(Res.string.settings_back))
        }
        Text(title, Modifier.padding(start = 24.dp), fontWeight = FontWeight.Bold, fontSize = 18.5.sp, color = ViColors.ink)
    }
}

/** White section; VidMate separates sections with 7dp of page grey. */
@Composable
internal fun Block(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().background(ViColors.white), content = content)
    Spacer(Modifier.height(7.dp))
}

/** 48dp row (taller with subtitle): 16.5sp title at x=13, 13.5sp grey subtitle, right value, 0.7dp rule inset 13dp. */
@Composable
internal fun Row(s: String, sub: String? = null, value: String? = null, last: Boolean = false, trailing: @Composable (() -> Unit)? = null, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        val long = (sub?.length ?: 0) > 45 && sub!!.contains(" ")
        androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().heightIn(min = 58.dp).padding(start = 16.dp, end = 16.dp, top = if (long) 20.dp else 15.dp, bottom = if (long) 20.dp else 15.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(s, fontSize = 15.2.sp, lineHeight = 19.sp, color = ViColors.ink)
                if (sub != null) Text(sub, Modifier.padding(top = 1.dp), fontSize = 12.sp, lineHeight = if (long) 16.sp else 14.5.sp, color = ViColors.setSub)
            }
            if (value != null) Text(value, Modifier.padding(start = 8.dp), fontSize = 11.5.sp, color = ViColors.setSub)
            trailing?.invoke()
        }
        if (!last) Box(Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(0.7.dp).background(ViColors.setRule))
    }
}

@Composable
internal fun Toggle(title: String, sub: String?, value: Boolean, last: Boolean = false, onChange: (Boolean) -> Unit) =
    Row(title, sub, last = last, trailing = { VSwitch(value) }) { onChange(!value) }

/** VidMate switch: 27x13dp track (light blue when on), 17dp thumb (blue #29A8F2 on, white with shadow off). */
@Composable
internal fun VSwitch(on: Boolean) {
    Box(Modifier.padding(start = 12.dp).size(34.dp, 20.dp), contentAlignment = Alignment.CenterStart) {
        Box(Modifier.fillMaxWidth().height(13.dp).clip(CircleShape).background(if (on) ViColors.setTrackOn else ViColors.setTrackOff))
        Box(Modifier.align(if (on) Alignment.CenterEnd else Alignment.CenterStart).size(20.dp).shadow(1.dp, CircleShape).clip(CircleShape)
            .background(if (on) ViColors.setThumbOn else ViColors.setThumbOff))
    }
}

/** Row opening a radio dialog. [options] are stored values; [label] renders one. */
@Composable
internal fun Choice(title: String, value: String, options: List<String>, sub: String? = null, showValue: Boolean = true, last: Boolean = false,
                    label: @Composable (String) -> String = { it }, onPick: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Row(title, sub, value = if (showValue) label(value) else null, last = last) { open = true }
    if (open) AlertDialog(onDismissRequest = { open = false }, title = { Text(title) }, confirmButton = {},
        text = {
            LazyColumn {
                items(options, key = { it }) { o ->
                    androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().clickable { onPick(o); open = false }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(o == value, { onPick(o); open = false }, colors = RadioButtonDefaults.colors(selectedColor = VRed)); Text(label(o))
                    }
                }
            }
        })
}

@Composable
internal fun TextPref(title: String, sub: String, value: String, onSave: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Row(title, sub) { open = true }
    if (open) {
        var t by remember { mutableStateOf(value) }
        AlertDialog(onDismissRequest = { open = false }, title = { Text(stringResource(Res.string.settings_folder_name)) },
            text = { OutlinedTextField(t, { t = it.filter { c -> c.isLetterOrDigit() || c in " _-" } }, singleLine = true) },
            confirmButton = { TextButton(enabled = t.isNotBlank(), onClick = { onSave(t.trim()); open = false }) { Text(stringResource(Res.string.settings_ok)) } })
    }
}
