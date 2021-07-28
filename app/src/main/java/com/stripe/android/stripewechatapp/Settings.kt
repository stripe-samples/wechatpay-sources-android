package com.stripe.android.stripewechatapp

import android.content.Context
import android.content.pm.PackageManager

internal class Settings(context: Context) {
    private val appContext = context.applicationContext

    val appId = getMetadata(METADATA_KEY_APP_ID)
    val publishableKey = getMetadata(METADATA_KEY_PUBLISHABLE_KEY)
    val backendUrl = getMetadata(METADATA_KEY_BACKEND_URL_KEY)

    private fun getMetadata(key: String): String {
        return appContext.packageManager
            .getApplicationInfo(appContext.packageName, PackageManager.GET_META_DATA)
            .metaData
            .getString(key)
            .takeIf { it?.isNotBlank() == true }
            .orEmpty()
    }

    private companion object {
        private const val METADATA_KEY_APP_ID =
            "com.stripe.android.stripewechatapp.metadata.app_id"
        private const val METADATA_KEY_PUBLISHABLE_KEY =
            "com.stripe.android.stripewechatapp.metadata.publishable_key"
        private const val METADATA_KEY_BACKEND_URL_KEY =
            "com.stripe.android.stripewechatapp.metadata.backend_url"
    }
}
