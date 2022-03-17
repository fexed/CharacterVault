package com.fexed.charactersheet

import android.content.Context
import com.fexed.charactersheet.character.Character
import com.fexed.charactersheet.character.dnd5e.DnD5eCharacter
import com.fexed.charactersheet.databinding.CharacterAttacksFragmentBinding

class DataLoader {
    companion object {
        var character : Character? = null

        fun loadCharacter(context: Context, filename: String) {
            character = DnD5eCharacter.load(context, filename)
        }

        fun loadCharacter(newcharacter: Character) {
            character = newcharacter
        }

        fun deleteCharacter(context: Context, filename: String) {
            context.deleteFile(filename)
        }
    }
}