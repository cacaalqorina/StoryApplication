package com.annisaalqorina.submissionstory.api

import com.annisaalqorina.submissionstory.modeldata.SignInBody
import com.annisaalqorina.submissionstory.modeldata.SignUpBody
import com.annisaalqorina.submissionstory.response.FileUploadResponse
import com.annisaalqorina.submissionstory.response.GetAllStoryResponse
import com.annisaalqorina.submissionstory.response.SignInResponse
import com.annisaalqorina.submissionstory.response.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface MyApi {

    @POST("login")
    fun login(
        @Body body: SignInBody
    ) : Call<SignInResponse>

    @POST("register")
    fun register(
        @Body body: SignUpBody
    ) : Call<SignUpResponse>

    @GET ("stories")
    fun getStories(
        @Header("Authorization") authToken : String
    ) : Call<GetAllStoryResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") authToken: String,
        @Part file : MultipartBody.Part,
        @Part("description") description: RequestBody
    ) : Call<FileUploadResponse>
}