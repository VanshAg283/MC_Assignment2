// Keys.kt
package com.blakester.jetrack

import android.content.Context

object Keys {
    fun getRestApiKey(context: Context): String = context.getString(R.string.mappls_rest_api_key)
    fun getMapSdkKey(context: Context): String = context.getString(R.string.mappls_map_sdk_key)
    fun getClientId(context: Context): String = context.getString(R.string.mappls_client_id)
    fun getClientSecret(context: Context): String = context.getString(R.string.mappls_client_secret)
    fun getFlightStatsAppId(context: Context): String = context.getString(R.string.FLIGHTSTATS_APP_ID)
    fun getFlightStatsAppKey(context: Context): String = context.getString(R.string.FLIGHTSTATS_APP_KEY)

}