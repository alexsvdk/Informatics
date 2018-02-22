package ru.temoteam.informatics.api.objects

import ru.temoteam.informatics.api.Requester
import java.net.HttpCookie
import kotlin.reflect.KProperty

/**
 * Created by a1exS on 2/18/2018.
 */
class Account(val username: String,val password: String,val rawCookie:String) {
    val MoodleSessionTest = rawCookie.substringAfter("MoodleSessionTest=").substringBefore(";")
    val MoodleSession = rawCookie.substringAfter("MoodleSession=").substringBefore(";")
    val headers = mapOf("Cookie" to "MoodleSessionTest=$MoodleSessionTest; MoodleSession=$MoodleSession; jsMath=font%3Asymbol%2Cwarn%3A0",
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
            "Connection" to "keep-alive",
            "Upgrade-Insecure-Requests" to "1",
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36",
            "Cache-Control" to "max-age=0")
    var data:Map<String,String>? = Requester.userData(this)
    val firstName by getfield()
    val lastName by getfield()
    val link by getfield()
    val city by getfield()
    val school by getfield()
    val classN by getfield()
    val year by getfield()
    val rawCourses by getfield()
    val lastLogin by getfield()
    val id by getfield()

    fun getfield(){}

    operator fun Any.getValue(account: Account, property: KProperty<*>): Any? {
        if (data==null) data = Requester.userData(account)
        return if (data!=null) data as Map<String, String> else null
    }
}

