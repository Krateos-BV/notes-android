/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 XeniaCloud
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.main.items

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "en")
class NoteModifiedFormatterTest {

    private val formatter = NoteModifiedFormatter(ApplicationProvider.getApplicationContext())

    private fun minutesAgo(minutes: Int) = Calendar.getInstance().apply { add(Calendar.MINUTE, -minutes) }

    @Test
    fun `less than a minute ago shows now`() {
        assertEquals("Now", formatter.format(Calendar.getInstance()))
    }

    @Test
    fun `one minute ago shows the minute count`() {
        assertEquals("1m", formatter.format(minutesAgo(1)))
    }

    @Test
    fun `minutes ago within the hour show the minute count`() {
        assertEquals("5m", formatter.format(minutesAgo(5)))
        assertEquals("59m", formatter.format(minutesAgo(59)))
    }

    @Test
    fun `hours ago show the hour count`() {
        assertEquals("2h", formatter.format(minutesAgo(120)))
    }
}
