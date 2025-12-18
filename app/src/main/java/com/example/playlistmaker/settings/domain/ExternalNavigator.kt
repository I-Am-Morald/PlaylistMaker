package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.sharing.domain.EmailData

interface ExternalNavigator {

    fun shareLink(link: String)
    fun openEmail(emailData: EmailData)
    fun openLink(link: String)


}