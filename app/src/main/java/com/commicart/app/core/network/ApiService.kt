package com.commicart.app.core.network

import com.commicart.app.features.auth.data.models.LoginRequest
import com.commicart.app.features.auth.data.models.LoginResponse
import com.commicart.app.features.auth.data.models.RegisterRequest
import com.commicart.app.features.profile.data.models.ChangePasswordRequest
import com.commicart.app.features.profile.data.models.EditProfileRequest
import com.commicart.app.features.profile.data.models.User
import com.commicart.app.shared.models.ApiResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import okhttp3.ResponseBody

interface ApiService {

    // Auth endpoints
    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<ApiResponse<String>>

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<ApiResponse<LoginResponse>>

    // User endpoints
    @GET("api/users/profile")
    fun getProfile(@Header("Authorization") token: String): Call<ApiResponse<User>>

    @PUT("api/users/profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: EditProfileRequest
    ): Call<ApiResponse<User>>

    @PUT("api/users/change-password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Call<ApiResponse<String>>

    @Multipart
    @POST("api/users/upload-profile-image")
    fun uploadProfileImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Call<ApiResponse<User>>

    @GET("api/users/profile-image/{userId}")
    fun getProfileImage(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Call<ResponseBody>
}