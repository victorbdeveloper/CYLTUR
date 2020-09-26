package com.cyl.cyltur.activities

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyl.cyltur.R
import com.cyl.cyltur.api_connection.ApiCall
import com.cyl.cyltur.data_base.DbUtils
import com.cyl.cyltur.shared_preferences.PrefManager
import com.cyl.cyltur.util.dismissDialog
import com.cyl.cyltur.util.newProgressDialog
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class ActivityHome : AppCompatActivity() {

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (needSync()) {
            progressDialog = newProgressDialog()
            ApiCall.getMonuments({ mit -> //se llama a la API y se guarda la respuesta en la variable monuments
                val monumentsDao = DbUtils.getDataBase(this).getMonumentDao()
                monumentsDao.insertDB(mit?.monumentos ?: emptyList())
                println(mit?.monumentos.toString())
                PrefManager(this).lastUpdate = System.currentTimeMillis()
                progressDialog?.dismissDialog()
                toast("Datos actualizados")
            }, { code ->
                progressDialog?.dismissDialog()
                toast("ERROR $code")
            })
        }

        addFunctionalityToButtons()
    }

    private fun needSync(): Boolean {
        
        val now = System.currentTimeMillis()
        var lastUpdate = 0L
        val prefManager = PrefManager(this)

        if (prefManager.lastUpdate != 0L) {
            lastUpdate = prefManager.lastUpdate
        }

        val millisBetween = (now - lastUpdate).toDouble()
        val daysBetween = millisBetween / 1000 / 60 / 60 / 24

        return daysBetween >= 7
    }

    private fun addFunctionalityToButtons() {

        btnSeeMap.setOnClickListener {
            startActivity<ActivityChooseProvince>(
                "selectedOption" to "mapa"
            )
        }

        btnMonumentList.setOnClickListener {
            startActivity<ActivityChooseProvince>(
                "selectedOption" to "monumentos"
            )
        }

        btnFavorites.setOnClickListener {
            startActivity<ActivityMonumentsList>(
                "title" to btnFavorites.text.toString(),
                "isFavorite" to true
            )
        }

        btnConfiguration.setOnClickListener {
            startActivity<ActivityConfiguration>()
        }

    }

}
