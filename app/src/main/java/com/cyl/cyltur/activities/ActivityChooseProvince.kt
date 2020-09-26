package com.cyl.cyltur.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cyl.cyltur.R
import kotlinx.android.synthetic.main.activity_choose_province.*
import org.jetbrains.anko.startActivity


class ActivityChooseProvince : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_province)

        addFunctionalityToButtons()

    }

    private fun addFunctionalityToButtons() {

        val selectedOption = intent.getStringExtra("selectedOption")

        btnBackActChooseProvince.setOnClickListener {
            onBackPressed()
        }

        btnAccept.setOnClickListener() {
            val provinces: ArrayList<String> = ArrayList()
            if (checkBoxAvila.isChecked) provinces.add("Ávila")
            if (checkBoxBurgos.isChecked) provinces.add("Burgos")
            if (checkBoxLeon.isChecked) provinces.add("León")
            if (checkBoxPalencia.isChecked) provinces.add("Palencia")
            if (checkBoxSalamanca.isChecked) provinces.add("Salamanca")
            if (checkBoxSegovia.isChecked) provinces.add("Segovia")
            if (checkBoxSoria.isChecked) provinces.add("Soria")
            if (checkBoxValladolid.isChecked) provinces.add("Valladolid")
            if (checkBoxZamora.isChecked) provinces.add("Zamora")

            if (provinces.isEmpty()) {
                showAlert()
            } else {

                if (selectedOption == "mapa") {
                    startActivity<ActivityMap>(
                        "call" to "ActivityChooseProvince",
                        "provinces" to provinces
                    )

                } else {
                    startActivity<ActivityMonumentsList>(
                        "provinces" to provinces,
                        "title" to "MONUMENTOS"

                    )
                }

            }

        }

        btnNone.setOnClickListener() {
            checkBoxAvila.isChecked = false
            checkBoxBurgos.isChecked = false
            checkBoxLeon.isChecked = false
            checkBoxPalencia.isChecked = false
            checkBoxSalamanca.isChecked = false
            checkBoxSegovia.isChecked = false
            checkBoxSoria.isChecked = false
            checkBoxValladolid.isChecked = false
            checkBoxZamora.isChecked = false
        }

        btnAll.setOnClickListener() {
            checkBoxAvila.isChecked = true
            checkBoxBurgos.isChecked = true
            checkBoxLeon.isChecked = true
            checkBoxPalencia.isChecked = true
            checkBoxSalamanca.isChecked = true
            checkBoxSegovia.isChecked = true
            checkBoxSoria.isChecked = true
            checkBoxValladolid.isChecked = true
            checkBoxZamora.isChecked = true

        }

        btnHomeActChooseProvince.setOnClickListener {
            startActivity<ActivityHome>()
            finish()
        }

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Debes seleccionar al menos una provincia")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}
