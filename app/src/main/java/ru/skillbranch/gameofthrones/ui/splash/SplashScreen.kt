package ru.skillbranch.gameofthrones.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_splash.*
import ru.skillbranch.gameofthrones.R

class SplashScreen : AppCompatActivity() {

    lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        viewModel.loading.observe(this, Observer {
            progress.visibility = if(it) View.VISIBLE else View.GONE
        })

        viewModel.errorEvent.observe(this, Observer {
            Snackbar.make(content, it, Snackbar.LENGTH_INDEFINITE)
        })

        viewModel.onCreate(this)
    }


}
