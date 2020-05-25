package com.erank.yogappl.ui.activities.splash

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.erank.yogappl.R
import com.erank.yogappl.data.models.CurrencyLayerResponse
import com.erank.yogappl.data.models.User
import com.erank.yogappl.ui.activities.login.LoginActivity
import com.erank.yogappl.ui.activities.main.MainActivity
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.extensions.alert
import com.erank.yogappl.utils.extensions.startZoomAnimation
import com.erank.yogappl.utils.extensions.toast
import com.erank.yogappl.utils.helpers.Connectivity
import com.erank.yogappl.utils.interfaces.MoneyConnectionCallback
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject


class SplashActivity : AppCompatActivity(),
    MoneyConnectionCallback,
    UserTaskCallback,
    TaskCallback<Void, Exception> {

    companion object {
        const val RC_LOGIN = 1
        private val TAG = SplashActivity::class.java.name
        private const val DEFAULT_ANIMATION_DURATION = 2000
    }

    @Inject
    lateinit var viewModel: SplashViewModel

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

        (application as App).getAppComponent().inject(this)

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

        if (!viewModel.getLocationPermissionIfNeeded(this)) {
            toast("needs location permission")
            return
        }

        viewModel.initLocationService()

        if (viewModel.isFbUserConnected) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, RC_LOGIN)
            return
        }

//        start floating animation
        floatAnimator.start()
        viewModel.fetchLoggedUser(this)
    }

    override fun onSuccessFetchingUser(user: User?) {
        viewModel.connectMoneyConverter(this)
    }

    override fun onFailedFetchingUser(e: Exception) =
        notifyError("user fetch problem", e)


    override fun onSuccessConnectingMoney() {
        viewModel.loadData(this, this)
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
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (viewModel.checkAllPermissionResults(requestCode, permissions, grantResults)) {
            startSetup()
        } else {
            toast("Can't continue without location permission")
            startSetup()
        }

    }
}