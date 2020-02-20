package ru.tsu.huffman

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application(){
    init {
        instance = this
    }

    companion object{
        private var instance : App? = null

        fun applicationContext() : Context{
            return  instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}