package com.cyl.cyltur.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyl.cyltur.R
import com.cyl.cyltur.adapters.MonumentsAdapter
import com.cyl.cyltur.data_base.DbUtils
import com.cyl.cyltur.model.Monumento
import kotlinx.android.synthetic.main.activity_monuments_list.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk27.coroutines.onEditorAction
import org.jetbrains.anko.startActivity


class ActivityMonumentsList : AppCompatActivity() {

    private lateinit var adapter: MonumentsAdapter
    private var monumentTypeFilter: List<String> = emptyList()
    private var isFavorite = false
    private var filter = ""
    private var provinces: List<String> = emptyList()

    private var textHandler = Handler()

    private var filterTask = Runnable {
        resetData()
    }

    companion object {
        const val INTENT_DETAIL = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monuments_list)
        isFavorite = intent.getBooleanExtra("isFavorite", false)
        provinces = intent.getStringArrayListExtra("provinces")?.toList() ?: emptyList()
        setToolbarTitle()

        etSearch.addTextChangedListener {
            filter = it?.toString() ?: ""
            textHandler.removeCallbacksAndMessages(null)
            if (filter.isEmpty()) {
                textHandler.post(filterTask)
            } else {
                textHandler.postDelayed(filterTask, 0)
            }
        }

        ivClearText.setOnClickListener { etSearch.setText("") }
        etSearch.onEditorAction { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val inputMethodManager =
                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
                inputMethodManager?.hideSoftInputFromWindow(etSearch.windowToken, 0)
                textHandler.removeCallbacksAndMessages(null)
                textHandler.post(filterTask)
            }
        }

        addFunctionalityToButtons()

        //RECYCLERVIEW
        adapter = MonumentsAdapter(this::onAdapterEvent)
        rvActMonumentsList.layoutManager = LinearLayoutManager(this)
        rvActMonumentsList.adapter = adapter
        resetData()
    }

    private fun onAdapterEvent(eventType: Int, position: Int) {
        when (eventType) {
            MonumentsAdapter.EVENT_DETAIL -> {
                val monument = adapter.data[position]
                startActivityForResult(
                    intentFor<ActivityMonumentDetail>(
                        "idMonument" to (monument.identificador ?: "error"),
                        "favoriteAct" to true
                    ), INTENT_DETAIL
                )
            }
        }

    }

    private fun setToolbarTitle() {
        val intentParam: Intent = intent
        tvToolbarTitleActMonumentList.text = intentParam.getStringExtra("title")
    }

    private fun addFunctionalityToButtons() {
        btnBackActMonumentList.setOnClickListener {
            onBackPressed()
        }

        btnHomeActMonumentsList.setOnClickListener {
            startActivity<ActivityHome>()
            finish()
        }

        btnFloatingActionUp.setOnClickListener {
            rvActMonumentsList.smoothScrollToPosition(0)
        }

        btnFilterActMonumentsList.setOnClickListener {
            filter()
        }

    }

    private fun resetData() {
        val finalFilter = if (filter.isEmpty()) null else "%${filter}%"
        val monumentDao = DbUtils.getDataBase(this).getMonumentDao()
        val monuments = when {
            isFavorite && monumentTypeFilter.isEmpty() -> monumentDao.getFavorites(finalFilter)
            isFavorite -> monumentDao.getMonumentsByFilterAndFavorites(
                finalFilter,
                monumentTypeFilter
            )
            !provinces.isNullOrEmpty() && monumentTypeFilter.isEmpty() -> monumentDao.getProvincesMonuments(
                finalFilter,
                provinces
            )
            !provinces.isNullOrEmpty() -> monumentDao.getMonumentsByFilterAndProvince(
                finalFilter,
                monumentTypeFilter,
                provinces
            )
            else -> monumentDao.getAll()
        }
        updateUI(monuments)
    }

    private fun filter() {

        lateinit var dialog: AlertDialog

        val monumentDao = DbUtils.getDataBase(this).getMonumentDao()
        val monuments = when {
            isFavorite -> monumentDao.getFavorites(null)
            !provinces.isNullOrEmpty() -> monumentDao.getProvincesMonuments(null, provinces)
            else -> monumentDao.getAll()
        }

        val monumentTypes = monuments.map { it.tipoMonumento }.distinct()

        val monumentChecked = mutableListOf<String>()
        val builder = AlertDialog.Builder(this)
        val currentFilters = monumentTypes.map {
            it in monumentTypeFilter
        }.toBooleanArray()

        builder.setTitle("Filtro de tipo de monumento")
        builder.setMultiChoiceItems(monumentTypes.toTypedArray(), currentFilters) { _, which, isChecked ->
            val element = monumentTypes[which]
            element?.let { type ->
                if (isChecked) {
                    monumentChecked.add(type)
                } else {
                    monumentChecked.remove(type)
                }
            }
        }
        builder.setPositiveButton("OK") { _, _ ->
            monumentTypeFilter = monumentChecked
            resetData()
        }

        builder.setNegativeButton("Cancelar")
        { _, _ ->

        }

        builder.setNeutralButton("Quitar filtros") { _, _ ->
            updateUI(monuments)
        }

        dialog = builder.create()
        dialog.show()
    }

    private fun updateUI(monuments: List<Monumento>) {
        adapter.data = monuments
        adapter.monumentType = adapter.data.mapNotNull { it.tipoMonumento }
        adapter.notifyDataSetChanged()
        val resultsCount = adapter.data.size
        val resultText = "$resultsCount"
        tvResultsCountActMonumentsList.text = resultText
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_DETAIL) {
            resetData()
        }
    }

}
