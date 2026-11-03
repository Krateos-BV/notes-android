/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 XeniaCloud
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.main.items

import androidx.test.core.app.ApplicationProvider
import com.nextcloud.android.common.core.utils.DateFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verifyNoInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NoteModifiedFormatterTest {

    companion object {
        private const val DELEGATED = "delegated"
    }

    private val dateFormatter = mock(DateFormatter::class.java)
    private val formatter = NoteModifiedFormatter(ApplicationProvider.getApplicationContext(), dateFormatter)

    private fun minutesAgo(minutes: Int) = Calendar.getInstance().apply { add(Calendar.MINUTE, -minutes) }

    private fun assertDelegated(modified: Calendar) {
        doReturn(DELEGATED).`when`(dateFormatter).getConditionallyRelativeFormattedTimeSpan(modified)
        assertEquals(DELEGATED, formatter.format(modified))
    }

    @Test
    fun `minutes within the hour show the minute count`() {
        assertEquals("1m", formatter.format(minutesAgo(1)))
        assertEquals("5m", formatter.format(minutesAgo(5)))
        assertEquals("59m", formatter.format(minutesAgo(59)))
        verifyNoInteractions(dateFormatter)
    }

    @Test
    fun `less than a minute ago is delegated to the library`() {
        assertDelegated(Calendar.getInstance())
    }

    @Test
    fun `an hour or more ago is delegated to the library`() {
        assertDelegated(minutesAgo(60))
        assertDelegated(minutesAgo(120))
    }
}
