package com.crp.wikiAppNew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.crp.wikiAppNew.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Manejar el clic en el botón "Lista" para navegar a ListPlacesFragment
        binding.Lista.setOnClickListener {
            navigateToListPlacesFragment()
        }

        // Manejar el clic en el botón "Persistente"
        binding.Persistente.setOnClickListener {
            navigateToPersistentFragment()
        }

        // Manejar el clic en el botón "Top"
        binding.Top.setOnClickListener {
            navigateToTopFragment()
        }
    }

    private fun navigateToListPlacesFragment() {
        val listPlacesFragment = ListPlacesFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, listPlacesFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToPersistentFragment() {
        val persistentFragment = PersistentFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, persistentFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToTopFragment() {
        val topFragment = TopFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, topFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
