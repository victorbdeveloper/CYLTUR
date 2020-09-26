package com.cyl.cyltur.data_base

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyl.cyltur.model.Monumento

@Dao
interface MonumentDao {

    //INSERTA TODOS LOS MONUMENTOS EN LA BD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDB(elements: List<Monumento>)

    //OBTIENE TODOS LOS MONUMENTOS
    @Query("SELECT * FROM Monumento")
    fun getAll(): List<Monumento>

    //OBTIENE TODOS LOS MONUMENTOS MARCADOS COMO FAVORITOS
    @Query("SELECT * FROM Monumento WHERE favorite = 1 AND (:filter IS NULL OR nombre LIKE :filter)")
    fun getFavorites(filter: String?): List<Monumento>

    //OBTIENE UNA LISTA CON TODOS LOS MONUMENTOS QUE PERTENECEN A LA LOCALIDAD QUE LE HAS PASADO, SEA 1 O MAS
    @Query("SELECT * FROM Monumento WHERE provincia IN (:provinces) AND (:filter IS NULL OR nombre LIKE :filter)")
    fun getProvincesMonuments(filter: String?, provinces: List<String>): List<Monumento>

    //OBTIENE UNA LISTA CON TODOS LOS MONUMENTOS QUE TIENEN EL TIPO QUE SE LE HA PASADO COMO PARAMETRO Y FILTRADO POR FAVORITOS, SEA 1 O MAS
    @Query("SELECT * FROM Monumento WHERE tipoMonumento IN (:monumentsType) AND favorite = 1 AND (:filter IS NULL OR nombre LIKE :filter)")
    fun getMonumentsByFilterAndFavorites(
        filter: String?,
        monumentsType: List<String>
    ): List<Monumento>

    //OBTIENE UNA LISTA CON TODOS LOS MONUMENTOS QUE TIENEN EL TIPO QUE SE LE HA PASADO COMO PARAMETRO Y FILTRADO POR PROVINCIA, SEA 1 O MAS
    @Query("SELECT * FROM Monumento WHERE tipoMonumento IN (:monumentsType) AND provincia IN (:provinces) AND (:filter IS NULL OR nombre LIKE :filter)")
    fun getMonumentsByFilterAndProvince(
        filter: String?,
        monumentsType: List<String>,
        provinces: List<String>
    ): List<Monumento>

    //OBTIENE UN MONUMENTO MEDIANTE UN ID
    @Query("SELECT * FROM Monumento WHERE identificador=:idMonument")
    fun getMonumentDetail(idMonument: String): Monumento?

    //CAMBIA EL ESTADO DE FAVORITO DE UN MONUMENTO --> hay que pasarle 1 o 0 para favorito true o false
    @Query("UPDATE Monumento SET favorite = :favorite WHERE id = :id")
    fun updateFavoritesState(id: Int, favorite: Int): Unit

    //BORRA TODOS LOS MONUMENTOS DE LA BD
    @Query("DELETE FROM Monumento")
    fun deleteAll(): Unit

}