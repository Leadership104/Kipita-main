package com.mytum.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.mytum.MainActivity
import com.mytum.R
import com.mytum.adapter.CreateGroupAdapter
import com.mytum.model.CreateGroupeItemObject
import com.mytum.model.SelectGroupeMemberItemObject
import com.mytum.proectUtil.ProjectUtil
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray

private var selectDataList: ArrayList<SelectGroupeMemberItemObject> = ArrayList()

class InviteMembersFragment : Fragment() {
    var dataList: ArrayList<SelectGroupeMemberItemObject> = ArrayList()
    var search_Bar: EditText? = null
    var recyclerView: RecyclerView? = null
    private var dogImg: ImageView? = null
    var parentLayout: CardView? = null
    var progressBar: ProgressBar? = null
    var categoryAdapter: InviteMembersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_invite_members, container, false)
        selectDataList = ArrayList()
        (activity as MainActivity).showMainMenu()
        parentLayout = view.findViewById(R.id.parentLayout)
        dogImg = view.findViewById(R.id.dog_img)
        progressBar = view.findViewById(R.id.progressBar)
        search_Bar = view.findViewById(R.id.searchBar)
        recyclerView = view.findViewById(R.id.recyclerView_reg)
        val name = arguments?.getString("name")
        val dis = arguments?.getString("dis")
        val type = arguments?.getString("type")
        val qId = arguments?.getString("id")
        val admin = arguments?.getBoolean("admin")
        val imgAdd = view.findViewById<ImageView>(R.id.img_add)
        val imgMail = view.findViewById<ImageView>(R.id.img_mail)
        val imgInsta = view.findViewById<ImageView>(R.id.img_insta)
        val imgFacebook = view.findViewById<ImageView>(R.id.img_facebook)
        imgMail.setOnClickListener {
            popupWindowInfo()
        }
        imgFacebook.setOnClickListener {
//            {"id":"3763247290661227","name":"Bablu Rao","email":"babluraog@gmail.com"}
            val accessToken = AccessToken.getCurrentAccessToken()
            if (accessToken != null && !accessToken.isExpired) {
                val request = GraphRequest.newMeRequest(
                    accessToken
                ) { `object_`, response_ ->
                    // Insert your code here
                    val data = response_!!.jsonObject
                    if (data != null) {
                        val friends = data.getJSONArray("data")
                        for (i in 0 until friends.length()) {
                            val friend = friends.getJSONObject(i)
                            val friendId = friend.getString("id")
                            val friendName = friend.getString("name")
                            // Handle friend data here
                            Log.d("Friend", "ID: $friendId, Name: $friendName")
                        }
                    } else {
                        Log.d("GraphRequest", "No friends data received.")
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "friends")
                request.parameters = parameters
                request.executeAsync()
            } else {
                Log.d("AccessToken", "Access Token is not available or expired.")
            }
        }
        val checkboxAll = view.findViewById<AppCompatCheckBox>(R.id.checkbox_all)
        checkboxAll.setOnClickListener(View.OnClickListener {
            if (checkboxAll.isChecked) {
                categoryAdapter!!.selectAll()
            } else {
                categoryAdapter!!.unSelectAll()
            }
        })
        imgAdd.setOnClickListener {
            try {
                if (!arguments?.getString("list").equals("")) {
                    val jsonArray = JSONArray(arguments?.getString("list"))
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject1 = jsonArray.getJSONObject(i)
                        val personItemObject = SelectGroupeMemberItemObject()
                        personItemObject.phone =
                            jsonObject1.getString("phone")
                        personItemObject.firstName =
                            jsonObject1.getString("firstName")
                        personItemObject.email =
                            jsonObject1.getString("email")
                        personItemObject.lastName =
                            jsonObject1.getString("lastName")
                        selectDataList.add(personItemObject)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val objectMapper = ObjectMapper()
            val jsonString = objectMapper.writeValueAsString(selectDataList)
            val bundles = Bundle()
            bundles.putString("list", jsonString)
            bundles.putString("title", name)
            bundles.putString("name", name)
            bundles.putString("dis", dis)
            bundles.putString("id", qId)
            admin?.let { it1 -> bundles.putBoolean("admin", it1) }
            if (type.equals("0")) {
                loadFragmentInner(CreateGroupFragment(), bundles)
            } else {
                loadFragmentInner(MembersManageFragment(), bundles)
            }
        }
        return view
    }

    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams", "NotifyDataSetChanged")
    private fun popupWindowInfo() {
        try {
            val inflater =
                requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(R.layout.popup_add_contact, null)
            val pw = PopupWindow(
                layout,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                true
            )
            pw?.animationStyle = R.style.pw_animation
            pw?.showAtLocation(layout, Gravity.BOTTOM, 0, 0)
            val image_close = layout.findViewById<ImageView>(R.id.image_top)
            val btn_add = layout.findViewById<Button>(R.id.btn_add)
            val et_fname = layout.findViewById<EditText>(R.id.et_fname)
            val et_lname = layout.findViewById<EditText>(R.id.et_lname)
            val et_mobile = layout.findViewById<EditText>(R.id.et_mobile)
            val et_mail = layout.findViewById<EditText>(R.id.et_mail)

            btn_add.setOnClickListener { v: View? ->
                if (et_fname.text.toString().isNotEmpty()) {
                    if (et_lname.text.toString().isNotEmpty()) {
                        if (et_mobile.text.toString().isNotEmpty()) {
                            if (et_mail.text.toString().isNotEmpty()) {
                                recyclerView!!.removeAllViewsInLayout()
                                val personItemObject = SelectGroupeMemberItemObject()
                                personItemObject.firstName = et_fname.text.toString()
                                personItemObject.lastName = et_lname.text.toString()
                                personItemObject.phone = et_mobile.text.toString().replace(" ", "")
                                personItemObject.email = et_mail.text.toString()
                                personItemObject.isChecked = true
                                dataList.add(personItemObject)
                                selectDataList.add(personItemObject)
                                categoryAdapter?.run {
                                    notifyDataSetChanged(); // notify changed
                                }
                                pw.dismiss()
                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    "Enter email",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {
                            Toast.makeText(requireActivity(), "Enter mobile no", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Enter last name", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(requireActivity(), "Enter first name", Toast.LENGTH_SHORT).show()
                }
            }
            image_close.setOnClickListener { v: View? -> pw.dismiss() }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchContacts(contentResolver: ContentResolver) {
//        val contactList = mutableListOf<String>()
        scope.launch {
            try {
                progressBar!!.isVisible = true
                dataList.clear()
                var phoneNumber: String? = ""
                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
                if (cursor != null && cursor.count > 0) {
                    while (cursor.moveToNext()) {
                        var mail = ""
                        val id =
                            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val name =
                            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                        // Fetch email address
                        val emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                            arrayOf(id),
                            null
                        )
                        while (emailCursor?.moveToNext() == true) {
                            mail =
                                emailCursor.getString(
                                    emailCursor.getColumnIndexOrThrow(
                                        ContactsContract.CommonDataKinds.Email.DATA
                                    )
                                )
                            val personItemObject = SelectGroupeMemberItemObject()
                            personItemObject.firstName = name
                            personItemObject.lastName = name
                            personItemObject.phone = phoneNumber?.replace(" ", "")
                            personItemObject.email = mail
                            personItemObject.isChecked = false
                            personItemObject.contactId = id.toInt()
                            dataList.add(personItemObject)
                        }
                        emailCursor?.close()
                        // Fetch phone number
                        val phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )

                        while (phoneCursor?.moveToNext() == true) {
                            phoneNumber =
                                phoneCursor.getString(
                                    phoneCursor.getColumnIndexOrThrow(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER
                                    )
                                )
                            val personItemObject = SelectGroupeMemberItemObject()
                            personItemObject.firstName = name
                            personItemObject.lastName = name
                            personItemObject.phone = phoneNumber?.replace(" ", "")
                            personItemObject.email = mail
                            personItemObject.isChecked = false
                            personItemObject.contactId = id.toInt()
                            dataList.add(personItemObject)
                        }
                        phoneCursor?.close()
                    }
                    cursor.close()
                }
                Log.d("aaaaaaaaaaaaaaaaaaaaaaa", "section3")
                dataList =
                    dataList.distinctBy { it.phone } as ArrayList<SelectGroupeMemberItemObject>
                recyclerView!!.visibility = View.VISIBLE
                recyclerView!!.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                categoryAdapter = InviteMembersAdapter(requireActivity(), dataList)
                recyclerView!!.adapter = categoryAdapter
                Log.d("aaaaaaaaaaaaaaaaaaaaaaa", "section4")
            } catch (e: Exception) {
                // Handle error
                Toast.makeText(requireContext(), "Error loading contacts", Toast.LENGTH_SHORT)
                    .show()
            } finally {
                // Hide progress bar
                progressBar!!.isVisible = false
            }
        }
//        ProjectUtil.dismissDialog(customDialog)
//        val membersManageFragment = MembersManageFragment()
//        membersManageFragment.hideProgressBar()
        // Use or display the contacts list
//        contactList.forEach { contact ->
//            println(contact)
//        }

    }

    class InviteMembersAdapter(
        activity: FragmentActivity?,
        allParticipant: java.util.ArrayList<SelectGroupeMemberItemObject>?
    ) :
        RecyclerView.Adapter<InviteMembersAdapter.ComplaintHolder>() {
        private val mContext: Context? = activity
        private var allParticipants = allParticipant
        private var isSelectedAll: Boolean = false

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintHolder {
            return ComplaintHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_invite_members, parent, false)
            )
        }

        override fun onBindViewHolder(
            holder: ComplaintHolder,
            @SuppressLint("RecyclerView") position: Int
        ) {
            val getData = allParticipants!![position]
            holder.textName.text = getData.firstName
            holder.text_phone.text = getData.phone
            holder.text_email.text = getData.email
            holder.checkbox.isChecked = getData.isChecked
            holder.checkbox.tag = (getData)
            holder.checkbox.setOnCheckedChangeListener { compoundButton, b ->
                try {
                    if (b) {
                        getData.isChecked = true
                        selectDataList.add((compoundButton.tag) as SelectGroupeMemberItemObject)
                        Log.d("aaaaaaaa tag", "" + compoundButton.tag)
//                        selectDataList.add(compoundButton.getTag(getData.contactId) as SelectGroupeMemberItemObject)
                    } else {
                        selectDataList.remove((compoundButton.tag) as SelectGroupeMemberItemObject)
//                        selectDataList.remove(compoundButton.getTag(getData.contactId) as SelectGroupeMemberItemObject)
                    }
                    Log.d("aaaaaa list", selectDataList.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun selectAll() {
            isSelectedAll = true
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        fun unSelectAll() {
            isSelectedAll = false
            notifyDataSetChanged()
        }

        // method for filtering our recyclerview items.
        @SuppressLint("NotifyDataSetChanged")
        fun filterList(filterlist: ArrayList<SelectGroupeMemberItemObject>) {
            // below line is to add our filtered
            // list in our course array list.
            allParticipants = filterlist
            // below line is to notify our adapter
            // as change in recycler view data.
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return allParticipants?.size ?: 0
        }

        inner class ComplaintHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal val textName: TextView = itemView.findViewById(R.id.text_name)
            internal val text_phone: TextView = itemView.findViewById(R.id.text_phone)
            internal val text_email: TextView = itemView.findViewById(R.id.text_email)
            val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { result: Boolean ->
        if (result) {
            //Permission granted
            viewLifecycleOwner.lifecycleScope.launch {
                fetchContacts(requireActivity().contentResolver)
            }
        } else {
            //permission denied
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                Toast.makeText(requireContext(), "No contacts access", Toast.LENGTH_SHORT).show()
                //show permission snackbar
            } else {
                Toast.makeText(requireContext(), "No contacts access", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadFragmentInner(fragment: Fragment?, bundle: Bundle?): Boolean {
        //switching fragment
        fragment!!.arguments = bundle
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return false
    }

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progressBar)
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            fetchContacts(requireActivity().contentResolver)
        }
        search_Bar?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called as the text is being changed
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed
                val newText = s?.toString() ?: ""
                filter(s?.toString() ?: "")
                // Do something with the new text
            }
        })
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<SelectGroupeMemberItemObject> = ArrayList()
        // running a for loop to compare elements.
        for (item in dataList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.firstName.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(requireActivity(), "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            categoryAdapter!!.filterList(filteredlist)
        }
    }

}