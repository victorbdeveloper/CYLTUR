package com.cyl.cyltur.activities

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.cyl.cyltur.R
import com.cyl.cyltur.data_base.DbUtils
import com.cyl.cyltur.model.Monumento
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_monument_detail.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class ActivityMonumentDetail : AppCompatActivity() {

    private var mMap: GoogleMap? = null
    private var idMonument = ""
    private var favoriteAct = false

    private var monument: Monumento? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monument_detail)

        favoriteAct = intent.getBooleanExtra("favoriteAct", false)
        idMonument = intent.getStringExtra("idMonument") ?: "error"
        monument = DbUtils.getDataBase(this).getMonumentDao().getMonumentDetail(idMonument)

        updateUI()
    }

    private fun updateUI() {
        monument?.let {
            tvDetailName.text = it.nombre ?: "No hay datos que mostrar"
            tvDetailProvince.text = it.poblacion?.provincia ?: "No hay datos que mostrar"
            tvDetailLocality.text = it.poblacion?.localidad ?: "No hay datos que mostrar"
            tvDetailMunicipality.text = it.poblacion?.municipio ?: "No hay datos que mostrar"
            tvDetailStreet.text = it.calle ?: "No hay datos que mostrar"
            tvDetailPostalCode.text = it.codigoPostal ?: "No hay datos que mostrar"
            tvDetailPhones.text = listFormatter(it.telefono)
            tvDetailEmail.text = listFormatter(it.email)
            tvDetailWeb.text = listFormatter(it.web)

            val schedulesAndPricesText = HtmlCompat.fromHtml(
                it.horariosYTarifas ?: "No hay datos que mostrar",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            tvDetailSchedulesAndPrices.text = schedulesAndPricesText

            tvDetailClassification.text = it.clasificacion ?: "No hay datos que mostrar"
            tvDetailMonumentType.text = it.tipoMonumento ?: "No hay datos que mostrar"
            tvDetailBuildingType.text = listFormatter(it.tipoConstruccion)
            tvDetailMainStyle.text = listFormatter(it.estiloPredominante)
            tvDetailHistoricalPeriod.text = listFormatter(it.periodoHistorico)

            val descriptionText = HtmlCompat.fromHtml(
                it.descripcion ?: "No hay datos que mostrar",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            tvDetailDescription.text = descriptionText

            if (it.favorite) {
                btnFavoriteActMonumentDetail.setImageResource(R.drawable.ic_favorite_enable)
            } else {
                btnFavoriteActMonumentDetail.setImageResource(R.drawable.ic_favorite_disable)
            }
            mapSetUp(it)
        }
        addFunctionalityToButtons()
    }

    private fun listFormatter(list: List<String>?): String {
        var listString = ""
        if (!list.isNullOrEmpty()) {
            for (item in list) {

                listString += if (item != list.lastOrNull()) {
                    "$item\n"
                } else {
                    item
                }
            }
        } else {
            listString = "No hay datos que mostrar"
        }
        return listString
    }

    private fun addFunctionalityToButtons() {

        btnBackActMonumentDetail.setOnClickListener {
            onBackPressed()
        }

        btnFavoriteActMonumentDetail.setOnClickListener {
            monument?.favorite = !(monument?.favorite ?: false)
            if (monument?.favorite == true) {
                btnFavoriteActMonumentDetail.setImageResource(R.drawable.ic_favorite_enable)
                toast("Añadido a Favoritos")
                DbUtils.getDataBase(this).getMonumentDao()
                    .updateFavoritesState(monument?.id ?: -1, 1)

            } else if (monument?.favorite == false) {
                btnFavoriteActMonumentDetail.setImageResource(R.drawable.ic_favorite_disable)
                toast("Elminiado de Favoritos")
                DbUtils.getDataBase(this).getMonumentDao()
                    .updateFavoritesState(monument?.id ?: -1, 0)
            }
        }

        btnVehicleActMonumentDetail.setOnClickListener {

            val latLngString =
                monument?.coordenadas?.latitud + "," + monument?.coordenadas?.longitud?.replace(
                    "#",
                    "0"
                )

            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Desea cargar la ruta en Google Maps?")
            builder.setPositiveButton("Si", DialogInterface.OnClickListener { _, _ ->
                val gmmIntentUri: Uri =
                    Uri.parse("google.navigation:q=$latLngString")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            })
            builder.setNegativeButton("No", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }

        btnMapActMonumentDetail.setOnClickListener {
            startActivity<ActivityMap>(
                "call" to "ActivityMonumentDetail",
                "idMonument" to idMonument
            )

        }

        btnHomeActMonumentDetail.setOnClickListener {
            startActivity<ActivityHome>()
            finish()
        }

    }

    private fun mapSetUp(monument: Monumento) {

        if (mMap == null) {
            val mapFragment = SupportMapFragment.newInstance()
            (supportFragmentManager.findFragmentById(R.id.frameLayoutMap) as? SupportMapFragment)?.let {
                supportFragmentManager.beginTransaction().remove(it).commit()
            }
            supportFragmentManager.beginTransaction().add(R.id.frameLayoutMap, mapFragment).commit()
            mapFragment.getMapAsync {
                mMap = it
                addMarker(monument)
            }
        } else {
            addMarker(monument)
        }
    }

    private fun addMarker(monument: Monumento) {
        mMap?.clear()

        val latLng = monument.latLng

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)

        mMap?.moveCamera(cameraUpdate)

        val markerOptions = MarkerOptions().position(latLng).title(monument.nombre)
            .snippet(monument.tipoMonumento)

        mMap?.addMarker(markerOptions)
    }

}
