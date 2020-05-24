package com.erank.yogappl.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.erank.yogappl.R
import com.erank.yogappl.ui.activities.DataInfoActivity
import com.erank.yogappl.ui.activities.newEditData.NewEditDataActivity
import com.erank.yogappl.ui.adapters.DataListAdapter
import com.erank.yogappl.ui.adapters.DataVH
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.models.DataInfo
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.SearchState
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.enums.SourceType.UPLOADS
import com.erank.yogappl.utils.extensions.alert
import com.erank.yogappl.utils.extensions.lowercaseName
import com.erank.yogappl.utils.extensions.toast
import com.erank.yogappl.utils.helpers.RemindersAdapter
import com.erank.yogappl.utils.interfaces.OnItemActionCallback
import com.erank.yogappl.utils.interfaces.SearchUpdateable
import com.erank.yogappl.utils.interfaces.TaskCallback
import kotlinx.android.synthetic.main.fragment_data_list.*
import kotlinx.android.synthetic.main.no_search_results.*

abstract class DataListFragment<T : BaseData, AT, X> : Fragment(),
    SearchUpdateable,
    OnItemActionCallback<T>,
    TaskCallback<Int, Exception>
        where X : DataVH<T>, AT : DataListAdapter<T, X> {

    companion object {
        val TAG: String = DataListFragment::class.java.name
        internal const val SOURCE_TYPE = "sourceType"
    }

    private val RC_EDIT = 1
    internal var isEditable: Boolean = false
    private val dataAdapter by lazy { createAdapter() }
    protected lateinit var currentSourceType: SourceType
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

        currentSourceType = arguments!!.getSerializable(SOURCE_TYPE) as SourceType

        emptyTV.text = getString(R.string.empty, dataType.lowercaseName)

        setIsEditable()

        createAdapter()
        observeData(getLiveData())
    }

    abstract fun createAdapter(): AT

    protected fun initAdapter(adapter: AT) = adapter.apply {

        adapter.callback = this@DataListFragment
        recyclerView.adapter = adapter

//        TODO fix swipe
        /*val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeAt(viewHolder.adapterPosition)
//                TODO handle deletion + edit + sign
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)*/

        setEmptyView(adapter.currentList.isEmpty())
    }

    override fun updateSearch(state: SearchState, query: String) {
        when (state) {
            SearchState.CLOSED -> dataAdapter.reset()
            SearchState.CHANGED -> dataAdapter.filter(query)
        }
    }

    abstract fun getLiveData(): LiveData<List<T>>


    private fun setEmptyView(isEmpty: Boolean) {
        emptyTV.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun observeData(liveData: LiveData<List<T>>) {

        liveData.observe(viewLifecycleOwner, Observer {
            dataAdapter.submitList(it)
            setEmptyView(it.isEmpty())
        })
    }

    override fun onItemSelected(item: T) = openActivity(item.id)

    override fun onEditAction(item: T) = openActivity(item.id)

    override fun onDeleteAction(item: T) {
        alert("Are you sure you want to delete?")
            ?.setPositiveButton("yes") { _, _ ->
                onDeleteConfirmed(item)
            }?.setNegativeButton("no", null)
            ?.show()
    }

    abstract fun onDeleteConfirmed(item: T)
    abstract val dataType: DataType

    private fun openActivity(id: String) {

        val cls = if (!isEditable) DataInfoActivity::class.java
        else NewEditDataActivity::class.java

        val dataInfo = DataInfo(dataType, id)

        val context = context ?: return

        val intent = Intent(context, cls)
            .putExtra("dataInfo", dataInfo)

        if (!isEditable)
            startActivity(intent)
        else
            startActivityForResult(intent, RC_EDIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                toast("Edited!")
            }
        }
    }

    override fun onSuccess(position: Int?) {
//        TODO use liveData
        dataAdapter.notifyDataSetChanged()
//        dataAdapter.notifyItemRemoved(position)
    }

    override fun onFailure(error: Exception) {
        alert("There was a problem", error.localizedMessage)
            ?.setPositiveButton("ok", null)
            ?.show()

        Log.d(TAG, "problem deleting", error)
    }

    private fun setIsEditable() {
        isEditable = currentSourceType == UPLOADS
    }


    override fun onSignAction(item: T) {

        progressBar.visibility = View.VISIBLE
        toggleSign(item, object : TaskCallback<Boolean, Exception> {
            override fun onSuccess(didSign: Boolean?) {
//                dataAdapter?.notifyItemChanged(i)
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