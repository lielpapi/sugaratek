package com.example.sugarate.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sugarate.R

class PlaceDetailsFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var openingHoursTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameTextView = view.findViewById(R.id.nameTextView)
        addressTextView = view.findViewById(R.id.addressTextView)
        openingHoursTextView = view.findViewById(R.id.openingHoursTextView)

        val name = arguments?.getString(ARG_NAME) ?: ""
        val address = arguments?.getString(ARG_ADDRESS) ?: ""
        val openingHours = arguments?.getString(ARG_OPENING_HOURS) ?: ""

        nameTextView.text = name
        addressTextView.text = address
        openingHoursTextView.text = openingHours
    }

    companion object {
        private const val ARG_NAME = "name"
        private const val ARG_ADDRESS = "address"
        private const val ARG_OPENING_HOURS = "openingHours"

        fun newInstance(name: String, address: String, openingHours: String): PlaceDetailsFragment {
            val fragment = PlaceDetailsFragment()
            val args = Bundle().apply {
                putString(ARG_NAME, name)
                putString(ARG_ADDRESS, address)
                putString(ARG_OPENING_HOURS, openingHours)
            }
            fragment.arguments = args
            return fragment
        }
    }
}