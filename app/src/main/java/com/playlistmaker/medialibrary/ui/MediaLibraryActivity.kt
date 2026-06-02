package com.playlistmaker.medialibrary.ui

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaLibraryBinding

    private val viewModel by viewModel<MediaLibraryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.viewPager.adapter = MediaLibraryViewPagerAdapter(this)

        setupTabs()
    }

    private fun setupTabs() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val tabView = layoutInflater.inflate(R.layout.item_media_library_tab, null)
            val tabText = tabView.findViewById<TextView>(R.id.tab_text)

            tabText.text = when (position) {
                FAVORITE_TRACKS_TAB_POSITION ->
                    getString(R.string.screen_media_library_favorite_tracks_tab_title)

                PLAYLISTS_TAB_POSITION ->
                    getString(R.string.screen_media_library_playlists_tab_title)

                else -> ""
            }

            tab.customView = tabView
        }.attach()

        updateTabs()

        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab) {
                    updateTabs()
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    updateTabs()
                }

                override fun onTabReselected(tab: TabLayout.Tab) = Unit
            }
        )
    }

    private fun updateTabs() {
        val selectedColor = getThemeColor(androidx.appcompat.R.attr.colorPrimary)

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i) ?: continue
            val customView = tab.customView ?: continue

            val text = customView.findViewById<TextView>(R.id.tab_text)
            val indicator = customView.findViewById<View>(R.id.tab_indicator)

            val isSelected = i == binding.tabLayout.selectedTabPosition

            text.setTextColor(selectedColor)

            indicator.setBackgroundColor(
                if (isSelected) selectedColor else Color.TRANSPARENT
            )
        }
    }

    private fun getThemeColor(@AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    companion object {
        private const val FAVORITE_TRACKS_TAB_POSITION = 0
        private const val PLAYLISTS_TAB_POSITION = 1
    }
}