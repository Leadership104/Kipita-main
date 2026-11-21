package com.mytum

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mytum.model.Login.DeleteRequest
import com.mytum.model.Registration.RegistrationResponse
import com.mytum.network.APIService
import com.mytum.network.RetrofitClient
import com.mytum.proectUtil.ProjectUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    var logout: Button? = null
    var userName: TextView? = null
    var zipcode: TextView? = null
    var back: ImageView? = null
    var edit: TextView? = null
    var updateProfileBtn: Button? = null
    var notificationBtn: Button? = null
    var changePasswordBtn: Button? = null
    var et_mobile: TextView? = null
    var profileNameText: TextView? = null
    var mailId: TextView? = null
    var text_public_private: TextView? = null
    var text_delete_account: TextView? = null
    var switch_image_mode: Switch? = null
    var dialog: Dialog? = null

    var gsc: GoogleSignInClient? = null
    var gso: GoogleSignInOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val window = window
        val yourView = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = resources.getColor(R.color.blue)
            if (yourView != null) {
                yourView.systemUiVisibility = 0
            }
        }
        dialog = Dialog(this)
        gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso!!)

        back = findViewById<View>(R.id.back) as ImageView
        updateProfileBtn = findViewById<View>(R.id.updateProfileBtn) as Button
        notificationBtn = findViewById<View>(R.id.notificationBtn) as Button
        changePasswordBtn = findViewById<View>(R.id.changePasswordBtn) as Button
        logout = findViewById<View>(R.id.logoutBtn) as Button
        userName = findViewById<View>(R.id.userName) as TextView
        zipcode = findViewById<View>(R.id.zipcode) as TextView
        profileNameText = findViewById<View>(R.id.profileNameText) as TextView
        edit = findViewById<View>(R.id.edit) as TextView
        et_mobile = findViewById<View>(R.id.et_mobile) as TextView
        mailId = findViewById<View>(R.id.mailId) as TextView
        text_public_private = findViewById<View>(R.id.text_public_private) as TextView
        switch_image_mode = findViewById<View>(R.id.switch_image_mode) as Switch
        text_delete_account = findViewById<View>(R.id.text_delete_account) as TextView

        switch_image_mode?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                text_public_private?.text = "Upload All Images as Public"
                ProjectUtil.setPublicOrPrivate(this@ProfileActivity, true)
            } else {
                text_public_private?.text = "Upload All Images as Private"
                ProjectUtil.setPublicOrPrivate(this@ProfileActivity, false)
            }
        }

        logout!!.setOnClickListener { view: View? ->
            signOut()
        }
        updateProfileBtn!!.setOnClickListener { view: View? ->
            openUpdateProfile()
        }
        notificationBtn!!.setOnClickListener { view: View? ->
            openNotificationSetting()
        }
        changePasswordBtn!!.setOnClickListener { view: View? ->
            openUpdatePassword()
        }
        back!!.setOnClickListener { view: View? ->
           finish()
        }

        text_delete_account!!.setOnClickListener { view: View? ->
            deleteAccount()
        }

        val shortName = ProjectUtil.getFirstName(this@ProfileActivity).first().toString()+" " +ProjectUtil.getLastName(this@ProfileActivity).first().toString()
        profileNameText!!.text = shortName

        ProjectUtil.getAddress(
            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext),
            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext), zipcode!!, this
        )
        mailId!!.setText(ProjectUtil.getMailId(this@ProfileActivity));
        userName!!.setText(ProjectUtil.getSirName(this@ProfileActivity) +" "+ProjectUtil.getFirstName(this@ProfileActivity)+" " +ProjectUtil.getLastName(this@ProfileActivity));
        et_mobile!!.setText(ProjectUtil.getMobileNumber(this@ProfileActivity));
        switch_image_mode?.isChecked = ProjectUtil.getPublicOrPrivate(this@ProfileActivity)

    }

    override fun onResume() {
        super.onResume()
        val shortName = ProjectUtil.getFirstName(this@ProfileActivity).first().toString()+" " +ProjectUtil.getLastName(this@ProfileActivity).first().toString()
        profileNameText!!.text = shortName

        ProjectUtil.getAddress(
            ProjectUtil.getLotitude(MyApplication.getInstance().applicationContext),
            ProjectUtil.getLongitude(MyApplication.getInstance().applicationContext), zipcode!!, this
        )
        mailId!!.setText(ProjectUtil.getMailId(this@ProfileActivity));
        userName!!.setText(ProjectUtil.getSirName(this@ProfileActivity) +" "+ProjectUtil.getFirstName(this@ProfileActivity)+" " +ProjectUtil.getLastName(this@ProfileActivity));
        et_mobile!!.setText(ProjectUtil.getMobileNumber(this@ProfileActivity));
        switch_image_mode?.isChecked = ProjectUtil.getPublicOrPrivate(this@ProfileActivity)

    }

    fun signOut() {

        try {
            gsc!!.signOut().addOnCompleteListener {

                ProjectUtil.setFirstName(this@ProfileActivity, "")
                ProjectUtil.setLasttName(this@ProfileActivity, "")
                ProjectUtil.setSirName(this@ProfileActivity, "")
                ProjectUtil.setMobileNumber(this@ProfileActivity,"")
                ProjectUtil.setMailId(this@ProfileActivity, "")
                ProjectUtil.setZipCode(this@ProfileActivity, "")

                var i = Intent(this@ProfileActivity, LoginActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
                startActivity(i)
                finish()
            }
            ProjectUtil.setSession(this@ProfileActivity, "0")
        } catch (e: Exception) {
            ProjectUtil.setFirstName(this@ProfileActivity, "")
            ProjectUtil.setLasttName(this@ProfileActivity, "")
            ProjectUtil.setSirName(this@ProfileActivity, "")
            ProjectUtil.setMobileNumber(this@ProfileActivity,"")
            ProjectUtil.setMailId(this@ProfileActivity, "")
            ProjectUtil.setZipCode(this@ProfileActivity, "")
            var i = Intent(this@ProfileActivity, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
            startActivity(i)
            finish()
            ProjectUtil.setSession(this@ProfileActivity, "0")
        }
    }

    fun openUpdateProfile() {
        startActivity(Intent(this@ProfileActivity, UpdateProfileActivity::class.java))
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)

    }

    fun openUpdatePassword() {
        startActivity(Intent(this@ProfileActivity, UpdatePasswordActivity::class.java))
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)

    }

    fun openNotificationSetting() {
        startActivity(Intent(this@ProfileActivity, NotificationSettingsActivity::class.java))
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)

    }

    fun deleteAccount() {
        showDialog()
    }

    private fun showDialog() {
        var deleteRequest = DeleteRequest();
        deleteRequest.userId = ProjectUtil.getUserId(this@ProfileActivity)
        val dialog = Dialog(this@ProfileActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_layout)
        val checkBox = dialog.findViewById(R.id.deleteCheckBox) as CheckBox
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checkBox.text = "If you would like to maintain your contribution to the public galaxy please press continue."
                deleteRequest.deletePublic = "1";
            }else{
                checkBox.text = "All images previously submitted will be permanently deleted and will no longer be available on this platform."
                deleteRequest.deletePublic = "0";
            }
        }
        val delete_cancel = dialog.findViewById(R.id.delete_cancel) as TextView
        val delete_continue = dialog.findViewById(R.id.delete_continue) as TextView
        delete_cancel.setOnClickListener {
            dialog.dismiss()
        }
        delete_continue.setOnClickListener {
            dialog.dismiss()
            registerRequest(deleteRequest)
        }
        dialog.show()

    }

    protected fun registerRequest(deleteRequest: DeleteRequest?) {
        ProjectUtil.showDialog(
            dialog,
            "Please wait ..."
        )
        val apiService = RetrofitClient.getClient().create(APIService::class.java)
        val getAllComplaint = apiService.deleteAccount("application/json", deleteRequest)
        getAllComplaint.enqueue(
            object : Callback<RegistrationResponse?> {
                override fun onResponse(
                    call: Call<RegistrationResponse?>,
                    response: Response<RegistrationResponse?>
                ) {
                    val getAllData = response.body()
                    ProjectUtil.dismissDialog(dialog)
                    if (getAllData == null) {
                        ProjectUtil.customToast(this@ProfileActivity, "Internal Server error")
                        return
                    }
                    val responseCode = response.code()
                    val handler = Handler()
                    handler.postDelayed({
                        if (getAllData.status.equals("success", ignoreCase = true)) {
                            ProjectUtil.customToast(this@ProfileActivity, getAllData.message)
                            ProjectUtil.dismissDialog(dialog)
                            signOut();
                            finish()
                        } else {
                            ProjectUtil.dismissDialog(dialog)
                            ProjectUtil.customToast(this@ProfileActivity, getAllData.message)
                        }
                    }, 2000)
                }

                override fun onFailure(call: Call<RegistrationResponse?>, t: Throwable) {
                    t.printStackTrace()
                    ProjectUtil.customToast(this@ProfileActivity, t.toString())
                    ProjectUtil.dismissDialog(dialog)
                }
            })
    }

}