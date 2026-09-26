package com.pointguatemala.transportesvictoria

import android.app.Application
import com.pointguatemala.transportesvictoria.data.session.SessionManager

class TransportesVictoriaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}
