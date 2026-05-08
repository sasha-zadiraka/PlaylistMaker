package com.playlistmaker.sharing.domain

import com.playlistmaker.sharing.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)
}