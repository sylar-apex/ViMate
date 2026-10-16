package org.app.vimate.appcore.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** A settings line: title, optional subtitle, and a switch ([checked]) or a value ([trailing]). */
@Composable
fun SettingRow(
    title: String,
    subtitle: String? = null,
    trailing: String? = null,
    checked: Boolean? = null,
    onClick: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (subtitle != null) {
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (checked != null) {
            Switch(checked = checked, onCheckedChange = { onClick() })
        } else if (trailing != null) {
            Text(trailing, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
