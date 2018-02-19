package ru.temoteam.informatics.api.objects

import ru.temoteam.informatics.api.Requester
import java.net.HttpCookie

/**
 * Created by a1exS on 2/18/2018.
 */
class Account(val username: String,val password: String,val rawCookie:String) {
    val MoodleSessionTest = rawCookie.substringAfter("MoodleSessionTest=").substringBefore(";")
    val MoodleSession = rawCookie.substringAfter("MoodleSession=").substringBefore(";")
    val headers = mapOf("Cookie" to "MoodleSessionTest=$MoodleSessionTest; MoodleSession=$MoodleSession")
    val data by lazy { Requester.account=this;Requester.userData() }
    val firstName by data
    val lastName by data
    val link by data
    val city by data
    val school by data
    val classN by data
    val year by data
    val rawCourses by data
    val lastLogin by data
    val id by data
}