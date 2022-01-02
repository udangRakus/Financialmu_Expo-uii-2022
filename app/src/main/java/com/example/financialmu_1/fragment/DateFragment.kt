package com.example.financialmu_1.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.financialmu_1.R
import com.example.financialmu_1.databinding.FragmentDateBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


class DateFragment(var listener: dateListener) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDateBinding
    private var clickDateStart: Boolean = false
    private var dateTemp: String =""
    private var dateStart: String = ""
    private var dateEnd: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDateBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView("Tanggal mulai", "Pilih")
        setUplistener()
    }

    private fun setUplistener() {
        binding.calenerView.setOnDateChangeListener { _, year, month, day ->
            dateTemp = "$day/${month + 1}/$year"
        }
        binding.textApply.setOnClickListener {
            when(clickDateStart){
                //ketika belum memilih tanggal
                false ->{
                    // lakukan pilihan tanggal
                    clickDateStart = true
                    dateStart = dateTemp
                    binding.calenerView.date = Date().time
                    setView("Tanggal akhir", "Terapkan")
                }
                true ->{
                    dateEnd = dateTemp
                    listener.onSucces(dateStart, dateEnd)
                    this.dismiss()
                }
            }
        }
    }

    private fun setView(title: String, apply: String) {
        binding.textTitle.text = title
        binding.textApply.text = apply
    }

    interface dateListener{
        fun onSucces(dateStart: String, dateEnd: String)
    }
}