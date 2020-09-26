package com.cyl.cyltur.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyl.cyltur.R
import com.cyl.cyltur.shared_preferences.PrefManager
import kotlinx.android.synthetic.main.activity_configuration.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class ActivityConfiguration : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        loadPreferences()

        addFunctionalityToButtons()

    }

    private fun loadPreferences() {
        when (PrefManager(this).mapType) {
            1 -> radioButtonNormalMap.isChecked = true
            2 -> radioButtonSatelliteMap.isChecked = true
            3 -> radioButtonTerrainMap.isChecked = true
            4 -> radioButtonHybridMap.isChecked = true
        }

    }

    private fun addFunctionalityToButtons() {

        btnBackActConfig.setOnClickListener() {
            onBackPressed()
        }

        btnHomeActConfig.setOnClickListener {
            startActivity<ActivityHome>()
            finish()
        }

        btnSave.setOnClickListener {

            when (radioGroupMapType.checkedRadioButtonId) {
                radioButtonNormalMap.id -> PrefManager(this).mapType = 1
                radioButtonSatelliteMap.id -> PrefManager(this).mapType = 2
                radioButtonTerrainMap.id -> PrefManager(this).mapType = 3
                radioButtonHybridMap.id -> PrefManager(this).mapType = 4
            }

            println(radioGroupMapType.checkedRadioButtonId.toString())

            toast("Ajustes guardados")

        }

    }

}
