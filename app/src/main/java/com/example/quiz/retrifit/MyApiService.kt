package com.example.quiz.retrifit

import com.example.quiz.data.otpRequest.ApiResponse
import com.example.quiz.data.otpRequest.RequestParameters
import com.example.quiz.data.otpVerify.OtpVerifyRespone
import com.example.quiz.data.otpVerify.VerifyParameters
import com.example.quiz.data.subscribe.SubscribeRequestParameters
import com.example.quiz.data.subscribe.SubscribeResponse
import com.example.quiz.data.subscription.StatusResponse
import com.example.quiz.data.subscription.VerifyParametersStatus
import com.example.quiz.data.unsubscribe.UnsubscribeRequestParameters
import com.example.quiz.data.unsubscribe.UnsubscribeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MyApiService {
    @POST("nazmul/subscription/otp/request")
    fun requestOtp(@Body requestParameters: RequestParameters): Call<ApiResponse>
    @POST("nazmul/subscription/otp/verify")
    fun verifyOtp(@Body verifyParameters: VerifyParameters): Call<OtpVerifyRespone>

    @POST("nazmul/subscription/status ")
    fun verifySubscription(@Body verifyParametersStatus: VerifyParametersStatus): Call<StatusResponse>

    @POST("nazmul/subscription/subscribe ")
    fun subscribe(@Body subscribeRequestParameters: SubscribeRequestParameters): Call<SubscribeResponse>

    @POST("nazmul/subscription/unsubscribe ")
    fun unsubscribe(@Body unsubscribeRequestParameters: UnsubscribeRequestParameters): Call<UnsubscribeResponse>
}