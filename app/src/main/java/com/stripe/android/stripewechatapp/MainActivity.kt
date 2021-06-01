package com.stripe.android.stripewechatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.stripe.android.Stripe
import com.stripe.android.createSource
import com.stripe.android.model.SourceParams
import com.stripe.android.model.WeChat
import com.stripe.android.stripewechatapp.databinding.ActivityMainBinding
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.coroutines.launch

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
    private val viewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolbar)

        viewBinding.button.setOnClickListener {
            viewBinding.button.isEnabled = false
            onCreateSource()
        }
    }

    private fun onCreateSource() {
        val weChatPaySourceParams = SourceParams.createWeChatPayParams(
            AMOUNT,
            CURRENCY,
            settings.appId,
            STATEMENT_DESCRIPTOR
        )

        lifecycleScope.launch {
            runCatching {
                stripe.createSource(weChatPaySourceParams)
            }.fold(
                onSuccess = { source ->
                    showSnackbar(
                        getString(R.string.created_source, source.id.orEmpty())
                    )
                    launchWeChat(source.weChat)
                },
                onFailure = {
                    showSnackbar(it.message.orEmpty())
                }
            )

            viewBinding.button.isEnabled = true
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
        Snackbar.make(viewBinding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    companion object {
        private const val AMOUNT = 1000L
        private const val CURRENCY = "usd"
        private const val STATEMENT_DESCRIPTOR = "WIDGET STORE"
    }
}