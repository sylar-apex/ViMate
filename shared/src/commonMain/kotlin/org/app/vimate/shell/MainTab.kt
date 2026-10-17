package org.app.vimate.shell

import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_nav_files
import org.app.vimate.appres.resources.ic_nav_files_on
import org.app.vimate.appres.resources.ic_nav_home
import org.app.vimate.appres.resources.ic_nav_home_on
import org.app.vimate.appres.resources.ic_nav_me
import org.app.vimate.appres.resources.ic_nav_me_on
import org.app.vimate.appres.resources.ic_nav_music
import org.app.vimate.appres.resources.ic_nav_music_on
import org.app.vimate.appres.resources.shell_nav_files
import org.app.vimate.appres.resources.shell_nav_home
import org.app.vimate.appres.resources.shell_nav_me
import org.app.vimate.appres.resources.shell_nav_music
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/** The four bottom-bar tabs (POC BottomBar items: HOME, MUSIC, MY FILES, ME), in [Tab] index order. */
enum class MainTab(val index: Int, val label: StringResource, val idle: DrawableResource, val active: DrawableResource) {
    HOME(Tab.HOME, Res.string.shell_nav_home, Res.drawable.ic_nav_home, Res.drawable.ic_nav_home_on),
    MUSIC(Tab.MUSIC, Res.string.shell_nav_music, Res.drawable.ic_nav_music, Res.drawable.ic_nav_music_on),
    FILES(Tab.FILES, Res.string.shell_nav_files, Res.drawable.ic_nav_files, Res.drawable.ic_nav_files_on),
    ME(Tab.ME, Res.string.shell_nav_me, Res.drawable.ic_nav_me, Res.drawable.ic_nav_me_on),
}
