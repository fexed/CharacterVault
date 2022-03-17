package com.fexed.charactersheet.character.dnd5e

import com.fexed.charactersheet.character.Diceroll

class Spell (
    val name : String,
    val desc : String,
    val level : Int,
    val macro : String,
) {
    val roll get() = macro.split(":")[0] + "D" + macro.split(":")[1] + " + " + (if (macro.split(":")[2] == "1") "prof + " else "") + (if (macro.split(":")[3] != "None") (macro.split(":")[3] + " + ") else " ") + macro.split(":")[4]
}