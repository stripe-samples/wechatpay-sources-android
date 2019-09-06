package com.stripe.android.stripewechatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

abstract class BaseWXPayEntryActivity : AppCompatActivity(), IWXAPIEventHandler {
    private lateinit var weChatApi : IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weChatApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true)
        weChatApi.handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
    }

    override fun onReq(req: BaseReq?) {
    }
}
