package com.fexed.charactersheet.ui.charactersummary

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.fexed.charactersheet.character.dnd5e.DnD5eCharacter
import com.fexed.charactersheet.R
import com.fexed.charactersheet.character.Diceroll
import com.fexed.charactersheet.characterselector.CharacterSelector
import com.fexed.charactersheet.ui.CharacterViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.overlay.BalloonOverlayRect
import java.io.File
import java.io.FileOutputStream
import java.lang.NumberFormatException

class CharacterSummary : Fragment() {

    companion object {
        fun newInstance() = CharacterSummary()
    }

    private val viewModel: CharacterViewModel by activityViewModels()

    private val getPropic = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val instream = requireContext().contentResolver.openInputStream(uri)
        val outfile = File("${requireContext().filesDir.absoluteFile}/${viewModel.currentcharacter.value!!.pgname}.png")
        val output = FileOutputStream(outfile)
        instream?.copyTo(output, 4 * 1024)
        instream?.close()
        output.close()
        val charact = viewModel.currentcharacter.value
        charact!!.portrait = outfile.absolutePath
        viewModel.updateCharacter(charact)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.character_summary_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (viewModel.currentcharacter.value == null) {
            val intent = Intent(context, CharacterSelector::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
            startActivity(intent)
        } else {
            val bottomSheetBehavior = BottomSheetBehavior.from(activity?.findViewById<LinearLayout>(R.id.bottomnav)!!)
            val sharedPreferences = requireContext().getSharedPreferences(requireContext().getString(R.string.app_package), Context.MODE_PRIVATE)
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                bottomSheetBehavior.setPeekHeight(200, true)
            } else {
                bottomSheetBehavior.setPeekHeight(125, true)
            }

            viewModel.currentcharacter.observe(viewLifecycleOwner) {
                activity?.findViewById<TextView>(R.id.pgname)!!.text = it.pgname
                activity?.findViewById<TextView>(R.id.pgclass)!!.text = it.pgclass
                activity?.findViewById<TextView>(R.id.pglv)!!.text = it.pglv.toString()
                activity?.findViewById<TextView>(R.id.pgrace)!!.text = it.pgrace

                if (it is DnD5eCharacter) {
                    if (viewModel.currentcharacter.value!!.portrait == null) {
                        activity?.findViewById<ImageView>(R.id.pgportrait)!!
                            .setImageResource(R.mipmap.nopic)
                    } else {
                        activity?.findViewById<ImageView>(R.id.pgportrait)!!.setImageDrawable(null)
                        activity?.findViewById<ImageView>(R.id.pgportrait)!!
                            .setImageURI(Uri.parse(viewModel.currentcharacter.value!!.portrait))
                    }
                    if (!(sharedPreferences.getBoolean("summarytutorial", false))) {
                        sharedPreferences.edit().putBoolean("summarytutorial", true).apply()
                        val keeptoeditballoon = Balloon.Builder(requireContext())
                            .setText(getString(R.string.keeptoedittutorial))
                            .setPadding(16)
                            .setIsVisibleOverlay(true)
                            .setOverlayColorResource(R.color.semitrasp)
                            .setOverlayPadding(8f)
                            .setDismissWhenOverlayClicked(true)
                            .setOverlayShape(BalloonOverlayRect)
                            .build()
                        val taptorollballoon = Balloon.Builder(requireContext())
                            .setText(getString(R.string.taptorolltutorial))
                            .setPadding(16)
                            .setIsVisibleOverlay(true)
                            .setOverlayColorResource(R.color.semitrasp)
                            .setOverlayPadding(8f)
                            .setDismissWhenOverlayClicked(true)
                            .setOverlayShape(BalloonOverlayRect)
                            .build()
                        val keeptoedithp = Balloon.Builder(requireContext())
                            .setText(getString(R.string.keeptoedit))
                            .setPadding(16)
                            .setArrowOrientation(ArrowOrientation.RIGHT)
                            .setIsVisibleOverlay(true)
                            .setOverlayColorResource(R.color.semitrasp)
                            .setOverlayPadding(8f)
                            .setDismissWhenOverlayClicked(true)
                            .setOverlayShape(BalloonOverlayRect)
                            .build()
                        val slidenotes = Balloon.Builder(requireContext())
                            .setText(getString(R.string.dragnotestutorial))
                            .setPadding(16)
                            .setIsVisibleOverlay(true)
                            .setOverlayColorResource(R.color.semitrasp)
                            .setOverlayPadding(8f)
                            .setDismissWhenOverlayClicked(true)
                            .setOverlayShape(BalloonOverlayRect)
                            .build()
                        val keeptoeditlv = Balloon.Builder(requireContext())
                            .setText(getString(R.string.keeptoedit))
                            .setPadding(16)
                            .setArrowOrientation(ArrowOrientation.RIGHT)
                            .setIsVisibleOverlay(true)
                            .setOverlayColorResource(R.color.semitrasp)
                            .setOverlayPadding(8f)
                            .setDismissWhenOverlayClicked(true)
                            .setOverlayShape(BalloonOverlayRect)
                            .build()
                        keeptoeditlv.relayShowAlignBottom(
                            keeptoeditballoon,
                            activity?.findViewById<TextView>(R.id.COS)!!
                        )
                        keeptoeditballoon.relayShowAlignBottom(
                            taptorollballoon,
                            activity?.findViewById<TextView>(R.id.INTmod)!!
                        )
                        taptorollballoon.relayShowAlignLeft(
                            keeptoedithp,
                            activity?.findViewById<TextView>(R.id.PFmax)!!
                        )
                        keeptoedithp.relayShowAlignBottom(
                            slidenotes,
                            activity?.findViewById<LinearLayout>(R.id.bottomnav)!!
                        )
                        activity?.findViewById<TextView>(R.id.pglv)!!.post {
                            keeptoeditlv.showAlignLeft(activity?.findViewById<TextView>(R.id.pglv)!!)
                        }
                    }
                    activity?.findViewById<TextView>(R.id.FOR)!!.text =
                        (it.STR + it.getbonuses("STR")).toString()
                    activity?.findViewById<TextView>(R.id.DEX)!!.text =
                        (it.DEX + it.getbonuses("DEX")).toString()
                    activity?.findViewById<TextView>(R.id.COS)!!.text =
                        (it.CON + it.getbonuses("CON")).toString()
                    activity?.findViewById<TextView>(R.id.SAG)!!.text =
                        (it.WIS + it.getbonuses("WIS")).toString()
                    activity?.findViewById<TextView>(R.id.INT)!!.text =
                        (it.INT + it.getbonuses("INT")).toString()
                    activity?.findViewById<TextView>(R.id.CAR)!!.text =
                        (it.CHA + it.getbonuses("CHA")).toString()
                    activity?.findViewById<TextView>(R.id.speed)!!.text =
                        it.speed.toString() + " ft."
                    activity?.findViewById<TextView>(R.id.initiative)!!.text =
                        (if (it.initative >= 0) "+" else "") + it.initative.toString()
                    activity?.findViewById<TextView>(R.id.passivperc)!!.text =
                        it.passiveperc.toString()

                    val modstr = (if (it.modSTR >= 0) "+" else "") + it.modSTR.toString()
                    activity?.findViewById<TextView>(R.id.FORmod)!!.text = modstr

                    val moddex = (if (it.modDEX >= 0) "+" else "") + it.modDEX.toString()
                    activity?.findViewById<TextView>(R.id.DEXmod)!!.text = moddex

                    val modcos = (if (it.modCON >= 0) "+" else "") + it.modCON.toString()
                    activity?.findViewById<TextView>(R.id.COSmod)!!.text = modcos

                    val modsag = (if (it.modWIS >= 0) "+" else "") + it.modWIS.toString()
                    activity?.findViewById<TextView>(R.id.SAGmod)!!.text = modsag

                    val modint = (if (it.modINT >= 0) "+" else "") + it.modINT.toString()
                    activity?.findViewById<TextView>(R.id.INTmod)!!.text = modint

                    val modcar = (if (it.modCHA >= 0) "+" else "") + it.modCHA.toString()
                    activity?.findViewById<TextView>(R.id.CARmod)!!.text = modcar

                    activity?.findViewById<TextView>(R.id.CA)!!.text =
                        (it.AC + it.getbonuses("AC")).toString()
                    activity?.findViewById<TextView>(R.id.PF)!!.text = it.CurrHP.toString()
                    activity?.findViewById<TextView>(R.id.PFmax)!!.text =
                        (it.MaxHP + it.getbonuses("MHP")).toString()
                    activity?.findViewById<TextView>(R.id.pgxp)!!.text = it.xp.toString()
                    if (it.pglv < 20 && it.xp >= it.nextlvlxp) {
                        activity?.findViewById<TextView>(R.id.pgxp)!!.text =
                            requireContext().getString(R.string.lvlup) + " " + it.xp.toString()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.newlevel, "" + (it.pglv + 1)),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    val profbonus = (if (it.profbonus >= 0) "+" else "") + it.profbonus.toString()
                    activity?.findViewById<TextView>(R.id.pgprof)!!.text = profbonus
                } else {
                    //TODO non è PG 5e
                }
            }
            prepareClickListeners()
            prepareLongClickListeners()
        }
    }
    
    private fun prepareClickListeners() {
        activity?.findViewById<EditText>(R.id.notespace)!!.text = Editable.Factory.getInstance().newEditable(viewModel.currentcharacter.value!!.notes)
        activity?.findViewById<EditText>(R.id.notespace)!!.doOnTextChanged { text, _, _, _ ->
            val charact = viewModel.currentcharacter.value
            charact!!.notes = text.toString()
            viewModel.updateCharacter(charact)
        }

        activity?.findViewById<TextView>(R.id.passivperc)!!.setOnClickListener {
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                Toast.makeText(requireContext(), R.string.passiveperccalc, Toast.LENGTH_LONG).show()
            }
        }

        activity?.findViewById<ImageButton>(R.id.xpaddbtn)!!.setOnClickListener {
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.addxpof)
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_NUMBER_FLAG_SIGNED)
                dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    try {
                        val int: Int = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                        charact.xp += int
                        viewModel.updateCharacter(charact)
                        dialog.dismiss()
                    } catch (e: NumberFormatException) {
                        dialog.findViewById<EditText>(R.id.newvalueinput).error = getString(R.string.numbererror)
                    }
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

        activity?.findViewById<ImageButton>(R.id.pfplus)!!.setOnClickListener {
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.editCurrHP(1)
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<ImageButton>(R.id.pfminus)!!.setOnClickListener {
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                charact.editCurrHP(-1)
                viewModel.updateCharacter(charact)
            }
        }

        activity?.findViewById<TextView>(R.id.initiative)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.initative),getString(R.string.initiative))
            }
        }

        activity?.findViewById<TextView>(R.id.FORtag)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modSTR),getString(R.string.forza))
            }
        }
        activity?.findViewById<TextView>(R.id.FORmod)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modSTR),getString(R.string.forza))
            }
        }

        activity?.findViewById<TextView>(R.id.DEXtag)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modDEX),getString(R.string.destrezza))
            }
        }

        activity?.findViewById<TextView>(R.id.DEXmod)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modDEX),getString(R.string.destrezza))
            }
        }

        activity?.findViewById<TextView>(R.id.COStag)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modCON),getString(R.string.costituzione))
            }
        }

        activity?.findViewById<TextView>(R.id.COSmod)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modCON),getString(R.string.costituzione))
            }
        }

        activity?.findViewById<TextView>(R.id.INTtag)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modINT),getString(R.string.intelligenza))
            }
        }

        activity?.findViewById<TextView>(R.id.INTmod)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modINT),getString(R.string.intelligenza))
            }
        }

        activity?.findViewById<TextView>(R.id.SAGtag)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modWIS),getString(R.string.saggezza))
            }
        }

        activity?.findViewById<TextView>(R.id.SAGmod)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modWIS),getString(R.string.saggezza))
            }
        }

        activity?.findViewById<TextView>(R.id.CARtag)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modCHA),getString(R.string.carisma))
            }
        }

        activity?.findViewById<TextView>(R.id.CARmod)!!.setOnClickListener {
            val character = viewModel.currentcharacter.value
            if (character is DnD5eCharacter) {
                rollDialog(Diceroll(1, 20, character.modCHA),getString(R.string.carisma))
            }
        }
    }

    private fun prepareLongClickListeners() {
        activity?.findViewById<ImageView>(R.id.pgportrait)!!.setOnLongClickListener{
            val charact = viewModel.currentcharacter.value
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.dialogpropictitle, charact!!.pgname))
            builder.setPositiveButton(R.string.dialogpropicselect) { d, _ ->
                d.dismiss()
                getPropic.launch("image/*")
            }
            builder.setNegativeButton(R.string.dialogpropicclear) {d, _ ->
                d.dismiss()
                charact.portrait = null
                viewModel.updateCharacter(charact)
            }
            builder.setNeutralButton(R.string.annulla) {d, _ ->
                d.dismiss()
            }
            builder.create().show()
            return@setOnLongClickListener true
        }

        activity?.findViewById<TextView>(R.id.pgclass)!!.setOnLongClickListener{
            //TODO classes
            val charact = viewModel.currentcharacter.value
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.newvaluelayout)
            dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.insertnewclass, charact!!.pgname)
            dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact.pgclass)
            dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
            dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                charact.pgclass = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString()
                viewModel.updateCharacter(charact)
                dialog.dismiss()
            }
            dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            return@setOnLongClickListener true
        }


        activity?.findViewById<TextView>(R.id.pgrace)!!.setOnLongClickListener{
            //TODO races
            val charact = viewModel.currentcharacter.value
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.newvaluelayout)
            dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.insertnewrace, charact!!.pgname)
            dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact.pgrace)
            dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
            dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                charact.pgrace = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString()
                viewModel.updateCharacter(charact)
                dialog.dismiss()
            }
            dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            return@setOnLongClickListener true
        }

        activity?.findViewById<TextView>(R.id.pglv)!!.setOnLongClickListener{
            val charact = viewModel.currentcharacter.value
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.newvaluelayout)
            dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.newvalue, getString(R.string.livello_totale))
            dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact!!.pglv.toString())
            dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
            dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
            dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                charact.pglv = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                viewModel.updateCharacter(charact)
                dialog.dismiss()
            }
            dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            return@setOnLongClickListener true
        }

        activity?.findViewById<TextView>(R.id.FOR)!!.setOnLongClickListener{
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.newvalue, getString(R.string.forza))
                dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact.STR.toString())
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
                var txt = ""
                for (str in charact.listbonuses("STR")) txt += str + "\n"
                if (txt == "") dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                else dialog.findViewById<TextView>(R.id.newvaluetxt).text = txt
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    charact.STR = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                    viewModel.updateCharacter(charact)
                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }

        activity?.findViewById<TextView>(R.id.DEX)!!.setOnLongClickListener{
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.newvalue, getString(R.string.destrezza))
                dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact.DEX.toString())
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
                var txt = ""
                for (str in charact.listbonuses("DEX")) txt += str + "\n"
                if (txt == "") dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                else dialog.findViewById<TextView>(R.id.newvaluetxt).text = txt
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    charact.DEX = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                    viewModel.updateCharacter(charact)
                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }

        activity?.findViewById<TextView>(R.id.COS)!!.setOnLongClickListener{
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.newvalue, getString(R.string.costituzione))
                dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact.CON.toString())
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
                var txt = ""
                for (str in charact.listbonuses("CON")) txt += str + "\n"
                if (txt == "") dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                else dialog.findViewById<TextView>(R.id.newvaluetxt).text = txt
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    charact.CON = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                    viewModel.updateCharacter(charact)
                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }

        activity?.findViewById<TextView>(R.id.SAG)!!.setOnLongClickListener{
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.newvalue, getString(R.string.saggezza))
                dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact.WIS.toString())
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
                var txt = ""
                for (str in charact.listbonuses("WIS")) txt += str + "\n"
                if (txt == "") dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                else dialog.findViewById<TextView>(R.id.newvaluetxt).text = txt
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    charact.WIS = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                    viewModel.updateCharacter(charact)
                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }

        activity?.findViewById<TextView>(R.id.INT)!!.setOnLongClickListener{
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.newvalue, getString(R.string.intelligenza))
                dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact.INT.toString())
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
                var txt = ""
                for (str in charact.listbonuses("INT")) txt += str + "\n"
                if (txt == "") dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                else dialog.findViewById<TextView>(R.id.newvaluetxt).text = txt
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    charact.INT = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                    viewModel.updateCharacter(charact)
                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }

        activity?.findViewById<TextView>(R.id.CAR)!!.setOnLongClickListener{
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.newvalue, getString(R.string.carisma))
                dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact.CHA.toString())
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
                var txt = ""
                for (str in charact.listbonuses("CHA")) txt += str + "\n"
                if (txt == "") dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                else dialog.findViewById<TextView>(R.id.newvaluetxt).text = txt
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    charact.CHA = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                    viewModel.updateCharacter(charact)
                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }

        activity?.findViewById<ImageButton>(R.id.pfplus)!!.setOnLongClickListener {
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.entercure)
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
                dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    val int : Int = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                    charact.CurrHP = charact.CurrHP + int
                    viewModel.updateCharacter(charact)
                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }

        activity?.findViewById<ImageButton>(R.id.pfminus)!!.setOnLongClickListener {
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.enterdamage)
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
                dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    val int : Int = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                    charact.CurrHP = charact.CurrHP - int
                    viewModel.updateCharacter(charact)
                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }

        activity?.findViewById<TextView>(R.id.PFmax)!!.setOnLongClickListener {
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.insertmaxpf)
                dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact.MaxHP.toString())
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
                var txt = ""
                for (str in charact.listbonuses("MHP")) txt += str + "\n"
                if (txt == "") dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                else dialog.findViewById<TextView>(R.id.newvaluetxt).text = txt
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    charact.MaxHP = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                    viewModel.updateCharacter(charact)
                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }

        activity?.findViewById<TextView>(R.id.CA)!!.setOnLongClickListener {
            val charact = viewModel.currentcharacter.value
            if (charact is DnD5eCharacter) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.newvaluelayout)
                dialog.findViewById<TextView>(R.id.newvaluetitle).text = getString(R.string.newvalue, getString(R.string.classe_armatura))
                dialog.findViewById<EditText>(R.id.newvalueinput).setText(charact.AC.toString())
                dialog.findViewById<EditText>(R.id.newvalueinput).setRawInputType(InputType.TYPE_CLASS_NUMBER)
                var txt = ""
                for (str in charact.listbonuses("AC")) txt += str + "\n"
                if (txt == "") dialog.findViewById<TextView>(R.id.newvaluetxt).isVisible = false
                else dialog.findViewById<TextView>(R.id.newvaluetxt).text = txt
                dialog.findViewById<Button>(R.id.newvalueconfirm).setOnClickListener {
                    charact.AC = dialog.findViewById<EditText>(R.id.newvalueinput).text.toString().toInt()
                    viewModel.updateCharacter(charact)
                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.newvaluecancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            } else {
                return@setOnLongClickListener false
            }
        }
    }

    private fun rollDialog(roll : Diceroll, title : String) {
        Diceroll.rollDialog(requireContext(), roll, title)
    }
}