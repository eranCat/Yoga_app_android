package com.erank.yogappl.fragments

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.erank.yogappl.R
import com.erank.yogappl.activities.DataInfoActivity
import com.erank.yogappl.activities.MainActivity
import com.erank.yogappl.activities.MainActivity.Companion.ACTION_ADDED
import com.erank.yogappl.activities.MainActivity.Companion.ACTION_UPDATE
import com.erank.yogappl.activities.MainActivity.Companion.SEARCH_ACTION
import com.erank.yogappl.activities.NewEditDataActivity
import com.erank.yogappl.adapters.DataListAdapter
import com.erank.yogappl.adapters.DataVH
import com.erank.yogappl.models.BaseData
import com.erank.yogappl.models.DataInfo
import com.erank.yogappl.utils.SearchBroadcastReceiver
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.enums.SourceType.UPLOADS
import com.erank.yogappl.utils.extensions.alert
import com.erank.yogappl.utils.extensions.lowercaseName
import com.erank.yogappl.utils.helpers.RemindersAdapter
import com.erank.yogappl.utils.interfaces.OnItemActionCallback
import com.erank.yogappl.utils.interfaces.SourceTypeDynamic
import com.erank.yogappl.utils.interfaces.TaskCallback
import kotlinx.android.synthetic.main.fragment_data_list.*
import kotlinx.android.synthetic.main.no_search_results.*

abstract class DataListFragment<T : BaseData, AT, X> : Fragment(),
    SourceTypeDynamic,
    OnItemActionCallback<T>,
    TaskCallback<Int, Exception>
        where X : DataVH<T>, AT : DataListAdapter<T, X> {

    companion object {
        val TAG: String = DataListFragment::class.java.name
        private const val SOURCE_TYPE = "sourceType"

        fun argsBundle(type: SourceType) = bundleOf(SOURCE_TYPE to type)
    }

    internal var isEditable: Boolean = false
    private var dataAdapter: AT? = null
    private lateinit var currentSourceType: SourceType
    private var remindersAdapter: RemindersAdapter<T>? = null

    private val emptyTV by lazy { empty_tv }
    private val recyclerView by lazy { data_recycler_view }
    private val progressBar by lazy { progress_bar }
    private val emptySearchTV by lazy { no_results_tv }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(
        R.layout.fragment_data_list,
        container, false
    )!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentSourceType = arguments!!.getSerializable("sourceType") as SourceType
        val liveData = getLiveData(currentSourceType)

        emptyTV.text = "Empty ${dataType.lowercaseName}"

        setIsEditable()
        createAdapter(liveData.value ?: mutableListOf())
        observeData(liveData)
    }

    private val broadcastReceiver = SearchBroadcastReceiver<T, X> { isSearching, isEmpty ->
        emptySearchTV.isVisible = isSearching && isEmpty
    }

    abstract fun getLiveData(sourceType: SourceType): MutableLiveData<MutableList<T>>

    abstract fun createAdapter(list: MutableList<T>)

    protected fun initAdapter(adapter: AT) {

        dataAdapter = adapter
        adapter.callback = this
        recyclerView.adapter = adapter

//        TODO fix swipe
//        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                adapter.removeAt(viewHolder.adapterPosition)
////                TODO handle deletion + edit + sign
//            }
//        }
//        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)

        setEmptyView(adapter.currentList.isEmpty())
        registerToMain()
        broadcastReceiver.setAdapter(adapter)
    }

    private fun setEmptyView(isEmpty: Boolean) {
        emptyTV.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun observeData(liveData: MutableLiveData<MutableList<T>>) {
        liveData.observe(viewLifecycleOwner, Observer {
            dataAdapter?.submitList(it)
            dataAdapter?.notifyDataSetChanged()
            dataAdapter?.initOriginalPositions()
            setEmptyView(it.isEmpty())
        })
    }

    override fun onPause() {
        super.onPause()
        unregisterFromMain()
    }

    override fun onResume() {
        super.onResume()
        registerToMain()
    }

    private fun registerToMain() {

        val ctx = context ?: return

        val filter = IntentFilter(SEARCH_ACTION)
        filter.addAction(ACTION_ADDED)
        filter.addAction(ACTION_UPDATE)

        LocalBroadcastManager.getInstance(ctx)
            .registerReceiver(broadcastReceiver, filter)
    }

    private fun unregisterFromMain() {
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .unregisterReceiver(broadcastReceiver)
        }
    }


    override fun onItemSelected(item: T, pos: Int) = openActivity(pos)

    override fun onEditAction(item: T, pos: Int) = openActivity(pos)

    override fun onDeleteAction(item: T, pos: Int) {
        alert("Are you sure you want to delete?")
            ?.setPositiveButton("yes") { _, _ ->
                onDeleteConfirmed(item, pos)
            }?.setNegativeButton("no", null)
            ?.show()
    }

    abstract fun onDeleteConfirmed(item: T, pos: Int)
    abstract val dataType: DataType

    private fun openActivity(pos: Int) {
        val context = context ?: return

        val acClass =
            if (!isEditable)
                DataInfoActivity::class.java
            else
                NewEditDataActivity::class.java

        val dataInfo = DataInfo(dataType, currentSourceType, pos)
        val intent = Intent(context, acClass)
            .putExtra("dataInfo", dataInfo)

        if (!isEditable)
            startActivity(intent)
        else
            startActivityForResult(intent, MainActivity.RC_EDIT)
    }

    override fun onSuccess(position: Int?) {
        dataAdapter?.notifyItemRemoved(position!!)
    }

    override fun onFailure(error: Exception) {
        alert("There was a problem", error.localizedMessage)
            ?.setPositiveButton("ok", null)
            ?.show()

        Log.d(TAG, "problem deleting", error)
    }

    override fun setSourceType(type: SourceType) {
        removeObserver()
        currentSourceType = type
        setIsEditable()
        getLiveData(type).let {
            observeData(it)
            createAdapter(it.value ?: mutableListOf())
        }
    }

    private fun removeObserver() {
        DataSource.getLessons(currentSourceType)
            .removeObservers(viewLifecycleOwner)
    }

    private fun setIsEditable() {
        isEditable = currentSourceType == UPLOADS
    }


    override fun onSignAction(item: T, i: Int) {

        progressBar.visibility = View.VISIBLE
        toggleSign(item, object : TaskCallback<Boolean, Exception> {
            override fun onSuccess(didSign: Boolean?) {
                dataAdapter?.notifyItemChanged(i)
                progressBar.visibility = View.GONE

                with(RemindersAdapter(item)) {
                    remindersAdapter = this
                    if (didSign!!)
                        showDialog(activity!!)
                    else
                        removeReminder(context!!, item)
                }
            }

            override fun onFailure(error: Exception) {
                progressBar.visibility = View.GONE
                //in or out
                alert("Failed signing", error.localizedMessage)
                    ?.setPositiveButton("ok", null)
                    ?.show()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        remindersAdapter?.tryAgainIfAvailable(activity!!, permissions, grantResults)
    }

    abstract fun toggleSign(item: T, callback: TaskCallback<Boolean, Exception>)
}