package com.playlistmaker.domain.impl

import com.playlistmaker.data.sharing.ExternalNavigator
import com.playlistmaker.domain.api.SharingInteractor
import com.playlistmaker.domain.models.EmailData
import com.playlistmaker.domain.models.SharingData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val sharingData: SharingData,
    private val emailData: EmailData
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(sharingData.shareAppLink)
    }

    override fun openTerms() {
        externalNavigator.openLink(sharingData.userAgreementLink)
    }

    override fun openSupport() {
        externalNavigator.openEmail(emailData)
    }
}