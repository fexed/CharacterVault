package com.fexed.charactersheet.character.dnd5e

import android.content.Context
import android.util.Log
import com.fexed.charactersheet.character.Character
import com.fexed.charactersheet.character.Diceroll
import com.fexed.charactersheet.character.InventoryItem
import com.google.gson.Gson
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Exception
import kotlin.math.*
import kotlin.math.round

class DnD5eCharacter(
    charname : String, charclass : Class, charrace : String,
    level : Int, portrait : String?, notes : String, var xp : Int,
    var STR : Int, var DEX : Int, var CON : Int,
    var INT : Int, var WIS : Int, var CHA : Int,
    var CurrHP : Int, var MaxHP : Int, var AC : Int,
    var tsstr : Boolean, var tsdex : Boolean, var tscon : Boolean,
    var tsint : Boolean, var tswis : Boolean, var tscha : Boolean,

    // STR
    var atletica_comp : Boolean, var atletica_exp : Boolean,

    // DEX
    var acrobazia_comp : Boolean, var acrobazia_exp : Boolean,
    var furtivita_comp : Boolean, var furtivita_exp : Boolean,
    var rapdimano_comp : Boolean, var rapdimano_exp : Boolean,

    // INT
    var investigare_comp : Boolean, var investigare_exp : Boolean,
    var arcano_comp : Boolean, var arcano_exp : Boolean,
    var storia_comp : Boolean, var storia_exp : Boolean,
    var religione_comp : Boolean, var religione_exp : Boolean,
    var natura_comp : Boolean, var natura_exp : Boolean,

    // WIS
    var sopravv_comp : Boolean, var sopravviv_exp : Boolean,
    var medicina_comp : Boolean, var medicina_exp : Boolean,
    var percezione_comp : Boolean, var percezione_exp : Boolean,
    var intuizione_comp : Boolean, var intuizione_exp : Boolean,
    var animali_comp : Boolean, var animali_exp : Boolean,

    // CHA
    var intimidire_comp : Boolean, var intimidire_exp : Boolean,
    var ingannare_comp : Boolean, var ingannare_exp : Boolean,
    var intrattenere_comp : Boolean, var intrattenere_exp : Boolean,
    var persuadere_comp : Boolean, var persuadere_exp : Boolean,

    var spellstat : Int, // -1:none, 0:WIS, 1:INT, 2:CHA
    var nslot1 : Int, var currslot1 : Int,
    var nslot2 : Int, var currslot2 : Int,
    var nslot3 : Int, var currslot3 : Int,
    var nslot4 : Int, var currslot4 : Int,
    var nslot5 : Int, var currslot5 : Int,
    var nslot6 : Int, var currslot6 : Int,
    var nslot7 : Int, var currslot7 : Int,
    var nslot8 : Int, var currslot8 : Int,
    var nslot9 : Int, var currslot9 : Int,
) : Character(charname, charclass.name, charrace, level, portrait, notes) {
    var cantrips : MutableList<Attack> = mutableListOf()
    var slot1 : MutableList<Attack> = mutableListOf()
    var slot2 : MutableList<Attack> = mutableListOf()
    var slot3 : MutableList<Attack> = mutableListOf()
    var slot4 : MutableList<Attack> = mutableListOf()
    var slot5 : MutableList<Attack> = mutableListOf()
    var slot6 : MutableList<Attack> = mutableListOf()
    var slot7 : MutableList<Attack> = mutableListOf()
    var slot8 : MutableList<Attack> = mutableListOf()
    var slot9 : MutableList<Attack> = mutableListOf()
    var currpoints : Int = 0
    var attacks : MutableList<Attack> = mutableListOf()
    var inventory : MutableList<InventoryItem> = mutableListOf()
    var racialbonuses : MutableList<String> = mutableListOf()
    var classbonuses : MutableList<String> = mutableListOf()
    var mp : Int = 0
    var mo : Int = 0
    var ma : Int = 0
    var mr : Int = 0
    val totalgold = 0.1*mp + mo + 10*ma + 100*mr
    val modSTR get() = mod(STR + getbonuses("STR"))
    val modDEX get() = mod(DEX + getbonuses("DEX"))
    val modCON get() = mod(CON + getbonuses("CON"))
    val modWIS get() = mod(WIS + getbonuses("WIS"))
    val modINT get() = mod(INT + getbonuses("INT"))
    val modCHA get() = mod(CHA + getbonuses("CHA"))
    val profbonus get() = comp(pglv)
    val bonustsstr get() = modSTR + (if (tsstr) profbonus else 0)
    val bonusatletica get() = modSTR + (if (atletica_comp) (if (atletica_exp) 2*profbonus else profbonus) else 0)
    val bonustsdex get() = modDEX + (if (tsdex) profbonus else 0)
    val bonusacrobazia get() = modDEX + (if (acrobazia_comp) (if (acrobazia_exp) 2*profbonus else profbonus) else 0)
    val bonusfurtivita get() = modDEX + (if (furtivita_comp) (if (furtivita_exp) 2*profbonus else profbonus) else 0)
    val bonusrapdimano get() = modDEX + (if (rapdimano_comp) (if (rapdimano_exp) 2*profbonus else profbonus) else 0)
    val bonustscon get() = modCON + (if (tscon) profbonus else 0)
    val bonustsint get() = modINT + (if (tsint) profbonus else 0)
    val bonusinvestigare get() = modINT + (if (investigare_comp) (if (investigare_exp) 2*profbonus else profbonus) else 0)
    val bonusarcano get() = modINT + (if (arcano_comp) (if (arcano_exp) 2*profbonus else profbonus) else 0)
    val bonusstoria get() = modINT + (if (storia_comp) (if (storia_exp) 2*profbonus else profbonus) else 0)
    val bonusreligione get() = modINT + (if (religione_comp) (if (religione_exp) 2*profbonus else profbonus) else 0)
    val bonusnatura get() = modINT + (if (natura_comp) (if (natura_exp) 2*profbonus else profbonus) else 0)
    val bonustswis get() = modWIS + (if (tswis) profbonus else 0)
    val bonussopravv get() = modWIS + (if (sopravv_comp) (if (sopravviv_exp) 2*profbonus else profbonus) else 0)
    val bonusmedicina get() = modWIS + (if (medicina_comp) (if (medicina_exp) 2*profbonus else profbonus) else 0)
    val bonuspercezione get() = modWIS + (if (percezione_comp) (if (percezione_exp) 2*profbonus else profbonus) else 0)
    val bonusintuiz get() = modWIS + (if (intuizione_comp) (if (intuizione_exp) 2*profbonus else profbonus) else 0)
    val bonusanimali get() = modWIS + (if (animali_comp) (if (animali_exp) 2*profbonus else profbonus) else 0)
    val bonustscha get() = modCHA + (if (tscha) profbonus else 0)
    val bonusintimidire get() = modCHA + (if (intimidire_comp) (if (intimidire_exp) 2*profbonus else profbonus) else 0)
    val bonusingannare get() = modCHA + (if (ingannare_comp) (if (ingannare_exp) 2*profbonus else profbonus) else 0)
    val bonusintrattenere get() = modCHA + (if (intrattenere_comp) (if (intrattenere_exp) 2*profbonus else profbonus) else 0)
    val bonuspersuadere get() = modCHA + (if (persuadere_comp) (if (persuadere_exp) 2*profbonus else profbonus) else 0)
    val spellatk get() = profbonus + when(spellstat) {0 -> modINT 1 -> modWIS 2 -> modCHA else -> 0}
    val spellcd get() = 8 + spellatk
    val carryingcapacity get() = 15*STR
    val pushdraglift get() = 2*carryingcapacity
    val encumbrancetreshold get() = 5*STR
    val heavilyencumbrancetreshold get() = 2*encumbrancetreshold
    val passiveperc get() = 10 + bonuspercezione
    val speed get() = (30 + getbonuses("SPD"))
    val initative get() = (modDEX + getbonuses("INIT"))
    val nextlvlxp get() = xptable[min(19, pglv)]
    var linkedclass : Class? = null

    fun poundstokg(value : Double) : Double {
        return value * 0.454
    }

    fun feettom(value : Double) : Double {
        return value * 0.3048
    }

    fun carriedweight() : Double {
        var sum : Double = 0.0
        for (item in inventory) {
            sum += (item.weight*item.quantity)
        }
        return sum
    }

    fun mod(stat : Int) : Int {
        return floor((stat.toDouble() - 10)/2).toInt()
    }

    fun comp(lv : Int) : Int {
        return ceil(1 + (lv.toDouble() / 4)).toInt()
    }

    fun editCurrHP(value : Int) : Int {
        CurrHP += value
        return CurrHP
    }

    fun addspell(attack: Attack, slot : Int) {
        when(slot) {
            0 -> cantrips.add(attack)
            1 -> slot1.add(attack)
            2 -> slot2.add(attack)
            3 -> slot3.add(attack)
            4 -> slot4.add(attack)
            5 -> slot5.add(attack)
            6 -> slot6.add(attack)
            7 -> slot7.add(attack)
            8 -> slot8.add(attack)
            9 -> slot9.add(attack)
        }
    }

    fun removespell(attack: Attack, slot : Int) {
        when(slot) {
            0 -> cantrips.remove(attack)
            1 -> slot1.remove(attack)
            2 -> slot2.remove(attack)
            3 -> slot3.remove(attack)
            4 -> slot4.remove(attack)
            5 -> slot5.remove(attack)
            6 -> slot6.remove(attack)
            7 -> slot7.remove(attack)
            8 -> slot8.remove(attack)
            9 -> slot9.remove(attack)
        }
    }

    fun castspell(level : Int) {
        when(level) {
            1 -> currslot1 = max(0, currslot1 - 1)
            2 -> currslot2 = max(0, currslot2 - 1)
            3 -> currslot3 = max(0, currslot3 - 1)
            4 -> currslot4 = max(0, currslot4 - 1)
            5 -> currslot5 = max(0, currslot5 - 1)
            6 -> currslot6 = max(0, currslot6 - 1)
            7 -> currslot7 = max(0, currslot7 - 1)
            8 -> currslot8 = max(0, currslot8 - 1)
            9 -> currslot9 = max(0, currslot9 - 1)
        }
    }

    fun refreshall(slots : Boolean, hp : Boolean) {
        if (slots) {
            currslot1 = nslot1
            currslot2 = nslot2
            currslot3 = nslot3
            currslot4 = nslot4
            currslot5 = nslot5
            currslot6 = nslot6
            currslot7 = nslot7
            currslot8 = nslot8
            currslot9 = nslot9
            if (linkedclass != null) {
                if (linkedclass!!.manapoints != null) {
                    currpoints = linkedclass!!.manapoints!![pglv-1]
                }
            }
        }

        if (hp && CurrHP < MaxHP) CurrHP = MaxHP + getbonuses("MHP")
    }

    fun addattack(attack: Attack) {
        attacks.add(attack)
    }

    fun removeattack(attack: Attack) {
        attacks.remove(attack)
    }

    fun additem(item: InventoryItem) {
        inventory.add(item)
    }

    fun removeitem(item: InventoryItem) {
        inventory.remove(item)
    }

    fun changeequip(item: InventoryItem) {
        inventory[inventory.indexOf(item)].equipped = item.equipped
    }

    fun getbonuses(filter : String) : Int {
        // macro is "bonus@[bonus@]"
        // bonus is "(AC|MHP|STR|DEX|CON|INT|WIS|CHA|SPD|INIT):bonus"
        var totalbonus : Int = 0
        for (item in inventory) {
            if (item.equipped) {
                for (bonus in item.macro.split("@")) {
                    try {
                        val stat = bonus.split(":")[0]
                        val plus = bonus.split(":")[1].toInt()
                        if (stat == filter) totalbonus += plus
                    } catch (e: Exception) {}
                }
            }
        }
        if (racialbonuses != null) {
            for (bonus in racialbonuses) {
                try {
                    val stat = bonus.split(":")[0]
                    val plus = bonus.split(":")[1].toInt()
                    if (stat == filter) totalbonus += plus
                } catch (e: Exception) {
                }
            }
        }
        if (classbonuses != null) {
            for (bonus in classbonuses) {
                try {
                    val stat = bonus.split(":")[0]
                    val plus = bonus.split(":")[1].toInt()
                    if (stat == filter) totalbonus += plus
                } catch (e: Exception) {
                }
            }
        }
        return totalbonus
    }

    fun listbonuses(filter : String) : List<String> {
        // macro is "bonus@[bonus@]"
        // bonus is "(AC|MHP|STR|DEX|CON|INT|WIS|CHA|SPD|INIT):bonus"
        var bonuses = mutableListOf<String>()
        for (item in inventory) {
            if (item.equipped) {
                for (bonus in item.macro.split("@")) {
                    try {
                        val stat = bonus.split(":")[0]
                        val plus = bonus.split(":")[1].toInt()
                        if (stat == filter) bonuses.add("+$plus from ${item.name}")
                    } catch (e: Exception) {}
                }
            }
        }
        if (racialbonuses != null) {
            for (bonus in racialbonuses) {
                try {
                    val stat = bonus.split(":")[0]
                    val plus = bonus.split(":")[1].toInt()
                    if (stat == filter) bonuses.add("+$plus racial bonus")
                } catch (e: Exception) {
                }
            }
        }
        if (classbonuses != null) {
            for (bonus in classbonuses) {
                try {
                    val stat = bonus.split(":")[0]
                    val plus = bonus.split(":")[1].toInt()
                    if (stat == filter) bonuses.add("+$plus class bonus")
                } catch (e: Exception) {
                }
            }
        }
        return bonuses
    }

    fun createroll(attack: Attack) : Diceroll {
        //n:max:prof:stat:bonus -> nDmax + (char.prof*prof + modstat + bonus)

        try {
            val macro = attack.macro.split(":")
            val n = macro[0].toInt()
            val max = macro[1].toInt()
            val prof = profbonus * macro[2].toInt()
            val stat = when (macro[3]) {
                "STR", "FOR", "0" -> modSTR
                "DEX", "1" -> modDEX
                "CON", "COS", "2" -> modCON
                "INT", "3" -> modINT
                "WIS", "SAG", "4" -> modWIS
                "CHA", "CAR", "5" -> modCHA
                else -> 0
            }
            val bonus = macro[4].toInt()
            Log.d("MACRO", Diceroll(n, max, (prof + stat + bonus)).desc())

            return Diceroll(n, max, (prof + stat + bonus))
        } catch (e: Exception) {
            return Diceroll()
        }
    }

    fun createroll(spell : Spell) : Diceroll {
        return createroll(Attack("", "", 0, spell.macro))
    }

    fun createroll(macro : String) : Diceroll {
        return createroll(Attack("", "", 0, macro))
    }

    override fun save(context: Context) {
        val filename = "$pgname.char"

        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            val gson = Gson()
            val json = gson.toJson(this)
            it.write(json.toByteArray())
        }
    }

    companion object {
        val xptable = intArrayOf(
            0, 300, 900, 2700, 6500, 14000, 23000, 34000, 46000, 64000, 85000, 100000,
            120000, 140000, 165000, 195000, 225000, 265000, 305000, 355000
        )

        fun load(context: Context, filename: String): DnD5eCharacter {
            context.openFileInput(filename).use {
                val gson = Gson()
                var json = ""
                for (str in it.bufferedReader().readLines()) {
                    json += str
                }
                return gson.fromJson(json, DnD5eCharacter::class.java)
            }
        }
    }
}