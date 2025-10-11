package com.cs360.eventtrackeratsushi.database

import android.content.Context
import com.cs360.eventtrackeratsushi.model.Event
import com.cs360.eventtrackeratsushi.model.User
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.query.Sort


class DatabaseHelper private constructor(context: Context) {

    private val realm: Realm

    /**
     * Singleton instance of the DatabaseHelper class.
     */
    companion object{
        @Volatile
        private var instance: DatabaseHelper? = null
        @JvmStatic
        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    init {
        val config = RealmConfiguration.Builder(
            schema = setOf(Event::class, User::class)
        )
            .name("event_tracker_FIXED.realm")
            .schemaVersion(1)
            .build()

        realm = Realm.open(config)
    }

    // --------------------------
    //  USER METHODS
    // --------------------------
    fun createUser(username: String, password: String): Boolean {
        if (checkUsernameExists(username)) return false
        val maxIdValue: Int? = realm.query(User::class).max("id", Int::class).find()

        val nextId = (maxIdValue ?: 0) + 1
        realm.writeBlocking {
            copyToRealm(User(nextId, username, password))
        }
        return true
    }

    fun checkUsernameExists(username: String): Boolean {
        return realm.query(User::class, "username == $0", username).find().isNotEmpty()
    }


    fun checkUser(username: String, password: String): Boolean {
        return realm.query(User::class, "username == $0 AND password == $1", username, password)
            .find().isNotEmpty()

    }

    fun getPasssword(username: String): String{
        return realm.query(User::class, "username == $0", username).find().first().password
    }

    fun getUserId(username: String): Int {
        val user = realm.query(User::class, "username == $0", username).find().firstOrNull()
        return user?.id ?: -1
    }

    // --------------------------
    //  EVENT METHODS
    // --------------------------
    fun createEvent(title: String, date: String, userId: Int): Boolean {
        val maxIdValue: Int? = realm.query(Event::class).max("id", Int::class).find()
        val nextId = (maxIdValue ?: 0) + 1
        realm.writeBlocking {
            copyToRealm(Event(nextId, title, date, userId))
        }
        return true
    }


    fun getEvent(id: Int): Event? {
        val e = realm.query(Event::class, "id == $0", id).find().firstOrNull()
        return e?.let { Event(it.id, it.title, it.date, it.userId) }
    }


    fun updateEvent(eventId: Int, newTitle: String, newDate: String): Boolean {
        realm.writeBlocking {
            val event = query(Event::class, "id == $0", eventId).find().firstOrNull()
            event?.apply {
                title = newTitle
                date = newDate
            }
        }
        return true
    }


    fun deleteEvent(eventId: Int): Boolean {
        realm.writeBlocking {
            val event = query(Event::class, "id == $0", eventId).find().firstOrNull()
            event?.let { delete(it) }
        }

        return true
    }

    fun getLastEventId(): Int {
        val lastEvent = realm.query(Event::class)
            .sort("id", Sort.DESCENDING)
            .first()
            .find()
        return lastEvent?.id ?: 0
    }
    fun getEventsForUser(userId: Int): ArrayList<Event> {
        val results = realm.query(Event::class, "userId == $0", userId)
            .sort("date", Sort.ASCENDING)
            .find()

        val list = ArrayList<Event>()
        results.forEach { e -> list.add(Event(e.id, e.title, e.date, e.userId)) }
        return list
    }


}