package com.enl.fund.cookie

import com.enl.config.ConfigHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import org.slf4j.LoggerFactory
import java.io.*

object CookieJar: CookieJar {
    private val logger = LoggerFactory.getLogger("CookieJar")
    private var cookies: MutableList<Cookie> = mutableListOf()
    private const val cookiePath = "cookie.txt"

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        if (this.cookies != cookies) {
            saveCookiesToDisk(cookies)
        }
        this.cookies = cookies
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        if (cookies.isEmpty()) {
            logger.debug("No cookie in the request, read from Disk")
            val cachedCookie = readCookiesInDisk()
            if (!cachedCookie.isNullOrEmpty()) {
                return cachedCookie
            }
            logger.debug("Can't find cookie in the disk, read from config.")
            val cookieConfig = ConfigHelper.getConfig().xqCookie
            if (cookieConfig.isEmpty()) {
                logger.error("No cookie in the config")
                return mutableListOf()
            }
            return processCookieInConfig(cookieConfig, url)
        } else {
            return cookies
        }
    }

    private fun processCookieInConfig(
        cookieConfig: String,
        url: HttpUrl
    ): MutableList<Cookie> {
        val cookieRawStrings = cookieConfig.split(";")
        val cookieList = mutableListOf<Cookie>()
        for (string in cookieRawStrings) {
            val cookiePair = string.split("=")
            if (cookiePair.size != 2) {
                logger.error("Invalid cookie: $string")
                continue
            }
            val cookieName = cookiePair[0].trim()
            val cookieValue = cookiePair[1].trim()
            val cookie = Cookie.Builder()
                .name(cookieName)
                .value(cookieValue)
                .domain(url.host())
                .build()
            cookieList.add(cookie)
        }
        return cookieList
    }

    private fun saveCookiesToDisk(cookies: MutableList<Cookie>) {
        logger.debug("Save cookies to disk")
        logger.debug("Cookies: $cookies")
        try {
            Gson().toJson(cookies).let { json ->
                FileWriter(cookiePath).use {
                    it.write(json)
                }
            }
            logger.debug("Save cookies to disk successfully")
        } catch (e: Exception) {
            logger.error("Failed to save cookies to disk", e)
        }
    }

    private fun readCookiesInDisk(): MutableList<Cookie>? {
        try {
            Gson().fromJson(FileInputStream(cookiePath).bufferedReader(), object: TypeToken<MutableList<Cookie>>() {}.type)
        } catch (e: Exception) {
            logger.error("Failed to read cookies from disk", e)
        }
        return null
    }
}