package com.erank.yogappl.ui.activities.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.data.enums.TextFieldValidStates
import com.erank.yogappl.data.models.Teacher
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.models.User.Type.STUDENT
import com.erank.yogappl.data.models.User.Type.TEACHER
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.extensions.alert
import com.erank.yogappl.utils.extensions.setTextChangedListener
import com.erank.yogappl.utils.extensions.toast
import com.erank.yogappl.utils.extensions.txt
import com.erank.yogappl.utils.helpers.MyImagePicker
import com.erank.yogappl.utils.helpers.UserValidator
import com.erank.yogappl.utils.helpers.UserValidator.Fields
import com.erank.yogappl.utils.interfaces.ImagePickerCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import kotlinx.android.synthetic.main.activity_register.*
import javax.inject.Inject

class RegisterActivity : AppCompatActivity(), UserTaskCallback, ImagePickerCallback {

    private lateinit var userValidator: UserValidator
    private val myImagePicker by lazy { MyImagePicker(this) }

    private val progressLayout by lazy { llProgressBar }

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

        val currentUser = viewModel.currentUser
        if (currentUser == null) {
            saveUser.setOnClickListener { saveUser() }
            initUserValidator()
            return
        }

        isEditingUser = true
        fillData(currentUser)

        saveUser.also { it.text = "Update information" }
            .setOnClickListener { updateUser() }

        initUserValidatorForUpdate()

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
                toast("data not saved")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateUser() {
        if (userValidator.isNotDataValid) {
            toast("one or more of the fields are incorrectly filled")
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

        progressLayout.visibility = View.VISIBLE
        viewModel.updateCurrentUser(this)
    }

    override fun onSuccessFetchingUser(user: User?) {
//        toast("${user.name}'s info was updated")
        if (isEditingUser) finish()
        else {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onFailedFetchingUser(e: Exception) {
        progressLayout.visibility = GONE
        alert("Things wasn't going as planned...", e.localizedMessage)
            .setPositiveButton("ok", null)
            .show()
        Log.d(TAG, e.localizedMessage)
    }

    private fun fillData(user: User) {
        etEmail.visibility = GONE
        etPassword.visibility = GONE
        spinnerUserType.visibility = GONE

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
        userValidator = UserValidator().apply {

            val etName = etName
            etName.setTextChangedListener {
                etName.error = validateName(it).errorMsg
            }

            val etEmail = etEmail
            etEmail.setTextChangedListener {
                etEmail.error = validateEmail(it).errorMsg
            }

            val etPassword = etPassword
            etPassword.setTextChangedListener {
                etPassword.error = validatePassword(it).errorMsg
            }

            etDate.setOnDateSetListener { validateBDate(it) }

            spinnerUserLevel.setOnItemSelectedListener { validateLevel(it) }
            spinnerUserType.setOnItemSelectedListener { validateType(it) }
        }
    }

    private fun initUserValidatorForUpdate() {
        userValidator = UserValidator(
            TextFieldValidStates.VALID,
            Fields.NAME, Fields.DATE, Fields.LEVEL
        )

        etName.setTextChangedListener {
            etName.error = userValidator.validateName(it).errorMsg
        }

        etDate.setOnDateSetListener { userValidator.validateBDate(it) }

        spinnerUserLevel.setOnItemSelectedListener { userValidator.validateLevel(it) }
    }

    private fun saveUser() {
        if (userValidator.isNotDataValid) {
            Log.d(TAG, "Invalid User info when saving")
            toast("Invalid data , please check", Toast.LENGTH_LONG)
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

        progressLayout.isVisible = true
        val pass = etPassword.txt
        viewModel.createUser(user, pass, this)
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