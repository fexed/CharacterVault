package com.fexed.charactersheet.ui.characterskills

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.fexed.charactersheet.R
import com.fexed.charactersheet.character.Diceroll
import com.fexed.charactersheet.character.dnd5e.DnD5eCharacter
import com.fexed.charactersheet.ui.CharacterViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.overlay.BalloonOverlayRect

class CharacterSkills : Fragment() {

    companion object {
        fun newInstance() = CharacterSkills()
    }

    private val viewModel: CharacterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.character_skills_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(CharacterViewModel::class.java)
        viewModel.currentcharacter.observe(viewLifecycleOwner, {
            activity?.findViewById<TextView>(R.id.skills_pgname)!!.text = it.pgname
            activity?.findViewById<TextView>(R.id.skills_pglv)!!.text = it.pglv.toString()

            if (it is DnD5eCharacter) {
                val sharedPreferences = context!!.getSharedPreferences(context!!.getString(R.string.app_package), Context.MODE_PRIVATE)
                if (!(sharedPreferences.getBoolean("skillstutorial", false))) {
                    sharedPreferences.edit().putBoolean("skillstutorial", true).apply()
                    val taptorollballoon = Balloon.Builder(context!!)
                        .setText(getString(R.string.taptorolltutorial))
                        .setPadding(16)
                        .setIsVisibleOverlay(true)
                        .setOverlayColorResource(R.color.semitrasp)
                        .setOverlayPadding(8f)
                        .setDismissWhenOverlayClicked(true)
                        .setOverlayShape(BalloonOverlayRect)
                        .build()
                    val taptorollballoon2 = Balloon.Builder(context!!)
                        .setText(getString(R.string.taptorolltutorial))
                        .setPadding(16)
                        .setIsVisibleOverlay(true)
                        .setOverlayColorResource(R.color.semitrasp)
                        .setOverlayPadding(8f)
                        .setDismissWhenOverlayClicked(true)
                        .setOverlayShape(BalloonOverlayRect)
                        .build()
                    val setrolltypetutorial = Balloon.Builder(context!!)
                        .setText(getString(R.string.dragtoadvantage))
                        .setPadding(16)
                        .setIsVisibleOverlay(true)
                        .setOverlayColorResource(R.color.semitrasp)
                        .setOverlayPadding(8f)
                        .setDismissWhenOverlayClicked(true)
                        .setOverlayShape(BalloonOverlayRect)
                        .build()
                    val customrolltutorial = Balloon.Builder(context!!)
                        .setText(getString(R.string.rolldialogtutorial))
                        .setPadding(16)
                        .setArrowOrientation(ArrowOrientation.RIGHT)
                        .setIsVisibleOverlay(true)
                        .setOverlayColorResource(R.color.semitrasp)
                        .setOverlayPadding(8f)
                        .setDismissWhenOverlayClicked(true)
                        .setOverlayShape(BalloonOverlayRect)
                        .build()
                    taptorollballoon2.relayShowAlignTop(taptorollballoon, activity?.findViewById<TextView>(R.id.atletica)!!)
                    taptorollballoon.relayShowAlignTop(setrolltypetutorial, activity?.findViewById<SeekBar>(R.id.rolltypeseekbar)!!)
                    setrolltypetutorial.relayShowAlignLeft(customrolltutorial, activity?.findViewById<FloatingActionButton>(R.id.dicefab)!!)
                    activity?.findViewById<TextView>(R.id.TSCOS)!!.post {
                        taptorollballoon2.showAlignBottom(activity?.findViewById<TextView>(R.id.TSCOS)!!)
                    }
                }

                activity?.findViewById<TextView>(R.id.skills_pgprof)!!.text = (if (it.profbonus >= 0) "+" else "") + it.profbonus.toString()

                activity?.findViewById<CheckBox>(R.id.comptsfor)!!.isChecked = it.tsstr
                activity?.findViewById<TextView>(R.id.TSFOR)!!.text = (if (it.bonustsstr >= 0) "+" else "") + it.bonustsstr.toString()
                activity?.findViewById<CheckBox>(R.id.compatletica)!!.isChecked = it.atletica_comp
                activity?.findViewById<CheckBox>(R.id.expatletica)!!.visibility = if (it.atletica_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expatletica)!!.isChecked = it.atletica_exp
                activity?.findViewById<TextView>(R.id.atletica)!!.text = (if (it.bonusatletica >= 0) "+" else "") + it.bonusatletica.toString()


                activity?.findViewById<CheckBox>(R.id.comptsdex)!!.isChecked = it.tsdex
                activity?.findViewById<TextView>(R.id.TSDEX)!!.text = (if (it.bonustsdex >= 0) "+" else "") + it.bonustsdex.toString()
                activity?.findViewById<CheckBox>(R.id.compacrobazia)!!.isChecked = it.acrobazia_comp
                activity?.findViewById<CheckBox>(R.id.expacrobazia)!!.visibility = if (it.acrobazia_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expacrobazia)!!.isChecked = it.acrobazia_exp
                activity?.findViewById<TextView>(R.id.acrobazia)!!.text = (if (it.bonusacrobazia >= 0) "+" else "") + it.bonusacrobazia.toString()
                activity?.findViewById<CheckBox>(R.id.compfurtivita)!!.isChecked = it.furtivita_comp
                activity?.findViewById<CheckBox>(R.id.expfurtivita)!!.visibility = if (it.furtivita_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expfurtivita)!!.isChecked = it.furtivita_exp
                activity?.findViewById<TextView>(R.id.furtivita)!!.text = (if (it.bonusfurtivita >= 0) "+" else "") + it.bonusfurtivita.toString()
                activity?.findViewById<CheckBox>(R.id.comprapiditadimano)!!.isChecked = it.rapdimano_comp
                activity?.findViewById<CheckBox>(R.id.exprapiditadimano)!!.visibility = if (it.rapdimano_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.exprapiditadimano)!!.isChecked = it.rapdimano_exp
                activity?.findViewById<TextView>(R.id.rapiditadimano)!!.text = (if (it.bonusrapdimano >= 0) "+" else "") + it.bonusrapdimano.toString()


                activity?.findViewById<CheckBox>(R.id.comptscos)!!.isChecked = it.tscon
                activity?.findViewById<TextView>(R.id.TSCOS)!!.text = (if (it.bonustscon >= 0) "+" else "") + it.bonustscon.toString()

                activity?.findViewById<CheckBox>(R.id.comptsint)!!.isChecked = it.tsint
                activity?.findViewById<TextView>(R.id.TSINT)!!.text = (if (it.bonustsint >= 0) "+" else "") + it.bonustsint.toString()
                activity?.findViewById<CheckBox>(R.id.compinvestigare)!!.isChecked = it.investigare_comp
                activity?.findViewById<CheckBox>(R.id.expinvestigare)!!.visibility = if (it.investigare_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expinvestigare)!!.isChecked = it.investigare_exp
                activity?.findViewById<TextView>(R.id.investigare)!!.text = (if (it.bonusinvestigare >= 0) "+" else "") + it.bonusinvestigare.toString()
                activity?.findViewById<CheckBox>(R.id.comparcano)!!.isChecked = it.arcano_comp
                activity?.findViewById<CheckBox>(R.id.exparcano)!!.visibility = if (it.arcano_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.exparcano)!!.isChecked = it.arcano_exp
                activity?.findViewById<TextView>(R.id.arcano)!!.text = (if (it.bonusarcano >= 0) "+" else "") + it.bonusarcano.toString()
                activity?.findViewById<CheckBox>(R.id.compstoria)!!.isChecked = it.storia_comp
                activity?.findViewById<CheckBox>(R.id.expstoria)!!.visibility = if (it.storia_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expstoria)!!.isChecked = it.storia_exp
                activity?.findViewById<TextView>(R.id.storia)!!.text = (if (it.bonusstoria >= 0) "+" else "") + it.bonusstoria.toString()
                activity?.findViewById<CheckBox>(R.id.compreligionefolklore)!!.isChecked = it.religione_comp
                activity?.findViewById<CheckBox>(R.id.expreligionefolklore)!!.visibility = if (it.religione_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expreligionefolklore)!!.isChecked = it.religione_exp
                activity?.findViewById<TextView>(R.id.religionefolklore)!!.text = (if (it.bonusreligione >= 0) "+" else "") + it.bonusreligione.toString()
                activity?.findViewById<CheckBox>(R.id.compnatura)!!.isChecked = it.natura_comp
                activity?.findViewById<CheckBox>(R.id.expnatura)!!.visibility = if (it.natura_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expnatura)!!.isChecked = it.natura_exp
                activity?.findViewById<TextView>(R.id.natura)!!.text = (if (it.bonusnatura >= 0) "+" else "") + it.bonusnatura.toString()


                activity?.findViewById<CheckBox>(R.id.comptssag)!!.isChecked = it.tswis
                activity?.findViewById<TextView>(R.id.TSSAG)!!.text = (if (it.bonustswis >= 0) "+" else "") + it.bonustswis.toString()
                activity?.findViewById<CheckBox>(R.id.compsopravvivenza)!!.isChecked = it.sopravv_comp
                activity?.findViewById<CheckBox>(R.id.expsopravvivenza)!!.visibility = if (it.sopravv_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expsopravvivenza)!!.isChecked = it.sopravviv_exp
                activity?.findViewById<TextView>(R.id.sopravvivenza)!!.text = (if (it.bonussopravv >= 0) "+" else "") + it.bonussopravv.toString()
                activity?.findViewById<CheckBox>(R.id.compmedicina)!!.isChecked = it.medicina_comp
                activity?.findViewById<CheckBox>(R.id.expmedicina)!!.visibility = if (it.medicina_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expmedicina)!!.isChecked = it.medicina_exp
                activity?.findViewById<TextView>(R.id.medicina)!!.text = (if (it.bonusmedicina >= 0) "+" else "") + it.bonusmedicina.toString()
                activity?.findViewById<CheckBox>(R.id.comppercezione)!!.isChecked = it.percezione_comp
                activity?.findViewById<CheckBox>(R.id.exppercezione)!!.visibility = if (it.percezione_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.exppercezione)!!.isChecked = it.percezione_exp
                activity?.findViewById<TextView>(R.id.percezione)!!.text = (if (it.bonuspercezione >= 0) "+" else "") + it.bonuspercezione.toString()
                activity?.findViewById<CheckBox>(R.id.compintuizione)!!.isChecked = it.intuizione_comp
                activity?.findViewById<CheckBox>(R.id.expintuizione)!!.visibility = if (it.intuizione_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expintuizione)!!.isChecked = it.intuizione_exp
                activity?.findViewById<TextView>(R.id.intuizione)!!.text = (if (it.bonusintuiz >= 0) "+" else "") + it.bonusintuiz.toString()
                activity?.findViewById<CheckBox>(R.id.companimali)!!.isChecked = it.animali_comp
                activity?.findViewById<CheckBox>(R.id.expanimali)!!.visibility = if (it.animali_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expanimali)!!.isChecked = it.animali_exp
                activity?.findViewById<TextView>(R.id.animali)!!.text = (if (it.bonusanimali >= 0) "+" else "") + it.bonusanimali.toString()

                activity?.findViewById<CheckBox>(R.id.comptscar)!!.isChecked = it.tscha
                activity?.findViewById<TextView>(R.id.TSCAR)!!.text = (if (it.bonustscha >= 0) "+" else "") + it.bonustscha.toString()
                activity?.findViewById<CheckBox>(R.id.compintimidire)!!.isChecked = it.intimidire_comp
                activity?.findViewById<CheckBox>(R.id.expintimidire)!!.visibility = if (it.intimidire_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expintimidire)!!.isChecked = it.intimidire_exp
                activity?.findViewById<TextView>(R.id.intimidire)!!.text = (if (it.bonusintimidire >= 0) "+" else "") + it.bonusintimidire.toString()
                activity?.findViewById<CheckBox>(R.id.compingannare)!!.isChecked = it.ingannare_comp
                activity?.findViewById<CheckBox>(R.id.expingannare)!!.visibility = if (it.ingannare_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expingannare)!!.isChecked = it.ingannare_exp
                activity?.findViewById<TextView>(R.id.ingannare)!!.text = (if (it.bonusingannare >= 0) "+" else "") + it.bonusingannare.toString()
                activity?.findViewById<CheckBox>(R.id.compintrattenere)!!.isChecked = it.intrattenere_comp
                activity?.findViewById<CheckBox>(R.id.expintrattenere)!!.visibility = if (it.intrattenere_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.expintrattenere)!!.isChecked = it.intrattenere_exp
                activity?.findViewById<TextView>(R.id.intrattenere)!!.text = (if (it.bonusintrattenere >= 0) "+" else "") + it.bonusintrattenere.toString()
                activity?.findViewById<CheckBox>(R.id.comppersuadere)!!.isChecked = it.persuadere_comp
                activity?.findViewById<CheckBox>(R.id.exppersuadere)!!.visibility = if (it.persuadere_comp) View.VISIBLE else View.INVISIBLE
                activity?.findViewById<CheckBox>(R.id.exppersuadere)!!.isChecked = it.persuadere_exp
                activity?.findViewById<TextView>(R.id.persuadere)!!.text = (if (it.bonuspersuadere >= 0) "+" else "") + it.bonuspersuadere.toString()

            } else {
                //TODO non è PG 5e
            }
        })

