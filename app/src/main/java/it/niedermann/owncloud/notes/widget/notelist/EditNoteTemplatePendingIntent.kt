/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.widget.notelist

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import it.niedermann.owncloud.notes.edit.EditNoteActivity

private const val TEMPLATE_REQUEST_CODE = 0
private const val FILL_IN_TEMPLATE_FLAGS =
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE or Intent.FILL_IN_COMPONENT

fun createEditNoteTemplatePendingIntent(context: Context): PendingIntent {
    val editNoteIntent = Intent(context, EditNoteActivity::class.java).setPackage(context.packageName)
    return PendingIntent.getActivity(context, TEMPLATE_REQUEST_CODE, editNoteIntent, FILL_IN_TEMPLATE_FLAGS)
}
