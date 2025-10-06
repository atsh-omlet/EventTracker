package com.cs360.eventtrackeratsushi.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import io.realm.kotlin.types.annotations.Index

class User : RealmObject {
    constructor()
    constructor(id: Int, username: String, password: String) {
        this.id = id
        this.username = username
        this.password = password
    }

    @PrimaryKey
    var id: Int = 0

    @Index
    var username: String = ""

    var password: String = ""
}
