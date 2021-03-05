package ru.skillbranch.gameofthrones.ui.characters

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_item_houses.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.util.Utils


class HousesAdapter(val activity: FragmentActivity, val pager: ViewPager2, val houses: List<HouseRes>,
                    val characterSelectedListener: (String) -> Unit) : FragmentStateAdapter(activity) {

    private val fragments = mutableMapOf<Int,HouseFragment>()

    init {
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                fragments[pager.currentItem]?.updateCharacters("")
            }
        })
    }

    override fun getItemCount() = houses.size

    override fun createFragment(position: Int): Fragment {
        val fr = HouseFragment(characterSelectedListener)
        val bundle = Bundle()
        bundle.putString(HouseFragment.ARG_NAME, houses[position].name)
        fr.arguments = bundle
        fragments[position] = fr
        return fr
    }

    fun setCharactersQuery(query: String?) {
        fragments[pager.currentItem]?.updateCharacters(query)
    }

    class HouseFragment(val characterSelectedListener: (String) -> Unit) : Fragment() {

        companion object {
            val ARG_NAME = "name"
        }

        private var houseShortName = ""
        private var characters: List<CharacterItem> = listOf()

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return inflater.inflate(R.layout.fragment_item_houses, container, false)
        }

        fun updateCharacters(query: String?) {
            arguments?.getString(ARG_NAME)?.apply {
                val shortName = Utils.getHouseShortName(this)

                if(characters.isEmpty() || this@HouseFragment.houseShortName != shortName) {
                    this@HouseFragment.houseShortName = shortName

                    RootRepository.findCharactersByHouseName(shortName) {
                        characters = it
                        updateAdapter(query)
                    }
                    return
                }

                updateAdapter(query)
            }
        }

        private fun updateAdapter(query: String?) {
            val filtered =
                if(TextUtils.isEmpty(query)) characters
                else characters.filter { it.name.contains(query!!, true) }

            Handler().postDelayed({
                var a = recyclerView?.adapter
                if(a == null) {
                    recyclerView?.adapter = CharactersAdapter(filtered) {
                        characterSelectedListener.invoke(it)
                    }
                } else {
                    (a as CharactersAdapter).updateData(filtered)
                }
            }, 100)
        }
    }


}