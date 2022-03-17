package com.fexed.charactersheet.character

import java.lang.Exception

class InventoryItem(var name: String, var desc: String, var weight: Double, var quantity : Int, var equipped : Boolean, var macro : String) {
    fun listbonuses() : String {
        // macro is "bonus@[bonus@]"
        // bonus is "(CA|PF|stat):bonus"
        var str = ""
        for (bonus in macro.split("@")) {
            try {
                val stat = bonus.split(":")[0]
                val plus = bonus.split(":")[1].toInt()
                str += stat + (if (plus > 0) "+" else "") + plus.toString() + ", "
            } catch (e: Exception) {}
        }
        return str.dropLast(2)
    }
}