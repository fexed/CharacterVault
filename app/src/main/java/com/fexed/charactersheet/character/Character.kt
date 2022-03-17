package com.fexed.charactersheet.character

import android.content.Context
import java.io.ObjectInputStream

abstract class Character (
    var pgname : String,
    var pgclass : String,
    var pgrace : String,
    var pglv : Int,
    var portrait : String?,
    var notes : String,
) {
    abstract fun save(context: Context)

    companion object {
        fun load(context: Context, filename: String): Character {
            context.openFileInput(filename).use {
                val os = ObjectInputStream(it)
                return os.readObject() as Character
            }
        }
    }
}