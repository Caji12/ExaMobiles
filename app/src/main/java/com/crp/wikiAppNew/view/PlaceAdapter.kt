package com.crp.wikiAppNew

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crp.wikiAppNew.data.PlaceEntity
import java.net.URLEncoder

class PlaceAdapter(private var places: List<PlaceEntity>, private val adapterOnClick: (String) -> Unit) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeImageView: ImageView = itemView.findViewById(R.id.iv_article_image)
        val placeNameTextView: TextView = itemView.findViewById(R.id.tv_place_name)
        val placeDescriptionTextView: TextView = itemView.findViewById(R.id.tv_place_description)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.tv_date_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.placeNameTextView.text = place.name
        holder.placeDescriptionTextView.text = place.Extract
        holder.dateTimeTextView.text = place.dateTime

        Glide.with(holder.itemView.context)
            .load(place.Url)
            .into(holder.placeImageView)

        holder.itemView.setOnClickListener {
            val encodedTitle = URLEncoder.encode(place.placeName, "UTF-8")
            val url = "https://en.wikipedia.org/wiki/$encodedTitle"
            adapterOnClick(url)
        }
    }

    override fun getItemCount(): Int {
        return places.size
    }

    fun updatePlaces(newPlaces: List<PlaceEntity>) {
        places = newPlaces
        notifyDataSetChanged()
    }
}

