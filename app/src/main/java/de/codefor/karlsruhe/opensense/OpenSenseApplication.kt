package de.codefor.karlsruhe.opensense

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox

class OpenSenseApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Mapbox.getInstance(applicationContext, BuildConfig.MAPBOX_API_TOKEN)
    }
}