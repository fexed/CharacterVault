package com.fexed.charactersheet.character

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.TextView
import androidx.core.view.isVisible
import com.fexed.charactersheet.R
import org.w3c.dom.Text

class Diceroll(n: Int, max: Int, bonus: Int) {
    companion object {
        fun rollDialog(context : Context, roll : Diceroll, title : String) {
            context.getSharedPreferences(context.getString(R.string.app_package), Context.MODE_PRIVATE).edit().putInt("dicerolled", context.getSharedPreferences(context.getString(R.string.app_package), Context.MODE_PRIVATE).getInt("dicerolled", 0) + roll.howmany).apply()
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.rolldialog)
            dialog.findViewById<TextView>(R.id.rolltitle).text = title
            dialog.findViewById<TextView>(R.id.rolltext).text = roll.desc()
            val result = roll.roll()
            assert(result.size == roll.howmany + 1)
            dialog.findViewById<TextView>(R.id.rollresult).text = result[roll.howmany].toString()

            var txt = ""
            for (i in 0 until roll.howmany) {
                txt += result[i].toString() + " + "
            }
            txt = txt.dropLast(3)
            txt += " (" + (if (roll.basebonus > 0) " + ${roll.basebonus}" else (if (roll.basebonus < 0) " - ${roll.basebonus*(-1)}" else "+ 0")) + ")"
            dialog.findViewById<TextView>(R.id.rolldice).text = txt

            if (roll.howmany == 1 && roll.dice == 20) {
                if (result[roll.howmany - 1] == 1 || result[roll.howmany - 1] == 20) {
                    dialog.findViewById<TextView>(R.id.rollnattxt).text = context.getString(R.string.naturaldice, result[roll.howmany - 1].toString())
                    dialog.findViewById<TextView>(R.id.rollnattxt).isVisible = true
                }
            }
            dialog.show()
        }


        fun rollAttack(context : Context, roll : Diceroll, atkbonus : Int, title : String) {
            context.getSharedPreferences(context.getString(R.string.app_package), Context.MODE_PRIVATE).edit().putInt("dicerolled", context.getSharedPreferences(context.getString(R.string.app_package), Context.MODE_PRIVATE).getInt("dicerolled", 0) + roll.howmany + 1).apply()
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.atkdialog)
            dialog.findViewById<TextView>(R.id.atkrolltitle).text = title
            dialog.findViewById<TextView>(R.id.atkdmgtxt).text = roll.desc()
            val result = roll.roll()
            assert(result.size == roll.howmany + 1)
            dialog.findViewById<TextView>(R.id.atkdmgresult).text = result[roll.howmany].toString()

            var txt = ""
            for (i in 0 until roll.howmany) {
                txt += result[i].toString() + " + "
            }
            txt = txt.dropLast(3)
            txt += " (" + (if (roll.basebonus > 0) " + ${roll.basebonus}" else (if (roll.basebonus < 0) " - ${roll.basebonus*(-1)}" else "+ 0")) + ")"
            dialog.findViewById<TextView>(R.id.atkdmgdice).text = txt

            val atkresult = Diceroll(1, 20, atkbonus).roll()
            dialog.findViewById<TextView>(R.id.atkrolltext).text = Diceroll(1, 20, atkbonus).desc()
            dialog.findViewById<TextView>(R.id.atkrollresult).text = atkresult[1].toString()

            txt = ""
            for (i in 0 until roll.howmany) {
                txt += atkresult[i].toString() + " + "
            }
            txt = txt.dropLast(3)
            txt += " (+ $atkbonus)"
            dialog.findViewById<TextView>(R.id.atkrolldice).text = txt

            if (atkresult[0] == 1 || atkresult[0] == 20) {
                dialog.findViewById<TextView>(R.id.atkrollnattxt).text = context.getString(R.string.naturaldice, atkresult[0].toString())
                dialog.findViewById<TextView>(R.id.atkrollnattxt).isVisible = true
            }

