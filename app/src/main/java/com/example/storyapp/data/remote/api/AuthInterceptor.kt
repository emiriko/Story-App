package com.example.storyapp.data.remote.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(private val token: String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()

        val isPublicEndpoint = original.headers["isPublic"] == "true"
    
        val requestBuilder = original.newBuilder()
            .removeHeader("isPublic")
            .method(original.method, original.body)
        
        if(!isPublicEndpoint) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        return chain.proceed(requestBuilder.build())
    }
}