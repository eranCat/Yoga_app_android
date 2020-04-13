package com.erank.yogappl.activities

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.erank.yogappl.R
import com.erank.yogappl.models.CurrencyLayerResponse
import com.erank.yogappl.models.User
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.extensions.alert
import com.erank.yogappl.utils.extensions.startZoomAnimation
import com.erank.yogappl.utils.extensions.toast
import com.erank.yogappl.utils.helpers.Connectivity
import com.erank.yogappl.utils.helpers.LocationHelper
import com.erank.yogappl.utils.helpers.LocationHelper.RPC_COARSE_LOCATION
import com.erank.yogappl.utils.helpers.LocationHelper.RPC_FINE_LOCATION
import com.erank.yogappl.utils.helpers.LocationHelper.checkPermissionResultsCoarseLocation
import com.erank.yogappl.utils.helpers.LocationHelper.checkPermissionResultsFineLocation
import com.erank.yogappl.utils.helpers.MoneyConverter
import com.erank.yogappl.utils.interfaces.MoneyConnectionCallback
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity(),
    MoneyConnectionCallback,
    UserTaskCallback,
    TaskCallback<Void, Exception> {

    companion object {
        const val RC_LOGIN = 1
        private val TAG = SplashActivity::class.java.name
        private const val DEFAULT_ANIMATION_DURATION = 2000
    }

    private val floatAnimator by lazy {
        ValueAnimator.ofFloat(0f, -80f).apply {

            addUpdateListener {
                logoImg.translationY = it.animatedValue as Float
            }

            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            duration = DEFAULT_ANIMATION_DURATION.toLong()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        TODO if not already in progress
//        with view model boolean
//        TODO maybe don't open main if activity paused
        startSetup()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_LOGIN -> if (resultCode == RESULT_OK)
                startSetup()
        }
    }

    private fun startSetup() {
        if (!Connectivity.isNetworkConnected(this)) {
            showInternetDialog()
            floatAnimator.cancel()
            logoImg.clearAnimation()
            return
        }


        if (!LocationHelper.getCoarseLocationPermissionIfNeeded(this)) {
            toast("needs coarse location permission")
            return
        }

        if (!LocationHelper.getFineLocationPermissionIfNeeded(this)) {
            toast("needs fine location permission")
            return
        }

        LocationHelper.initLocationService(this)

        if (FirebaseAuth.getInstance().currentUser == null) {

            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, RC_LOGIN)

            return
        }

//        start floating animation
        floatAnimator.start()
        DataSource.fetchLoggedUser(this)
    }

    override fun onSuccessFetchingUser(user: User?) {
        MoneyConverter.connect(this, this)
    }

    override fun onFailedFetchingUser(e: Exception) =
        notifyError("user fetch problem", e)


    override fun onSuccessConnectingMoney() {
        DataSource.loadData(this, this)
    }

    override fun onSuccess(result: Void?) {
        logoImg.startZoomAnimation(this::openMain)
    }

    private fun openMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onFailure(e: Exception) = notifyError(e.localizedMessage, e)

    override fun onFailedConnectingMoney(error: CurrencyLayerResponse.Error) =
        notifyError(error.info)

    private fun notifyError(msg: String, e: Exception? = null) {
        logoImg.clearAnimation()
        e?.localizedMessage?.let {
            toast(it)
            Log.d(TAG, it, e)

        } ?: Log.d(TAG, msg)
    }

    private fun showInternetDialog() {
        alert(null, "No internet connection")
            .setPositiveButton("retry") { _, _ ->
                startSetup()
            }.show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            RPC_COARSE_LOCATION -> {
                val isGranted = checkPermissionResultsCoarseLocation(
                    this, permissions, grantResults
                )

                val toastMsg = "can't continue without location permission"

                checkGranted(isGranted, toastMsg)
            }

            RPC_FINE_LOCATION -> {
                val isGranted = checkPermissionResultsFineLocation(
                    this, permissions, grantResults
                )

                val toastMsg = "Can't continue without fine location permission"

                checkGranted(isGranted, toastMsg)
            }
        }
    }

    private fun checkGranted(isGranted: Boolean, msg: String) =
        if (isGranted) startSetup()
        else toast(msg)
}