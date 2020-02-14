package com.stripe.android.stripewechatapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Source
import com.stripe.android.model.SourceParams
import com.stripe.android.model.WeChat
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private val settings: Settings by lazy {
        Settings(this)
    }
    private val stripe: Stripe by lazy {
        Stripe(this, settings.publishableKey)
    }
    private val weChatApi : IWXAPI by lazy {
        WXAPIFactory.createWXAPI(this, settings.appId, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val weChatPaySourceParams = SourceParams.createWeChatPayParams(
            AMOUNT,
            CURRENCY,
            settings.appId,
            STATEMENT_DESCRIPTOR
        )
        button.setOnClickListener {
            button.isEnabled = false
            stripe.createSource(
                weChatPaySourceParams,
                callback = object : ApiResultCallback<Source> {
                    override fun onError(e: Exception) {
                        button.isEnabled = true
                        showSnackbar(e.message.orEmpty())
                    }

                    override fun onSuccess(result: Source) {
                        button.isEnabled = true
                        showSnackbar(getString(R.string.created_source, result.id.orEmpty()))
                        launchWeChat(result.weChat)
                    }
                }
            )
        }
    }

    private fun launchWeChat(weChat: WeChat) {
        val success = weChatApi.registerApp(settings.appSignature)
        if (success) {
            showSnackbar("Starting WeChat Pay")
            weChatApi.sendReq(createPayReq(weChat))
        } else {
            showSnackbar("Failed to start WeChat Pay")
        }
    }

    private fun createPayReq(weChat: WeChat): PayReq {
        return PayReq().apply {
            appId = weChat.appId
            partnerId = weChat.partnerId
            prepayId = weChat.prepayId
            packageValue = weChat.packageValue
            nonceStr = weChat.nonce
            timeStamp = weChat.timestamp
            sign = weChat.sign
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById<View>(android.R.id.content), message, Snackbar.LENGTH_SHORT)
            .show()
    }

    companion object {
        private const val AMOUNT = 1000L
        private const val CURRENCY = "usd"
        private const val STATEMENT_DESCRIPTOR = "WIDGET STORE"
    }
}