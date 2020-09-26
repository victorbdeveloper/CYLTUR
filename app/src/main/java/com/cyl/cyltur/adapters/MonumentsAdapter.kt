package com.cyl.cyltur.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyl.cyltur.R
import com.cyl.cyltur.model.Monumento
import kotlinx.android.synthetic.main.element_monument.view.*
import kotlinx.android.synthetic.main.element_nodata.view.*


class MonumentsAdapter(private val listener: (eventType: Int, position: Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_MONUMENT = 1
        const val TYPE_NODATA = 0
        const val EVENT_DETAIL = 1
    }

    var loading = false
    var data: List<Monumento> = emptyList()
    var monumentType: List<String> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_MONUMENT -> MonumentViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.element_monument,
                        parent,
                        false
                    ), listener
            )
            else -> NoDataViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.element_nodata,
                        parent,
                        false
                    ), R.string.tv_no_data
            )

        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MonumentViewHolder -> {
                holder.setData(data[position])

            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if (!loading) {
            if (data.isNotEmpty()) {
                TYPE_MONUMENT
            } else {
                TYPE_NODATA
            }
        } else {
            TYPE_NODATA
        }
    }

    override fun getItemCount(): Int {
        return if (!loading && data.isEmpty()) {
            1
        } else {
            data.size
        }
    }

    class MonumentViewHolder(
        parent: View, val listener: (eventType: Int, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(parent) {

        fun setData(element: Monumento) {

            itemView.apply {
                tvElementMonumentName.text = element.nombre
                tvElementMonumentType.text = element.tipoMonumento ?: ""
                tvElementMonumentProvince.text = element.poblacion?.provincia ?: ""
                setOnClickListener { listener(EVENT_DETAIL, adapterPosition) }
            }
        }
    }

    class NoDataViewHolder(parent: View, text: Int) : RecyclerView.ViewHolder(parent) {
        init {
            itemView.tvNoDataText.setText(text)
        }
    }
}