package com.stripe.android.stripewechatapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.stripe.android.Stripe
import com.stripe.android.StripeApiBeta
import com.stripe.android.createPaymentMethod
import com.stripe.android.getPaymentIntentResult
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.PaymentMethodOptionsParams
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

    private val viewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolbar)

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
                            stripe.confirmPayment(this@MainActivity, confirmPaymentIntentParams)
                        }
                    }
                }
            }
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (stripe.isPaymentResult(requestCode, data)) {
            lifecycleScope.launch {
                // stripe.isPaymentResult already verifies data is not null
                val intentResult = stripe.getPaymentIntentResult(requestCode, data!!)
                viewModel.appendStatus("WeChat PaymentIntent confirmation finishes with outcome: ${intentResult.outcome}")
                intentResult.failureMessage?.let {
                    viewModel.appendStatus("failureMessage: $it")
                }
                viewModel.inProgress.postValue(false)
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(viewBinding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}