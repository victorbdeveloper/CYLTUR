package com.cyl.cyltur.activities

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cyl.cyltur.R
import com.cyl.cyltur.data_base.DbUtils
import com.cyl.cyltur.model.Monumento
import com.cyl.cyltur.shared_preferences.PrefManager
import com.cyl.cyltur.util.convertDpToPixel
import com.cyl.cyltur.util.newProgressDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.startActivity


class ActivityMap : AppCompatActivity(), GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private var monumentTypeFilter: List<String> = emptyList()
    private var progressDialog:Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        if(intent.getStringExtra("idMonument")!=null){
            btnFilterActMap.visibility= View.GONE
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        progressDialog=newProgressDialog()
        mapFragment.getMapAsync {
            mMap=it
            val prefManager = PrefManager(this)
            it.mapType = prefManager.mapType
            it.setOnMarkerClickListener(this)
            progressDialog?.dismiss()
            resetData()
            addFunctionalityToButtons()
        }
    }

    private fun resetData() {
        val monumentDao = DbUtils.getDataBase(this).getMonumentDao()
        val idMonument = intent.getStringExtra("idMonument")
        val provinces = intent.getStringArrayListExtra("provinces")?.toList() ?: emptyList()
        val monuments = when {
            !provinces.isNullOrEmpty() && monumentTypeFilter.isEmpty() -> monumentDao.getProvincesMonuments(
                null,
                provinces
            )
            !provinces.isNullOrEmpty() -> monumentDao.getMonumentsByFilterAndProvince(
                null,
                monumentTypeFilter,
                provinces
            )
            idMonument!=null->{
                val monument=monumentDao.getMonumentDetail(idMonument)
                if(monument!=null) listOf(monument) else emptyList()
            }
            else -> monumentDao.getAll()
        }
        updateMap(monuments)
    }

    fun updateMap(monuments:List<Monumento>) {
        mMap.clear()
        val builder = LatLngBounds.builder()
        monuments.forEach { monumento->
            val markerOptions=getMarker(monumento)
            builder?.include(markerOptions.position)
            mMap.addMarker(markerOptions).tag =
                monumento.identificador //se añade un tag al marker que es el id del monumento

        }
        val bounds=builder.build()
        when(monuments.size){
            0->mMap.moveCamera(
                CameraUpdateFactory
                    .newLatLngZoom(LatLng(41.652269, -4.728630), 7f))
            1->{
                val monument=monuments[0]
                val latLng = monument.latLng
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
            }
            else-> mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, convertDpToPixel(20f)))
        }
    }

    private fun getMarker(monument: Monumento):MarkerOptions {

        val latLng = monument.latLng

        val markerOptions =
            MarkerOptions().position(latLng).title(monument.nombre).snippet(monument.tipoMonumento)

        when (monument.tipoMonumento) {
            "Yacimientos arqueológicos" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ancient))
            "Monasterios", "Santuarios" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_chapel))
            "Castillos", "Reales Sitios", "Casas Nobles" -> markerOptions.icon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_castle)
            )
            "Catedrales" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cathedral))
            "Casas Consistoriales" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_townhall))
            "Plazas Mayores" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_square))
            "Iglesias y Ermitas" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_church))
            "Palacios" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_palace))
            "Sinagogas" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_synagogue))
            "Molinos" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_windmill))
            "Cruceros" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cross))
            "Fuentes" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_waterdrop))
            "Hórreos" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hut))
            "Murallas y puertas" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_citywalls))
            "Torres" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tower))
            "Puentes" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bridge))
            "Esculturas" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_statue))
            "Otros edificios" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_others))
            "Conjunto Etnológico", "Sitio Histórico" -> markerOptions.icon(
                BitmapDescriptorFactory.fromResource(
                    R.drawable.ic_oldmonument
                )
            )
            "Paraje pintoresco" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_forest))
            "Jardín Histórico" -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flowers))
        }
        return markerOptions
    }

    private fun filter() {

        val dialog: AlertDialog

        val monumentDao = DbUtils.getDataBase(this).getMonumentDao()
        val provinces = intent.getStringArrayListExtra("provinces")?.toList() ?: emptyList()
        val monuments = monumentDao.getProvincesMonuments(null, provinces)
        val monumentTypes = monuments.map { it.tipoMonumento }.distinct()
        val monumentChecked = mutableListOf<String>()
        val builderFilter = AlertDialog.Builder(this)

        val currentFilters = monumentTypes.map {
            it in monumentTypeFilter
        }.toBooleanArray()

        builderFilter.setTitle("Filtro de tipo de monumento")
        builderFilter.setMultiChoiceItems(
            monumentTypes.toTypedArray(),
            currentFilters
        ) { _, which, isChecked ->
            val element = monumentTypes[which]
            element?.let { type ->
                if (isChecked) {
                    monumentChecked.add(type)
                } else {
                    monumentChecked.remove(type)
                }
            }
        }

        builderFilter.setPositiveButton("OK") { _, _ ->
            monumentTypeFilter = monumentChecked
            resetData()
        }

        builderFilter.setNegativeButton("Cancelar")
        { _, _ ->

        }

        builderFilter.setNeutralButton("Quitar filtros") { _, _ ->
            monumentTypeFilter = emptyList()
            resetData()
        }

        dialog = builderFilter.create()
        dialog.show()

    }

    override fun onMarkerClick(marker: Marker): Boolean {

        btnInfoActMap.setOnClickListener {

            if (marker.isInfoWindowShown) {
                startActivity<ActivityMonumentDetail>(
                    "idMonument" to (marker.tag ?: "error")
                )

            } else {
                showAlert()
            }

        }
        return false
    }

    private fun addFunctionalityToButtons() {

        btnBackActMap.setOnClickListener {
            onBackPressed()
        }

        btnInfoActMap.setOnClickListener {
            showAlert()
        }

        btnHomeActMap.setOnClickListener {
            startActivity<ActivityHome>()
            finish()
        }

        btnFilterActMap.setOnClickListener {
            filter()
        }

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Debes seleccionar un monumento para poder ver su información")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


}
