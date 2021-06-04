package com.example.therickandmorty.view.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.therickandmorty.databinding.CharactersRecyclerItemBinding
import com.example.therickandmorty.view_model.CharacterListViewModel

class CharacterRecyclerViewAdapter(
    private val viewModel: CharacterListViewModel,
    private val parentLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<CharacterRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterRecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CharactersRecyclerItemBinding.inflate(layoutInflater, parent, false)
        return CharacterRecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterRecyclerViewHolder, position: Int) {
        holder.binding.characterHeadline = viewModel.getCharacter(position)
        holder.binding.viewModel = viewModel
        holder.binding.lifecycleOwner = parentLifecycleOwner
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return viewModel.getCharacterCount()
    }
}
