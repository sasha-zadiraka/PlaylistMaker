package com.playlistmaker.sharing.domain

interface SharingInteractor {
    fun shareApp()
    fun shareText(text: String)
    fun openTerms()
    fun openSupport()
}