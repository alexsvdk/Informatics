package ru.temoteam.informatics.api.objects

import java.net.HttpCookie

/**
 * Created by a1exS on 2/18/2018.
 */
class Account(val username: String,val password: String,val rawCookie:String) {
    val MoodleSessionTest = rawCookie.substringAfter("MoodleSessionTest=").substringBefore(";")
    val MoodleSession = rawCookie.substringAfter("MoodleSession=").substringBefore(";")
    val headers = mapOf("Cookie" to "MoodleSessionTest=$MoodleSessionTest; MoodleSession=$MoodleSession")


}