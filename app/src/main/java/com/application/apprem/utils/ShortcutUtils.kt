package com.application.apprem.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.application.apprem.R
import com.application.apprem.activities.HomeworkActivity
import com.application.apprem.activities.NotesActivity
import com.application.apprem.activities.SummaryActivity


@RequiresApi(25)
class ShortcutUtils {

    companion object {
        fun createShortcuts(context: Context) {

            val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            val shortcutList = mutableListOf<ShortcutInfo>()
            shortcutList.add(createAddHomeworkShortcut(context))
            shortcutList.add(createNotesShortcut(context))
            shortcutList.add(createSummaryShortcut(context))

            shortcutManager!!.dynamicShortcuts = shortcutList
        }


        private const val size = 256
        private const val padding = 65
        private fun createShortcut(context: Context, id: String, shortLabel: String, iconId: Int, intent: Intent): ShortcutInfo {
            val icon = ContextCompat.getDrawable(context, iconId)
            icon?.setTint(Color.WHITE)

            val background = ContextCompat.getDrawable(context, R.drawable.shortcuts_background)
            val combined = LayerDrawable(arrayOf(background, icon))
            combined.setLayerInset(1, padding, padding, padding, padding)

            val combinedIcon = if (Build.VERSION.SDK_INT > 25) Icon.createWithAdaptiveBitmap(combined.toBitmap(size, size)) else Icon.createWithBitmap(combined.toBitmap(size, size))

            return ShortcutInfo.Builder(context, id)
                    .setShortLabel(shortLabel)
                    .setIcon(combinedIcon)
                    .setIntent(intent)
                    .build()
        }

        private fun createAddHomeworkShortcut(context: Context): ShortcutInfo {
            return createShortcut(context, "add_homework", context.getString(R.string.add_homework), R.drawable.ic_homework, Intent(context, HomeworkActivity::class.java).setAction(HomeworkActivity.ACTION_ADD_HOMEWORK))
        }

        private fun createNotesShortcut(context: Context): ShortcutInfo {
            return createShortcut(context, "open_notes", context.getString(R.string.notes_activity_title), R.drawable.ic_notes, Intent(context, NotesActivity::class.java).setAction(Intent.ACTION_VIEW))
        }

        private fun createSummaryShortcut(context: Context): ShortcutInfo {
            return createShortcut(context, "summary", context.getString(R.string.summary_activity_title), R.drawable.ic_summary, Intent(context, SummaryActivity::class.java).setAction(Intent.ACTION_VIEW))
        }
    }
}