package com.cyl.cyltur.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Mit(
    var monumentos: List<Monumento>? = null
) : Serializable

@Entity
data class Monumento(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @SerializedName("Descripcion")
    var descripcion: String? = null,
    var calle: String? = null,
    var clasificacion: String? = null,
    var codigoPostal: String? = null,
    @Embedded
    var coordenadas: Coordenadas? = null,
    val email: List<String>? = null,
    var estiloPredominante: List<String>? = null,
    var fax: String? = null,
    var horariosYTarifas: String? = null,
    var identificador: String? = null,
    var identificadorBienInteresCultural: List<String>? = null,
    var nombre: String? = null,
    var periodoHistorico: List<String>? = null,
    @Embedded
    var poblacion: Poblacion? = null,
    var telefono: List<String>? = null,
    var tipoConstruccion: List<String>? = null,
    var tipoMonumento: String? = null,
    val web: List<String>? = null,
    var favorite: Boolean = false
) : Serializable{
    val latLng:LatLng
    get() = LatLng(
        coordenadas?.latitud?.toDouble() ?: 0.0,
        coordenadas?.longitud?.replace("#", "0")?.toDouble() ?: 0.0)
}

data class Coordenadas(
    var latitud: String? = null,
    var longitud: String? = null
) : Serializable

data class Poblacion(
    var localidad: String? = null,
    var municipio: String? = null,
    var provincia: String? = null
) : Serializable