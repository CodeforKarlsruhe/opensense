package de.codefor.karlsruhe.opensense

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import net.danlew.android.joda.JodaTimeAndroid

class OpenSenseApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(this)
        Mapbox.getInstance(applicationContext, BuildConfig.MAPBOX_API_TOKEN)
    }
}