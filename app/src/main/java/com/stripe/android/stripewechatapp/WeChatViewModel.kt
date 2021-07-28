package com.stripe.android.stripewechatapp

import android.app.Application
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.stripe.android.stripewechatapp.service.BackendApi
import com.stripe.android.stripewechatapp.service.BackendApiFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext

class WeChatViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val resources: Resources = application.resources
    private val workContext: CoroutineContext = Dispatchers.IO
    private val backendApi: BackendApi = BackendApiFactory(application).create()

    val inProgress = MutableLiveData<Boolean>()
    val status = MutableLiveData<String>()

    fun createWeChatPaymentIntent(
    ) = makeBackendRequest(
        R.string.creating_payment_intent,
        R.string.payment_intent_status
    ) {
        backendApi.createPaymentIntent(
            mapOf("supported_payment_methods" to "wechat_pay")
                .toMutableMap()
        )
    }

    private fun makeBackendRequest(
        @StringRes creatingStringRes: Int,
        @StringRes resultStringRes: Int,
        apiMethod: suspend () -> ResponseBody
    ) = liveData {
        inProgress.postValue(true)
        status.postValue(resources.getString(creatingStringRes))

        val result = withContext(workContext) {
            runCatching {
                JSONObject(apiMethod().string())
            }
        }

        result.fold(
            onSuccess = {
                val intentStatus = resources.getString(
                    resultStringRes,
                    it.getString("status")
                )
                appendStatus("PaymentIntent created with status $intentStatus")
            },
            onFailure = {
                val errorMessage =
                    (it as? HttpException)?.response()?.errorBody()?.string()
                        ?: it.message
                appendStatus("Failed to create PaymentIntent with error $errorMessage")
                inProgress.value = false
            }
        )

        emit(result)
    }

    fun appendStatus(newStatus: String?) {
        status.value = ("${status.value}\n\n$newStatus")
    }
}