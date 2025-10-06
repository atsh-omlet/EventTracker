package com.cs360.eventtrackeratsushi.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

class Event : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var title: String = ""
    var date: String = ""
    var userId: Int = 0

    constructor()

    constructor(id: Int, title: String, date: String, userId: Int) : this() {
        this.id = id
        this.title = title
        this.date = date
        this.userId = userId
    }


    fun getFormattedDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd 'at' h:mm a", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            date
        }
    }
}
