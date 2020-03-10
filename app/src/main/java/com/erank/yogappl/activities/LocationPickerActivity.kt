package com.erank.yogappl.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import com.erank.yogappl.R
import com.erank.yogappl.activities.NewEditDataActivity.Companion.RC_LOCATION
import com.erank.yogappl.adapters.LocationsAdapter
import com.erank.yogappl.models.LocationResult
import com.erank.yogappl.utils.extensions.setIconTintCompat
import com.erank.yogappl.utils.extensions.toast
import com.erank.yogappl.utils.helpers.LocationHelper
import com.erank.yogappl.utils.interfaces.OnLocationSelectedCallback
import kotlinx.android.synthetic.main.activity_location_picker.*


class LocationPickerActivity : AppCompatActivity(),
    SearchView.OnQueryTextListener, OnLocationSelectedCallback {

    private val resultsRV by lazy { results_recycler }
    private val locationsEmptyTv by lazy { locations_empty_tv }

    private val locationsAdapter by lazy { LocationsAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        resultsRV.adapter = locationsAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.app_bar_search)

        searchItem.setIconTintCompat()

        val searchView = searchItem.actionView as SearchView
        searchView.isIconified = false
        searchView.requestFocus()

        searchView.queryHint = "Find your place"
        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onQueryTextSubmit(query: String) = false

    override fun onQueryTextChange(query: String): Boolean {

        LocationHelper.getLocationResults(query) {
            locationsAdapter.submitList(it)
            locationsAdapter.notifyDataSetChanged()

            locationsEmptyTv.visibility = if (it.isEmpty()) VISIBLE else GONE
        }

        return false
    }

    override fun onSelected(location: LocationResult) {
        setResult(RESULT_OK, Intent().putExtra("location", location))
//        finishActivity(RC_LOCATION)
        finish()
    }
}
