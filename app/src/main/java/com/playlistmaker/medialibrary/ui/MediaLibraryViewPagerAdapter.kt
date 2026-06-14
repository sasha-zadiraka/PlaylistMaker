package com.playlistmaker.medialibrary.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.playlistmaker.medialibrary.ui.favorites.FavoriteTracksFragment
import com.playlistmaker.medialibrary.ui.playlists.PlaylistsFragment

class MediaLibraryViewPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = TAB_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FAVORITE_TRACKS_TAB_POSITION -> FavoriteTracksFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }

    companion object {
        private const val TAB_COUNT = 2
        private const val FAVORITE_TRACKS_TAB_POSITION = 0
        private const val PLAYLISTS_TAB_POSITION = 1
    }
}