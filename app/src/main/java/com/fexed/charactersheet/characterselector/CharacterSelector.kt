package com.fexed.charactersheet.characterselector

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fexed.charactersheet.DataLoader
import com.fexed.charactersheet.MainActivity
import com.fexed.charactersheet.R
import com.fexed.charactersheet.Settings
import com.fexed.charactersheet.character.Character
import com.fexed.charactersheet.character.dnd5e.Class
import com.fexed.charactersheet.character.dnd5e.DnD5eCharacter
import com.fexed.charactersheet.character.dnd5e.Race
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.animations.BalloonRotateDirection
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.NumberFormatException

class CharacterSelector : AppCompatActivity() {
    private lateinit var charactersAdapter: CharactersRecyclerViewAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbarmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val loadChar = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        //TODO handle different types of characters
        try {
            val instream = uri?.let { contentResolver.openInputStream(it) }
            val gson = Gson()
            var json = ""
            for (str in instream?.bufferedReader()!!.readLines()) {
                json += str
            }
            val character = gson.fromJson(json, DnD5eCharacter::class.java)
            saveAndClose(character)
        } catch (ex: Exception) {
            Toast.makeText(this, R.string.importerror, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_loader)

        findViewById<FloatingActionButton>(R.id.newcharacterfab)!!.visibility = View.INVISIBLE
        findViewById<FloatingActionButton>(R.id.characterremovefab)!!.visibility = View.INVISIBLE
        findViewById<FloatingActionButton>(R.id.characterloadfab)!!.visibility = View.INVISIBLE
        findViewById<FloatingActionButton>(R.id.characteroptionsfab)!!.visibility = View.VISIBLE
        findViewById<FloatingActionButton>(R.id.characterclearfab)!!.visibility = View.INVISIBLE

        val files = filesDir.listFiles()
        val savedcharacters : MutableList<Character> = mutableListOf()
        val uris : MutableList<Uri> = mutableListOf()
        if (files != null) {
            for (file in files) {
                if (file.extension == "char") {
                    try {
                        val character = DnD5eCharacter.load(this, file.name)
                        savedcharacters.add(character)
                        uris.add(FileProvider.getUriForFile(this, "com.fexed.charactersheet.FileProvider", file))
                    } catch (e: Exception) {
                        //TODO try other characters
                    }
                }
            }
        }

        if (savedcharacters.size == 0) {
            findViewById<FloatingActionButton>(R.id.newcharacterfab)!!.visibility = View.VISIBLE
            findViewById<FloatingActionButton>(R.id.characterremovefab)!!.visibility = View.VISIBLE
            findViewById<FloatingActionButton>(R.id.characterloadfab)!!.visibility = View.VISIBLE

            val newcharacterprompt = Balloon.Builder(this)
                .setText(getString(R.string.createnewcharacterprompt))
                .setPadding(16)
                .setArrowOrientation(ArrowOrientation.END)
                .build()
            findViewById<FloatingActionButton>(R.id.characterremovefab)!!.post {
                newcharacterprompt.showAlignLeft(findViewById<FloatingActionButton>(R.id.newcharacterfab)!!)
            }
        }

        findViewById<FloatingActionButton>(R.id.characteroptionsfab)!!.setOnClickListener {
            if (findViewById<FloatingActionButton>(R.id.newcharacterfab)!!.visibility == View.INVISIBLE) {
                findViewById<FloatingActionButton>(R.id.newcharacterfab)!!.visibility = View.VISIBLE
                findViewById<FloatingActionButton>(R.id.characterremovefab)!!.visibility = View.VISIBLE
                findViewById<FloatingActionButton>(R.id.characterloadfab)!!.visibility = View.VISIBLE
            } else {
                findViewById<FloatingActionButton>(R.id.newcharacterfab)!!.visibility = View.INVISIBLE
                findViewById<FloatingActionButton>(R.id.characterremovefab)!!.visibility = View.INVISIBLE
                findViewById<FloatingActionButton>(R.id.characterloadfab)!!.visibility = View.INVISIBLE
            }
        }


        findViewById<FloatingActionButton>(R.id.characterloadfab)!!.setOnClickListener {
            findViewById<FloatingActionButton>(R.id.newcharacterfab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characterremovefab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characterloadfab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characteroptionsfab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characterclearfab)!!.visibility = View.VISIBLE
            loadChar.launch("*/*")
        }


        findViewById<FloatingActionButton>(R.id.characterremovefab)!!.setOnClickListener {
            findViewById<FloatingActionButton>(R.id.newcharacterfab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characterremovefab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characterloadfab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characteroptionsfab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characterclearfab)!!.visibility = View.VISIBLE
            charactersAdapter = CharactersRecyclerViewAdapter(savedcharacters, uris, true, this)
            val charactersrecv = findViewById<RecyclerView>(R.id.charactersrecv)!!
            charactersrecv.adapter = null
            charactersrecv.adapter = charactersAdapter
        }

        findViewById<FloatingActionButton>(R.id.characterclearfab)!!.setOnClickListener {
            findViewById<FloatingActionButton>(R.id.characterclearfab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characteroptionsfab)!!.visibility = View.VISIBLE
            charactersAdapter = CharactersRecyclerViewAdapter(savedcharacters, uris, false, this)
            val charactersrecv = findViewById<RecyclerView>(R.id.charactersrecv)!!
            charactersrecv.adapter = null
            charactersrecv.adapter = charactersAdapter
        }
        
        findViewById<FloatingActionButton>(R.id.newcharacterfab).setOnClickListener {
            findViewById<FloatingActionButton>(R.id.newcharacterfab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characterremovefab)!!.visibility = View.INVISIBLE
            findViewById<FloatingActionButton>(R.id.characterloadfab)!!.visibility = View.INVISIBLE
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.newcharacter)
            var selectedrace = Race("", arrayOf(""), false)

            dialog.findViewById<ImageButton>(R.id.raceselectbtn).setOnClickListener {
                val builder = Dialog(this)
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
                builder.setContentView(R.layout.dbitemselectdialog)
                builder.findViewById<TextView>(R.id.dbselecttitle).text = getString(R.string.selectrace)
                val spinnderadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, this.resources.getStringArray(R.array.selectablerace))
                builder.findViewById<Spinner>(R.id.dbselectspinner).adapter = spinnderadapter

                builder.findViewById<EditText>(R.id.dbselectsearchbox).doAfterTextChanged { txt ->
                    var list = this.resources.getStringArray(R.array.selectablerace).toList()
                    list = list.filter { it.lowercase().contains(txt.toString().lowercase()) }
                    val newadapt = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
                    builder.findViewById<Spinner>(R.id.dbselectspinner).adapter = newadapt
                }

                builder.findViewById<Button>(R.id.dbitemconfirm).setOnClickListener { _ ->
                    builder.dismiss()
                    val selecteditem = builder.findViewById<Spinner>(R.id.dbselectspinner).selectedItem
                    val selected =resources.getStringArray(R.array.selectablerace).indexOf(selecteditem)
                    dialog.findViewById<EditText>(R.id.newcharrace).setText(when (selected) {
                        0 -> ""
                        else -> resources.getStringArray(R.array.selectablerace)[selected]
                    })
                    selectedrace = when (selected) {
                        0 -> Race("", arrayOf(""), false)
                        else -> Race.standardraces[selected-1]
                    }
                }

                builder.show()
            }

