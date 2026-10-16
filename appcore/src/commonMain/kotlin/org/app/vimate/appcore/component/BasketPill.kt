package org.app.vimate.appcore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.basket_many
import org.app.vimate.appres.resources.basket_one
import org.app.vimate.appres.resources.ic_file_added
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** "N files added" floating over the tabs while the batch basket has items. Nothing when empty. */
@Composable
fun BasketPill(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (count == 0) return
    val ink = MaterialTheme.colorScheme.onSurface
    Row(
        modifier.height(36.dp).shadow(4.dp, CircleShape).clip(CircleShape).background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick).padding(start = 14.dp, end = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(Res.drawable.ic_file_added), null, tint = ink, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(8.dp))
        Text(if (count == 1) stringResource(Res.string.basket_one) else stringResource(Res.string.basket_many, count.toString()), color = ink, fontSize = 15.sp)
    }
}
