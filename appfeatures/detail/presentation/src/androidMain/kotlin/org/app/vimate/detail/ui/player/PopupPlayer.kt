package org.app.vimate.detail.ui.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.YouTube

/** The popup's icons, as compose painters (the app's drawables are compose resources, not Android ones). */
data class PopupIcons(val close: Painter, val pause: Painter, val play: Painter, val next: Painter, val zoom: Painter, val full: Painter)

/**
 * VidMate "Popup": a 180x103dp draggable floating window over other apps. A tap shows its controls for 3 s
 * (quality + speed, close, pause + next, zoom, seek, expand). Expand reopens the video page at the same position.
 */
object PopupPlayer {
    private var root: FrameLayout? = null
    private var player: ExoPlayer? = null
    private var video: Video? = null
    private val main = Handler(Looper.getMainLooper())
    /** MainActivity.EXTRA_VIDEO. */
    private const val EXTRA_VIDEO = "video"

    fun canDraw(ctx: Context) = Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(ctx)

    fun requestPermission(ctx: Context) {
        ctx.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun Painter.bitmap(sizePx: Int, density: Float): Bitmap {
        val img = ImageBitmap(sizePx, sizePx)
        CanvasDrawScope().draw(Density(density), LayoutDirection.Ltr, Canvas(img), Size(sizePx.toFloat(), sizePx.toFloat())) { draw(size) }
        return img.asAndroidBitmap()
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    fun show(ctx: Context, icons: PopupIcons, v: Video, videoUrl: String, audioUrl: String?, qualityLabel: String, positionMs: Long, onNext: (() -> Unit)? = null) {
        close()
        val app = ctx.applicationContext
        val wm = app.getSystemService(WindowManager::class.java)
        val dm = app.resources.displayMetrics
        fun dp(x: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, dm).toInt()
        fun bmp(p: Painter) = p.bitmap(dp(48f), dm.density)
        val pauseBmp = bmp(icons.pause); val playBmp = bmp(icons.play)
        val http = DefaultHttpDataSource.Factory().setUserAgent(YouTube.streamUA).setAllowCrossProtocolRedirects(true)
        fun src(u: String) = ProgressiveMediaSource.Factory(http).createMediaSource(MediaItem.fromUri(u))
        val p = ExoPlayer.Builder(app).build().apply {
            setMediaSource(if (audioUrl != null) MergingMediaSource(src(videoUrl), src(audioUrl)) else src(videoUrl), positionMs)
            prepare(); play()
        }
        val box = FrameLayout(app).apply { setBackgroundColor(Color.BLACK) }
        val surface = SurfaceView(app)
        box.addView(surface, FrameLayout.LayoutParams(-1, -1))
        p.setVideoSurfaceView(surface)

        // Controls layer (hidden until tapped).
        val ctl = FrameLayout(app).apply { setBackgroundColor(0x55000000); visibility = View.GONE }
        box.addView(ctl, FrameLayout.LayoutParams(-1, -1))
        fun text(t: String) = TextView(app).apply { text = t; setTextColor(Color.WHITE); textSize = 13.5f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
        fun icon(res: Painter, sizeDp: Float, g: Int, mx: Float, my: Float, click: () -> Unit) = ImageView(app).apply {
            setImageBitmap(bmp(res)); setColorFilter(Color.WHITE); setOnClickListener { click() }
            ctl.addView(this, FrameLayout.LayoutParams(dp(sizeDp), dp(sizeDp), g).apply {
                if (g and Gravity.END == Gravity.END) rightMargin = dp(mx) else leftMargin = dp(mx)
                if (g and Gravity.BOTTOM == Gravity.BOTTOM) bottomMargin = dp(my) else topMargin = dp(my)
            })
        }
        ctl.addView(LinearLayout(app).apply {
            addView(text(qualityLabel)); addView(text("1x").apply { setPadding(dp(14f), 0, 0, 0) })
        }, FrameLayout.LayoutParams(-2, -2, Gravity.TOP or Gravity.START).apply { leftMargin = dp(8f); topMargin = dp(6f) })
        icon(icons.close, 22f, Gravity.TOP or Gravity.END, 8f, 6f) { close() }
        val playBtn = ImageView(app).apply {
            setImageBitmap(pauseBmp); setColorFilter(Color.WHITE)
            setOnClickListener { if (p.isPlaying) p.pause() else p.play() }
        }
        ctl.addView(playBtn, FrameLayout.LayoutParams(dp(28f), dp(28f), Gravity.CENTER))
        icon(icons.next, 30f, Gravity.CENTER, 0f, 0f) { onNext?.invoke() }.apply { translationX = dp(38f).toFloat() }
        // Seek line + blue dot.
        val track = View(app).apply { setBackgroundColor(0x00FFFFFF) }
        ctl.addView(track, FrameLayout.LayoutParams(-1, dp(1.3f), Gravity.BOTTOM).apply { leftMargin = dp(40f); rightMargin = dp(40f); bottomMargin = dp(16f) })
        val dot = View(app).apply { background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(0xFF2380FE.toInt()) } }
        ctl.addView(dot, FrameLayout.LayoutParams(dp(12f), dp(12f), Gravity.BOTTOM or Gravity.START).apply { leftMargin = dp(40f); bottomMargin = dp(10f) })

        val lp = WindowManager.LayoutParams(dp(180f), dp(103f),
            if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.TOP or Gravity.START; x = (dm.widthPixels - dp(180f)) / 2; y = dm.heightPixels / 2 - dp(52f)
        }
        // Zoom toggles between small and large; expand reopens the video page.
        icon(icons.zoom, 24f, Gravity.BOTTOM or Gravity.START, 8f, 6f) {
            val big = lp.width > dp(200f)
            lp.width = dp(if (big) 180f else 280f); lp.height = dp(if (big) 103f else 158f); wm.updateViewLayout(box, lp)
        }
        icon(icons.full, 24f, Gravity.BOTTOM or Gravity.END, 8f, 6f) {
            PopupResume.resume = v.id to p.currentPosition
            app.packageManager.getLaunchIntentForPackage(app.packageName)?.let { launch ->
                app.startActivity(launch.putExtra(EXTRA_VIDEO, v.id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP))
            }
            close()
        }

        val hide = Runnable { ctl.visibility = View.GONE }
        fun reveal() { ctl.visibility = View.VISIBLE; main.removeCallbacks(hide); main.postDelayed(hide, 3000) }
        val tick = object : Runnable {
            override fun run() {
                val d = p.duration.takeIf { it > 0 } ?: 0
                val w = track.width
                if (d > 0 && w > 0) (dot.layoutParams as FrameLayout.LayoutParams).let { it.leftMargin = dp(40f) + (w * p.currentPosition / d).toInt() - dp(6f); dot.layoutParams = it }
                playBtn.setImageBitmap(if (p.isPlaying) pauseBmp else playBmp)
                if (root != null) main.postDelayed(this, 500)
            }
        }
        var sx = 0f; var sy = 0f; var ox = 0; var oy = 0; var moved = false
        val drag = View.OnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> { sx = e.rawX; sy = e.rawY; ox = lp.x; oy = lp.y; moved = false }
                MotionEvent.ACTION_MOVE -> {
                    val dx = e.rawX - sx; val dy = e.rawY - sy
                    if (kotlin.math.abs(dx) + kotlin.math.abs(dy) > 12) moved = true
                    if (moved) { lp.x = ox + dx.toInt(); lp.y = oy + dy.toInt(); wm.updateViewLayout(box, lp) }
                }
                MotionEvent.ACTION_UP -> if (!moved) { if (ctl.visibility == View.VISIBLE) ctl.visibility = View.GONE else reveal() }
            }
            true
        }
        surface.setOnTouchListener(drag)
        ctl.setOnTouchListener(drag)
        p.addListener(object : Player.Listener {})
        wm.addView(box, lp)
        root = box; player = p; video = v
        main.post(tick)
    }

    fun close() {
        player?.release(); player = null
        root?.let { runCatching { it.context.getSystemService(WindowManager::class.java).removeView(it) } }
        root = null
    }
}