            var selectedclass = Class.emptyclass

            dialog.findViewById<ImageButton>(R.id.classselectbtn).setOnClickListener {
                val builder = Dialog(this)
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
                builder.setContentView(R.layout.dbitemselectdialog)
                builder.findViewById<TextView>(R.id.dbselecttitle).text = getString(R.string.selectclass)
                val spinnderadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, this.resources.getStringArray(R.array.selectableclasses))
                builder.findViewById<Spinner>(R.id.dbselectspinner).adapter = spinnderadapter

                builder.findViewById<EditText>(R.id.dbselectsearchbox).doAfterTextChanged { txt ->
                    var list = this.resources.getStringArray(R.array.selectableclasses).toList()
                    list = list.filter { it.lowercase().contains(txt.toString().lowercase()) }
                    val newadapt = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
                    builder.findViewById<Spinner>(R.id.dbselectspinner).adapter = newadapt
                }

                builder.findViewById<Button>(R.id.dbitemconfirm).setOnClickListener { _ ->
                    builder.dismiss()
                    val selecteditem = builder.findViewById<Spinner>(R.id.dbselectspinner).selectedItem
                    val selected =resources.getStringArray(R.array.selectableclasses).indexOf(selecteditem)
                    dialog.findViewById<EditText>(R.id.newcharclass).setText(when (selected) {
                        0 -> ""
                        else -> resources.getStringArray(R.array.selectableclasses)[selected]
                    })
                    selectedclass = when (selected) {
                        0 -> Class.emptyclass
                        else -> Class.standardclasses[selected-1]
                    }
                }

