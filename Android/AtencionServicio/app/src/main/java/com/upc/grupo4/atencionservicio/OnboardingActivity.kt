package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.upc.grupo4.atencionservicio.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = OnboardingAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, _ ->

            tab.setIcon(R.drawable.tab_unselected_dot)
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.setIcon(R.drawable.tab_selected_dot)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.setIcon(R.drawable.tab_unselected_dot)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.btnSkip.paintFlags = binding.btnSkip.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.btnSkip.setOnClickListener {
            goToService()
        }

        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < adapter.itemCount - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                goToService()
            }
        }
    }

    private fun goToService() {
        val intent = Intent(this, ServiceActivity::class.java)
        startActivity(intent)
        finish()
    }
}
