package ru.temoteam.informatics.api

import khttp.post
import khttp.responses.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import ru.temoteam.informatics.api.objects.*
import java.util.*


/**
 * Created by a1exS on 2/18/2018.
 */
object Requester{
    private val baseRequestUrl = "http://informatics.mccme.ru"
    var account:Account? = null

    val mainPage:Response by lazy { baseReq("", emptyMap()) }

    fun baseReq(patch:String,params:Map<String,String>?,data:Any?):Response =
            post(if (patch.contains("http://")) patch else baseRequestUrl+patch, account?.headers ?: emptyMap(),params ?: emptyMap(),data)

    fun baseReq(patch:String,params:Map<String,String>?):Response =
            baseReq(patch,params,null)

    fun baseReq(patch:String):Response =
            baseReq(patch,null,null)

    fun login(username:String,password:String):Account? =
            try { Account(username,password,post(baseRequestUrl +"/login/index.php", emptyMap(), mapOf("username" to username,"password" to password)).headers["Set-Cookie"]!!) }
            catch (e:Exception){ null }

    fun userData():Map<String,String>{
        val res = HashMap<String,String>()
        val page = Jsoup.parse(mainPage.text)
        res["link"] = page.body().getElementById("header-home").getElementsByClass("logininfo").select("a").attr("href")
        val (firstName,lastName) = page.body().getElementById("header-home").getElementsByClass("logininfo").select("a").text().split(" ")
        res["firstName"] = firstName
        res["lastName"] = lastName
        val userdata = Jsoup.parse(baseReq(res["link"]!!).text).body().getElementById("content").getElementsByClass("userinfobox")[0].getElementsByClass("list")[0].getElementsByClass("info c1")
        res["city"] = userdata[0].text()
        res["school"] = userdata[1].text()
        res["classN"] = userdata[2].text()
        res["year"] = userdata[3].text()
        res["rawCourses"] = userdata[5].html()
        res["lastLogin"] = userdata[6].text()
        res["id"] = res["link"]!!.substringAfter("?id=").substringBefore("&")
        return res
    }

    fun courses():Map<String,Any>{
        val res = HashMap<String,Any>()
        val page = Jsoup.parse(mainPage.text).body()
        page.getElementsByClass("tree")[0].select("div > ul > li").forEach {res[it.getElementsByClass("node category")[0].text()] = course(it.select("#${it.id()} > ul > li"))}
        return res
    }

    private fun course(elements: Elements):Map<String,Any>{
        val res = HashMap<String,Any>()
        elements.forEach {
            val nodes = it.getElementsByClass("node ")
            if (nodes.isEmpty()){
                val a = it.select("a")
                res[a.text()] = a.attr("href")
            }
                res[nodes[0].text()] = course(it.select("#${it.id()} > ul > li"))
        }
        return res
    }
}