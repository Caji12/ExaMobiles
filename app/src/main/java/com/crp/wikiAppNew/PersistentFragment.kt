package com.crp.wikiAppNew

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class PersistentFragment : Fragment() {

    private lateinit var frequentPlacesInput: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_persistent, container, false)
        frequentPlacesInput = view.findViewById(R.id.et_frequent_places)
        saveButton = view.findViewById(R.id.btn_save)
        backButton = view.findViewById(R.id.btn_back)

        // Load the saved value from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val frequentPlaces = sharedPreferences.getInt("FrequentPlaces", 5) // Default value is 5
        frequentPlacesInput.setText(frequentPlaces.toString())

        saveButton.setOnClickListener {
            val newFrequentPlaces = frequentPlacesInput.text.toString().toIntOrNull()
            if (newFrequentPlaces != null) {
                saveFrequentPlaces(newFrequentPlaces)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun saveFrequentPlaces(frequentPlaces: Int) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("FrequentPlaces", frequentPlaces)
        editor.apply()
        Toast.makeText(requireContext(), "Frequent places saved", Toast.LENGTH_SHORT).show()
    }
}
