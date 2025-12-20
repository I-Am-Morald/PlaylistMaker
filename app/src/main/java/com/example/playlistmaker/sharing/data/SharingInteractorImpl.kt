package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.settings.domain.ExternalNavigator
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.domain.EmailData
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val settingsRepository: SettingsRepository,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun termsApp() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun supportApp() {
        externalNavigator.openEmail(
            getSupportEmail()
        )
    }

    private fun getShareAppLink(): String {
        return settingsRepository.getShareAppLink()
    }

    private fun getSupportEmail(): EmailData {
        return settingsRepository.getEmailData()
    }

    private fun getTermsLink(): String {
        return settingsRepository.getTermsLink()
    }
}