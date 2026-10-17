package org.app.vimate.me.ui.wide

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Where feedback and support requests go. */
internal const val SUPPORT_EMAIL = "support@vimate.app"

private fun mailto(subject: String, body: String): String {
    fun enc(s: String) = buildString {
        s.encodeToByteArray().forEach { b ->
            val c = b.toInt().toChar()
            if (c.isLetterOrDigit() || c in "-_.~") append(c) else append('%').append(((b.toInt() and 0xFF) + 0x100).toString(16).substring(1).uppercase())
        }
    }
    return "mailto:$SUPPORT_EMAIL?subject=${enc(subject)}&body=${enc(body)}"
}

@Composable
private fun DialogFrame(title: String, onDismiss: () -> Unit, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.width(520.dp).heightIn(max = 680.dp).clip(RoundedCornerShape(18.dp)).background(ViColors.surface).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 17.sp, color = ViColors.ink)
                Box(Modifier.size(28.dp).clip(CircleShape).clickable(onClick = onDismiss), contentAlignment = Alignment.Center) {
                    Ic(Res.drawable.ic_close, Modifier.size(16.dp), ViColors.ink)
                }
            }
            content()
        }
    }
}

private val feedbackKinds = listOf(Res.string.me_fb_bug, Res.string.me_fb_idea, Res.string.me_fb_download, Res.string.me_fb_other)

/** Feedback: what kind, a message and an optional reply address; Send opens the mail app addressed to support. */
@Composable
internal fun FeedbackDialog(onDismiss: () -> Unit) {
    val uri = LocalUriHandler.current
    val toast = LocalToast.current
    var kind by remember { mutableStateOf(feedbackKinds.first()) }
    var text by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val kindLabel = stringResource(kind)
    val subject = stringResource(Res.string.me_feedback_subject) + " · " + kindLabel
    val thanks = stringResource(Res.string.me_feedback_thanks)
    DialogFrame(stringResource(Res.string.me_feedback), onDismiss) {
        Text(stringResource(Res.string.me_fb_intro), Modifier.padding(top = 4.dp), fontSize = 13.sp, color = ViColors.sectionLabel)
        Row(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            feedbackKinds.forEach { k ->
                val sel = k == kind
                Text(stringResource(k), Modifier.clip(CircleShape).background(if (sel) VRed else ViColors.chipIdle).clickable { kind = k }
                    .padding(horizontal = 14.dp, vertical = 7.dp), color = if (sel) Color.White else ViColors.ink, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
        Field(text, { text = it }, stringResource(Res.string.me_describe_problem), Modifier.padding(top = 14.dp).height(150.dp), single = false)
        Field(email, { email = it }, stringResource(Res.string.me_fb_email_hint), Modifier.padding(top = 10.dp).height(44.dp), single = true)
        val ok = text.isNotBlank()
        Box(Modifier.padding(top = 16.dp).fillMaxWidth().height(44.dp).clip(RoundedCornerShape(12.dp)).background(if (ok) VRed else ViColors.batchDoneOff)
            .clickable(enabled = ok) {
                val body = text.trim() + if (email.isNotBlank()) "\n\nReply to: ${email.trim()}" else ""
                runCatching { uri.openUri(mailto(subject, body)) }
                toast(thanks); onDismiss()
            }, contentAlignment = Alignment.Center) {
            Text(stringResource(Res.string.me_submit), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun Field(value: String, onChange: (String) -> Unit, hint: String, modifier: Modifier, single: Boolean) {
    val shape = RoundedCornerShape(10.dp)
    BasicTextField(value, onChange, modifier.fillMaxWidth(), singleLine = single, textStyle = TextStyle(fontSize = 14.sp, color = ViColors.ink), cursorBrush = SolidColor(VRed),
        decorationBox = { inner ->
            Box(Modifier.fillMaxWidth().clip(shape).background(ViColors.wideCanvas).border(1.dp, ViColors.wideHairline, shape).padding(12.dp),
                contentAlignment = if (single) Alignment.CenterStart else Alignment.TopStart) {
                if (value.isEmpty()) Text(hint, color = ViColors.hint, fontSize = 14.sp)
                inner()
            }
        })
}

private val faq: List<Pair<StringResource, StringResource>> = listOf(
    Res.string.me_q1 to Res.string.me_a1, Res.string.me_q3 to Res.string.me_a3, Res.string.me_q4 to Res.string.me_a4,
    Res.string.me_q6 to Res.string.me_a6, Res.string.me_q2 to Res.string.me_a2, Res.string.me_q9 to Res.string.me_a9, Res.string.me_q10 to Res.string.me_a10,
)

/** Help: expandable FAQ, then support contact (email) and the app version. */
@Composable
internal fun HelpDialog(onDismiss: () -> Unit, onFeedback: () -> Unit) {
    val uri = LocalUriHandler.current
    var open by remember { mutableStateOf<Int?>(0) }
    val subject = stringResource(Res.string.me_help_subject)
    DialogFrame(stringResource(Res.string.me_help_center), onDismiss) {
        Column(Modifier.weight(1f, fill = false).padding(top = 12.dp).verticalScroll(rememberScrollState())) {
            Text(stringResource(Res.string.me_hot_questions), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ViColors.ink)
            faq.forEachIndexed { i, (q, a) ->
                val shape = RoundedCornerShape(10.dp)
                Column(Modifier.padding(top = 8.dp).fillMaxWidth().clip(shape).background(ViColors.wideCanvas).border(1.dp, ViColors.wideHairline, shape)
                    .clickable { open = if (open == i) null else i }.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(q), Modifier.weight(1f), fontWeight = FontWeight.Medium, fontSize = 13.5.sp, color = ViColors.ink)
                        Text(if (open == i) "−" else "+", color = ViColors.sectionLabel, fontSize = 16.sp)
                    }
                    if (open == i) Text(stringResource(a), Modifier.padding(top = 6.dp), fontSize = 13.sp, lineHeight = 18.sp, color = ViColors.muted)
                }
            }
            Text(stringResource(Res.string.me_contact_support), Modifier.padding(top = 18.dp), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ViColors.ink)
            Row(Modifier.padding(top = 8.dp).height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Action(Res.drawable.ic_t_feedback, Res.string.me_email_support, SUPPORT_EMAIL, Modifier.weight(1f)) { runCatching { uri.openUri(mailto(subject, "")) } }
                Action(Res.drawable.ic_t_help, Res.string.me_feedback, stringResource(Res.string.me_fb_short), Modifier.weight(1f), onFeedback)
            }
            Text(stringResource(Res.string.settings_version), Modifier.padding(top = 14.dp).align(Alignment.CenterHorizontally), fontSize = 12.sp, color = ViColors.sectionLabel)
        }
    }
}

/** Equal-height support row: tinted icon, title, one-line hint and a chevron. */
@Composable
private fun Action(icon: org.jetbrains.compose.resources.DrawableResource, title: StringResource, sub: String, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(modifier.fillMaxHeight().clip(shape).background(ViColors.wideCanvas).border(1.dp, ViColors.wideHairline, shape).clickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.detailDownloadPill), contentAlignment = Alignment.Center) {
            Ic(icon, Modifier.size(18.dp), VRed)
        }
        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(stringResource(title), color = ViColors.ink, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, maxLines = 1)
            Text(sub, color = ViColors.sectionLabel, fontSize = 12.sp, maxLines = 1)
        }
        Ic(Res.drawable.ic_chev_right, Modifier.size(16.dp), ViColors.sectionLabel)
    }
}
