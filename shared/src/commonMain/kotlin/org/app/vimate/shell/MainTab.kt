package org.app.vimate.shell

import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_nav_files
import org.app.vimate.appres.resources.ic_nav_files_on
import org.app.vimate.appres.resources.ic_nav_home
import org.app.vimate.appres.resources.ic_nav_home_on
import org.app.vimate.appres.resources.ic_nav_me
import org.app.vimate.appres.resources.ic_nav_me_on
import org.app.vimate.appres.resources.ic_nav_music
import org.app.vimate.appres.resources.ic_nav_music_on
import org.app.vimate.appres.resources.tab_files
import org.app.vimate.appres.resources.tab_home
import org.app.vimate.appres.resources.tab_me
import org.app.vimate.appres.resources.tab_music
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/** The four bottom-navigation tabs. */
enum class MainTab(val label: StringResource, val idle: DrawableResource, val active: DrawableResource) {
    HOME(Res.string.tab_home, Res.drawable.ic_nav_home, Res.drawable.ic_nav_home_on),
    MUSIC(Res.string.tab_music, Res.drawable.ic_nav_music, Res.drawable.ic_nav_music_on),
    FILES(Res.string.tab_files, Res.drawable.ic_nav_files, Res.drawable.ic_nav_files_on),
    ME(Res.string.tab_me, Res.drawable.ic_nav_me, Res.drawable.ic_nav_me_on),
}
