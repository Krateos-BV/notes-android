/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 XeniaCloud
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shared.ecosystem

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import it.niedermann.owncloud.notes.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class XeniaEcosystemManagerTest {

    private lateinit var activity: Activity
    private lateinit var manager: XeniaEcosystemManager

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        activity.setTheme(R.style.AppTheme)
        manager = XeniaEcosystemManager(activity)
    }

    @Test
    fun opensInstalledXeniaAppWithAccount() {
        install(XeniaEcosystemApp.FILES.packageName)

        manager.openApp(XeniaEcosystemApp.FILES, ACCOUNT)

        val started = shadowOf(activity).nextStartedActivity
        assertEquals("com.nextcloud.intent.OPEN_ECOSYSTEM_APP", started.action)
        assertEquals("eu.xeniacloud.files", started.`package`)
        assertEquals(ACCOUNT, started.getStringExtra("KEY_ACCOUNT"))
    }

    @Test
    fun talkTargetsXeniaTalk() {
        install(XeniaEcosystemApp.TALK.packageName)

        manager.openApp(XeniaEcosystemApp.TALK, ACCOUNT)

        assertEquals("eu.xeniacloud.talk", shadowOf(activity).nextStartedActivity.`package`)
    }

    @Test
    fun startsNothingWhenAppIsMissing() {
        install("com.nextcloud.client")

        manager.openApp(XeniaEcosystemApp.FILES, ACCOUNT)

        assertNull(shadowOf(activity).nextStartedActivity)
    }

    @Test
    fun startsNothingWithoutAccount() {
        install(XeniaEcosystemApp.FILES.packageName)

        manager.openApp(XeniaEcosystemApp.FILES, null)

        assertNull(shadowOf(activity).nextStartedActivity)
    }

    private fun install(packageName: String) {
        val packageManager = shadowOf(activity.packageManager)
        packageManager.installPackage(PackageInfo().apply { this.packageName = packageName })
        val launcher = ComponentName(packageName, "$packageName.LauncherActivity")
        packageManager.addActivityIfNotPresent(launcher)
        packageManager.addIntentFilterForActivity(
            launcher,
            IntentFilter(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        )
    }

    private companion object {
        const val ACCOUNT = "user@portal.example.eu"
    }
}
