package com.crp.wikiAppNew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crp.wikiAppNew.data.VisitSummaryEntity

class VisitSummaryAdapter(private var places: List<VisitSummaryEntity>) : RecyclerView.Adapter<VisitSummaryAdapter.PlaceViewHolder>() {

    fun updatePlaces(newPlaces: List<VisitSummaryEntity>) {
        places = newPlaces
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.top_item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place)
    }

    override fun getItemCount(): Int = places.size

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val placeName: TextView = itemView.findViewById(R.id.tv_place_name)
        private val placeDescription: TextView = itemView.findViewById(R.id.tv_place_description)
        private val visitCount: TextView = itemView.findViewById(R.id.tv_visit_count)
        private val placeImage: ImageView = itemView.findViewById(R.id.iv_place_image)

        fun bind(place: VisitSummaryEntity) {
            placeName.text = place.placeName
            placeDescription.text = place.description
            visitCount.text = "Visitas: ${place.visitCount}"
            Glide.with(itemView.context).load(place.imageUrl).into(placeImage)
        }
    }
}
