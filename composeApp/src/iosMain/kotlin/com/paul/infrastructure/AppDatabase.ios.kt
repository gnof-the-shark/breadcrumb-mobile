package com.paul.infrastructure

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSString
import platform.Foundation.stringByAppendingPathComponent

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val documents = (NSHomeDirectory() as NSString).stringByAppendingPathComponent("Documents")
    val dbFilePath = (documents as NSString).stringByAppendingPathComponent("strava_room.db")
    return Room.databaseBuilder<AppDatabase>(name = dbFilePath)
}