            dialog.show()
        }

        fun rollDouble(context : Context, roll : Diceroll, title : String, advantage : Boolean = true) {
            context.getSharedPreferences(context.getString(R.string.app_package), Context.MODE_PRIVATE).edit().putInt("dicerolled", context.getSharedPreferences(context.getString(R.string.app_package), Context.MODE_PRIVATE).getInt("dicerolled", 0) + roll.howmany*2).apply()
            val dialog = Dialog(context)
            val newtitle = title + "\n(" + (if (advantage) context.getString(R.string.advantage) else context.getString(R.string.disadvantage)) + ")"
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.atkdialog)
            dialog.findViewById<TextView>(R.id.atkrolltitle).text = newtitle
            dialog.findViewById<TextView>(R.id.atkdmgtxt).text = roll.desc()
            dialog.findViewById<TextView>(R.id.atkrolldice2).isVisible = false
            dialog.findViewById<TextView>(R.id.atkrolldice3).isVisible = false
            val result = roll.roll()
            assert(result.size == roll.howmany + 1)
            dialog.findViewById<TextView>(R.id.atkdmgresult).text = result[roll.howmany].toString()

            var txt = ""
            for (i in 0 until roll.howmany) {
                txt += result[i].toString() + " + "
            }
            txt = txt.dropLast(3)
            txt += " (" + (if (roll.basebonus > 0) " + ${roll.basebonus}" else (if (roll.basebonus < 0) " - ${roll.basebonus*(-1)}" else "+ 0")) + ")"
            dialog.findViewById<TextView>(R.id.atkdmgdice).text = txt

            val secondresult = roll.roll()
            dialog.findViewById<TextView>(R.id.atkrolltext).text = roll.desc()
            dialog.findViewById<TextView>(R.id.atkrollresult).text = secondresult[roll.howmany].toString()

            txt = ""
            for (i in 0 until roll.howmany) {
                txt += secondresult[i].toString() + " + "
            }
            txt = txt.dropLast(3)
            txt += " (" + (if (roll.basebonus > 0) " + ${roll.basebonus}" else (if (roll.basebonus < 0) " - ${roll.basebonus*(-1)}" else "+ 0")) + ")"
            dialog.findViewById<TextView>(R.id.atkrolldice).text = txt

            if (advantage) {
                if (result[roll.howmany] > secondresult[roll.howmany]) {
                    dialog.findViewById<TextView>(R.id.atkrollresult).alpha = 0.5F
                    if (result[roll.howmany] == 1 || result[roll.howmany] == 20) {
                        dialog.findViewById<TextView>(R.id.atkrollnattxt).text = context.getString(R.string.naturaldice, result[roll.howmany].toString())
                        dialog.findViewById<TextView>(R.id.atkrollnattxt).isVisible = true
                    }
                } else {
                    dialog.findViewById<TextView>(R.id.atkdmgresult).alpha = 0.5F
                    if (secondresult[roll.howmany] == 1 || secondresult[roll.howmany] == 20) {
                        dialog.findViewById<TextView>(R.id.atkrollnattxt).text = context.getString(R.string.naturaldice, secondresult[roll.howmany].toString())
                        dialog.findViewById<TextView>(R.id.atkrollnattxt).isVisible = true
                    }
                }
            } else {
                if (result[roll.howmany] < secondresult[roll.howmany]) {
                    dialog.findViewById<TextView>(R.id.atkrollresult).alpha = 0.5F
                    if (result[roll.howmany] == 1 || result[roll.howmany] == 20) {
                        dialog.findViewById<TextView>(R.id.atkrollnattxt).text = context.getString(R.string.naturaldice, result[roll.howmany].toString())
                        dialog.findViewById<TextView>(R.id.atkrollnattxt).isVisible = true
                    }
                } else {
                    dialog.findViewById<TextView>(R.id.atkdmgresult).alpha = 0.5F
                    if (secondresult[roll.howmany] == 1 || secondresult[roll.howmany] == 20) {
                        dialog.findViewById<TextView>(R.id.atkrollnattxt).text = context.getString(R.string.naturaldice, secondresult[roll.howmany].toString())
                        dialog.findViewById<TextView>(R.id.atkrollnattxt).isVisible = true
                    }
                }
            }

            dialog.show()
        }
    }

    var howmany : Int = n
    var dice : Int = max
    var basebonus : Int = bonus
    var isvalid : Boolean = true

    fun roll() : List<Int> {
        var roll = 0
        val results = mutableListOf<Int>()
        for (i in 1..howmany) {
            val res = (1..dice).random()
            roll += res
            results.add(res)
        }
        results.add(roll + basebonus)
        return results
    }

    fun desc() : String {
        return "" + howmany + "D" + dice + (if (basebonus > 0) " + $basebonus" else (if (basebonus < 0) " - ${basebonus*(-1)}" else ""))
    }

    constructor() : this(0, 0, 0) {
        this.isvalid = false
    }
}