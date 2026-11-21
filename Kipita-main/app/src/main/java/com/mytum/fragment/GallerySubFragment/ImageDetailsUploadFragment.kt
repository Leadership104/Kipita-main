package com.mytum.fragment.GallerySubFragment

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mytum.adapter.UploadImageAdapter
import com.mytum.model.ImageList.ImageListResponse
import com.mytum.model.ImageList.ImageUploadData
import com.mytum.model.ImageList.ImageUploadRequest
import com.mytum.model.ImageList.ImageUploadResponse
import com.mytum.proectUtil.ProjectUtil
import com.mytum.R
import com.mytum.client.connection.YelpFusionApi
import com.mytum.client.connection.YelpFusionApiFactory
import com.mytum.interfaces.ComplaintClickPressed
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class ImageDetailsUploadFragment  : AppCompatActivity(), ComplaintClickPressed {

    var customDialog: Dialog? = null
    var data = ImageUploadRequest()
    var tagList = ArrayList<ImageUploadData>()
//    var imageData: ImageView? = null
//    var publicCheckBox: CheckBox? = null
//    var privateCheckBox: CheckBox? = null
//    var modeData: String? = ""
//    var imageTag: EditText? = null
//    var imageName: EditText? = null
//    var listdataparentLayout: LinearLayout? = null
//    var dateLayout: LinearLayout? = null
//    var date: EditText? = null
//    var lati: EditText? = null
//    var long12: EditText? = null
    var lisData: RecyclerView? = null
    var submitBtn: Button? = null
    var failed_msg: TextView? = null
    var uploadAdapter: UploadImageAdapter? = null
    var backarrow: ImageView? = null


    public override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_details)
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

        Log.d("aaaaaaaaaaaaaaaaaaaa", "ImageDetailsUploadFragment")

        customDialog = Dialog(this!!)
        backarrow = findViewById(R.id.backarrow);
//        listdataparentLayout = findViewById(R.id.listdataparentLayout);
//        dateLayout = findViewById(R.id.dateLayout);
        lisData = findViewById(R.id.lisData);
//        imageData = findViewById(R.id.imageData)
//        imageTag = findViewById(R.id.imageTag)
        submitBtn = findViewById(R.id.submitBtn)
        failed_msg = findViewById(R.id.failed_msg)