                builder.show()
            }

            dialog.findViewById<Button>(R.id.newcharconfirm).setOnClickListener {
                val nametxtv = dialog.findViewById<EditText>(R.id.newcharname)
                val classtxtv = dialog.findViewById<EditText>(R.id.newcharclass)
                val racetxtv = dialog.findViewById<EditText>(R.id.newcharrace)
                val lvtxtv = dialog.findViewById<EditText>(R.id.newcharlevel)
                val name = nametxtv.text.toString()
                val clas = classtxtv.text.toString()
                val race = racetxtv.text.toString()
                try {
                    val level = lvtxtv.text.toString().toInt()

                    if (name == "") {
                        nametxtv.error = getString(R.string.pgnameerror)
                    } else {
                        if (clas == "") {
                            classtxtv.error = getString(R.string.pgclasserror)
                        } else {
                            if (race == "") {
                                racetxtv.error = getString(R.string.pgraceerror)
                            } else {
                                dialog.dismiss()
                                val dummyclass = Class.emptyclass
                                dummyclass.name = clas
                                val character = when(dialog.findViewById<Spinner>(R.id.newchartype).selectedItemPosition) {
                                    0 -> DnD5eCharacter(name, dummyclass, race, level, null, xp = 0,
                                        STR = 10, DEX = 10, CON = 10, INT = 10, WIS = 10, CHA = 10,
                                        CurrHP = 0, MaxHP = 0, AC = 10, spellstat =  -1,
                                        tsstr = false, tsdex = false, tscon = false, tsint = false, tswis = false, tscha = false,
                                        atletica_comp = false, atletica_exp = false,
                                        acrobazia_comp = false, acrobazia_exp = false,
                                        furtivita_comp = false, furtivita_exp = false,
                                        rapdimano_comp = false, rapdimano_exp = false,
                                        investigare_comp = false, investigare_exp = false,
                                        arcano_comp = false, arcano_exp = false,
                                        storia_comp =  false, storia_exp = false,
                                        religione_comp = false, religione_exp = false,
                                        natura_comp = false, natura_exp = false,
                                        sopravv_comp = false, sopravviv_exp = false,
                                        medicina_comp = false, medicina_exp = false,
                                        percezione_comp = false, percezione_exp = false,
                                        intuizione_comp = false, intuizione_exp = false,
                                        animali_comp = false, animali_exp = false,
                                        intimidire_comp = false, intimidire_exp = false,
                                        ingannare_comp = false, ingannare_exp = false,
                                        intrattenere_comp = false, intrattenere_exp = false,
                                        persuadere_comp = false, persuadere_exp = false,
                                        nslot1 = 0, nslot2 = 0, nslot3 = 0, nslot4 = 0, nslot5 = 0,
                                        nslot6 = 0, nslot7 = 0, nslot8 = 0, nslot9 = 0,
                                        currslot1 = 0, currslot2 = 0, currslot3 = 0, currslot4 = 0, currslot5 = 0,
                                        currslot6 = 0, currslot7 = 0, currslot8 = 0, currslot9 = 0,
                                        notes = ""
                                    )
                                    else -> DnD5eCharacter(name, dummyclass, race, level, null, xp = 0,
                                        STR = 10, DEX = 10, CON = 10, INT = 10, WIS = 10, CHA = 10,
                                        CurrHP = 0, MaxHP = 0, AC = 10, spellstat =  -1,
                                        tsstr = false, tsdex = false, tscon = false, tsint = false, tswis = false, tscha = false,
                                        atletica_comp = false, atletica_exp = false,
                                        acrobazia_comp = false, acrobazia_exp = false,
                                        furtivita_comp = false, furtivita_exp = false,
                                        rapdimano_comp = false, rapdimano_exp = false,
                                        investigare_comp = false, investigare_exp = false,
                                        arcano_comp = false, arcano_exp = false,
                                        storia_comp =  false, storia_exp = false,
                                        religione_comp = false, religione_exp = false,
                                        natura_comp = false, natura_exp = false,
                                        sopravv_comp = false, sopravviv_exp = false,
                                        medicina_comp = false, medicina_exp = false,
                                        percezione_comp = false, percezione_exp = false,
                                        intuizione_comp = false, intuizione_exp = false,
                                        animali_comp = false, animali_exp = false,
                                        intimidire_comp = false, intimidire_exp = false,
                                        ingannare_comp = false, ingannare_exp = false,
                                        intrattenere_comp = false, intrattenere_exp = false,
                                        persuadere_comp = false, persuadere_exp = false,
                                        nslot1 = 0, nslot2 = 0, nslot3 = 0, nslot4 = 0, nslot5 = 0,
                                        nslot6 = 0, nslot7 = 0, nslot8 = 0, nslot9 = 0,
                                        currslot1 = 0, currslot2 = 0, currslot3 = 0, currslot4 = 0, currslot5 = 0,
                                        currslot6 = 0, currslot7 = 0, currslot8 = 0, currslot9 = 0,
                                        notes = ""
                                    )
                                }
                                if (dialog.findViewById<Spinner>(R.id.newchartype).selectedItemPosition == 0) {
                                    handlerace(character, selectedrace, selectedclass)
                                    // DnD 5e

                                } else { // not DnD 5e
                                    saveAndClose(character)
                                }
                            }
                        }
                    }
                } catch (e: NumberFormatException) {
                    lvtxtv.error = getString(R.string.pglverror)
                }
            }

            dialog.show()
        }

        val charactersrecv = findViewById<RecyclerView>(R.id.charactersrecv)!!
        charactersrecv.layoutManager = LinearLayoutManager(this)
        charactersAdapter = CharactersRecyclerViewAdapter(savedcharacters, uris,false, this)
        charactersrecv.adapter = null
        charactersrecv.adapter = charactersAdapter
        charactersrecv.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    fun saveAndClose(character: Character) {
        character.save(this)
        DataLoader.loadCharacter(character)
        val intent = Intent(this, MainActivity::class.java)
        val firebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString("name", character.pgname)
        bundle.putString("race", character.pgrace)
        bundle.putString("class", character.pgclass)
        bundle.putString("level", character.pglv.toString())
        firebaseAnalytics.logEvent("new_character", bundle)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
        startActivity(intent)
        finish()
    }

    fun handlerace(character: DnD5eCharacter, selectedrace: Race, selectedclass: Class) {
        if (selectedrace.selecttwo) {
            val builder = Dialog(this)
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
            builder.setContentView(R.layout.selectcustombonuses)
            builder.findViewById<Button>(R.id.selectablestatok).setOnClickListener {
                val bonus1 = when (builder.findViewById<Spinner>(R.id.selectablestat1).selectedItemPosition) {
                    1 -> "STR"
                    2 -> "DEX"
                    3 -> "CON"
                    4 -> "INT"
                    5 -> "WIS"
                    6 -> "CHA"
                    else -> "None"
                } + ":1"
                val bonus2 = when (builder.findViewById<Spinner>(R.id.selectablestat2).selectedItemPosition) {
                    1 -> "STR"
                    2 -> "DEX"
                    3 -> "CON"
                    4 -> "INT"
                    5 -> "WIS"
                    6 -> "CHA"
                    else -> "None"
                } + ":1"
                if (selectedrace.bonuses != null) {
                    selectedrace.bonuses = selectedrace.bonuses!! + arrayOf(bonus1, bonus2)
                } else {
                    selectedrace.bonuses = arrayOf(bonus1, bonus2)
                }
                for (bonus in selectedrace.bonuses!!) {
                    character.racialbonuses.add(bonus)
                }
                builder.dismiss()

                handleclass(character, selectedclass)
            }
            builder.show()
        } else {
            for (bonus in selectedrace.bonuses!!) {
                character.racialbonuses.add(bonus)
            }

            handleclass(character, selectedclass)
        }
    }

    fun handleclass(character: DnD5eCharacter, selectedclass: Class) {
        character.linkedclass = selectedclass
        character.tsstr = selectedclass.savesprofs[0]
        character.tsdex = selectedclass.savesprofs[1]
        character.tscon = selectedclass.savesprofs[2]
        character.tsint = selectedclass.savesprofs[3]
        character.tswis = selectedclass.savesprofs[4]
        character.tscha = selectedclass.savesprofs[5]
        character.spellstat = selectedclass.spellstat
        character.nslot1 = selectedclass.spellslots[character.pglv-1][0]
        character.currslot1 = selectedclass.spellslots[character.pglv-1][0]
        character.nslot2 = selectedclass.spellslots[character.pglv-1][1]
        character.currslot2 = selectedclass.spellslots[character.pglv-1][1]
        character.nslot3 = selectedclass.spellslots[character.pglv-1][2]
        character.currslot3 = selectedclass.spellslots[character.pglv-1][2]
        character.nslot4 = selectedclass.spellslots[character.pglv-1][3]
        character.currslot4 = selectedclass.spellslots[character.pglv-1][3]
        character.nslot5 = selectedclass.spellslots[character.pglv-1][4]
        character.currslot5 = selectedclass.spellslots[character.pglv-1][4]
        character.nslot6 = selectedclass.spellslots[character.pglv-1][5]
        character.currslot6 = selectedclass.spellslots[character.pglv-1][5]
        character.nslot7 = selectedclass.spellslots[character.pglv-1][6]
        character.currslot7 = selectedclass.spellslots[character.pglv-1][6]
        character.nslot8 = selectedclass.spellslots[character.pglv-1][7]
        character.currslot8 = selectedclass.spellslots[character.pglv-1][7]
        character.nslot9 = selectedclass.spellslots[character.pglv-1][8]
        character.currslot9 = selectedclass.spellslots[character.pglv-1][8]
        if (selectedclass.proftoselect > 0) {
            promptproficiencies(character, selectedclass.selectableprofs.sortedArray(), selectedclass.proftoselect)
        } else {
            saveAndClose(character)
        }
    }

    fun promptproficiencies(character: DnD5eCharacter, selectableprofs: Array<Int>, num : Int) {
        val profbuilder = Dialog(this)
        profbuilder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        profbuilder.setContentView(R.layout.selectstat)
        profbuilder.findViewById<TextView>(R.id.spellstattitle).text = getString(R.string.selectproficiency, num.toString())

        var selectablestats = arrayOf<String>()
        for (j in selectableprofs) {
            selectablestats += resources.getStringArray(R.array.selectableskills)[j]
        }
        val spinnderadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, selectablestats)
        profbuilder.findViewById<Spinner>(R.id.spellstatspinner).adapter = spinnderadapter
        profbuilder.findViewById<Button>(R.id.spellstatselect_confirm).setOnClickListener {
            when (selectableprofs[profbuilder.findViewById<Spinner>(R.id.spellstatspinner).selectedItemPosition]) {
                0 -> character.atletica_comp = true
                1 -> character.acrobazia_comp = true
                2 -> character.furtivita_comp = true
                3 -> character.rapdimano_comp = true
                4 -> character.investigare_comp = true
                5 -> character.arcano_comp = true
                6 -> character.storia_comp = true
                7 -> character.religione_comp = true
                8 -> character.natura_comp = true
                9 -> character.sopravv_comp = true
                10 -> character.medicina_comp = true
                11 -> character.percezione_comp = true
                12 -> character.intuizione_comp = true
                13 -> character.animali_comp = true
                14 -> character.intimidire_comp = true
                15 -> character.ingannare_comp = true
                16 -> character.intrattenere_comp = true
                17 -> character.persuadere_comp = true
            }
            profbuilder.dismiss()
            if (num - 1 == 0) {
                saveAndClose(character)
            } else {
                val newprofs = selectableprofs.copyOfRange(0, profbuilder.findViewById<Spinner>(R.id.spellstatspinner).selectedItemPosition) + selectableprofs.copyOfRange(profbuilder.findViewById<Spinner>(R.id.spellstatspinner).selectedItemPosition + 1, selectableprofs.size)
                promptproficiencies(character, newprofs, num-1)
            }
        }
        profbuilder.show()
    }
}