package com.example.buynow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.buynow.databinding.FragmentShowCaseBinding

class ShowCase : Fragment() {

    private lateinit var binding: FragmentShowCaseBinding
    private lateinit var videoAdapter: VideoAdapter

    private val videoUris = listOf(
        "android.resource://com.example.buynow/${R.raw.video1}",
        "android.resource://com.example.buynow/${R.raw.video2}",
        "android.resource://com.example.buynow/${R.raw.video2}"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowCaseBinding.inflate(inflater, container, false)

        videoAdapter = VideoAdapter(videoUris)
        binding.viewPager.adapter = videoAdapter

        return binding.root
    }
}
