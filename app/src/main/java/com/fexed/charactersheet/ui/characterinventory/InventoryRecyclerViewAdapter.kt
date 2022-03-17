package com.fexed.charactersheet.ui.characterinventory

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.fexed.charactersheet.R
import com.fexed.charactersheet.character.Diceroll
import com.fexed.charactersheet.character.InventoryItem
import com.fexed.charactersheet.character.dnd5e.Attack
import com.fexed.charactersheet.character.dnd5e.DnD5eCharacter
import com.fexed.charactersheet.character.dnd5e.Spell
import com.fexed.charactersheet.ui.CharacterViewModel
import java.lang.IndexOutOfBoundsException

class InventoryRecyclerViewAdapter(private val viewModel : CharacterViewModel, private val items : MutableList<InventoryItem>, private var character : DnD5eCharacter, private var deletemode: Boolean) : RecyclerView.Adapter<InventoryRecyclerViewAdapter.AttackViewHolder>() {
    class AttackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view : View = itemView
        private var item : InventoryItem? = null

        fun bindItem(item : InventoryItem, deletemode : Boolean) {
            this.item = item
            view.findViewById<TextView>(R.id.itemname)!!.text = item.name
            view.findViewById<TextView>(R.id.itemdesc)!!.text = item.desc + "\n" + item.listbonuses()
            view.findViewById<TextView>(R.id.itemqnt)!!.text = "x" + item.quantity.toString()
            view.findViewById<TextView>(R.id.itemweight)!!.text = item.weight.toString() + " kg"
            view.findViewById<CheckBox>(R.id.itemequipcheckbox)!!.isChecked = item.equipped
            if (deletemode) {
                view.findViewById<ImageButton>(R.id.deleteitem)!!.isVisible = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttackViewHolder {
        val inflated = LayoutInflater.from(parent.context).inflate(R.layout.inventoryitem, parent, false)
        return AttackViewHolder(inflated)
    }

    override fun onBindViewHolder(holder: AttackViewHolder, position: Int) {
        holder.bindItem(items[position], deletemode)
        holder.view.findViewById<CheckBox>(R.id.itemequipcheckbox)!!.setOnCheckedChangeListener { _, b ->
            items[position].equipped = b
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.changeequip(items[position])
                viewModel.updateCharacter(charact)
            }
        }
        if (deletemode) {
            holder.view.findViewById<ImageButton>(R.id.deleteitem)!!.setOnClickListener {
                holder.view.findViewById<Button>(R.id.deleteitemconfirm)!!.isVisible = true
                holder.view.findViewById<ImageButton>(R.id.deleteitem)!!.isVisible = false
                holder.view.findViewById<Button>(R.id.deleteitemconfirm)!!.setOnClickListener {
                    val charact = viewModel.currentcharacter.value
                    if (charact is DnD5eCharacter) {
                        charact.removeitem(items[position])
                        viewModel.updateCharacter(charact)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}