package com.fexed.charactersheet.ui.characterattacks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fexed.charactersheet.R
import com.fexed.charactersheet.character.Diceroll
import com.fexed.charactersheet.character.dnd5e.Attack
import com.fexed.charactersheet.character.dnd5e.DnD5eCharacter
import com.fexed.charactersheet.ui.CharacterViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.overlay.BalloonOverlayRect
import org.w3c.dom.Text
import kotlin.math.max

class CharacterSpells : Fragment() {
    companion object {
        fun newInstance() = CharacterSpells()
    }

    private val viewModel: CharacterViewModel by activityViewModels()
    private lateinit var cantripssadapter : AttackRecyclerViewAdapter
    private lateinit var slot1sadapter : AttackRecyclerViewAdapter
    private lateinit var slot2sadapter : AttackRecyclerViewAdapter
    private lateinit var slot3sadapter : AttackRecyclerViewAdapter
    private lateinit var slot4sadapter : AttackRecyclerViewAdapter
    private lateinit var slot5sadapter : AttackRecyclerViewAdapter
    private lateinit var slot6sadapter : AttackRecyclerViewAdapter
    private lateinit var slot7sadapter : AttackRecyclerViewAdapter
    private lateinit var slot8sadapter : AttackRecyclerViewAdapter
    private lateinit var slot9sadapter : AttackRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.character_spells_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.currentcharacter.observe(viewLifecycleOwner, {
            activity?.findViewById<TextView>(R.id.spells_pgname)!!.text = it.pgname
            activity?.findViewById<TextView>(R.id.spells_pglv)!!.text = it.pglv.toString()
            if (it is DnD5eCharacter) {
                val sharedPreferences = context!!.getSharedPreferences(context!!.getString(R.string.app_package), Context.MODE_PRIVATE)
                if (!(sharedPreferences.getBoolean("spellstutorial", false))) {
                    sharedPreferences.edit().putBoolean("spellstutorial", true).apply()
                    val taptorollballoon = Balloon.Builder(context!!)
                        .setText(getString(R.string.taptorolltutorial))
                        .setPadding(16)
                        .setIsVisibleOverlay(true)
                        .setOverlayColorResource(R.color.semitrasp)
                        .setOverlayPadding(8f)
                        .setDismissWhenOverlayClicked(true)
                        .setOverlayShape(BalloonOverlayRect)
                        .build()
                    val taptorollballoon2 = Balloon.Builder(context!!)
                        .setText(getString(R.string.keeptoedit))
                        .setPadding(16)
                        .setArrowPosition(0.33F)
                        .setIsVisibleOverlay(true)
                        .setOverlayColorResource(R.color.semitrasp)
                        .setOverlayPadding(8f)
                        .setDismissWhenOverlayClicked(true)
                        .setOverlayShape(BalloonOverlayRect)
                        .build()
                    taptorollballoon2.relayShowAlignBottom(taptorollballoon, activity?.findViewById<TextView>(R.id.spells_atkbonus)!!)
                    activity?.findViewById<TextView>(R.id.spells_stat)!!.post {
                        taptorollballoon2.showAlignBottom(activity?.findViewById<TextView>(R.id.spells_stat)!!)
                    }
                }

                activity?.findViewById<TextView>(R.id.spells_pgprof)!!.text = (if (it.profbonus >= 0) "+" else "") + it.profbonus.toString()
                activity?.findViewById<TextView>(R.id.spells_atkbonus)!!.text = (if (it.spellatk >= 0) "+" else "") + it.spellatk.toString()
                activity?.findViewById<TextView>(R.id.spells_savedc)!!.text = it.spellcd.toString()
                activity?.findViewById<TextView>(R.id.spells_stat)!!.text = when(it.spellstat) {
                    0 -> getString(R.string.intelligenza)
                    1 -> getString(R.string.saggezza)
                    2 -> getString(R.string.carisma)
                    else -> getString(R.string.none)
                }
                preparespells(false)
                prepareClicks()
                prepareLongClicks()
                preparefab()
            } else {
                //TODO non è PG 5e
            }
        })
    }

    private fun prepareClicks() {
        val charact = viewModel.currentcharacter.value
        if (charact is DnD5eCharacter) {
            activity?.findViewById<TextView>(R.id.spells_atkbonus)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, charact.spellatk), getString(R.string.spellatk))
            }

            if (charact.currslot1 > 0) {
                activity?.findViewById<Button>(R.id.castlv1btn)!!.isEnabled = true
                activity?.findViewById<Button>(R.id.castlv1btn)!!.setOnClickListener {
                    charact.castspell(1)
                    viewModel.updateCharacter(charact)
                }
            } else activity?.findViewById<Button>(R.id.castlv1btn)!!.isEnabled = false

            if (charact.currslot2 > 0) {
                activity?.findViewById<Button>(R.id.castlv2btn)!!.isEnabled = true
                activity?.findViewById<Button>(R.id.castlv2btn)!!.setOnClickListener {
                    charact.castspell(2)
                    viewModel.updateCharacter(charact)
                }
            } else activity?.findViewById<Button>(R.id.castlv2btn)!!.isEnabled = false

            if (charact.currslot3 > 0) {
                activity?.findViewById<Button>(R.id.castlv3btn)!!.isEnabled = true
                activity?.findViewById<Button>(R.id.castlv3btn)!!.setOnClickListener {
                    charact.castspell(3)
                    viewModel.updateCharacter(charact)
                }
            } else activity?.findViewById<Button>(R.id.castlv3btn)!!.isEnabled = false

            if (charact.currslot4 > 0) {
                activity?.findViewById<Button>(R.id.castlv4btn)!!.isEnabled = true
                activity?.findViewById<Button>(R.id.castlv4btn)!!.setOnClickListener {
                    charact.castspell(4)
                    viewModel.updateCharacter(charact)
                }
            } else activity?.findViewById<Button>(R.id.castlv4btn)!!.isEnabled = false

            if (charact.currslot5 > 0) {
                activity?.findViewById<Button>(R.id.castlv5btn)!!.isEnabled = true
                activity?.findViewById<Button>(R.id.castlv5btn)!!.setOnClickListener {
                    charact.castspell(5)
                    viewModel.updateCharacter(charact)
                }
            } else activity?.findViewById<Button>(R.id.castlv5btn)!!.isEnabled = false

            if (charact.currslot6 > 0) {
                activity?.findViewById<Button>(R.id.castlv6btn)!!.isEnabled = true
                activity?.findViewById<Button>(R.id.castlv6btn)!!.setOnClickListener {
                    charact.castspell(6)
                    viewModel.updateCharacter(charact)
                }
            } else activity?.findViewById<Button>(R.id.castlv6btn)!!.isEnabled = false

            if (charact.currslot7 > 0) {
                activity?.findViewById<Button>(R.id.castlv7btn)!!.isEnabled = true
                activity?.findViewById<Button>(R.id.castlv7btn)!!.setOnClickListener {
                    charact.castspell(7)
                    viewModel.updateCharacter(charact)
                }
            } else activity?.findViewById<Button>(R.id.castlv7btn)!!.isEnabled = false

            if (charact.currslot8 > 0) {
                activity?.findViewById<Button>(R.id.castlv8btn)!!.isEnabled = true
                activity?.findViewById<Button>(R.id.castlv8btn)!!.setOnClickListener {
                    charact.castspell(8)
                    viewModel.updateCharacter(charact)
                }
            } else activity?.findViewById<Button>(R.id.castlv8btn)!!.isEnabled = false

            if (charact.currslot9 > 0) {
                activity?.findViewById<Button>(R.id.castlv9btn)!!.isEnabled = true
                activity?.findViewById<Button>(R.id.castlv9btn)!!.setOnClickListener {
                    charact.castspell(9)
                    viewModel.updateCharacter(charact)
                }
            } else activity?.findViewById<Button>(R.id.castlv9btn)!!.isEnabled = false
        }
    }

    private fun prepareLongClicks() {
        val charact = viewModel.currentcharacter.value
        if (charact is DnD5eCharacter) {
            activity?.findViewById<TextView>(R.id.spells_stat)!!.setOnLongClickListener {
                val dialog = Dialog(context!!)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.selectstat)
                dialog.findViewById<Button>(R.id.spellstatselect_confirm)!!.setOnClickListener {
                    dialog.dismiss()
                    charact.spellstat = dialog.findViewById<Spinner>(R.id.spellstatspinner).selectedItemPosition
                    viewModel.updateCharacter(charact)
                }
                dialog.show()

                return@setOnLongClickListener true
            }
            
            activity?.findViewById<TextView>(R.id.level1maxslots)!!.setOnLongClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.insertmaxslots) + " (" + getString(R.string.livello_1) + ")")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setText(charact.nslot1.toString())
                builder.setView(input)

                builder.setPositiveButton(R.string.ok) {_, _ ->
                    val int : Int = input.text.toString().toInt()
                    charact.nslot1 = int
                    viewModel.updateCharacter(charact)
                }
                builder.setNegativeButton(R.string.annulla) {dialog, _ -> dialog.cancel()}

                builder.show()
                return@setOnLongClickListener true
            }

            activity?.findViewById<TextView>(R.id.level2maxslots)!!.setOnLongClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.insertmaxslots) + " (" + getString(R.string.livello_2) + ")")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setText(charact.nslot2.toString())
                builder.setView(input)

                builder.setPositiveButton(R.string.ok) {_, _ ->
                    val int : Int = input.text.toString().toInt()
                    charact.nslot2 = int
                    viewModel.updateCharacter(charact)
                }
                builder.setNegativeButton(R.string.annulla) {dialog, _ -> dialog.cancel()}

                builder.show()
                return@setOnLongClickListener true
            }

            activity?.findViewById<TextView>(R.id.level3maxslots)!!.setOnLongClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.insertmaxslots) + " (" + getString(R.string.livello_3) + ")")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setText(charact.nslot3.toString())
                builder.setView(input)

                builder.setPositiveButton(R.string.ok) {_, _ ->
                    val int : Int = input.text.toString().toInt()
                    charact.nslot3 = int
                    viewModel.updateCharacter(charact)
                }
                builder.setNegativeButton(R.string.annulla) {dialog, _ -> dialog.cancel()}

                builder.show()
                return@setOnLongClickListener true
            }

            activity?.findViewById<TextView>(R.id.level4maxslots)!!.setOnLongClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.insertmaxslots) + " (" + getString(R.string.livello_4) + ")")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setText(charact.nslot4.toString())
                builder.setView(input)

                builder.setPositiveButton(R.string.ok) {_, _ ->
                    val int : Int = input.text.toString().toInt()
                    charact.nslot4 = int
                    viewModel.updateCharacter(charact)
                }
                builder.setNegativeButton(R.string.annulla) {dialog, _ -> dialog.cancel()}

                builder.show()
                return@setOnLongClickListener true
            }

            activity?.findViewById<TextView>(R.id.level5maxslots)!!.setOnLongClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.insertmaxslots) + " (" + getString(R.string.livello_5) + ")")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setText(charact.nslot5.toString())
                builder.setView(input)

                builder.setPositiveButton(R.string.ok) {_, _ ->
                    val int : Int = input.text.toString().toInt()
                    charact.nslot5 = int
                    viewModel.updateCharacter(charact)
                }
                builder.setNegativeButton(R.string.annulla) {dialog, _ -> dialog.cancel()}

                builder.show()
                return@setOnLongClickListener true
            }

            activity?.findViewById<TextView>(R.id.level6maxslots)!!.setOnLongClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.insertmaxslots) + " (" + getString(R.string.livello_6) + ")")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setText(charact.nslot6.toString())
                builder.setView(input)

                builder.setPositiveButton(R.string.ok) {_, _ ->
                    val int : Int = input.text.toString().toInt()
                    charact.nslot6 = int
                    viewModel.updateCharacter(charact)
                }
                builder.setNegativeButton(R.string.annulla) {dialog, _ -> dialog.cancel()}

                builder.show()
                return@setOnLongClickListener true
            }

            activity?.findViewById<TextView>(R.id.level7maxslots)!!.setOnLongClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.insertmaxslots) + " (" + getString(R.string.livello_7) + ")")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setText(charact.nslot7.toString())
                builder.setView(input)

                builder.setPositiveButton(R.string.ok) {_, _ ->
                    val int : Int = input.text.toString().toInt()
                    charact.nslot7 = int
                    viewModel.updateCharacter(charact)
                }
                builder.setNegativeButton(R.string.annulla) {dialog, _ -> dialog.cancel()}

                builder.show()
                return@setOnLongClickListener true
            }

            activity?.findViewById<TextView>(R.id.level8maxslots)!!.setOnLongClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.insertmaxslots) + " (" + getString(R.string.livello_8) + ")")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setText(charact.nslot8.toString())
                builder.setView(input)

                builder.setPositiveButton(R.string.ok) {_, _ ->
                    val int : Int = input.text.toString().toInt()
                    charact.nslot8 = int
                    viewModel.updateCharacter(charact)
                }
                builder.setNegativeButton(R.string.annulla) {dialog, _ -> dialog.cancel()}

                builder.show()
                return@setOnLongClickListener true
            }

            activity?.findViewById<TextView>(R.id.level9maxslots)!!.setOnLongClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.insertmaxslots) + " (" + getString(R.string.livello_9) + ")")
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setText(charact.nslot9.toString())
                builder.setView(input)

                builder.setPositiveButton(R.string.ok) {_, _ ->
                    val int : Int = input.text.toString().toInt()
                    charact.nslot9 = int
                    viewModel.updateCharacter(charact)
                }
                builder.setNegativeButton(R.string.annulla) {dialog, _ -> dialog.cancel()}

                builder.show()
                return@setOnLongClickListener true
            }
        }
    }

    private fun preparespells(deletemode : Boolean) {
        val charact = viewModel.currentcharacter.value
        if (charact is DnD5eCharacter) {
            if (charact.linkedclass != null) {
                if (charact.linkedclass!!.manapoints != null) {
                    activity?.findViewById<TextView>(R.id.spells_pointslbl)!!.isVisible = true
                    activity?.findViewById<TextView>(R.id.spells_points)!!.isVisible = true
                    activity?.findViewById<ImageButton>(R.id.pointspendbtn)!!.isVisible = true
                    activity?.findViewById<TextView>(R.id.spells_points)!!.text = charact.currpoints.toString()
                    activity?.findViewById<ImageButton>(R.id.pointspendbtn)!!.setOnClickListener {
                        charact.currpoints = max(0, charact.currpoints-1)
                        viewModel.updateCharacter(charact)
                    }

                }
            }

            val cantripsrecv = activity?.findViewById<RecyclerView>(R.id.cantripsrecv)!!
            cantripsrecv.layoutManager = LinearLayoutManager(context)
            cantripssadapter = AttackRecyclerViewAdapter(viewModel, charact.cantrips, charact, deletemode, 0)
            cantripsrecv.adapter = null
            cantripsrecv.adapter = cantripssadapter
            cantripsrecv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))

            val slot1recv = activity?.findViewById<RecyclerView>(R.id.slot1recv)!!
            slot1recv.layoutManager = LinearLayoutManager(context)
            slot1sadapter = AttackRecyclerViewAdapter(viewModel, charact.slot1, charact, deletemode, 1)
            slot1recv.adapter = null
            slot1recv.adapter = slot1sadapter
            slot1recv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
            activity?.findViewById<TextView>(R.id.level1currslots)!!.text = charact.currslot1.toString()
            activity?.findViewById<TextView>(R.id.level1maxslots)!!.text = charact.nslot1.toString()

            val slot2recv = activity?.findViewById<RecyclerView>(R.id.slot2recv)!!
            slot2recv.layoutManager = LinearLayoutManager(context)
            slot2sadapter = AttackRecyclerViewAdapter(viewModel, charact.slot2, charact, deletemode, 2)
            slot2recv.adapter = null
            slot2recv.adapter = slot2sadapter
            slot2recv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
            activity?.findViewById<TextView>(R.id.level2currslots)!!.text = charact.currslot2.toString()
            activity?.findViewById<TextView>(R.id.level2maxslots)!!.text = charact.nslot2.toString()

            val slot3recv = activity?.findViewById<RecyclerView>(R.id.slot3recv)!!
            slot3recv.layoutManager = LinearLayoutManager(context)
            slot3sadapter = AttackRecyclerViewAdapter(viewModel, charact.slot3, charact, deletemode, 3)
            slot3recv.adapter = null
            slot3recv.adapter = slot3sadapter
            slot3recv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
            activity?.findViewById<TextView>(R.id.level3currslots)!!.text = charact.currslot3.toString()
            activity?.findViewById<TextView>(R.id.level3maxslots)!!.text = charact.nslot3.toString()

            val slot4recv = activity?.findViewById<RecyclerView>(R.id.slot4recv)!!
            slot4recv.layoutManager = LinearLayoutManager(context)
            slot4sadapter = AttackRecyclerViewAdapter(viewModel, charact.slot4, charact, deletemode, 4)
            slot4recv.adapter = null
            slot4recv.adapter = slot4sadapter
            slot4recv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
            activity?.findViewById<TextView>(R.id.level4currslots)!!.text = charact.currslot4.toString()
            activity?.findViewById<TextView>(R.id.level4maxslots)!!.text = charact.nslot4.toString()

            val slot5recv = activity?.findViewById<RecyclerView>(R.id.slot5recv)!!
            slot5recv.layoutManager = LinearLayoutManager(context)
            slot5sadapter = AttackRecyclerViewAdapter(viewModel, charact.slot5, charact, deletemode, 5)
            slot5recv.adapter = null
            slot5recv.adapter = slot5sadapter
            slot5recv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
            activity?.findViewById<TextView>(R.id.level5currslots)!!.text = charact.currslot5.toString()
            activity?.findViewById<TextView>(R.id.level5maxslots)!!.text = charact.nslot5.toString()

            val slot6recv = activity?.findViewById<RecyclerView>(R.id.slot6recv)!!
            slot6recv.layoutManager = LinearLayoutManager(context)
            slot6sadapter = AttackRecyclerViewAdapter(viewModel, charact.slot6, charact, deletemode, 6)
            slot6recv.adapter = null
            slot6recv.adapter = slot6sadapter
            slot6recv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
            activity?.findViewById<TextView>(R.id.level6currslots)!!.text = charact.currslot6.toString()
            activity?.findViewById<TextView>(R.id.level6maxslots)!!.text = charact.nslot6.toString()

            val slot7recv = activity?.findViewById<RecyclerView>(R.id.slot7recv)!!
            slot7recv.layoutManager = LinearLayoutManager(context)
            slot7sadapter = AttackRecyclerViewAdapter(viewModel, charact.slot7, charact, deletemode, 7)
            slot7recv.adapter = null
            slot7recv.adapter = slot7sadapter
            slot7recv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
            activity?.findViewById<TextView>(R.id.level7currslots)!!.text = charact.currslot7.toString()
            activity?.findViewById<TextView>(R.id.level7maxslots)!!.text = charact.nslot7.toString()

            val slot8recv = activity?.findViewById<RecyclerView>(R.id.slot8recv)!!
            slot8recv.layoutManager = LinearLayoutManager(context)
            slot8sadapter = AttackRecyclerViewAdapter(viewModel, charact.slot8, charact, deletemode, 8)
            slot8recv.adapter = null
            slot8recv.adapter = slot8sadapter
            slot8recv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
            activity?.findViewById<TextView>(R.id.level8currslots)!!.text = charact.currslot8.toString()
            activity?.findViewById<TextView>(R.id.level8maxslots)!!.text = charact.nslot8.toString()

            val slot9recv = activity?.findViewById<RecyclerView>(R.id.slot9recv)!!
            slot9recv.layoutManager = LinearLayoutManager(context)
            slot9sadapter = AttackRecyclerViewAdapter(viewModel, charact.slot9, charact, deletemode, 9)
            slot9recv.adapter = null
            slot9recv.adapter = slot9sadapter
            slot9recv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
            activity?.findViewById<TextView>(R.id.level9currslots)!!.text = charact.currslot9.toString()
            activity?.findViewById<TextView>(R.id.level9maxslots)!!.text = charact.nslot9.toString()
        }
    }

    @SuppressLint("CutPasteId")
    private fun preparefab() {
        activity?.findViewById<FloatingActionButton>(R.id.spellsaddfab)!!.visibility = View.INVISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.spellsremovefab)!!.visibility = View.INVISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.spellsoptionsfab)!!.visibility = View.VISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.spellsclearfab)!!.visibility = View.INVISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.spellseditslotsfab)!!.visibility = View.INVISIBLE

        activity?.findViewById<FloatingActionButton>(R.id.spellsoptionsfab)!!.setOnClickListener {
            if (activity?.findViewById<FloatingActionButton>(R.id.spellsaddfab)!!.visibility == View.INVISIBLE) {
                activity?.findViewById<FloatingActionButton>(R.id.spellsaddfab)!!.visibility = View.VISIBLE
                activity?.findViewById<FloatingActionButton>(R.id.spellsremovefab)!!.visibility = View.VISIBLE
                activity?.findViewById<FloatingActionButton>(R.id.spellseditslotsfab)!!.visibility = View.VISIBLE
            } else {
                activity?.findViewById<FloatingActionButton>(R.id.spellsaddfab)!!.visibility = View.INVISIBLE
                activity?.findViewById<FloatingActionButton>(R.id.spellsremovefab)!!.visibility = View.INVISIBLE
                activity?.findViewById<FloatingActionButton>(R.id.spellseditslotsfab)!!.visibility = View.INVISIBLE
            }
        }

        activity?.findViewById<FloatingActionButton>(R.id.spellsaddfab)!!.setOnClickListener {
            val dialog = Dialog(context!!)
            activity?.findViewById<FloatingActionButton>(R.id.spellsaddfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.spellsremovefab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.spellseditslotsfab)!!.visibility = View.INVISIBLE
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.newattack)
            dialog.findViewById<TextView>(R.id.textView8).text = getString(R.string.newspell)
            var macro = ""
            dialog.findViewById<ImageButton>(R.id.attacklistbtn).setOnClickListener {
                val builder = Dialog(context!!)
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
                builder.setContentView(R.layout.dbitemselectdialog)
                builder.findViewById<TextView>(R.id.dbselecttitle).text = getString(R.string.newspell)
                val spinnderadapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, context!!.resources.getStringArray(R.array.spellnames))
                builder.findViewById<Spinner>(R.id.dbselectspinner).adapter = spinnderadapter

                builder.findViewById<EditText>(R.id.dbselectsearchbox).doAfterTextChanged { txt ->
                    var list = context!!.resources.getStringArray(R.array.spellnames).toList()
                    list = list.filter { it.lowercase().contains(txt.toString().lowercase()) }
                    val newadapt = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, list)
                    builder.findViewById<Spinner>(R.id.dbselectspinner).adapter = newadapt
                }

                builder.findViewById<Button>(R.id.dbitemconfirm).setOnClickListener { _ ->
                    builder.dismiss()
                    val selecteditem = builder.findViewById<Spinner>(R.id.dbselectspinner).selectedItem
                    val selected = context!!.resources.getStringArray(R.array.spellnames).indexOf(selecteditem)
                    val spellname = context!!.resources.getStringArray(R.array.spellnames)[selected]
                    val spelldesc = context!!.resources.getStringArray(R.array.spelldescs)[selected]
                    val spelllevel = context!!.resources.getStringArray(R.array.spelllevels)[selected].toInt()
                    val spellmacro = context!!.resources.getStringArray(R.array.spellmacros)[selected]

                    dialog.findViewById<EditText>(R.id.attackname_input).setText(spellname)
                    dialog.findViewById<EditText>(R.id.attackdesc_input).setText(spelldesc)
                    dialog.findViewById<Spinner>(R.id.spelllevel_input).setSelection(spelllevel, true)
                    macro = spellmacro
                    if (macro != "") {
                        val splittedmacro = spellmacro.split(":")
                        val txt =
                            splittedmacro[0] + "D${splittedmacro[1]} +" + (if (splittedmacro[2] == "1") " prof + " else " ") + (if (splittedmacro[3] != "None") "${splittedmacro[3]} + " else " ") + splittedmacro[4]
                        dialog.findViewById<Button>(R.id.createmacrobtn).text = txt
                    } else {
                        dialog.findViewById<Button>(R.id.createmacrobtn).text = getString(R.string.setmacro)
                    }

                }

                builder.show()
            }
            dialog.findViewById<Button>(R.id.createmacrobtn).setOnClickListener {
                val newmacrodialog = Dialog(context!!)
                newmacrodialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                newmacrodialog.setContentView(R.layout.newmacro)
                newmacrodialog.findViewById<Button>(R.id.newmacro_ok).setOnClickListener {
                    var txt = newmacrodialog.findViewById<EditText>(R.id.newmacro_n).text.toString()
                    if (txt == "") {
                        newmacrodialog.findViewById<EditText>(R.id.newmacro_n).error = getString(R.string.numbererror)
                    } else {
                        val n = txt
                        txt = newmacrodialog.findViewById<EditText>(R.id.newmacro_max).text.toString()
                        if (txt == "") {
                            newmacrodialog.findViewById<EditText>(R.id.newmacro_max).error = getString(R.string.numbererror)
                        } else {
                            val max = txt
                            val b = newmacrodialog.findViewById<CheckBox>(R.id.profchckbx).isChecked
                            val prof = (if (b) "1" else "0")
                            val stat =
                                when (newmacrodialog.findViewById<Spinner>(R.id.newmacro_stat).selectedItemPosition) {
                                    1 -> "STR"
                                    2 -> "DEX"
                                    3 -> "COS"
                                    4 -> "INT"
                                    5 -> "WIS"
                                    6 -> "CHA"
                                    else -> "None"
                                }
                            txt =
                                newmacrodialog.findViewById<EditText>(R.id.newmacro_bonus).text.toString()
                            val bonus: String = (if (txt == "") "0" else txt)
                            macro = "$n:$max:$prof:$stat:$bonus"
                            txt = n + "D$max +" + (if (prof == "1") " prof + " else " ") + (if (stat != "None") "$stat + " else " ") + bonus
                            dialog.findViewById<Button>(R.id.createmacrobtn).text = txt
                            newmacrodialog.dismiss()
                        }
                    }
                }
                newmacrodialog.findViewById<Button>(R.id.newmacro_cancel).setOnClickListener {
                    macro = ""
                    dialog.findViewById<Button>(R.id.createmacrobtn).text = getString(R.string.setmacro)
                    newmacrodialog.dismiss()
                }

                newmacrodialog.show()
            }
            dialog.findViewById<Button>(R.id.attackok_input).setOnClickListener {
                val name = dialog.findViewById<EditText>(R.id.attackname_input).text.toString()
                if (name == "") {
                    dialog.findViewById<EditText>(R.id.attackname_input).error = getString(R.string.errorattackname)
                } else {
                    val spellslot = dialog.findViewById<Spinner>(R.id.spelllevel_input).selectedItemPosition
                    val desc = dialog.findViewById<EditText>(R.id.attackdesc_input).text.toString()
                    val charact = viewModel.currentcharacter.value
                    if (charact is DnD5eCharacter) {
                        charact.addspell(Attack(name, desc, 2, macro), spellslot)
                        viewModel.updateCharacter(charact)
                    }
                    dialog.dismiss()
                }
            }
            dialog.findViewById<Button>(R.id.attackcancel_input).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        activity?.findViewById<FloatingActionButton>(R.id.spellsremovefab)!!.setOnClickListener {
            activity?.findViewById<FloatingActionButton>(R.id.spellsaddfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.spellsremovefab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.spellsoptionsfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.spellseditslotsfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.spellsclearfab)!!.visibility = View.VISIBLE
            preparespells(true)
        }

        activity?.findViewById<FloatingActionButton>(R.id.spellsclearfab)!!.setOnClickListener {
            activity?.findViewById<FloatingActionButton>(R.id.spellsclearfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.spellsoptionsfab)!!.visibility = View.VISIBLE
            preparespells(false)
        }

        activity?.findViewById<FloatingActionButton>(R.id.spellseditslotsfab)!!.setOnClickListener {

            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(context!!)
                dialog.setContentView(R.layout.editspellslotsdialog)
                
                var nslot1 = charact.nslot1
                var nslot2 = charact.nslot2
                var nslot3 = charact.nslot3
                var nslot4 = charact.nslot4
                var nslot5 = charact.nslot5
                var nslot6 = charact.nslot6
                var nslot7 = charact.nslot7
                var nslot8 = charact.nslot8
                var nslot9 = charact.nslot9

                if (charact.linkedclass != null) {
                    dialog.findViewById<Button>(R.id.loadslotsfromclassbtn)!!.setOnClickListener {
                        nslot1 = charact.linkedclass!!.spellslots[charact.pglv-1][0]
                        nslot2 = charact.linkedclass!!.spellslots[charact.pglv-1][1]
                        nslot3 = charact.linkedclass!!.spellslots[charact.pglv-1][2]
                        nslot4 = charact.linkedclass!!.spellslots[charact.pglv-1][3]
                        nslot5 = charact.linkedclass!!.spellslots[charact.pglv-1][4]
                        nslot6 = charact.linkedclass!!.spellslots[charact.pglv-1][5]
                        nslot7 = charact.linkedclass!!.spellslots[charact.pglv-1][6]
                        nslot8 = charact.linkedclass!!.spellslots[charact.pglv-1][7]
                        nslot9 = charact.linkedclass!!.spellslots[charact.pglv-1][8]
                        dialog.findViewById<TextView>(R.id.lv1slotsdialog)!!.text = nslot1.toString()
                        dialog.findViewById<TextView>(R.id.lv2slotsdialog)!!.text = nslot2.toString()
                        dialog.findViewById<TextView>(R.id.lv3slotsdialog)!!.text = nslot3.toString()
                        dialog.findViewById<TextView>(R.id.lv4slotsdialog)!!.text = nslot4.toString()
                        dialog.findViewById<TextView>(R.id.lv5slotsdialog)!!.text = nslot5.toString()
                        dialog.findViewById<TextView>(R.id.lv6slotsdialog)!!.text = nslot6.toString()
                        dialog.findViewById<TextView>(R.id.lv7slotsdialog)!!.text = nslot7.toString()
                        dialog.findViewById<TextView>(R.id.lv8slotsdialog)!!.text = nslot8.toString()
                        dialog.findViewById<TextView>(R.id.lv9slotsdialog)!!.text = nslot9.toString()
                    }
                } else {
                    dialog.findViewById<Button>(R.id.loadslotsfromclassbtn)!!.isEnabled = false
                }

                dialog.findViewById<TextView>(R.id.lv1slotsdialog)!!.text = nslot1.toString()
                dialog.findViewById<ImageButton>(R.id.addslot1dialog)!!.setOnClickListener { 
                    nslot1 += 1
                    dialog.findViewById<TextView>(R.id.lv1slotsdialog)!!.text = nslot1.toString()
                }
                dialog.findViewById<ImageButton>(R.id.removeslot1dialog)!!.setOnClickListener {
                    nslot1 = max(0, nslot1-1)
                    dialog.findViewById<TextView>(R.id.lv1slotsdialog)!!.text = nslot1.toString()
                }
                dialog.findViewById<TextView>(R.id.lv2slotsdialog)!!.text = nslot2.toString()
                dialog.findViewById<ImageButton>(R.id.addslot2dialog)!!.setOnClickListener {
                    nslot2 += 1
                    dialog.findViewById<TextView>(R.id.lv2slotsdialog)!!.text = nslot2.toString()
                }
                dialog.findViewById<ImageButton>(R.id.removeslot2dialog)!!.setOnClickListener {
                    nslot2 = max(0, nslot2-1)
                    dialog.findViewById<TextView>(R.id.lv2slotsdialog)!!.text = nslot2.toString()
                }
                dialog.findViewById<TextView>(R.id.lv3slotsdialog)!!.text = nslot3.toString()
                dialog.findViewById<ImageButton>(R.id.addslot3dialog)!!.setOnClickListener {
                    nslot3 += 1
                    dialog.findViewById<TextView>(R.id.lv3slotsdialog)!!.text = nslot3.toString()
                }
                dialog.findViewById<ImageButton>(R.id.removeslot3dialog)!!.setOnClickListener {
                    nslot3 = max(0, nslot3-1)
                    dialog.findViewById<TextView>(R.id.lv3slotsdialog)!!.text = nslot3.toString()
                }
                dialog.findViewById<TextView>(R.id.lv4slotsdialog)!!.text = nslot4.toString()
                dialog.findViewById<ImageButton>(R.id.addslot4dialog)!!.setOnClickListener {
                    nslot4 += 1
                    dialog.findViewById<TextView>(R.id.lv4slotsdialog)!!.text = nslot4.toString()
                }
                dialog.findViewById<ImageButton>(R.id.removeslot4dialog)!!.setOnClickListener {
                    nslot4 = max(0, nslot4-1)
                    dialog.findViewById<TextView>(R.id.lv4slotsdialog)!!.text = nslot4.toString()
                }
                dialog.findViewById<TextView>(R.id.lv5slotsdialog)!!.text = nslot5.toString()
                dialog.findViewById<ImageButton>(R.id.addslot5dialog)!!.setOnClickListener {
                    nslot5 += 1
                    dialog.findViewById<TextView>(R.id.lv5slotsdialog)!!.text = nslot5.toString()
                }
                dialog.findViewById<ImageButton>(R.id.removeslot5dialog)!!.setOnClickListener {
                    nslot5 = max(0, nslot5-1)
                    dialog.findViewById<TextView>(R.id.lv5slotsdialog)!!.text = nslot5.toString()
                }
                dialog.findViewById<TextView>(R.id.lv6slotsdialog)!!.text = nslot6.toString()
                dialog.findViewById<ImageButton>(R.id.addslot6dialog)!!.setOnClickListener {
                    nslot6 += 1
                    dialog.findViewById<TextView>(R.id.lv6slotsdialog)!!.text = nslot6.toString()
                }
                dialog.findViewById<ImageButton>(R.id.removeslot6dialog)!!.setOnClickListener {
                    nslot6 = max(0, nslot6-1)
                    dialog.findViewById<TextView>(R.id.lv6slotsdialog)!!.text = nslot6.toString()
                }
                dialog.findViewById<TextView>(R.id.lv7slotsdialog)!!.text = nslot7.toString()
                dialog.findViewById<ImageButton>(R.id.addslot7dialog)!!.setOnClickListener {
                    nslot7 += 1
                    dialog.findViewById<TextView>(R.id.lv7slotsdialog)!!.text = nslot7.toString()
                }
                dialog.findViewById<ImageButton>(R.id.removeslot7dialog)!!.setOnClickListener {
                    nslot7 = max(0, nslot7-1)
                    dialog.findViewById<TextView>(R.id.lv7slotsdialog)!!.text = nslot7.toString()
                }
                dialog.findViewById<TextView>(R.id.lv8slotsdialog)!!.text = nslot8.toString()
                dialog.findViewById<ImageButton>(R.id.addslot8dialog)!!.setOnClickListener {
                    nslot8 += 1
                    dialog.findViewById<TextView>(R.id.lv8slotsdialog)!!.text = nslot8.toString()
                }
                dialog.findViewById<ImageButton>(R.id.removeslot8dialog)!!.setOnClickListener {
                    nslot8 = max(0, nslot8-1)
                    dialog.findViewById<TextView>(R.id.lv8slotsdialog)!!.text = nslot8.toString()
                }
                dialog.findViewById<TextView>(R.id.lv9slotsdialog)!!.text = nslot9.toString()
                dialog.findViewById<ImageButton>(R.id.addslot9dialog)!!.setOnClickListener {
                    nslot9 += 1
                    dialog.findViewById<TextView>(R.id.lv9slotsdialog)!!.text = nslot9.toString()
                }
                dialog.findViewById<ImageButton>(R.id.removeslot9dialog)!!.setOnClickListener {
                    nslot9 = max(0, nslot9-1)
                    dialog.findViewById<TextView>(R.id.lv9slotsdialog)!!.text = nslot9.toString()
                }

                dialog.findViewById<Button>(R.id.editslotsdialogok)!!.setOnClickListener {
                    dialog.dismiss()
                    charact.nslot1 = nslot1
                    charact.nslot2 = nslot2
                    charact.nslot3 = nslot3
                    charact.nslot4 = nslot4
                    charact.nslot5 = nslot5
                    charact.nslot6 = nslot6
                    charact.nslot7 = nslot7
                    charact.nslot8 = nslot8
                    charact.nslot9 = nslot9
                    viewModel.updateCharacter(charact)
                }

                dialog.findViewById<Button>(R.id.editslotsdialogcancel)!!.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    private fun rollDialog(roll : Diceroll, title : String) {
        Diceroll.rollDialog(context!!, roll, title)
    }
}