//        publicCheckBox = findViewById(R.id.publicCheckBox)
//        privateCheckBox = findViewById(R.id.privateCheckBox)
//
//
//        imageName = findViewById(R.id.imageName)
//        date = findViewById(R.id.date)
//        lati = findViewById(R.id.lat)
//        long12 = findViewById(R.id.long12)
//
//        dateLayout!!.setVisibility(View.GONE)

        backarrow!!.setOnClickListener(View.OnClickListener { v: View? -> onFinish() })
        submitBtn!!.text = "Submit";
        submitBtn!!.setOnClickListener { view: View? ->
            if(submitBtn!!.text.toString().equals("Submit")){
                onImageUpload(data)
            }else{
                finish();
            }

        }

        for(temp in data.imageData){
            var imageUploadData = ImageUploadData()
            imageUploadData.message = "Image upload pending"
            imageUploadData.thumb = temp
            imageUploadData.status = "Pending"
            tagList.add(imageUploadData)
        }

        lisData!!.layoutManager = GridLayoutManager(this, 2)
        uploadAdapter = UploadImageAdapter(this, tagList, this@ImageDetailsUploadFragment)
        lisData!!.setAdapter(uploadAdapter)

    }


    fun onFinish() {
        ProjectUtil.setPhotoPath(this, "")
        ProjectUtil.setPhotoPath(this, null)
        finish()
    }

    fun onImageUpload(provider: ImageUploadRequest) {

        ProjectUtil.showDialog(customDialog, "Please wait...")
        try {
            var imageDataList = ArrayList<String>()
            var imageLatLngList = ArrayList<String>()
            var imageDateTimeList = ArrayList<String>()
            for(temp in provider.imageData) {
                var bitmap: Bitmap? = null
                var f = File(temp)

                var rotate = 0
                try {
                    var options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)

                    val matrix = Matrix()

                    val imageFile: File = File(temp)
                    val exif = ExifInterface(
                        imageFile.absolutePath
                    )
                    val orientation: Int = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                    }
                    matrix.postRotate(rotate.toFloat())
                    val rotatedBitmap: Bitmap = Bitmap.createBitmap(
                        bitmap!!, 0, 0, bitmap!!.getWidth(),
                        bitmap!!.getHeight(), matrix, true
                    )

                    imageDataList.add(ProjectUtil.getStringImage(rotatedBitmap));
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            for(temp in provider.location) {
                try {
                    imageLatLngList.add(temp);
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            for(temp in provider.createdDateTime) {
                try {
                    imageDateTimeList.add(temp);
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            var mode = "public"
            if(ProjectUtil.getPublicOrPrivate(this@ImageDetailsUploadFragment)){
                mode = "public"
            }else{
                mode = "private"
            }
            var imageUploadRequest = ImageUploadRequest()
            imageUploadRequest.imageData = imageDataList
            imageUploadRequest.location = imageLatLngList
            imageUploadRequest.createdDateTime = imageDateTimeList
            imageUploadRequest.userID = ProjectUtil.getUserId(this)
            imageUploadRequest.imageId = "" + System.currentTimeMillis();
            imageUploadRequest.imageName = "" + System.currentTimeMillis();
//            imageUploadRequest.imageTag = tagList.toString().replace("]", "").replace("[", "")
            imageUploadRequest.action = "add"
            imageUploadRequest.mode = mode
            imageUploadRequest.dateAdded = "" + System.currentTimeMillis();
            imageUploadRequest.imageLat = ProjectUtil.getLotitude(this)
            imageUploadRequest.imageLong = ProjectUtil.getLongitude(this)
            imageUploadRequest.currentLat = ProjectUtil.getCurrentLotitude(this)
            imageUploadRequest.currentLng = ProjectUtil.getCurrentLongitude(this)
            imageUploadRequest.timeZone = ProjectUtil.getCurrentTimeZone()

            val gson = Gson()
            var daa = gson.toJson(imageUploadRequest)

//            uploadFile(f, imageUploadRequest)

            val apiService = YelpFusionApiFactory.getApplication().create(
                YelpFusionApi::class.java
            )
            val getAllComplaint = apiService.imageUpload("application/json", imageUploadRequest)
            getAllComplaint.enqueue(object : Callback<ImageUploadResponse?> {
                override fun onResponse(
                    call: Call<ImageUploadResponse?>, response: Response<ImageUploadResponse?>
                ) {
                    var getAllData = response.body()
                    var handler = Handler()
                    handler.postDelayed(Runnable {
                        var shouldShowFailedMsg = false
                        getAllData!!.data.forEachIndexed { index, e ->
                            tagList.get(index).status = getAllData!!.data.get(index).status
                            tagList.get(index).message = getAllData!!.data.get(index).message
                            if(getAllData!!.data.get(index).status.equals("fail")){
                                shouldShowFailedMsg = true
                            }
                        }
                        if(shouldShowFailedMsg){
                            failed_msg?.visibility = View.VISIBLE
                        }else{
                            failed_msg?.visibility = View.GONE
                        }
                        submitBtn!!.text = "Ok";
                        uploadAdapter?.notifyDataSetChanged()
                        ProjectUtil.dismissDialog(customDialog)


//                        ProjectUtil.customToast(this@ImageDetailsUploadFragment, getAllData!!.message)
                        ProjectUtil.setPhotoPath(this@ImageDetailsUploadFragment, "")
                        ProjectUtil.setPhotoPath(this@ImageDetailsUploadFragment, null)
//                        finish()
                    }, 2000)
                }

                override fun onFailure(call: Call<ImageUploadResponse?>, t: Throwable) {
                    t.printStackTrace()
                    ProjectUtil.dismissDialog(customDialog)
                    ProjectUtil.customToast(this@ImageDetailsUploadFragment, t.toString())
                    ProjectUtil.setPhotoPath(this@ImageDetailsUploadFragment, "")

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            ProjectUtil.dismissDialog(customDialog)
            ProjectUtil.customToast(this@ImageDetailsUploadFragment, e.toString())
            ProjectUtil.setPhotoPath(this@ImageDetailsUploadFragment, "")
        }
    }


    override fun onClickEvent(postion: Int) {
//        tagList.remove(tagList.get(postion))
//        if(tagList.size > 0){
//            listdataparentLayout!!.setVisibility(View.VISIBLE)
//        }else{
//            listdataparentLayout!!.setVisibility(View.GONE)
//        }
//        tagAdapter!!.notifyDataSetChanged()
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
//        tagList.add(hashtag)
//        if(tagList.size > 0){
//            listdataparentLayout!!.setVisibility(View.VISIBLE)
//        }else{
//            listdataparentLayout!!.setVisibility(View.GONE)
//        }
//        tagAdapter!!.notifyDataSetChanged()
    }

    private fun uploadFile(file: File,  imageUploadRequest: ImageUploadRequest) {
//        val file = File(path)


        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            RequestBody.create("image/*".toMediaTypeOrNull(), file)
        )

//        // create RequestBody instance from file
//        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//
//        // MultipartBody.Part is used to send also the actual file name
//        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
//
//        // add another part within the multipart request
//        val descriptionString = ""
//        val description = descriptionString
//            .toRequestBody(okhttp3.MultipartBody.FORM)

        val apiService = YelpFusionApiFactory.getApplication().create(
            YelpFusionApi::class.java
        )
        val getAllComplaint = apiService.imageUploadMultiPart("application/json", imageUploadRequest, filePart)
        getAllComplaint.enqueue(object : Callback<ImageListResponse?> {
            override fun onResponse(
                call: Call<ImageListResponse?>, response: Response<ImageListResponse?>
            ) {
                var getAllData = response.body()
                var handler = Handler()
                handler.postDelayed(Runnable {
                    ProjectUtil.customToast(this@ImageDetailsUploadFragment, getAllData!!.message)
                    ProjectUtil.setPhotoPath(this@ImageDetailsUploadFragment, "")
                    ProjectUtil.setPhotoPath(this@ImageDetailsUploadFragment, "")
                    finish()
                }, 2000)
            }

            override fun onFailure(call: Call<ImageListResponse?>, t: Throwable) {
                t.printStackTrace()
                ProjectUtil.dismissDialog(customDialog)
                ProjectUtil.customToast(this@ImageDetailsUploadFragment, t.toString())
                ProjectUtil.setPhotoPath(this@ImageDetailsUploadFragment, "")

            }
        })


    }

    override fun onMoreInfo(postion: Int) {
    }



}