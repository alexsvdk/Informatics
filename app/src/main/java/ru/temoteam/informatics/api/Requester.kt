package ru.temoteam.informatics.api

import khttp.get
import khttp.post
import khttp.responses.Response
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import ru.temoteam.informatics.api.objects.*
import java.net.URL
import java.util.*
import kotlin.collections.HashMap


/**
 * Created by a1exS on 2/18/2018.
 */
object Requester{
    private val baseRequestUrl = "http://informatics.mccme.ru"
    var account:Account? = null

    val mainPage:Response by lazy { baseReq("", emptyMap()) }

    fun baseReq(patch:String,params:Map<String,String>?,data:Any?):Response =
            post(if (patch.startsWith("http://")) patch else baseRequestUrl+patch, account?.headers ?: emptyMap(),params ?: emptyMap(),data)

    fun baseReq(patch:String,params:Map<String,String>?):Response =
            baseReq(patch,params,null)

    fun baseReq(patch:String):Response =
            baseReq(patch,null,null)

    fun login(username:String,password:String):Account? =
            try { Account(username,password,post(baseRequestUrl +"/login/index.php", emptyMap(), mapOf("username" to username,"password" to password)).headers["Set-Cookie"]!!) }
            catch (e:Exception){e.printStackTrace();null }

    fun userData():Map<String,String>?{
        try {
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
        catch (e:Exception){return null}

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
                res[a.text()] = a.attr("href").substringAfter("id=")
            }
            else{
                res[nodes[0].text()] = course(it.select("#${it.id()} > ul > li"))
            }
        }
        return res
    }

    fun tasklists(courseId:String):List<Pair<String,String>>{
        val ts = baseReq("/course/view.php?id="+courseId).text.split("<a title=\"Условия задач\"   href=\"")
        return (1 until ts.size).mapTo(LinkedList()) { Pair(ts[it].substringAfter("\">").substringBefore("</a>"),ts[it].substringAfter("id=").substringBefore("\"")) }
    }

    fun tasklist(tasklistId:String):List<Pair<String,String>>{
        val res = LinkedList<Pair<String,String>>()
        val ts = Jsoup.parse(URL("http://informatics.mccme.ru//mod/statements/view.php?id="+tasklistId),2000)
        val title = ts.getElementsByClass("statements_chapter_title")[0].ownText()
        res.add(Pair(title.substringAfter(". "),title.substringAfter("№").substringBefore(".")))
        ts.getElementsByClass("statements_toc_alpha")[0].select("a").forEach {res.add(Pair(it.ownText(),it.attr("href").substringAfter("chapterid=").substringBefore("\"")))}
        return res
    }


    fun task(taskId:String):Task{
        val data = HashMap<String,Any>()
        val page = Jsoup.parse(URL("http://informatics.mccme.ru/mod/statements/view3.php?chapterid="+taskId),2000)
        data["task"] = page.getElementsByClass("legend").text().replace("$","")
        data["input"] = page.getElementsByClass("input-specification").text().replace("$","").replace("Входные данные","").replace("\\le"," ≤").replace("\\,","")
        data["outbut"] = page.getElementsByClass("output-specification").text().replace("$","").replace("Выходные данные","").replace("\\le"," ≤").replace("\\,","")
        val samples = LinkedList<Pair<String,String>>()
        println( page.getElementsByClass("sample-test").size)
        page.getElementsByClass("sample-test").forEach {
            val content = it.getElementsByClass("content")
            samples.add(Pair(content[0].text(),content[1].text()))
        }
        data["samples"] = samples
        return Task(taskId,data)
    }

    fun userData(account: Account): Map<String, String>? {
        this.account=account
        return userData()
    }
}