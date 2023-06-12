package com.fexed.charactersheet

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setTitle(R.string.settings)

        val version = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0).versionName + " (" + applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0).versionCode + ")"
        findViewById<TextView>(R.id.vertxt).text = version

        val sharedPreferences = this.getSharedPreferences(this.getString(R.string.app_package), Context.MODE_PRIVATE)
        findViewById<Button>(R.id.resettutorialbtn).setOnClickListener {
            sharedPreferences.edit().putBoolean("spellstutorial", false).apply()
            sharedPreferences.edit().putBoolean("skillstutorial", false).apply()
            sharedPreferences.edit().putBoolean("summarytutorial", false).apply()
        }

        findViewById<TextView>(R.id.dicethrown).text = sharedPreferences.getInt("dicerolled", 0).toString()
    }
}