/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.widget.notelist

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import it.niedermann.owncloud.notes.edit.EditNoteActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class EditNoteTemplatePendingIntentTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun templateStaysMutableOnAndroid14SoFillInNoteIdReachesTheEditor() {
        val flags = shadowOf(createEditNoteTemplatePendingIntent(context)).flags

        assertTrue(flags and PendingIntent.FLAG_MUTABLE != 0)
        assertEquals(0, flags and PendingIntent.FLAG_IMMUTABLE)
    }

    @Test
    fun templateExplicitlyTargetsTheEditor() {
        val savedIntent = shadowOf(createEditNoteTemplatePendingIntent(context)).savedIntent

        assertEquals(EditNoteActivity::class.java.name, savedIntent.component?.className)
        assertEquals(context.packageName, savedIntent.`package`)
    }
}
