package com.example.therickandmorty.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.therickandmorty.R
import com.example.therickandmorty.model.Common
import com.example.therickandmorty.view.fragment.CharacterDetailFragment

class CharacterDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_detail_activity)

        if (savedInstanceState == null) {
            val characterId = intent.getStringExtra(Common.CHARACTER_ID_KEY)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, CharacterDetailFragment.newInstance(characterId))
                .commit()
        }
    }
}