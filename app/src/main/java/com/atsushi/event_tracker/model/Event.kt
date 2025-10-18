package com.atsushi.event_tracker.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Event model class
 */
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
}
