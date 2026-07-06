package com.playlistmaker.medialibrary.ui

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.FragmentMediaLibraryBinding
class MediaLibraryFragment : Fragment() {

    private var _binding: FragmentMediaLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<MediaLibraryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

                else -> getString(R.string.screen_media_library_playlists_tab_title)
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
        requireActivity().theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val FAVORITE_TRACKS_TAB_POSITION = 0
        private const val PLAYLISTS_TAB_POSITION = 1
    }
}