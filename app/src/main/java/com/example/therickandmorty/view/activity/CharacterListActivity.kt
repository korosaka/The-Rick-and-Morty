package com.example.therickandmorty.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.therickandmorty.R
import com.example.therickandmorty.view.fragment.CharacterListFragment

class CharacterListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, CharacterListFragment())
                .commit()
        }
    }
}