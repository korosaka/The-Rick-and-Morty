package com.example.therickandmorty.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.therickandmorty.R
import com.example.therickandmorty.databinding.FragmentCharacterListBinding
import com.example.therickandmorty.view.recycler_view.CharacterRecyclerViewAdapter
import com.example.therickandmorty.view_model.CharacterListViewModel
import kotlinx.android.synthetic.main.fragment_character_list.*

class CharacterListFragment : Fragment() {

    private val viewModel: CharacterListViewModel by lazy {
        ViewModelProviders.of(this).get(CharacterListViewModel::class.java)
    }
    private lateinit var binding: FragmentCharacterListBinding
    private lateinit var recyclerAdapter: CharacterRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recyclerAdapter = CharacterRecyclerViewAdapter(viewModel, viewLifecycleOwner)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_character_list, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filtering_edit.addTextChangedListener(viewModel.createTextChangeListener())

        val recyclerView = characters_recycler_view
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = recyclerAdapter

        viewModel.liveCharacters.observe(viewLifecycleOwner) {
            recyclerAdapter.notifyDataSetChanged()
        }
    }
}