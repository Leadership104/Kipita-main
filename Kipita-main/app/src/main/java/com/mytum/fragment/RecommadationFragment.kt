package com.mytum.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mytum.MainActivity
import com.mytum.R
import com.mytum.fragment.recomamandtionSubFragment.EntertainmentMainFragment
import com.mytum.fragment.recomamandtionSubFragment.RestaurantMainFragment
import com.mytum.fragment.recomamandtionSubFragment.ShoppingMainFragment
import com.mytum.listners.ClickPressed

class RecommadationFragment : Fragment(), ClickPressed {
    var active: Fragment? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, null)
        (activity as MainActivity?)!!.showMainMenu()

        val btn1 = view.findViewById<View>(R.id.btn1) as Button
        val btn2 = view.findViewById<View>(R.id.btn2) as Button
        val btn3 = view.findViewById<View>(R.id.btn3) as Button

        val fromWhichCategory = arguments?.getInt("fromWhichCategory", 1)
        when (fromWhichCategory) {
            1 -> {
                loadFragmentInner(RestaurantMainFragment(), null)
                btn1.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_red)
                btn2.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)
                btn3.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)

            }
            2 -> {
                loadFragmentInner(EntertainmentMainFragment(), null)
                btn1.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)
                btn2.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_red)
                btn3.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)

            }
            else -> {
                loadFragmentInner(ShoppingMainFragment(), null)
                btn1.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)
                btn2.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)
                btn3.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_red)
            }
        }


        btn1.setOnClickListener {
            loadFragmentInner(RestaurantMainFragment(), null)
            btn1.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_red)
            btn2.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)
            btn3.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)
        }
        btn2.setOnClickListener {
            loadFragmentInner(EntertainmentMainFragment(), null)
            btn1.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)
            btn2.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_red)
            btn3.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)
        }
        btn3.setOnClickListener {
            loadFragmentInner(ShoppingMainFragment(), null)
            btn1.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)
            btn2.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_grey)
            btn3.background = ContextCompat.getDrawable(requireContext(),R.drawable.background_btn_red)
        }
        return view
    }

    override fun onClickEvent(postion: Int) {}
    fun loadFragmentInner(fragment: Fragment?, bundle: Bundle?): Boolean {
        //switching fragment
        fragment!!.arguments = bundle
        if (fragment != null) {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_recomendation, fragment)
                .commit()
            return true
        }
        return false
    }
}