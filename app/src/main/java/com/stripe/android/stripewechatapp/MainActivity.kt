package com.stripe.android.stripewechatapp

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.stripe.android.Stripe
import com.stripe.android.StripeApiBeta
import com.stripe.android.createPaymentMethod
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.PaymentMethodOptionsParams
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.stripewechatapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: WeChatViewModel by viewModels()
    private val settings: Settings by lazy {
        Settings(this)
    }
    private val stripe: Stripe by lazy {
        Stripe(
            this,
            settings.publishableKey,
            betas = setOf(StripeApiBeta.WeChatPayV1)
        )
    }

    private lateinit var paymentLauncher: PaymentLauncher

    private val viewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolbar)

        paymentLauncher = PaymentLauncher.create(
            this,
            settings.publishableKey
        ) { paymentResult ->
            viewModel.appendStatus("WeChat PaymentIntent confirmation finishes with $paymentResult")
            if (paymentResult is PaymentResult.Failed) {
                viewModel.appendStatus("  Exception: ${paymentResult.throwable}")
            }
            viewModel.inProgress.postValue(false)
        }

        viewBinding.button.setOnClickListener {
            viewBinding.status.text = ""
            createAndConfirmWeChatPaymentIntent()
        }

        viewModel.status.observe(this, Observer(viewBinding.status::setText))
        viewModel.inProgress.observe(this) { isInProgress ->
            viewBinding.button.isEnabled = !isInProgress
            viewBinding.progress.visibility = if (isInProgress) View.VISIBLE else View.INVISIBLE
        }
    }


    private fun createAndConfirmWeChatPaymentIntent() {
        viewModel.createWeChatPaymentIntent().observe(
            this,
            { result ->
                result.onSuccess { responseData ->
                    val secret = responseData.getString("secret")
                    viewModel.appendStatus("Creating WeChat PaymentMethod...")
                    lifecycleScope.launch {
                        stripe.createPaymentMethod(PaymentMethodCreateParams.createWeChatPay()).id?.let { paymentMethodId ->
                            viewModel.appendStatus("WeChat PaymentMethod created, confirming PaymentIntent by opening WeChat app...")
                            val confirmPaymentIntentParams =
                                ConfirmPaymentIntentParams.createWithPaymentMethodId(
                                    paymentMethodId = paymentMethodId,
                                    clientSecret = secret,
                                    paymentMethodOptions = PaymentMethodOptionsParams.WeChatPay(
                                        settings.appId
                                    )
                                )
                            paymentLauncher.confirm(confirmPaymentIntentParams)
                        }
                    }
                }
            }
        )

    }
}