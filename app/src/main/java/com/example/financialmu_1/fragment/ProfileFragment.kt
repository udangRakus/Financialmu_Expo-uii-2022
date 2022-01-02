package com.example.financialmu_1.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.financialmu_1.R
import com.example.financialmu_1.activity.LoginActivity
import com.example.financialmu_1.databinding.FragmentProfileBinding
import com.example.financialmu_1.preference.PreferenceManager
import com.example.financialmu_1.util.PrefUtil

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val pref by lazy {PreferenceManager(requireContext())}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUplistener()
        binding.textBalance.text = requireActivity().intent.getStringExtra("balance")
    }

    override fun onStart() {
        super.onStart()
        getAvatar()
    }

    private fun setUplistener(){
        binding.cardLogout.setOnClickListener {
            pref.clear()
            Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(requireActivity(), LoginActivity::class.java)
                    .addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
            )
            requireActivity().finish()
        }
    }
    private fun getAvatar() {
        binding.texUsername.text = pref.getString(PrefUtil.pref_username)
        binding.textDate.text = pref.getString(PrefUtil.pref_date)
    }




}