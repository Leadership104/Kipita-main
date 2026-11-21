package com.mytum

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mytum.adapter.TagAdapter
import com.mytum.interfaces.ComplaintClickPressed
import com.mytum.model.ImageList.ImageArrayListList
import com.mytum.proectUtil.OnSwipeTouchListener
import com.mytum.proectUtil.ProjectUtil
import com.squareup.picasso.Picasso

class ImageViewerActivity1 : AppCompatActivity(), ComplaintClickPressed, SwipeGestureListener {
    private val mScaleGestureDetector: ScaleGestureDetector? = null
    private val mScaleFactor = 1.0f
    var data = ArrayList<ImageArrayListList>()
    private var positionIndex = 0
    var updatedBy: TextView? = null
//    var imageName: TextView? = null
    var updatedDate: TextView? = null
    var imageView: ImageView? = null
    var backarrow: ImageView? = null

    var listtagparentLayout: LinearLayout? = null
    var iv_close_tag: ImageView? = null
    var image_swipe_up: ImageView? = null
    var lisTagData: RecyclerView? = null
    var tagList = ArrayList<String>()
    var tagAdapter: TagAdapter? = null
    var onSwipeTouchListener: OnSwipeTouchListener? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        val yourView = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = resources.getColor(R.color.blue_bottom)
            if (yourView != null) {
                yourView.systemUiVisibility = 0
            }
        }
        setContentView(R.layout.activity_image_viewer1)
        var bundle = intent.getBundleExtra("data")
        data = bundle!!.getParcelableArrayList("allData")!!
        positionIndex = bundle.getInt("position", 0)
        Log.i(TAG, "aaaaaaa onCreate: "+positionIndex)

        if (data == null) {
            finish()
        }
//        imageName = findViewById(R.id.imageName)
        updatedBy = findViewById(R.id.updatedBy)
        updatedDate = findViewById(R.id.updatedDate)
        imageView = findViewById(R.id.viewer_imageview)
        backarrow = findViewById(R.id.backarrow)
        backarrow!!.setOnClickListener(View.OnClickListener { v: View? -> finish() })

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.activity_image_viewer1_bottom_sheet)
        listtagparentLayout = bottomSheetDialog.findViewById(R.id.listtagparentLayout);
        lisTagData = bottomSheetDialog.findViewById(R.id.lisTagData);

        iv_close_tag = bottomSheetDialog.findViewById(R.id.iv_close_tag);
        image_swipe_up = findViewById(R.id.image_swipe_up);


        image_swipe_up!!.setOnClickListener(View.OnClickListener { v: View? ->
            bottomSheetDialog.show()

        })
        iv_close_tag!!.setOnClickListener(View.OnClickListener { v: View? ->
            bottomSheetDialog.dismiss()
        })
        onSwipeTouchListener = OnSwipeTouchListener(this, imageView as View, this)
        updateImageData(0)

    }

    fun updateImageData(type: Int) {
//        var position = 0
        if(type == 0){
            positionIndex
        }else if(type == 1){
//            position = positionIndex-1
            positionIndex -= 1
            Log.d("aaaaaaaa minus", ""+positionIndex)
        }else if(type == 2){
//            position = positionIndex+1
            positionIndex += 1
            Log.d("aaaaaaaa plus", ""+positionIndex)
        }
        if(positionIndex >= 0 && positionIndex < data.size) {
            Picasso.get().load(data.get(positionIndex).path).into(imageView)
            updatedDate!!.setText(data.get(positionIndex).dateAdded)
            updatedBy!!.setText(data.get(positionIndex).uploadedBy)
            tagList.clear()
            tagList.addAll(data.get(positionIndex).tags)
            lisTagData!!.layoutManager = GridLayoutManager(this, 4)
            tagAdapter = TagAdapter(this, tagList, this@ImageViewerActivity1, true)
            lisTagData!!.setAdapter(tagAdapter)
        }else{
            ProjectUtil.customToast(this@ImageViewerActivity1, "NO Image to swipe")
        }
    }

    override fun onClickEvent(postion: Int) {
        ///call public fragment with tag name to filter the data

        val returnIntent = Intent()
        returnIntent.putExtra("result", tagList.get(postion))

        setResult(RESULT_OK, returnIntent)
        finish()

//        supportFragmentManager
//            .beginTransaction()
//            .addToBackStack("ABC")
//            .replace(R.id.fragment_container, GalleryFragment())
//            .commit()
    }

    override fun onCallEvent(postion: Int) {

    }

    override fun onSiteUrl(postion: Int) {

    }

    override fun onImageClick(postion: Int) {

    }

    override fun onAddressClick(postion: Int) {

    }

    override fun onMoreInfo(postion: Int) {
    }


    companion object {
        private const val TAG = "ImageViewerActivity"
        const val EXTRA_PATH = "path"
    }

    class MyBottomSheetDialogFragment(
        _tagList: ArrayList<String>,
        _ctx: Context
    ) : BottomSheetDialogFragment(), ComplaintClickPressed {

        val tagList = _tagList
        val ctx = _ctx

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.activity_image_viewer1_bottom_sheet, container, false)

            var lisTagData = view.findViewById<RecyclerView>(R.id.lisTagData);
            var iv_close_tag = view.findViewById<ImageView>(R.id.iv_close_tag);
            lisTagData!!.layoutManager = GridLayoutManager(ctx, 4)

            val tagAdapter = TagAdapter(ctx, tagList, this@MyBottomSheetDialogFragment, true)
            lisTagData!!.setAdapter(tagAdapter)

            return view
        }
        override fun onClickEvent(postion: Int) {
            ///call public fragment with tag name to filter the data
//            supportFragmentManager
//                .beginTransaction()
//                .addToBackStack("ABC")
//                .replace(R.id.fragment_container, GalleryFragment())
//                .commit()
        }

        override fun onCallEvent(postion: Int) {

        }

        override fun onSiteUrl(postion: Int) {

        }

        override fun onImageClick(postion: Int) {

        }

        override fun onAddressClick(postion: Int) {

        }

        override fun onMoreInfo(postion: Int) {
        }
    }


    override fun swipeRight() {
        updateImageData(1)
//        Toast.makeText(this@ImageViewerActivity1, "Swiped Right 1", Toast.LENGTH_SHORT).show()
    }

    override fun swipeLeft() {
        updateImageData(2)
//        Toast.makeText(this@ImageViewerActivity1, "Swiped Left 2", Toast.LENGTH_SHORT).show()
    }


}