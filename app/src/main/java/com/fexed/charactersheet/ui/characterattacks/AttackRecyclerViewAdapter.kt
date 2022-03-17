package com.fexed.charactersheet.ui.characterattacks

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.fexed.charactersheet.R
import com.fexed.charactersheet.character.Diceroll
import com.fexed.charactersheet.character.dnd5e.Attack
import com.fexed.charactersheet.character.dnd5e.DnD5eCharacter
import com.fexed.charactersheet.character.dnd5e.Spell
import com.fexed.charactersheet.ui.CharacterViewModel
import java.lang.IndexOutOfBoundsException

class AttackRecyclerViewAdapter(private val viewModel : CharacterViewModel, private val attacks : MutableList<Attack>, private var character : DnD5eCharacter, private var deletemode: Boolean, private var slotn : Int) : RecyclerView.Adapter<AttackRecyclerViewAdapter.AttackViewHolder>() {
    class AttackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var view : View = itemView
        private var attack : ComputedAttack? = null

        override fun onClick(v: View?) {
            if (v!!.id == R.id.rollattack) {
                Diceroll.rollAttack(v.context, attack!!.macro, attack!!.atkbonus, attack!!.name)
            }
        }

        fun bindAttack(attack : ComputedAttack, deletemode : Boolean) {
            this.attack = attack
            view.findViewById<TextView>(R.id.attackname)!!.text = attack.name
            view.findViewById<TextView>(R.id.attackdesc)!!.text = attack.desc
            view.findViewById<TextView>(R.id.attacktype)!!.text = attack.type
            if (attack.macro.isvalid) {
                view.findViewById<TextView>(R.id.attackroll)!!.text = attack.macro.desc()
                view.findViewById<ImageButton>(R.id.rollattack)!!.setOnClickListener(this)
            } else {
                view.findViewById<ImageButton>(R.id.rollattack)!!.isVisible = false
            }
            if (deletemode) {
                view.findViewById<ImageButton>(R.id.rollattack)!!.isVisible = false
                view.findViewById<ImageButton>(R.id.deleteattack)!!.isVisible = true
            }
        }
    }

    class ComputedAttack (val name : String, val type : String, val desc : String, val roll : String, val macro : Diceroll, val atkbonus : Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttackViewHolder {
        val inflated = LayoutInflater.from(parent.context).inflate(R.layout.attackview, parent, false)
        return AttackViewHolder(inflated)
    }

    override fun onBindViewHolder(holder: AttackViewHolder, position: Int) {
        try {
            holder.bindAttack(
                ComputedAttack(
                    attacks[position].name,
                    when(attacks[position].category) {0 -> "Melee" 1 -> "Ranged" 2 -> "Spell" else -> ""},
                    attacks[position].desc,
                    attacks[position].roll,
                    character.createroll(attacks[position]),
                    when(attacks[position].category) {0 -> character.modSTR + character.profbonus 1 -> character.modDEX + character.profbonus 2 -> character.spellatk else -> 0}
                ), deletemode
            )
        } catch (e: IndexOutOfBoundsException) {
            holder.bindAttack(
                ComputedAttack(
                    attacks[position].name,
                    when(attacks[position].category) {0 -> "Melee" 1 -> "Ranged" 2 -> "Spell" else -> ""},
                    attacks[position].desc,
                    "",
                    character.createroll(attacks[position]),
                    0
                ), deletemode
            )
        }
        if (deletemode) {
            holder.view.findViewById<ImageButton>(R.id.deleteattack)!!.setOnClickListener {
                holder.view.findViewById<Button>(R.id.deleteattackconfirm)!!.isVisible = true
                holder.view.findViewById<ImageButton>(R.id.deleteattack)!!.isVisible = false
                holder.view.findViewById<Button>(R.id.deleteattackconfirm)!!.setOnClickListener {
                    val charact = viewModel.currentcharacter.value
                    if (charact is DnD5eCharacter) {
                        if (slotn < 0) charact.removeattack(attacks[position])
                        else charact.removespell(attacks[position], slotn)
                        viewModel.updateCharacter(charact)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return attacks.size
    }
}