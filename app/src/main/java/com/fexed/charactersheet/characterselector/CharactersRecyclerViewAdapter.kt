package com.fexed.charactersheet.characterselector

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.fexed.charactersheet.DataLoader
import com.fexed.charactersheet.MainActivity
import com.fexed.charactersheet.R
import com.fexed.charactersheet.character.Character
import com.fexed.charactersheet.character.dnd5e.DnD5eCharacter
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class CharactersRecyclerViewAdapter(private val items : MutableList<Character>, private val uris : MutableList<Uri>, private val deletemode: Boolean, private val context: Activity) : RecyclerView.Adapter<CharactersRecyclerViewAdapter.CharacterViewHolder>() {
    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view : View = itemView
        private var item : Character? = null

        fun bindItem(item : Character, deletemode : Boolean) {
            this.item = item
            view.findViewById<TextView>(R.id.charactername).text = item.pgname
            view.findViewById<TextView>(R.id.characterrace).text = item.pgrace
            view.findViewById<TextView>(R.id.characterclass).text = item.pgclass
            view.findViewById<TextView>(R.id.characterlv).text = "LV " + item.pglv
            view.findViewById<TextView>(R.id.charactertype).text = if (item is DnD5eCharacter) "DnD 5e" else "Base Character"
            if (item.portrait == null) {
                view.findViewById<ImageView>(R.id.charactllistimg)!!.setImageResource(R.mipmap.nopic)
            } else {
                view.findViewById<ImageView>(R.id.charactllistimg)!!.setImageDrawable(null)
                view.findViewById<ImageView>(R.id.charactllistimg)!!.setImageURI(Uri.parse(item.portrait))
            }
            if (deletemode) {
                view.findViewById<ImageButton>(R.id.deletecurrcharacter)!!.isVisible = true
                view.findViewById<ImageButton>(R.id.sharecharacter)!!.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val inflated = LayoutInflater.from(parent.context).inflate(R.layout.characterlistelement, parent, false)
        return CharacterViewHolder(inflated)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bindItem(items[position], deletemode)
        holder.view.findViewById<ConstraintLayout>(R.id.container).setOnClickListener {
            DataLoader.loadCharacter(items[position])
            val firebaseAnalytics = Firebase.analytics
            val bundle = Bundle()
            bundle.putString("name", items[position].pgname)
            bundle.putString("race", items[position].pgrace)
            bundle.putString("class", items[position].pgclass)
            bundle.putString("level", items[position].pglv.toString())
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
            context.startActivity(intent)
        }
        holder.view.findViewById<ImageButton>(R.id.sharecharacter).setOnClickListener {
            val path = uris[position]
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.exportingchar, items[position].pgname))
            intent.putExtra(Intent.EXTRA_STREAM, path)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "text/json"
            context.startActivity(intent)
        }
        if (deletemode) {
            holder.view.findViewById<ImageButton>(R.id.deletecurrcharacter)!!.setOnClickListener {
                holder.view.findViewById<Button>(R.id.deletecharacterconfirm)!!.isVisible = true
                holder.view.findViewById<ImageButton>(R.id.deletecurrcharacter)!!.isVisible = false
                holder.view.findViewById<Button>(R.id.deletecharacterconfirm)!!.setOnClickListener {
                    DataLoader.deleteCharacter(context, items[position].pgname + ".char")
                    items.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}