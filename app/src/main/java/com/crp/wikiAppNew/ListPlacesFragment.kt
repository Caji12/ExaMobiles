package com.crp.wikiAppNew

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crp.wikiAppNew.data.AppDatabase
import com.crp.wikiAppNew.data.PlaceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListPlacesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_places, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PlaceAdapter(emptyList()) { url -> openArticle(url) }
        recyclerView.adapter = adapter

        loadPlacesFromDatabase()

        val backButton: Button = view.findViewById(R.id.button)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadPlacesFromDatabase() {
        val db = AppDatabase.getDatabase(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            val places = db.placeDao().getAllPlaces()
            withContext(Dispatchers.Main) {
                if (places.isNotEmpty()) {
                    Log.d("ListPlacesFragment", "Places loaded: ${places.size}")
                    adapter.updatePlaces(places)
                } else {
                    Log.d("ListPlacesFragment", "No places found in database")
                }
            }
        }
    }

    private fun openArticle(url: String) {
        val articleFragment = WebViewFragment.newInstance(url)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, articleFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
