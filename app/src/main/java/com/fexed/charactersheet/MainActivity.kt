package com.fexed.charactersheet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fexed.charactersheet.character.dnd5e.DnD5eCharacter
import com.fexed.charactersheet.characterselector.CharacterSelector
import com.fexed.charactersheet.databinding.ActivityMainBinding
import com.fexed.charactersheet.ui.CharacterViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navView: BottomNavigationView = binding.navView

        //val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)!!.findNavController()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_summary, R.id.navigation_skills, R.id.navigation_attacks, R.id.navigation_spells, R.id.navigation_inventory
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        MobileAds.initialize(this)
        val adBannerHome = findViewById<AdView>(R.id.bannerHome)
        adBannerHome?.loadAd(AdRequest.Builder().build())
    }

    override fun onStop() {
        super.onStop()
        val viewModel: CharacterViewModel by viewModels()
        viewModel.currentcharacter.value?.save(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.charactermenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changecharacter -> {
                val viewModel: CharacterViewModel by viewModels()
                viewModel.currentcharacter.value!!.save(this)
                val intent = Intent(this, CharacterSelector::class.java)
                startActivity(intent)
            }

            R.id.rest -> {
                val viewModel: CharacterViewModel by viewModels()
                val character = viewModel.currentcharacter.value
                if (character is DnD5eCharacter) {
                    character.refreshall(slots = true, hp = true)
                    viewModel.updateCharacter(character)
                }
                Toast.makeText(this, R.string.resttxt, Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}