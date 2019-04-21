package com.example.sunshine.activities.newlocation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import com.example.sunshine.R
import com.example.sunshine.ViewModelFactory
import com.example.sunshine.utils.Pref
import com.example.sunshine.utils.SunshineSyncUtils
import kotlinx.android.synthetic.main.activity_new_location.*

class NewLocation : AppCompatActivity() {

    private lateinit var mViewModel: NewLocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_location)

        val adapter = AutoSuggestAdapter(this,android.R.layout.simple_dropdown_item_1line)
        new_city_edit_text.threshold = 2
        new_city_edit_text.setAdapter(adapter)
        new_city_edit_text.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = new_city_edit_text.text.trim().toString()
                mViewModel.searchCityByName(text)
            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })
        new_city_edit_text.setOnItemClickListener { _, _, position, _ ->
            Pref.setString("City", adapter.getObject(position))
            SunshineSyncUtils.startImmediateSync(this)
            finish()
        }

        back_image.setOnClickListener { finish() }

        mViewModel = ViewModelProviders.of(this,ViewModelFactory())
            .get(NewLocationViewModel::class.java)
        mViewModel.cities.observe(this, Observer {
            it?.let{
                adapter.setData(it)
                adapter.notifyDataSetChanged()
            }
        })
    }
}
