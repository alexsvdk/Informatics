package ru.temoteam.informatics.api.objects

/**
 * Created by a1exS on 2/19/2018.
 */
class Task(id:String, data: Map<String,Any>) {
    val timelim:String by data
    val memlim:String by data
    val task:String by data
    val input:String by data
    val outbut:String by data
    val samples:List<Pair<String,String>> by data
}