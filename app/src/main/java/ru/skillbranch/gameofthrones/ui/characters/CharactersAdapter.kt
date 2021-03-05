package ru.skillbranch.gameofthrones.ui.characters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_characters.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.util.Utils

class CharactersAdapter(
    var items: List<CharacterItem>,
    val listener: (String) -> Unit
) : RecyclerView.Adapter<CharactersAdapter.CharacterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_characters, parent, false)
        return CharacterViewHolder(this, listener, v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateData(list: List<CharacterItem>) {
        val diffCallback = object: DiffUtil.Callback() {
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean = items[oldPos].id == list[newPos].id

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean = items[oldPos].hashCode() == list[newPos].hashCode()

            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = list.size
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = list
        diffResult.dispatchUpdatesTo(this)
    }

    class CharacterViewHolder(val adapter: CharactersAdapter, val listener: (String) -> Unit, convertView: View) : RecyclerView.ViewHolder(convertView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun bind(item: CharacterItem) {
            ivAvatar.setImageResource(Utils.getHouseIconRes(item.house)!!)
            tvTitle.text = if(!TextUtils.isEmpty(item.name)) item.name else "Information is unknown"
            tvDescripton.text = getDescription(item)
            underline.visibility = if(adapterPosition < adapter.itemCount - 1) View.VISIBLE else View.INVISIBLE

            itemView.setOnClickListener {
                listener.invoke(item.id)
            }
        }

        private fun getDescription(item: CharacterItem): String {
            var descrBuilder = StringBuilder()
            if(item.titles.isNotEmpty()) {
                descrBuilder.append(item.titles.joinToString(" "))
            }
            if(item.aliases.isNotEmpty()) {
                if(descrBuilder.isNotEmpty()) {
                    descrBuilder.append(" - ")
                }
                descrBuilder.append(item.aliases.joinToString("•"))
            }
            if(descrBuilder.isEmpty()) {
                descrBuilder.append("Information is unknown")
            }
            return descrBuilder.toString()
        }
    }

}