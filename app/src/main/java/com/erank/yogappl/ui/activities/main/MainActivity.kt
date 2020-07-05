package com.erank.yogappl.ui.activities.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat.START
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.DataType.EVENTS
import com.erank.yogappl.data.enums.SearchState
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.enums.SourceType.*
import com.erank.yogappl.data.models.DataInfo
import com.erank.yogappl.ui.activities.newEditData.NewEditDataActivity
import com.erank.yogappl.ui.activities.register.RegisterActivity
import com.erank.yogappl.ui.fragments.TabsFragment
import com.erank.yogappl.ui.fragments.events.EventsListFragment
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.SearchWatcher
import com.erank.yogappl.utils.extensions.*
import com.erank.yogappl.utils.interfaces.SearchUpdateable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val TABS_FRAGMENT_TAG = "tabs"
        private const val RC_NEW = 123
    }

    private lateinit var sourceType: SourceType

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as App).getAppComponent().inject(this)

        bottom_nav_view.setOnNavigationItemSelectedListener(this)
        bottom_nav_view.selectedItemId = R.id.action_all
        bottom_nav_view.setOnNavigationItemReselectedListener {}

        initDrawer()
        val adView = viewModel.loadBannerAd()
        bannnerAdViewHolder.addView(adView)
    }

    private fun openNewDataActivity() {
        val intent = Intent(this, NewEditDataActivity::class.java)

        if (viewModel.isUserStudent) {
            intent.putExtra("dataInfo", DataInfo(EVENTS))
            startActivityForResult(
                intent,
                RC_NEW
            )
            return
        }

        val dataTypes = DataType.values()

        val items = dataTypes.map {
            getString(R.string.create_new_data, getString(it.singular))
        }.toTypedArray()

        alert(R.string.add_new)
            .setItems(items) { _, i ->

                val dataInfo = DataInfo(dataTypes[i])

                intent.putExtra("dataInfo", dataInfo)

                startActivityForResult(
                    intent,
                    RC_NEW
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()

    }

    override fun onActivityResult(rc: Int, result: Int, data: Intent?) {
        super.onActivityResult(rc, result, data)

        when (rc) {
            RC_NEW -> when (result) {
                RESULT_OK -> toast(R.string.added)
                RESULT_CANCELED -> toast(R.string.discarded)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        menu.findItem(R.id.action_search)?.also {

            val watcher = object : SearchWatcher() {
                override fun updateSearch(state: SearchState, query: String) {
                    val manager = supportFragmentManager
                    val fragment = manager.findFragmentByTag(TABS_FRAGMENT_TAG)
                        ?: manager.findFragmentByTag("events")
                        ?: return

                    if (fragment.isVisible.not()) return

                    (fragment as? SearchUpdateable)?.updateSearch(state, query)
                }
            }

            it.setIconTintCompat()
            it.setOnActionExpandListener(watcher)

            (it.actionView as? SearchView)?.setOnQueryTextListener(watcher)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            openNewDataActivity()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun loadFragment(type: SourceType): Boolean {
        sourceType = type

        val colors = resources.getIntArray(R.array.tabs_colors)
        val color = colors[type.ordinal]

        main_toolbar.animateColor(color)

        nav_view.getHeaderView(0).setBackgroundColor(color)

        changeWindowBarColor(color)

        when (type) {
            ALL, SIGNED -> replaceTabs(type)
            //student can only have events
            UPLOADS ->
                if (viewModel.isUserStudent.not())
                    replaceTabs(type)
                else
                    replaceFragment(EventsListFragment.newInstance(type), "events")
        }

        return true
    }

    private fun changeWindowBarColor(@ColorInt color: Int) = window?.let {

        it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        it.statusBarColor = color.withAlpha(180)
    }


    private fun replaceTabs(type: SourceType) =
        replaceFragment(
            TabsFragment.newInstance(type),
            TABS_FRAGMENT_TAG
        )

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .setTransition(TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.section_frame, fragment, tag)
            .commit()
    }

    private fun initDrawer() {

        setSupportActionBar(main_toolbar)

        ActionBarDrawerToggle(
            this, drawer_layout, main_toolbar,
            R.string.open, R.string.close
        ).let {
            drawer_layout.addDrawerListener(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            it.syncState()
        }

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun fillDrawer() {
        val currentUser = viewModel.user ?: return

        nav_view.setNavigationItemSelectedListener(this)

        val headerView = nav_view.getHeaderView(0)

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
//            tabs
            R.id.action_all -> loadFragment(ALL)
            R.id.action_signed -> loadFragment(SIGNED)
            R.id.action_uploads -> loadFragment(UPLOADS)

            R.id.nav_edit_profile -> {
                openUserData()
                closeDrawer()
                true
            }
            R.id.nav_signOut -> {
                showSignOutDialog()
                closeDrawer()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSignOutDialog() {
        alert(null, R.string.confirm_signout)
            .setPositiveButton(R.string.yep) { _, _ ->
                viewModel.signOut()
                finish()
            }
            .setNegativeButton(R.string.nope, null)
            .show()
    }

    private fun closeDrawer() {
        drawer_layout.closeDrawer(START)
        drawer_layout.isSelected = false
    }

    private fun openUserData() = startActivity(
        Intent(this, RegisterActivity::class.java)
    )

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(START)) {
            drawer_layout.closeDrawer(START)
        } else {
            super.onBackPressed()
        }
    }

}