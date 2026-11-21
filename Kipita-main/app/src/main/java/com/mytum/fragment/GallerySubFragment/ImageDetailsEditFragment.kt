package com.mytum.fragment.GallerySubFragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mytum.R
import com.mytum.adapter.TagAdapter
import com.mytum.client.connection.YelpFusionApi
import com.mytum.client.connection.YelpFusionApiFactory
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.ImageList.ImageArrayListList
import com.mytum.model.ImageList.ImageListResponse
import com.mytum.model.ImageList.ImageUploadRequest
import com.mytum.proectUtil.ProjectUtil
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ImageDetailsEditFragment : AppCompatActivity(), ComplaintClickPressed {

    var data = ImageArrayListList()
    var tagList = ArrayList<String>()
    var imageData: ImageView? = null
    var publicCheckBox: CheckBox? = null
    var privateCheckBox: CheckBox? = null
    var modeData: String? = ""
    var imageTag: EditText? = null
//    var imageName: EditText? = null
    var listdataparentLayout: LinearLayout? = null
    var date: EditText? = null
//    var lati: EditText? = null
//    var long12: EditText? = null
    var lisData: RecyclerView? = null
    var submitBtn: Button? = null
    var tagAdapter: TagAdapter? = null
    var backarrow: ImageView? = null
    var dateLayout: LinearLayout? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_details_edit)
        val window = window
        val yourView = window.decorView
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = resources.getColor(R.color.blue)
            if (yourView != null) {
                yourView.systemUiVisibility = 0
            }
        }

        var bundle = intent.getBundleExtra("data")
        data = bundle!!.getParcelable("data")!!

        if (data == null) {
            finish()
        }
        backarrow = findViewById(R.id.backarrow);
        listdataparentLayout = findViewById(R.id.listdataparentLayout);
        lisData = findViewById(R.id.lisData);
        imageData = findViewById(R.id.imageData)
        imageTag = findViewById(R.id.imageTag)
        submitBtn = findViewById(R.id.submitBtn)
        publicCheckBox = findViewById(R.id.publicCheckBox)
        privateCheckBox = findViewById(R.id.privateCheckBox)
//        imageName = findViewById(R.id.imageName)
        date = findViewById(R.id.date)
//        lati = findViewById(R.id.lat)
        dateLayout = findViewById(R.id.dateLayout);
//        long12 = findViewById(R.id.long12)
        backarrow!!.setOnClickListener(View.OnClickListener { v: View? -> finish() })
        imageTag!!.setImeOptions(EditorInfo.IME_ACTION_DONE);
        try {
            Picasso.get()
                .load(data.getPath())
                .placeholder(R.drawable.splash)
                .into(imageData)
        } catch (e: Exception) {

        }

        submitBtn!!.setOnClickListener { view: View? ->
            onImageUpdate()
        }



        publicCheckBox!!.setOnClickListener(View.OnClickListener { view ->
            if ((view as CompoundButton).isChecked) {
                privateCheckBox!!.isChecked = false
                modeData = "public"
            } else {
                println("Un-Checked")
            }
        })


        privateCheckBox!!.setOnClickListener(View.OnClickListener { view ->
            if ((view as CompoundButton).isChecked) {
                publicCheckBox!!.isChecked = false
                modeData = "private"
            } else {
                println("Un-Checked")
            }
        })


        if (data.mode.equals("public")) {
            publicCheckBox!!.isChecked = true
            modeData = "public"
        } else {
            privateCheckBox!!.isChecked = true
            modeData = "private"
        }

        dateLayout!!.setVisibility(View.GONE)

//        imageName!!.setText(data.name)
//        mode!!.setText(data.mode)
//        lati!!.setText(data.lat)
        date!!.setText(data.dateAdded)
//        long12!!.setText(data.lng)

        tagList.clear()
        tagList.addAll(data.tags)

        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lisData!!.layoutManager = layoutManager
        tagAdapter = TagAdapter(this, tagList, this@ImageDetailsEditFragment, false)
        lisData!!.setAdapter(tagAdapter)

        imageTag!!.setOnEditorActionListener(TextView.OnEditorActionListener { view, actionId, event ->
//            ProjectUtil.customToast(this@ImageDetailsEditFragment, "Working" + actionId)

            if (actionId == EditorInfo.IME_ACTION_DONE || event.action == KeyEvent.ACTION_DOWN || event.action == KeyEvent.KEYCODE_ENTER) {
                tagList.add(view.text.toString())
                tagAdapter?.notifyDataSetChanged()
                view.text = ""
                val imm: InputMethodManager = this@ImageDetailsEditFragment.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
                return@OnEditorActionListener true
            }
            false
        })
    }


    fun onImageUpdate() {

        try {
            val imageUploadRequest = ImageUploadRequest()

            imageUploadRequest.userID = ProjectUtil.getUserId(this@ImageDetailsEditFragment)
            imageUploadRequest.imageId = data.imgID
            imageUploadRequest.mode = modeData
            imageUploadRequest.action = "edit"
            imageUploadRequest.imageTag = tagList.toString().replace("]", "").replace("[", "")
            imageUploadRequest.imageName = data.name//imageName!!.text.toString();
            imageUploadRequest.imageTag = imageTag!!.text.toString()

            val apiService = YelpFusionApiFactory.getApplication().create(
                YelpFusionApi::class.java
            )
            val getAllComplaint = apiService.imageEdit("application/json", imageUploadRequest)
            getAllComplaint.enqueue(object : Callback<ImageListResponse?> {
                override fun onResponse(
                    call: Call<ImageListResponse?>, response: Response<ImageListResponse?>
                ) {
                    var getAllData = response.body()
                    var handler = Handler()
                    handler.postDelayed(Runnable {
                        finish()
                        ProjectUtil.setPhotoPath(this@ImageDetailsEditFragment, "")

                    }, 2000)
                }

                override fun onFailure(call: Call<ImageListResponse?>, t: Throwable) {
                    t.printStackTrace()
                    ProjectUtil.customToast(this@ImageDetailsEditFragment, t.toString())
                    ProjectUtil.setPhotoPath(this@ImageDetailsEditFragment, "")

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.customToast(this@ImageDetailsEditFragment, e.toString())
            ProjectUtil.setPhotoPath(this@ImageDetailsEditFragment, "")
        }
    }

    override fun onClickEvent(postion: Int) {
        tagList.remove(tagList.get(postion))
        if (tagList.size > 0) {
            listdataparentLayout!!.setVisibility(View.VISIBLE)
        } else {
            listdataparentLayout!!.setVisibility(View.GONE)
        }
        tagAdapter!!.notifyDataSetChanged()
    }

    override fun onCallEvent(postion: Int) {

    }

    override fun onSiteUrl(postion: Int) {

    }

    override fun onImageClick(postion: Int) {

    }

    override fun onAddressClick(postion: Int) {

    }


    fun addHashTag(hashtag: String) {
        tagList.add(hashtag)
        if (tagList.size > 0) {
            listdataparentLayout!!.setVisibility(View.VISIBLE)
        } else {
            listdataparentLayout!!.setVisibility(View.GONE)
        }
        tagAdapter!!.notifyDataSetChanged()
    }

    override fun onMoreInfo(postion: Int) {
    }

    private fun calculateSpanCount(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val itemWidth = resources.getDimensionPixelSize(R.dimen._100sdp) // Define your item width in dimens.xml
        return screenWidth / itemWidth
    }
}