package com.erank.yogappl.ui.activities.location

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.erank.yogappl.R
import com.erank.yogappl.data.models.LocationResult
import com.erank.yogappl.ui.adapters.LocationsAdapter
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.extensions.hide
import com.erank.yogappl.utils.extensions.setIconTintCompat
import com.erank.yogappl.utils.extensions.show
import com.erank.yogappl.utils.interfaces.OnLocationSelectedCallback
import com.erank.yogappl.utils.runOnBackground
import kotlinx.android.synthetic.main.activity_location_picker.*
import javax.inject.Inject


class LocationPickerActivity : AppCompatActivity(),
    SearchView.OnQueryTextListener, OnLocationSelectedCallback {

    private val resultsRV by lazy { results_recycler }
    private val locationsEmptyTv by lazy { locations_empty_tv }
    private val locationsAdapter by lazy { LocationsAdapter(this) }

    @Inject
    lateinit var viewModel: LocationPickerVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        (application as App).getAppComponent().inject(this)

        resultsRV.adapter = locationsAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.app_bar_search)

        searchItem.setIconTintCompat()

        val searchView = searchItem.actionView as SearchView
        searchView.isIconified = false
        searchView.requestFocus()

        searchView.queryHint = getString(R.string.find_your_place)
        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onQueryTextSubmit(query: String) = true

    override fun onQueryTextChange(query: String): Boolean {
        if (query.isEmpty()) {
            locationsAdapter.submitList(emptyList())
            return false
        }

        runOnBackground({
            viewModel.getLocationResults(query)
        }) { results ->
            locationsAdapter.submitList(results)
            with(locationsEmptyTv) {
                if (results.isEmpty()) show()
                else hide()
            }
        }

        return false
    }

    override fun onSelected(location: LocationResult) {
        setResult(RESULT_OK, Intent().putExtra("location", location))
        finish()
    }
}
