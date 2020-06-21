package com.erank.yogappl.ui.activities.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.vvalidator.form
import com.afollestad.vvalidator.form.Form
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.data.models.Teacher
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.models.User.Type.STUDENT
import com.erank.yogappl.data.models.User.Type.TEACHER
import com.erank.yogappl.ui.custom_views.ProgressDialog
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.extensions.alert
import com.erank.yogappl.utils.extensions.hide
import com.erank.yogappl.utils.extensions.toast
import com.erank.yogappl.utils.extensions.txt
import com.erank.yogappl.utils.extensions.validator.assertation.BDateAssertion
import com.erank.yogappl.utils.extensions.validator.birthDatePicker
import com.erank.yogappl.utils.helpers.MyImagePicker
import com.erank.yogappl.utils.interfaces.ImagePickerCallback
import com.erank.yogappl.utils.runOnBackground
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterActivity : AppCompatActivity(), ImagePickerCallback {

    private lateinit var userValidator: Form
    private val myImagePicker by lazy { MyImagePicker(this) }
    private val progressDialog by lazy { ProgressDialog(this) }

    companion object {
        private val TAG = RegisterActivity::class.java.name
    }

    private var isEditingUser: Boolean = false

    @Inject
    lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        (application as App).getAppComponent().inject(this)

        cameraBtn.setOnClickListener { handleCameraBtn() }

        val currentUser = viewModel.currentUser ?:run {
            saveUser.setOnClickListener { saveUser() }
            initUserValidator()
            return
        }

        initUserValidatorForUpdate()

        isEditingUser = true
        fillData(currentUser)

        saveUser.also { it.setText(R.string.update_info) }
            .setOnClickListener { updateUser() }
    }

    private fun handleCameraBtn() {
        val canRemove = viewModel.canRemove
        myImagePicker.show(this, canRemove)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        toast("data not saved")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> if (isEditingUser) {
                toast(R.string.data_not_saved)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateUser() {
        val result = userValidator.validate()
        if (!result.success()) {
            toast(R.string.fields_incorrectly_filled)
            return
        }

        viewModel.currentUser?.apply {
            name = etName.txt
            about = etAbout.txt

            etDate.date?.let { bDate = it }
            spinnerUserLevel.enumValue?.let { level = it }

            viewModel.profileImage?.urls?.let {
                profileImageUrl = it.small
            }

        } ?: return

        progressDialog.show()
        runOnBackground({
            try {
                viewModel.updateCurrentUser()
            } catch (e: Exception) {
                withContext(Main) { onFail(e) }
            }
        }, this@RegisterActivity::onSuccess)
    }


    private fun onFail(e: Exception) {
        progressDialog.dismiss()
        alert(R.string.things_wasnt_going_as_planned, e.localizedMessage)
            .setPositiveButton(R.string.ok, null)
            .show()
        Log.d(TAG, e.localizedMessage)
    }

    private fun onSuccess() {
        if (isEditingUser) finish()
        else {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun fillData(user: User) {
        etEmail.hide()
        etPassword.hide()
        spinnerUserType.hide()

        Glide.with(this)
            .load(user.profileImageUrl)
            .placeholder(R.drawable.camera)
            .circleCrop()
            .into(cameraBtn)

        etName.setText(user.name)
        etAbout.setText(user.about)
        etDate.setDatePickerDate(user.bDate)
        spinnerUserLevel.setSelectedItem(user.level)
    }

    private fun initUserValidator() {
        userValidator = form {

            input(etName, "name") {
                isNotEmpty()
                length().atLeast(3).description(R.string.at_least3)
            }

            input(etEmail, "email") {
                isNotEmpty()
                isEmail()
            }

            input(etPassword, "pass") {
                isNotEmpty()
                length().atLeast(6).description(R.string.atLeast6)
            }

            birthDatePicker(etDate, "date") {
                BDateAssertion()
            }

            spinner(spinnerUserLevel, "level") {

            }
            spinner(spinnerUserType, "type") {

            }
        }
    }

    private fun initUserValidatorForUpdate() {
        userValidator = form {
            input(etName, "name") {
                isNotEmpty()
                length().atLeast(3).description(R.string.at_least3)
            }
            birthDatePicker(etDate, "date") {
                BDateAssertion()
            }
            spinner(spinnerUserLevel, "level") {

            }
        }
    }

    private fun saveUser() {
        val result = userValidator.validate()
        if (result.success().not()) {
            Log.d(TAG, "Invalid User info when saving")
            toast(R.string.invalid_data_check, Toast.LENGTH_LONG)
            return
        }

        val name = etName.txt
        val email = etEmail.txt
        val about = etAbout.txt
        val bDate = etDate.date!!
        val level = spinnerUserLevel.enumValue!!

        val selectedImageURL = viewModel.profileImage?.urls?.small

        val user = when (spinnerUserType.enumValue!!) {

            STUDENT -> User(name, email, bDate, level, about, selectedImageURL)

            TEACHER -> Teacher(name, email, bDate, level, about, selectedImageURL)
        }

        progressDialog.show()
        val pass = etPassword.txt
        runOnBackground({
            try {
                viewModel.createUser(user, pass)
            } catch (e: Exception) {
                withContext(Main) { onFail(e) }
            }
        }, this@RegisterActivity::onSuccess)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result code is RESULT_OK only if the user selects an Image
        myImagePicker.checkActivityResult(this, requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        myImagePicker.openCamIfPossible(this, requestCode, grantResults)
    }

    override fun onImageRemove() {
        cameraBtn.setImageResource(R.drawable.camera)
        viewModel.profileImage = null
    }

    override fun onSelectedImage(result: MyImagePicker.Result) {

        viewModel.profileImage = result

        with(result) {
            (uri ?: urls?.small ?: bitmap)?.let {
                Glide.with(cameraBtn)
                    .load(it)
                    .fallback(R.drawable.yoga_model)
                    .placeholder(R.drawable.yoga_model)
                    .circleCrop()
                    .into(cameraBtn)
            }
        }

    }
}

