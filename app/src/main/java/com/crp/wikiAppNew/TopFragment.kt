package com.crp.wikiAppNew

import android.content.Context
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
import com.crp.wikiAppNew.data.VisitSummaryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TopFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VisitSummaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_top, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = VisitSummaryAdapter(emptyList())
        recyclerView.adapter = adapter

        loadTopVisitedPlaces()

        val backButton: Button = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadTopVisitedPlaces() {
        val db = AppDatabase.getDatabase(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            val sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val limit = sharedPreferences.getInt("FrequentPlaces", 5) // Default limit is 5
            val topVisitedPlaces = db.placeDao().getTopVisitedPlaces(limit)
            withContext(Dispatchers.Main) {
                if (topVisitedPlaces.isNotEmpty()) {
                    Log.d("TopFragment", "Top visited places loaded: ${topVisitedPlaces.size}")
                    adapter.updatePlaces(topVisitedPlaces)
                } else {
                    Log.d("TopFragment", "No top visited places found in database")
                }
            }
        }
    }
}
