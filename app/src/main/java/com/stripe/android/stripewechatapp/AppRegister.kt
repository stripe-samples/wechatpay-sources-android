package com.stripe.android.stripewechatapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class AppRegister : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val settings = Settings(context)
        val api = WXAPIFactory.createWXAPI(context, settings.appId, true)
        api.registerApp(settings.appSignature)
    }
}
