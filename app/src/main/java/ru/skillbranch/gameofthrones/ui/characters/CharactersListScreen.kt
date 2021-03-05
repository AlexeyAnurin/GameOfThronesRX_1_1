package ru.skillbranch.gameofthrones.ui.characters

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_characters.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.ui.character.CharacterScreen
import ru.skillbranch.gameofthrones.util.Utils


class CharactersListScreen : AppCompatActivity() {

    lateinit var viewModel: HousesViewModel
    var houses: List<HouseRes>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters)
        initToolbar()
        initViews()
        initViewModel()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun initViews() {
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                var searchView = toolbar.menu.findItem(R.id.action_search)?.actionView
                if(searchView != null) {
                    (searchView as SearchView).setQuery("", false)
                }

                val house = houses!![position]

                val primaryRes = Utils.getHouseColor(Utils.getHouseShortName(house.name), Utils.COLOR_PRIMARY)
                val accentRes = Utils.getHouseColor(Utils.getHouseShortName(house.name), Utils.COLOR_ACCENT)

                val rippleDr = appBarLayout.background as RippleDrawable
                val rippleColor = ColorStateList.valueOf(getColor(accentRes))
                rippleDr.setColor(rippleColor)

                val tab = tabLayout.getTabAt(position)!!
                val spotX = tab.view.left - tabLayout.scrollX + tab.view.width / 2
                val spotY = tabLayout.bottom - tab.view.height / 2
                rippleDr.setHotspot(spotX.toFloat(), spotY.toFloat())

                appBarLayout.background.setTint(getColor(primaryRes))
                appBarLayout.isPressed = true

                Handler().postDelayed({
                    appBarLayout.isPressed = false
                }, 200)
            }
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(HousesViewModel::class.java)

        viewModel.getAllHouses().observe(this, Observer {
            houses = Utils.getOrderedHouses(it)
            pager.adapter = HousesAdapter(this, pager, houses!!) {
                openCharacter(it)
            }

            TabLayoutMediator(tabLayout, pager) { tab, position ->
                tab.text = Utils.getHouseShortName(houses?.getOrNull(position)?.name ?: "")
            }.attach()
        })
    }

    private fun openCharacter(id: String) {
        val intent = Intent(this, CharacterScreen::class.java)
        intent.putExtra(CharacterScreen.EXTRA_ID, id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.characters_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView?.queryHint = getString(R.string.hintSearch)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                (pager.adapter as HousesAdapter).setCharactersQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (pager.adapter as HousesAdapter).setCharactersQuery(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }
}
