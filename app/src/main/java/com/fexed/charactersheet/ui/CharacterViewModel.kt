package com.fexed.charactersheet.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fexed.charactersheet.character.Character
import com.fexed.charactersheet.DataLoader

class CharacterViewModel : ViewModel() {
    private val _currentcharacter : MutableLiveData<Character> by lazy {
        MutableLiveData(DataLoader.character)
    }

    public val currentcharacter : LiveData<Character> = _currentcharacter

    fun updateCharacter(character: Character) {
        _currentcharacter.value = character
    }
}