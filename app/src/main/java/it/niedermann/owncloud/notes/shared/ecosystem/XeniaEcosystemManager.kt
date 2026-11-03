/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2026 XeniaCloud
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shared.ecosystem

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.nextcloud.android.common.core.utils.ecosystem.AccountReceiverCallback
import com.nextcloud.android.common.core.utils.ecosystem.EcosystemManager
import it.niedermann.owncloud.notes.R
import it.niedermann.owncloud.notes.branding.BrandedSnackbar

/**
 * android-common's EcosystemManager can only open the upstream apps, because its EcosystemApp enum hardcodes their
 * package names. The intent action and extra stay the same, since all three Xenia apps listen for them.
 */
class XeniaEcosystemManager(private val activity: Activity) {

    private val libraryManager = EcosystemManager(activity)

    fun openApp(app: XeniaEcosystemApp, accountName: String?) {
        if (accountName.isNullOrBlank()) {
            showSnackbar(R.string.xenia_ecosystem_account_missing)
            return
        }
        if (activity.packageManager.getLaunchIntentForPackage(app.packageName) == null) {
            showSnackbar(R.string.xenia_ecosystem_app_not_installed)
            return
        }
        try {
            activity.startActivity(launchIntent(app, accountName))
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "${app.packageName} does not handle $ECOSYSTEM_INTENT_ACTION", e)
            showSnackbar(R.string.xenia_ecosystem_app_not_installed)
        }
    }

    fun receiveAccount(intent: Intent?, callback: AccountReceiverCallback) =
        libraryManager.receiveAccount(intent, callback)

    private fun launchIntent(app: XeniaEcosystemApp, accountName: String) = Intent(ECOSYSTEM_INTENT_ACTION).apply {
        setPackage(app.packageName)
        putExtra(EXTRA_KEY_ACCOUNT, accountName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

    private fun showSnackbar(@StringRes messageRes: Int) {
        val root = activity.findViewById<View>(android.R.id.content) ?: return
        BrandedSnackbar.make(root, messageRes, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "XeniaEcosystemManager"
        private const val ECOSYSTEM_INTENT_ACTION = "com.nextcloud.intent.OPEN_ECOSYSTEM_APP"
        private const val EXTRA_KEY_ACCOUNT = "KEY_ACCOUNT"
    }
}
