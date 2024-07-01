package com.example.calculator

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.time.times

object CurrencyApi {
    private const val key = "fca_live_wFMlhnW5YzZfrWlpuhiKwi1yRmGJ7rEU9UqUcbtM"
     var basecurency="USD"
     var currencies="EUR"
     lateinit var amount:String

    private const val symbolsurl = "https://api.freecurrencyapi.com/v1/latest?apikey=${key}"


    private val client = OkHttpClient()
    private val gson = Gson()

    private val symbolsrequest = Request.Builder().url(symbolsurl).build()


    suspend fun getlistofCurr(): List<String> {
        val listofcurrencies = mutableListOf<String>()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(symbolsrequest).execute()
                val body = response.body?.string()

                if (response.isSuccessful && !body.isNullOrEmpty()) {
                    val json = Gson().fromJson(body, JsonObject::class.java)
                    val currenciesJson = json.getAsJsonObject("data")

                    currenciesJson.keySet().forEach { currencyCode ->
                        val currency = Currency(currencyCode)
                        listofcurrencies.add(currency.currecy)
                    }
                } else {
                    Log.d("CurrencyApi", "Error: ${response.code} - ${response.message}")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            listofcurrencies
        }
    }

    suspend fun getcertaincurrency():String{

        var  currencyurl = "https://api.freecurrencyapi.com/v1/latest?apikey=fca_live_wFMlhnW5YzZfrWlpuhiKwi1yRmGJ7rEU9UqUcbtM&currencies=${currencies}&base_currency=${basecurency}"

        val currencyrequest = Request.Builder().url(currencyurl).build()
        var newamount = amount.toDouble()

        Log.d("", currencyurl)
        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(currencyrequest).execute()
                val body = response.body?.string()

                if (response.isSuccessful && !body.isNullOrEmpty()) {
                    val json = Gson().fromJson(body, JsonObject::class.java)
                    val currenciesJson = json.getAsJsonObject("data")

                    currenciesJson.entrySet().forEach { entry->
                        newamount = newamount*entry.value.toString().toDouble()


                    }

                } else {
                    Log.d("CurrencyApi", "Error: ${response.code} - ${response.message}")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            newamount.toString()
        }

    }

}