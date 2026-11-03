/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 XeniaCloud
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.main.items

import android.content.Context
import android.text.format.DateUtils
import com.nextcloud.android.common.core.R
import com.nextcloud.android.common.core.utils.DateFormatter
import java.util.Calendar

class NoteModifiedFormatter @JvmOverloads constructor(
    private val context: Context,
    private val dateFormatter: DateFormatter = DateFormatter(context)
) {
    fun format(modified: Calendar): String {
        val span = System.currentTimeMillis() - modified.timeInMillis
        if (span !in DateUtils.MINUTE_IN_MILLIS until DateUtils.HOUR_IN_MILLIS) {
            return dateFormatter.getConditionallyRelativeFormattedTimeSpan(modified)
        }
        val minutes = (span / DateUtils.MINUTE_IN_MILLIS).toInt()
        return context.resources.getQuantityString(R.plurals.date_formatting_relative_minutes, minutes, minutes)
    }
}
