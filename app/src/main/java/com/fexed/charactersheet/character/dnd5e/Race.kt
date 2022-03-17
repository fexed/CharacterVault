package com.fexed.charactersheet.character.dnd5e

class Race(
    var name : String,
    var bonuses : Array<String>?,
    var selecttwo : Boolean
) {
    companion object {
        //https://summoninggrounds.com/5e-racial-stat-bonus-chart/
        val standardraces = arrayListOf(
            Race("Hill Dwarf", arrayOf("CON:2", "WIS:1"), false),
            Race("Mountain Dwarf", arrayOf("STR:2", "CON:2"), false),
            Race("High Elf", arrayOf("DEX:2", "INT:1"), false),
            Race("Wood Elf", arrayOf("DEX:2", "WIS:1"), false),
            Race("Drow", arrayOf("DEX:2", "CHA:1"), false),
            Race("Lightfoot Halfling", arrayOf("DEX:2", "CHA:1"), false),
            Race("Stout Halfling", arrayOf("DEX:2", "CON:1"), false),
            Race("Human", arrayOf("STR:1", "DEX:1", "CON:1", "INT:1", "WIS:1", "CHA:1"), false),
            Race("Human (variant)", null, true),
            Race("Dragonborn", arrayOf("STR:2", "CHA:1"), false),
            Race("Forest Gnome", arrayOf("DEX:1", "INT:2"), false),
            Race("Rock Gnome", arrayOf("CON:1", "INT:2"), false),
            Race("Half-Elf", arrayOf("CHA:2"), true),
            Race("Half-Orc", arrayOf("STR:2", "CON:1"), false),
            Race("Tiefling", arrayOf("INT:1", "CHA:1"), false),
            Race("Aasimar", arrayOf("WIS:1", "CHA:1"), false),
            Race("Eladrin Elf", arrayOf("DEX:2", "INT:1"), false),
            Race("Aarakocra", arrayOf("DEX:2", "WIS:1"), false),
        )
    }
}