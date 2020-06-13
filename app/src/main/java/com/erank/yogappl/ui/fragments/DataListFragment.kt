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
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.SearchState
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.enums.SourceType.UPLOADS
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.models.DataInfo
import com.erank.yogappl.ui.activities.dataInfo.DataInfoActivity
import com.erank.yogappl.ui.activities.newEditData.NewEditDataActivity
import com.erank.yogappl.ui.adapters.DataListAdapter
import com.erank.yogappl.ui.adapters.DataVH
import com.erank.yogappl.ui.custom_views.ProgressDialog
import com.erank.yogappl.utils.extensions.*
import com.erank.yogappl.utils.helpers.RemindersAdapter
import com.erank.yogappl.utils.interfaces.OnItemActionCallback
import com.erank.yogappl.utils.interfaces.SearchUpdateable
import com.erank.yogappl.utils.runOnBackground
import kotlinx.android.synthetic.main.fragment_data_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class DataListFragment<T : BaseData, AT, X> : Fragment(),
    SearchUpdateable,
    OnItemActionCallback<T>
        where X : DataVH<T>, AT : DataListAdapter<T, X> {

    companion object {
        val TAG: String = DataListFragment::class.java.name
        internal const val SOURCE_TYPE = "sourceType"
    }

    private val RC_EDIT = 1
    internal var isEditable: Boolean = false
    protected val dataAdapter by lazy { createAdapter() }
    protected lateinit var currentSourceType: SourceType
    private var remindersAdapter: RemindersAdapter<T>? = null

    private val emptyTV by lazy { empty_tv }
    private val recyclerView by lazy { data_recycler_view }
    private val progressDialog by lazy { ProgressDialog(requireContext()) }
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

        emptyTV.text = getString(R.string.empty, dataType.lowerCased(context!!))

        setIsEditable()

        createAdapter()
        observeData(getLiveData())
    }

    abstract fun createAdapter(): AT

    protected fun initAdapter(adapter: AT) = adapter.also {

        adapter.callback = this
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
            SearchState.CHANGED -> CoroutineScope(IO).launch {
                val filteredData = getFilteredData(query)
                withContext(Main) {
                    dataAdapter.submitList(filteredData)
                }
            }
            SearchState.CLOSED -> observeData(getLiveData())
        }
    }

    abstract fun getLiveData(): LiveData<List<T>>
    abstract suspend fun getFilteredData(query: String): List<T>

    private fun setEmptyView(isEmpty: Boolean) {
        emptyTV.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun observeData(liveData: LiveData<List<T>>) {

        liveData.observe(viewLifecycleOwner, Observer {
            dataAdapter.submitList(it)
            dataAdapter.notifyDataSetChanged()
            onListUpdated(it)
            setEmptyView(it.isEmpty())
        })
    }

    protected open fun onListUpdated(list: List<T>) {}

    override fun onItemSelected(item: T) = openActivity(item.id)

    override fun onEditAction(item: T) = openActivity(item.id)

    override fun onDeleteAction(item: T) {
        alert(R.string.confirm_delete_data)
            ?.setPositiveButton(android.R.string.yes) { _, _ ->
                onDeleteConfirmed(item)
            }?.setNegativeButton(android.R.string.no, null)
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
                toast(R.string.edited)
            }
        }
    }

    fun onFailure(error: Exception) {
        alert(R.string.problemFound, error.localizedMessage)
            ?.setPositiveButton(R.string.ok, null)
            ?.show()

        Log.d(TAG, "problem deleting", error)
    }

    private fun setIsEditable() {
        isEditable = currentSourceType == UPLOADS
    }

    override fun onSignAction(item: T) {

        progressDialog.show()
        runOnBackground({
            toggleSign(item)
        }) { isSigned ->
            progressDialog.dismiss()

            RemindersAdapter(item).also {
                remindersAdapter = it
                if (isSigned)
                    it.showDialog(activity!!)
                else
                    it.removeReminder(item)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        remindersAdapter?.tryAgainIfAvailable(activity!!, permissions, grantResults)
    }

    abstract suspend fun toggleSign(item: T): Boolean
}