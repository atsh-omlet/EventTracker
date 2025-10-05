package com.cs360.eventtrackeratsushi
import android.app.Application
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApplication : Application() {

    lateinit var realm: Realm

    override fun onCreate() {
        super.onCreate()

        val config = RealmConfiguration.Builder(schema = setOf())
            .name("event_tracker.realm")
            .build()

        realm = Realm.open(config)
    }

    override fun onTerminate() {
        super.onTerminate()
        realm.close()
    }
}