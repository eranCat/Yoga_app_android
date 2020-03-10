package com.erank.yogappl.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.fragments.EventsListFragment
import com.erank.yogappl.fragments.TabsFragment
import com.erank.yogappl.models.DataInfo
import com.erank.yogappl.models.User.Type.STUDENT
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.DataType.EVENTS
import com.erank.yogappl.utils.enums.SearchState
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.enums.SourceType.*
import com.erank.yogappl.utils.extensions.alert
import com.erank.yogappl.utils.extensions.cName
import com.erank.yogappl.utils.extensions.setIconTintCompat
import com.erank.yogappl.utils.helpers.AuthHelper
import com.erank.yogappl.utils.interfaces.SearchViewCallbackAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    SearchViewCallbackAdapter {

    companion object {
        private const val PACKAGE = "com.erank.yogappl.activities.mainActivity"

        const val SEARCH_ACTION = "$PACKAGE.search"
        const val ACTION_ADDED = "$PACKAGE.added"
        const val ACTION_UPDATE = "$PACKAGE.update"
        const val RC_NEW = 123
        const val RC_EDIT = 111
    }

    private lateinit var sourceType: SourceType
    private val drawerLayout by lazy { drawer_layout }
    private val navigationView by lazy { nav_view }
    private val toolbar by lazy { main_toolbar }
    private val bottomTabs by lazy { bottom_nav_view }
    private val addFab by lazy { add_fab }

    private var tabsFragment: TabsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomTabs.setOnNavigationItemSelectedListener(this::onNavigationItemSelected)
        bottomTabs.selectedItemId = R.id.action_all

        initDrawer()

        addFab.setOnClickListener { openNewDataActivity() }
    }

    private fun openNewDataActivity() {
        val intent = Intent(this, NewEditDataActivity::class.java)

        if (DataSource.currentUser?.type == STUDENT) {
            intent.putExtra("dataInfo", DataInfo(EVENTS))
            startActivityForResult(intent, RC_NEW)
            return
        }

        val dataTypes = DataType.values()

        val items = dataTypes.map {
            "Create new ${it.singular}"
        }.toTypedArray()

        alert("Add new lesson or event")
            .setItems(items) { _, i ->

                val dataInfo = DataInfo(dataTypes[i])

                intent.putExtra("dataInfo", dataInfo)

                startActivityForResult(intent, RC_NEW)
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    override fun onActivityResult(rc: Int, result: Int, data: Intent?) {
        super.onActivityResult(rc, result, data)

        if (result != RESULT_OK)
            return

        val action = when (rc) {
            RC_NEW -> ACTION_ADDED
            RC_EDIT -> ACTION_UPDATE//starts from fragment
            else -> return
        }

        data?.let {
            LocalBroadcastManager.getInstance(this)
                .sendBroadcast(Intent(action).putExtras(it))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)

        searchItem.setIconTintCompat()

        searchItem.setOnActionExpandListener(this)

        val searchView = searchItem!!.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                openNewDataActivity()
                true
            }
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadFragment(type: SourceType): Boolean {
        sourceType = type
        title = type.cName

        val color = resources.getIntArray(R.array.tabs_colors)[type.ordinal]
        supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
        val headerView = navigationView.getHeaderView(0)
        headerView.setBackgroundColor(color)

        when (type) {
            ALL, SIGNED -> updateTabFragment(type)
            //student can only have events
            UPLOADS ->
                if (DataSource.currentUser?.type != STUDENT)
                    updateTabFragment(type)
                else
                    replaceFragment(EventsListFragment.newInstance(type))
        }

        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.section_frame, fragment)
            .setTransition(TRANSIT_FRAGMENT_FADE)
            .disallowAddToBackStack()
            .commit()
    }

    private fun updateTabFragment(sourceType: SourceType): TabsFragment {
        if (tabsFragment == null) {

            tabsFragment = TabsFragment.newInstance(sourceType)

            val fragments = supportFragmentManager.fragments
            if (tabsFragment != fragments.firstOrNull())
                replaceFragment(tabsFragment!!)
        }

        tabsFragment!!.setSourceType(sourceType)

        return tabsFragment!!
    }

    private fun initDrawer() {

        setSupportActionBar(toolbar)

        ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open, R.string.close
        ).let {
            drawerLayout.addDrawerListener(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            it.syncState()
        }

        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun fillDrawer() {
        val currentUser = DataSource.currentUser ?: return

        navigationView.setNavigationItemSelectedListener(this)

        val headerView = navigationView.getHeaderView(0)

        val profileIV: ImageView = headerView.findViewById(R.id.profile_Img)
        profileIV.setOnClickListener { openUserData() }

        Glide.with(this)
            .load(currentUser.profileImageUrl)
            .placeholder(R.drawable.yoga_model)
            .circleCrop()
            .into(profileIV)

        headerView.login_email.text = currentUser.name
    }

    override fun onResume() {
        super.onResume()
        fillDrawer()
    }

    private fun showSignOutDialog() {
        alert(null, "Are you sure you want to sign out?")
            .setPositiveButton("Yep") { _, _ ->
                AuthHelper.signOut(this)
                finish()
            }
            .setNegativeButton("Nope", null)
            .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
//            tabs
            R.id.action_all -> loadFragment(ALL)
            R.id.action_signed -> loadFragment(SIGNED)
            R.id.action_uploads -> loadFragment(UPLOADS)

            R.id.nav_edit_profile -> {
                openUserData()
                drawerLayout.closeDrawer(GravityCompat.START)
                drawerLayout.isSelected = false
                true
            }
            R.id.nav_signOut -> {
                showSignOutDialog()
                drawerLayout.closeDrawer(GravityCompat.START)
                drawerLayout.isSelected = false
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openUserData() = startActivity(
        Intent(this, RegisterActivity::class.java)
    )

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        sendSearchBroadcast(SearchState.OPENED)
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        sendSearchBroadcast(SearchState.CLOSED)
        return true
    }

    override fun onQueryTextChange(q: String?): Boolean {
        sendSearchBroadcast(SearchState.CHANGED, q ?: "")
        return true
    }

    private fun sendSearchBroadcast(state: SearchState, query: String = "") {

        val intent = Intent(SEARCH_ACTION)
            .putExtra("query", query)
            .putExtra("state", state)

        LocalBroadcastManager
            .getInstance(this)
            .sendBroadcast(intent)
    }
}
