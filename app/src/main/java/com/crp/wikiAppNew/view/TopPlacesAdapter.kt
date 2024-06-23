package com.crp.wikiAppNew.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crp.wikiAppNew.R
import com.crp.wikiAppNew.data.VisitSummaryEntity

class TopPlacesAdapter(private var topPlaces: List<VisitSummaryEntity>) :
    RecyclerView.Adapter<TopPlacesAdapter.TopPlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.top_item_place, parent, false)
        return TopPlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopPlaceViewHolder, position: Int) {
        val place = topPlaces[position]
        holder.bind(place)
    }

    override fun getItemCount(): Int = topPlaces.size

    fun updateTopPlaces(newTopPlaces: List<VisitSummaryEntity>) {
        topPlaces = newTopPlaces
        notifyDataSetChanged()
    }

    class TopPlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val placeImageView: ImageView = itemView.findViewById(R.id.iv_place_image)
        private val placeNameTextView: TextView = itemView.findViewById(R.id.tv_place_name)
        private val placeDescriptionTextView: TextView = itemView.findViewById(R.id.tv_place_description)
        private val visitCountTextView: TextView = itemView.findViewById(R.id.tv_visit_count)

        fun bind(place: VisitSummaryEntity) {
            placeNameTextView.text = place.placeName
            placeDescriptionTextView.text = place.description
            visitCountTextView.text = "Visitas: ${place.visitCount}"

            Glide.with(itemView.context)
                .load(place.imageUrl)
                .into(placeImageView)
        }
    }
}
