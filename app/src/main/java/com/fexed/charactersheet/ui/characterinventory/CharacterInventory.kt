package com.fexed.charactersheet.ui.characterinventory

import android.app.AlertDialog
import android.app.Dialog
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fexed.charactersheet.R
import com.fexed.charactersheet.character.InventoryItem
import com.fexed.charactersheet.character.dnd5e.DnD5eCharacter
import com.fexed.charactersheet.ui.CharacterViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text
import java.lang.NumberFormatException
import kotlin.math.max
import kotlin.math.min

class CharacterInventory : Fragment() {
    
    private val viewModel: CharacterViewModel by activityViewModels()
    private lateinit var inventoryadapter : InventoryRecyclerViewAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.character_inventory_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.currentcharacter.observe(viewLifecycleOwner, {
            if (it is DnD5eCharacter) {
                activity?.findViewById<TextView>(R.id.totalweight)!!.text = it.carriedweight().toString() + " kg"
                activity?.findViewById<TextView>(R.id.maxweight)!!.text = it.carryingcapacity.toString() + " kg"
                activity?.findViewById<TextView>(R.id.mptxtv)!!.text = it.mp.toString()
                activity?.findViewById<TextView>(R.id.motxtv)!!.text = it.mo.toString()
                activity?.findViewById<TextView>(R.id.matxtv)!!.text = it.ma.toString()
                activity?.findViewById<TextView>(R.id.mrtxtv)!!.text = it.mr.toString()
                prepareinventory(false)
                preparefab()
            } else {
                //TODO non è PG 5e
            }
        })
    }

    private fun prepareinventory(deletemode : Boolean) {
        val charact = viewModel.currentcharacter.value
        if (charact is DnD5eCharacter) {
            val inventoryrecv = activity?.findViewById<RecyclerView>(R.id.inventorylistrecv)!!
            inventoryrecv.layoutManager = LinearLayoutManager(context)
            inventoryadapter = InventoryRecyclerViewAdapter(viewModel, charact.inventory, charact, deletemode)
            inventoryrecv.adapter = null
            inventoryrecv.adapter = inventoryadapter
        }
    }

    private fun preparefab() {
        activity?.findViewById<FloatingActionButton>(R.id.invaddfab)!!.visibility = View.INVISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.invremovefab)!!.visibility = View.INVISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.invoptionsfab)!!.visibility = View.VISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.invclearfab)!!.visibility = View.INVISIBLE

        activity?.findViewById<FloatingActionButton>(R.id.invoptionsfab)!!.setOnClickListener {
            if (activity?.findViewById<FloatingActionButton>(R.id.invaddfab)!!.visibility == View.INVISIBLE) {
                activity?.findViewById<FloatingActionButton>(R.id.invaddfab)!!.visibility = View.VISIBLE
                activity?.findViewById<FloatingActionButton>(R.id.invremovefab)!!.visibility = View.VISIBLE
            } else {
                activity?.findViewById<FloatingActionButton>(R.id.invaddfab)!!.visibility = View.INVISIBLE
                activity?.findViewById<FloatingActionButton>(R.id.invremovefab)!!.visibility = View.INVISIBLE
            }
        }

        activity?.findViewById<FloatingActionButton>(R.id.invaddfab)!!.setOnClickListener {
            activity?.findViewById<FloatingActionButton>(R.id.invaddfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.invremovefab)!!.visibility = View.INVISIBLE
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.newitem)
            dialog.findViewById<ImageButton>(R.id.itemlistbtn).setOnClickListener {
                val builder = Dialog(requireContext())
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
                builder.setContentView(R.layout.dbitemselectdialog)
                builder.findViewById<TextView>(R.id.dbselecttitle).text = getString(R.string.newitem)
                val spinnderadapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, requireContext().resources.getStringArray(R.array.itemnames))
                builder.findViewById<Spinner>(R.id.dbselectspinner).adapter = spinnderadapter

                builder.findViewById<EditText>(R.id.dbselectsearchbox).doAfterTextChanged { txt ->
                    var list = requireContext().resources.getStringArray(R.array.itemnames).toList()
                    list = list.filter { it.lowercase().contains(txt.toString().lowercase()) }
                    val newadapt = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, list)
                    builder.findViewById<Spinner>(R.id.dbselectspinner).adapter = newadapt
                }

                builder.findViewById<Button>(R.id.dbitemconfirm).setOnClickListener { _ ->
                    builder.dismiss()
                    val selecteditem = builder.findViewById<Spinner>(R.id.dbselectspinner).selectedItem
                    val selected = requireContext().resources.getStringArray(R.array.itemnames).indexOf(selecteditem)
                    val itemname = requireContext().resources.getStringArray(R.array.itemnames)[selected]
                    val itemdesc = requireContext().resources.getStringArray(R.array.itemdescs)[selected]
                    val itemweight = requireContext().resources.getStringArray(R.array.itemweights)[selected].toDouble()
                    val itembonuses = requireContext().resources.getStringArray(R.array.itembonuses)[selected]

                    dialog.findViewById<EditText>(R.id.itemname_input).setText(itemname)
                    dialog.findViewById<EditText>(R.id.itemdesc_input).setText(itemdesc)
                    dialog.findViewById<EditText>(R.id.itemweight_input).setText(itemweight.toString())

                    try {
                        dialog.findViewById<Spinner>(R.id.newitem_bonus).setSelection(
                            when (itembonuses.split("@")[0].split(":")[0]) {
                                "AC" -> 1
                                "MHP" -> 2
                                "STR" -> 3
                                "DEX" -> 4
                                "CON" -> 5
                                "INT" -> 6
                                "WIS" -> 7
                                "CHA" -> 8
                                "SPD" -> 9
                                "INIT" -> 10
                                else -> 0
                            }
                        )
                        dialog.findViewById<EditText>(R.id.itembonus_n).setText(itembonuses.split("@")[0].split(":")[1])
                    } catch (e: Exception) {}
                }

                builder.show()
            }
            dialog.findViewById<Button>(R.id.newitem_ok).setOnClickListener {
                val name = dialog.findViewById<EditText>(R.id.itemname_input).text.toString()
                if (name == "") {
                    dialog.findViewById<EditText>(R.id.itemname_input).error = getString(R.string.errorattackname)
                } else {
                    if (dialog.findViewById<Spinner>(R.id.newitem_bonus).selectedItemPosition > 0 && dialog.findViewById<EditText>(R.id.itembonus_n).text.toString() == "") {
                        dialog.findViewById<EditText>(R.id.itembonus_n).error = getString(R.string.numbererror)
                    } else {
                        val bonus = when (dialog.findViewById<Spinner>(R.id.newitem_bonus).selectedItemPosition) {
                                1 -> "AC"
                                2 -> "MHP"
                                3 -> "STR"
                                4 -> "DEX"
                                5 -> "CON"
                                6 -> "INT"
                                7 -> "WIS"
                                8 -> "CHA"
                                9 -> "SPD"
                                10 -> "INIT"
                                else -> "None"
                            } + ":" + dialog.findViewById<EditText>(R.id.itembonus_n).text.toString() + "@"
                        val desc = dialog.findViewById<EditText>(R.id.itemdesc_input).text.toString()

                        try {
                            val weight = dialog.findViewById<EditText>(R.id.itemweight_input).text.toString().toDouble()
                            try {
                                val quantity =
                                    dialog.findViewById<EditText>(R.id.itemquantity_input).text.toString()
                                        .toInt()
                                val charact = viewModel.currentcharacter.value
                                if (charact is DnD5eCharacter) {
                                    charact.additem(
                                        InventoryItem(
                                            name,
                                            desc,
                                            weight,
                                            quantity,
                                            false,
                                            bonus
                                        )
                                    )
                                    viewModel.updateCharacter(charact)
                                }
                                dialog.dismiss()
                            } catch (e: NumberFormatException) {dialog.findViewById<EditText>(R.id.itemquantity_input).error = getString(R.string.numbererror)}
                        } catch (e: NumberFormatException) {dialog.findViewById<EditText>(R.id.itemweight_input).error = getString(R.string.numbererror)}
                    }
                }
            }
            dialog.show()
        }

        activity?.findViewById<FloatingActionButton>(R.id.invremovefab)!!.setOnClickListener {
            activity?.findViewById<FloatingActionButton>(R.id.invaddfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.invremovefab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.invoptionsfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.invclearfab)!!.visibility = View.VISIBLE
            prepareinventory(true)
        }

        activity?.findViewById<FloatingActionButton>(R.id.invclearfab)!!.setOnClickListener {
            activity?.findViewById<FloatingActionButton>(R.id.invclearfab)!!.visibility = View.INVISIBLE
            activity?.findViewById<FloatingActionButton>(R.id.invoptionsfab)!!.visibility = View.VISIBLE
            prepareinventory(false)
        }

        activity?.findViewById<FloatingActionButton>(R.id.goldfab)!!.setOnClickListener {
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.spenddialog)
                var mpvar = 0
                val mptxtv = dialog.findViewById<TextView>(R.id.dialogmptxtv)
                mptxtv.text = "+" + mpvar.toString()
                dialog.findViewById<ImageButton>(R.id.dialogaddmp).setOnClickListener {
                    mpvar += 1
                    mptxtv.text = (if (mpvar >= 0) "+" else "") + mpvar.toString()
                }
                dialog.findViewById<ImageButton>(R.id.dialogremovemp).setOnClickListener {
                    mpvar = max(mpvar - 1, charact.mp*-1)
                    mptxtv.text = (if (mpvar >= 0) "+" else "") + mpvar.toString()
                }
                var movar = 0
                val motxtv = dialog.findViewById<TextView>(R.id.dialogmotxtv)
                motxtv.text = "+" + movar.toString()
                dialog.findViewById<ImageButton>(R.id.dialogaddmo).setOnClickListener {
                    movar += 1
                    motxtv.text = (if (movar >= 0) "+" else "") + movar.toString()
                }
                dialog.findViewById<ImageButton>(R.id.dialogremovemo).setOnClickListener {
                    movar = max(movar - 1, charact.mo*-1)
                    motxtv.text = (if (movar >= 0) "+" else "") + movar.toString()
                }
                var mavar = 0
                val matxtv = dialog.findViewById<TextView>(R.id.dialogmatxtv)
                matxtv.text = "+" + mavar.toString()
                dialog.findViewById<ImageButton>(R.id.dialogaddma).setOnClickListener {
                    mavar += 1
                    matxtv.text = (if (mavar >= 0) "+" else "") + mavar.toString()
                }
                dialog.findViewById<ImageButton>(R.id.dialogremovema).setOnClickListener {
                    mavar = max(mavar - 1, charact.ma*-1)
                    matxtv.text = (if (mavar >= 0) "+" else "") + mavar.toString()
                }
                var mrvar = 0
                val mrtxtv = dialog.findViewById<TextView>(R.id.dialogmrtxtv)
                mrtxtv.text = "+" + mrvar.toString()
                dialog.findViewById<ImageButton>(R.id.dialogaddmr).setOnClickListener {
                    mrvar += 1
                    mrtxtv.text = (if (mrvar >= 0) "+" else "") + mrvar.toString()
                }
                dialog.findViewById<ImageButton>(R.id.dialogremovemr).setOnClickListener {
                    mrvar = max(mrvar - 1, charact.mr*-1)
                    mrtxtv.text = (if (mrvar >= 0) "+" else "") + mrvar.toString()
                }

                dialog.findViewById<Button>(R.id.spenddialogcancel).setOnClickListener {
                    dialog.dismiss()
                }

                dialog.findViewById<Button>(R.id.spenddialogok).setOnClickListener {
                    dialog.dismiss()
                    charact.mp += mpvar
                    charact.mo += movar
                    charact.ma += mavar
                    charact.mr += mrvar
                    viewModel.updateCharacter(charact)
                }

                dialog.show()
            }
        }
    }
}