package com.atsushi.event_tracker.database

import android.content.Context
import com.atsushi.event_tracker.model.Event
import com.atsushi.event_tracker.model.User
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.query.Sort


/**
 * Database layer
 */
class DatabaseHelper private constructor() {

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
                instance ?: DatabaseHelper().also { instance = it }
            }
        }
    }

    init {
        val config = RealmConfiguration.Builder(
            schema = setOf(Event::class, User::class)
        )
            .name("event_tracker.realm")
            .schemaVersion(1)
            .build()

        realm = Realm.open(config)
    }

    // --------------------------
    //  USER METHODS
    // --------------------------
    /**
     * Creates a new user in the database.
     */
    fun createUser(username: String, password: String): Boolean {
        if (checkUsernameExists(username)) return false
        val maxIdValue: Int? = realm.query(User::class).max("id", Int::class).find()

        val nextId = (maxIdValue ?: 0) + 1
        realm.writeBlocking {
            copyToRealm(User(nextId, username, password))
        }
        return true
    }

    /**
     * Updates the password of a user in the database.
     */
    fun updatePassword(userId: Int, newPassword: String): Boolean {
        return realm.writeBlocking {
            val user = query(User::class, "id == $0", userId).find().firstOrNull()
            if (user != null) {
                user.password = newPassword
                true
            } else {
                false
            }
        }
    }

    /**
     * Checks if a username already exists in the database.
     */
    fun checkUsernameExists(username: String): Boolean {
        return realm.query(User::class, "username == $0", username).find().isNotEmpty()
    }


    /**
     * Gets the password of a user from the database.
     */
    fun getPasssword(username: String): String{
        return realm.query(User::class, "username == $0", username).find().first().password
    }

    /**
     * Gets the id of a user from the database.
     */
    fun getUserId(username: String): Int {
        val user = realm.query(User::class, "username == $0", username).find().firstOrNull()
        return user?.id ?: -1
    }

    /**
     * Deletes a user from the database.
     */
    fun deleteUser(userId: Int): Boolean {
        return realm.writeBlocking {
            // Delete associated events first
            val events = query(Event::class, "userId == $0", userId).find()
            delete(events)

            // Then delete the user
            val user = query(User::class, "id == $0", userId).find().firstOrNull()
            val userDeleted = user != null
            user?.let { delete(it) }

            userDeleted
        }
    }

    // --------------------------
    //  EVENT METHODS
    // --------------------------

    /**
     * Creates a new event in the database.
     */
    fun createEvent(title: String, date: String, userId: Int): Boolean {
        val maxIdValue: Int? = realm.query(Event::class).max("id", Int::class).find()
        val nextId = (maxIdValue ?: 0) + 1
        realm.writeBlocking {
            copyToRealm(Event(nextId, title, date, userId))
        }
        return true
    }

    /**
     * Gets an event from the database.
     */
    fun getEvent(id: Int): Event? {
        val e = realm.query(Event::class, "id == $0", id).find().firstOrNull()
        return e?.let { Event(it.id, it.title, it.date, it.userId) }
    }

    /**
     * Updates an event in the database.
     */
    fun updateEvent(eventId: Int, newTitle: String, newDate: String): Boolean {
        var updated = false
        realm.writeBlocking {
            val event = query(Event::class, "id == $0", eventId).find().firstOrNull()
            if (event != null) {
                event.title = newTitle
                event.date = newDate
                updated = true
            }
        }
        return updated
    }

    /**
     * Deletes an event from the database.
     */
    fun deleteEvent(eventId: Int): Boolean {
        var deleted = false;
        realm.writeBlocking {
            val event = query(Event::class, "id == $0", eventId).find().firstOrNull()
            if (event != null) {
                delete(event)
                deleted = true
            }
        }
        return deleted
    }

    /**
     * Deletes all events associated with a user from the database.
     */
    fun deleteAllEvents(userId: Int): Boolean {
        return realm.writeBlocking {
            val events = query(Event::class, "userId == $0", userId)
            val hadEvents = events.find().isNotEmpty()
            delete(events)
            hadEvents
        }
    }

    /**
     * Gets the last event id in the database.
     */
    fun getLastEventId(): Int {
        val lastEvent = realm.query(Event::class)
            .sort("id", Sort.DESCENDING)
            .first()
            .find()
        return lastEvent?.id ?: 0
    }

    /**
     * Gets all events associated with a user from the database.
     */
    fun getEventsForUser(userId: Int): ArrayList<Event> {
        val results = realm.query(Event::class, "userId == $0", userId)
            .sort("date", Sort.ASCENDING)
            .find()

        val list = results.map { e ->
            Event(e.id, e.title, e.date, e.userId)
        }
        return ArrayList(list)
    }


}