package com.playlistmaker.sharing.domain

import com.playlistmaker.sharing.domain.models.EmailData
import com.playlistmaker.sharing.domain.models.SharingData

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