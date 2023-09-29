package com.example.knuseklubben.data.network.helpers

import okhttp3.OkHttpClient

/**
 * Sets up a pre request hook that adds credentials and user agent to all
 * requests using this httpClient
 * @param authorizationToken
 *
 */

fun okHttpBuilder(authorizationToken: String): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $authorizationToken")
                .addHeader("User-Agent", "Knuseklubben")
                .build()
            chain.proceed(newRequest)
        }
        .build()
}
