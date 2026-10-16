package org.app.vimate.sharedlogic.download.remote

import com.psbapp.networking.client.getHttpClientEngine
import io.ktor.client.HttpClient

/**
 * The http client for file downloads: the app's networking engine without the API client's base url,
 * auth and request timeout, which would cut long downloads short.
 */
fun downloadHttpClient(): HttpClient = HttpClient(getHttpClientEngine()) {
    followRedirects = true
    expectSuccess = false
}
