package com.stripe.android.stripewechatapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var weChatApi : IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        weChatApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true)

        val stripe = Stripe(this, Constants.STRIPE_KEY)
        val weChatPaySourceParams = SourceParams.createWeChatPayParams(
            AMOUNT,
            CURRENCY,
            Constants.APP_ID,
            STATEMENT_DESCRIPTOR
        )
        button.setOnClickListener {
            Toast.makeText(this, R.string.creating_source_object, Toast.LENGTH_SHORT)
                .show()

            stripe.createSource(weChatPaySourceParams,
                object : ApiResultCallback<Source> {
                    override fun onError(e: Exception) {
                    }

                    override fun onSuccess(source: Source) {
                        launchWeChat(source.weChat)
                    }
                })
        }
    }

    private fun launchWeChat(weChat: WeChat) {
        val success = weChatApi.registerApp(Constants.APP_SIGNATURE)
        assert(success)
        weChatApi.sendReq(createPayReq(weChat))
    }

    private fun createPayReq(weChat: WeChat): PayReq {
        val weChatReq = PayReq()
        weChatReq.appId = weChat.appId
        weChatReq.partnerId = weChat.partnerId
        weChatReq.prepayId = weChat.prepayId
        weChatReq.packageValue = weChat.packageValue
        weChatReq.nonceStr = weChat.nonce
        weChatReq.timeStamp = weChat.timestamp
        weChatReq.sign = weChat.sign

        return weChatReq
    }

    companion object {
        private const val AMOUNT = 1000L
        private const val CURRENCY = "usd"
        private const val STATEMENT_DESCRIPTOR = "WIDGET STORE"
    }
}