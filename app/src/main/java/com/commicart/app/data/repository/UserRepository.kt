package com.commicart.app.data.repository

import android.content.Context
import com.commicart.app.data.models.*
import com.commicart.app.data.network.ApiService
import com.commicart.app.data.network.RetrofitClient
import com.commicart.app.utils.TokenManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UserRepository(private val context: Context) {

    private val apiService: ApiService = RetrofitClient.instance
    private val tokenManager = TokenManager(context)

    interface AuthCallback {
        fun onSuccess(data: Any?)
        fun onError(message: String)
    }

    interface ProfileCallback {
        fun onSuccess(user: User)
        fun onError(message: String)
    }

    interface ImageUploadCallback {
        fun onSuccess(user: User)
        fun onError(message: String)
    }

    fun register(request: RegisterRequest, callback: AuthCallback) {
        apiService.register(request).enqueue(object : Callback<ApiResponse<String>> {
            override fun onResponse(
                call: Call<ApiResponse<String>>,
                response: Response<ApiResponse<String>>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        callback.onSuccess(apiResponse.data)
                    } else {
                        callback.onError(apiResponse?.message ?: "Registration failed")
                    }
                } else {
                    callback.onError("Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                callback.onError(t.message ?: "Network error")
            }
        })
    }

    fun login(request: LoginRequest, callback: AuthCallback) {
        apiService.login(request).enqueue(object : Callback<ApiResponse<LoginResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<LoginResponse>>,
                response: Response<ApiResponse<LoginResponse>>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true && apiResponse.data != null) {
                        tokenManager.saveToken(apiResponse.data.token)
                        callback.onSuccess(apiResponse.data)
                    } else {
                        callback.onError(apiResponse?.message ?: "Login failed")
                    }
                } else {
                    callback.onError("Invalid credentials")
                }
            }

            override fun onFailure(call: Call<ApiResponse<LoginResponse>>, t: Throwable) {
                callback.onError(t.message ?: "Network error")
            }
        })
    }

    fun getProfile(callback: ProfileCallback) {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            callback.onError("Not authenticated")
            return
        }

        apiService.getProfile("Bearer $token").enqueue(object : Callback<ApiResponse<User>> {
            override fun onResponse(
                call: Call<ApiResponse<User>>,
                response: Response<ApiResponse<User>>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true && apiResponse.data != null) {
                        callback.onSuccess(apiResponse.data)
                    } else {
                        callback.onError(apiResponse?.message ?: "Failed to get profile")
                    }
                } else {
                    callback.onError("Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<User>>, t: Throwable) {
                callback.onError(t.message ?: "Network error")
            }
        })
    }

    fun updateProfile(request: EditProfileRequest, callback: ProfileCallback) {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            callback.onError("Not authenticated")
            return
        }

        apiService.updateProfile("Bearer $token", request)
            .enqueue(object : Callback<ApiResponse<User>> {
                override fun onResponse(
                    call: Call<ApiResponse<User>>,
                    response: Response<ApiResponse<User>>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse?.success == true && apiResponse.data != null) {
                            callback.onSuccess(apiResponse.data)
                        } else {
                            callback.onError(apiResponse?.message ?: "Update failed")
                        }
                    } else {
                        callback.onError("Server error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<User>>, t: Throwable) {
                    callback.onError(t.message ?: "Network error")
                }
            })
    }

    fun changePassword(request: ChangePasswordRequest, callback: AuthCallback) {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            callback.onError("Not authenticated")
            return
        }

        apiService.changePassword("Bearer $token", request)
            .enqueue(object : Callback<ApiResponse<String>> {
                override fun onResponse(
                    call: Call<ApiResponse<String>>,
                    response: Response<ApiResponse<String>>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse?.success == true) {
                            callback.onSuccess(apiResponse.data)
                        } else {
                            callback.onError(apiResponse?.message ?: "Password change failed")
                        }
                    } else {
                        callback.onError("Server error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                    callback.onError(t.message ?: "Network error")
                }
            })
    }

    fun uploadProfileImage(imageFile: File, callback: ImageUploadCallback) {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            callback.onError("Not authenticated")
            return
        }

        val requestBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

        apiService.uploadProfileImage("Bearer $token", part)
            .enqueue(object : Callback<ApiResponse<User>> {
                override fun onResponse(
                    call: Call<ApiResponse<User>>,
                    response: Response<ApiResponse<User>>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse?.success == true && apiResponse.data != null) {
                            callback.onSuccess(apiResponse.data)
                        } else {
                            callback.onError(apiResponse?.message ?: "Upload failed")
                        }
                    } else {
                        callback.onError("Server error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<User>>, t: Throwable) {
                    callback.onError(t.message ?: "Network error")
                }
            })
    }
}