        prepareCheckboxes()
        prepareTouchEvents()
        prepareFab()
    }

    private fun prepareCheckboxes() {
        activity?.findViewById<CheckBox>(R.id.comptsfor)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.tsstr = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.comptsdex)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.tsdex = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.comptscos)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.tscon = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.comptsint)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.tsint = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.comptssag)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.tswis = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.comptscar)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.tscha = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compatletica)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.atletica_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expatletica)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.atletica_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compacrobazia)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.acrobazia_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expacrobazia)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.acrobazia_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compfurtivita)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.furtivita_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expfurtivita)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.furtivita_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.comprapiditadimano)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.rapdimano_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.exprapiditadimano)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.rapdimano_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compinvestigare)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.investigare_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expinvestigare)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.investigare_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.comparcano)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.arcano_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.exparcano)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.arcano_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compstoria)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.storia_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expstoria)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.storia_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compreligionefolklore)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.religione_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expreligionefolklore)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.religione_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compnatura)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.natura_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expnatura)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.natura_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compsopravvivenza)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.sopravv_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expsopravvivenza)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.sopravviv_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compmedicina)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.medicina_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expmedicina)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.medicina_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.comppercezione)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.percezione_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.exppercezione)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.percezione_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compintuizione)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.intuizione_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expintuizione)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.intuizione_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.companimali)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.animali_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expanimali)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.animali_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compintimidire)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.intimidire_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expintimidire)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.intimidire_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compingannare)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.ingannare_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expingannare)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.ingannare_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.compintrattenere)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.intrattenere_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.expintrattenere)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.intrattenere_exp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.comppersuadere)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.persuadere_comp = b
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<CheckBox>(R.id.exppersuadere)!!.setOnCheckedChangeListener { _, b ->
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.persuadere_exp = b
                viewModel.updateCharacter(charact)
            }
        }
    }

    private fun prepareTouchEvents() {
    val character = viewModel.currentcharacter.value
        if (character is DnD5eCharacter) {
            activity?.findViewById<TextView>(R.id.atletica)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusatletica), getString(R.string.atletica))
            }

            activity?.findViewById<TextView>(R.id.acrobazia)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusacrobazia), getString(R.string.acrobazia))
            }

            activity?.findViewById<TextView>(R.id.furtivita)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusfurtivita), getString(R.string.furtivit))
            }

            activity?.findViewById<TextView>(R.id.rapiditadimano)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusrapdimano), getString(R.string.rapidit_di_mano))
            }

            activity?.findViewById<TextView>(R.id.investigare)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusinvestigare), getString(R.string.investigare))
            }

            activity?.findViewById<TextView>(R.id.arcano)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusarcano), getString(R.string.arcano))
            }

            activity?.findViewById<TextView>(R.id.storia)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusstoria), getString(R.string.storia))
            }

            activity?.findViewById<TextView>(R.id.religionefolklore)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusreligione), getString(R.string.religione_e_folklore))
            }

            activity?.findViewById<TextView>(R.id.natura)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusnatura), getString(R.string.natura))
            }

            activity?.findViewById<TextView>(R.id.sopravvivenza)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonussopravv), getString(R.string.sopravvivenza))
            }

            activity?.findViewById<TextView>(R.id.medicina)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusmedicina), getString(R.string.medicina))
            }

            activity?.findViewById<TextView>(R.id.percezione)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonuspercezione), getString(R.string.percezione))
            }

            activity?.findViewById<TextView>(R.id.intuizione)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusintuiz), getString(R.string.intuizione))
            }

            activity?.findViewById<TextView>(R.id.animali)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusanimali), getString(R.string.animali))
            }

            activity?.findViewById<TextView>(R.id.intimidire)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusintimidire), getString(R.string.intimidire))
            }

            activity?.findViewById<TextView>(R.id.ingannare)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusingannare), getString(R.string.ingannare))
            }

            activity?.findViewById<TextView>(R.id.intrattenere)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonusintrattenere), getString(R.string.intrattenere))
            }

            activity?.findViewById<TextView>(R.id.persuadere)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonuspersuadere), getString(R.string.persuadere))
            }

            activity?.findViewById<TextView>(R.id.TSFOR)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonustsstr), getString(R.string.tiro_salvezza) + " (" + getString(R.string.str) + ")")
            }

            activity?.findViewById<TextView>(R.id.TSDEX)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonustsdex), getString(R.string.tiro_salvezza) + " (" + getString(R.string.dex) + ")")
            }

            activity?.findViewById<TextView>(R.id.TSCOS)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonustscon), getString(R.string.tiro_salvezza) + " (" + getString(R.string.cos) + ")")
            }

            activity?.findViewById<TextView>(R.id.TSINT)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonustsint), getString(R.string.tiro_salvezza) + " (" + getString(R.string.inte) + ")")
            }

            activity?.findViewById<TextView>(R.id.TSSAG)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonustswis), getString(R.string.tiro_salvezza) + " (" + getString(R.string.sag) + ")")
            }

            activity?.findViewById<TextView>(R.id.TSCAR)!!.setOnClickListener {
                rollDialog(Diceroll(1, 20, character.bonustscha), getString(R.string.tiro_salvezza) + " (" + getString(R.string.car) + ")")
            }
        }
    }

    private fun prepareFab() {
        val character = viewModel.currentcharacter.value
        if (character is DnD5eCharacter) {
            activity?.findViewById<FloatingActionButton>(R.id.dicefab)!!.setOnClickListener {
                var macro = ""
                val newmacrodialog = Dialog(context!!)
                newmacrodialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                newmacrodialog.setContentView(R.layout.newmacro)
                newmacrodialog.findViewById<TextView>(R.id.newmacrotitle).text = getString(R.string.newroll)
                newmacrodialog.findViewById<Button>(R.id.newmacro_ok).setOnClickListener {
                    var txt = newmacrodialog.findViewById<EditText>(R.id.newmacro_n).text.toString()
                    if (txt == "") {
                        newmacrodialog.findViewById<EditText>(R.id.newmacro_n).error =
                            getString(R.string.numbererror)
                    } else {
                        val n = txt
                        txt =
                            newmacrodialog.findViewById<EditText>(R.id.newmacro_max).text.toString()
                        if (txt == "") {
                            newmacrodialog.findViewById<EditText>(R.id.newmacro_max).error =
                                getString(R.string.numbererror)
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
                            txt =
                                newmacrodialog.findViewById<EditText>(R.id.newmacro_bonus).text.toString()
                            val bonus: String = (if (txt == "") "0" else txt)
                            macro = "$n:$max:$prof:$stat:$bonus"
                            txt =
                                n + "D$max +" + (if (prof == "1") " prof + " else " ") + (if (stat != "None") "$stat + " else " ") + bonus
                            newmacrodialog.dismiss()
                            val roll = character.createroll(macro)
                            rollDialog(roll, txt)
                        }
                    }
                }
                newmacrodialog.findViewById<Button>(R.id.newmacro_cancel).setOnClickListener {
                    macro = ""
                    newmacrodialog.dismiss()
                }

                newmacrodialog.show()
            }
        }
    }

    private fun rollDialog(roll : Diceroll, title : String) {
        when (activity?.findViewById<SeekBar>(R.id.rolltypeseekbar)!!.progress) {
            0 -> Diceroll.rollDouble(context!!, roll, title, false)
            1 -> Diceroll.rollDialog(context!!, roll, title)
            2 -> Diceroll.rollDouble(context!!, roll, title, true)
        }
    }
}