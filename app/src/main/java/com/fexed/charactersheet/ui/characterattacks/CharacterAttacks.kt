package com.fexed.charactersheet.ui.characterattacks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.widget.doAfterTextChanged
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

class CharacterAttacks : Fragment() {
    companion object {
        fun newInstance() = CharacterAttacks()
    }

    private val viewModel: CharacterViewModel by activityViewModels()
    private lateinit var attackadapter : AttackRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.character_attacks_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.currentcharacter.observe(viewLifecycleOwner, {
            activity?.findViewById<TextView>(R.id.atks_pgname)!!.text = it.pgname
            activity?.findViewById<TextView>(R.id.atks_pglv)!!.text = it.pglv.toString()
            if (it is DnD5eCharacter) {
                activity?.findViewById<TextView>(R.id.atks_pgprof)!!.text = (if (it.profbonus >= 0) "+" else "") + it.profbonus.toString()
                prepareattacks(false)
                preparefab()
            } else {
                //TODO non è PG 5e
            }
        })
    }

    private fun prepareattacks(deletemode : Boolean) {
        val charact = viewModel.currentcharacter.value
        if (charact is DnD5eCharacter) {
            val attackrecv = activity?.findViewById<RecyclerView>(R.id.attacksrecv)!!
            attackrecv.layoutManager = LinearLayoutManager(context)
            attackadapter = AttackRecyclerViewAdapter(viewModel, charact.attacks, charact, deletemode, -1)
            attackrecv.adapter = null
            attackrecv.adapter = attackadapter
            attackrecv.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
        }
    }

    @SuppressLint("CutPasteId")
    private fun preparefab() {
        activity?.findViewById<FloatingActionButton>(R.id.addfab)!!.visibility = View.INVISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.removefab)!!.visibility = View.INVISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.optionsfab)!!.visibility = View.VISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.clearfab)!!.visibility = View.INVISIBLE

        activity?.findViewById<FloatingActionButton>(R.id.optionsfab)!!.setOnClickListener {
            if (activity?.findViewById<FloatingActionButton>(R.id.addfab)!!.visibility == View.INVISIBLE) {
                activity?.findViewById<FloatingActionButton>(R.id.addfab)!!.visibility = View.VISIBLE
                activity?.findViewById<FloatingActionButton>(R.id.removefab)!!.visibility = View.VISIBLE
            } else {
                activity?.findViewById<FloatingActionButton>(R.id.addfab)!!.visibility = View.INVISIBLE
                activity?.findViewById<FloatingActionButton>(R.id.removefab)!!.visibility = View.INVISIBLE
            }
        }

        activity?.findViewById<FloatingActionButton>(R.id.addfab)!!.setOnClickListener {
            val dialog = Dialog(context!!)
            activity?.findViewById<FloatingActionButton>(R.id.addfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.removefab)!!.visibility = View.INVISIBLE
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.newattack)
            val atktypeadapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, context!!.resources.getStringArray(R.array.selectableatktype))
            dialog.findViewById<Spinner>(R.id.spelllevel_input).adapter = atktypeadapter
            var macro = ""
            dialog.findViewById<ImageButton>(R.id.attacklistbtn).setOnClickListener {
                val builder = Dialog(context!!)
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
                builder.setContentView(R.layout.dbitemselectdialog)
                builder.findViewById<TextView>(R.id.dbselecttitle).text = getString(R.string.newattack)
                val spinnderadapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, context!!.resources.getStringArray(R.array.weaponnames))
                builder.findViewById<Spinner>(R.id.dbselectspinner).adapter = spinnderadapter

                builder.findViewById<EditText>(R.id.dbselectsearchbox).doAfterTextChanged { txt ->
                    var list = context!!.resources.getStringArray(R.array.weaponnames).toList()
                    list = list.filter { it.lowercase().contains(txt.toString().lowercase()) }
                    val newadapt = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, list)
                    builder.findViewById<Spinner>(R.id.dbselectspinner).adapter = newadapt
                }

                builder.findViewById<Button>(R.id.dbitemconfirm).setOnClickListener { _ ->
                    builder.dismiss()
                    val selecteditem = builder.findViewById<Spinner>(R.id.dbselectspinner).selectedItem
                    val selected = context!!.resources.getStringArray(R.array.weaponnames).indexOf(selecteditem)
                    val weaponname = context!!.resources.getStringArray(R.array.weaponnames)[selected]
                    val weapondesc = context!!.resources.getStringArray(R.array.weapondescs)[selected]
                    val weaponmacro = context!!.resources.getStringArray(R.array.weaponmacros)[selected]
                    val weaponetype = context!!.resources.getStringArray(R.array.weapontypes)[selected]

                    dialog.findViewById<EditText>(R.id.attackname_input).setText(weaponname)
                    dialog.findViewById<EditText>(R.id.attackdesc_input).setText(weapondesc)
                    dialog.findViewById<Spinner>(R.id.spelllevel_input).setSelection(when (weaponetype) {
                        "Melee" -> 0
                        "Ranged" -> 1
                        "Spell" -> 2
                        else -> 0
                    })
                    macro = weaponmacro
                    val splittedmacro = weaponmacro.split(":")
                    val txt = splittedmacro[0] + "D${splittedmacro[1]} +" + (if (splittedmacro[2] == "1") " prof + " else " ") + (if (splittedmacro[3] != "None") "${splittedmacro[3]} + " else " ") + splittedmacro[4]
                    dialog.findViewById<Button>(R.id.createmacrobtn).text = txt

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
                            txt = newmacrodialog.findViewById<EditText>(R.id.newmacro_bonus).text.toString()
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
                    val desc = dialog.findViewById<EditText>(R.id.attackdesc_input).text.toString()
                    val charact = viewModel.currentcharacter.value
                    if (charact is DnD5eCharacter) {
                        charact.addattack(Attack(name, desc, dialog.findViewById<Spinner>(R.id.spelllevel_input).selectedItemPosition, macro))
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

        activity?.findViewById<FloatingActionButton>(R.id.removefab)!!.setOnClickListener {
            activity?.findViewById<FloatingActionButton>(R.id.addfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.removefab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.optionsfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.clearfab)!!.visibility = View.VISIBLE
            prepareattacks(true)
        }

        activity?.findViewById<FloatingActionButton>(R.id.clearfab)!!.setOnClickListener {
            activity?.findViewById<FloatingActionButton>(R.id.clearfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.optionsfab)!!.visibility = View.VISIBLE
            prepareattacks(false)
        }

        activity?.findViewById<FloatingActionButton>(R.id.atkdicefab)!!.setOnClickListener {
            var macro = "1:20:"
            val dialog = Dialog(context!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.atkrolldialog)
            dialog.findViewById<Button>(R.id.atkrollbtn).setOnClickListener {
                macro += (if (dialog.findViewById<CheckBox>(R.id.atkrollprofchk).isChecked) "1:" else "0:")
                macro += when(dialog.findViewById<Spinner>(R.id.atkrollstatspinner).selectedItemPosition) {
                    1 -> "DEX"
                    else -> "STR"
                }
                macro += ":0"
                val charact = viewModel.currentcharacter.value
                if (charact is DnD5eCharacter) {
                    val roll = charact.createroll(macro)
                    when (dialog.findViewById<SeekBar>(R.id.atkrolltypeseekbar)!!.progress) {
                        0 -> Diceroll.rollDouble(context!!, roll, getString(R.string.newattack), false)
                        1 -> Diceroll.rollDialog(context!!, roll, getString(R.string.newattack))
                        2 -> Diceroll.rollDouble(context!!, roll, getString(R.string.newattack), true)
                    }
                }
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}