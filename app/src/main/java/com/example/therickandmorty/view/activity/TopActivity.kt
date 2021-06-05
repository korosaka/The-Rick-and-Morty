package com.example.therickandmorty.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.therickandmorty.R
import com.example.therickandmorty.model.Common
import com.example.therickandmorty.model.DataType

class TopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)
    }

    fun useProductData(view: View) {
        moveToCharacterListScreen(DataType.PRODUCT)
    }

    fun useTestData(view: View) {
        moveToCharacterListScreen(DataType.TEST)
    }

    private fun moveToCharacterListScreen(type: DataType) {
        val intent = Intent(this, CharacterListActivity::class.java)
        Common.dataType = type
        startActivity(intent)
    }
}