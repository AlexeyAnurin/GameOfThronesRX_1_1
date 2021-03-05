package ru.skillbranch.gameofthrones.ui.character

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_character.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.util.Utils

class CharacterScreen : AppCompatActivity() {

    companion object {
        val EXTRA_ID = "id"
    }

    lateinit var viewModel: CharacterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)
        initToolbar()
        initViewModel()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(CharacterViewModel::class.java)

        viewModel.getCharacter().observe(this, Observer { charFull ->
            val primaryColorRes = Utils.getHouseColor(charFull.house, Utils.COLOR_PRIMARY)
            val accentColorRes = Utils.getHouseColor(charFull.house, Utils.COLOR_ACCENT)

            collapsibleToolbarLayout.setContentScrimColor(getColor(primaryColorRes))
            toolbarImage.setImageResource(Utils.getHouseImageRes(charFull.house)!!)

            collapsibleToolbarLayout.title = charFull.name
            wordsValue.text = charFull.words
            bornValue.text = charFull.born
            titlesValue.text = charFull.titles.joinToString("\n")
            aliasesValue.text = charFull.aliases.joinToString("\n")

            llFather.visibility = if(charFull.father != null) View.VISIBLE else View.GONE
            if(charFull.father != null) {
                btnFather.text = charFull.father.name
                btnFather.backgroundTintList = ColorStateList.valueOf(getColor(primaryColorRes))
                btnFather.setOnClickListener {
                    openCharacter(charFull.father.id)
                }
            }

            llMother.visibility = if(charFull.mother != null) View.VISIBLE else View.GONE
            if(charFull.mother != null) {
                btnMother.text = charFull.mother.name
                btnMother.backgroundTintList = ColorStateList.valueOf(getColor(primaryColorRes))
                btnMother.setOnClickListener {
                    openCharacter(charFull.mother.id)
                }
            }

            wordsMarker.imageTintList = ColorStateList.valueOf(getColor(accentColorRes))
            bornMarker.imageTintList = ColorStateList.valueOf(getColor(accentColorRes))
            titlesMarker.imageTintList = ColorStateList.valueOf(getColor(accentColorRes))
            aliasesMarker.imageTintList = ColorStateList.valueOf(getColor(accentColorRes))

            if(!TextUtils.isEmpty(charFull.died)) {
                Snackbar
                    .make(content, "${charFull.name} - \"${charFull.died}\"", Snackbar.LENGTH_INDEFINITE)
                    .show()
            }
        })

        viewModel.onId(intent.getStringExtra(EXTRA_ID)!!)
    }

    private fun openCharacter(id: String) {
        val intent = Intent(this, CharacterScreen::class.java)
        intent.putExtra(EXTRA_ID, id)
        startActivity(intent)
    }
}
