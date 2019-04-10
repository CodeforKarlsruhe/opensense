package de.codefor.karlsruhe.opensense

import android.support.multidex.MultiDexApplication
import com.mapbox.mapboxsdk.Mapbox
import net.danlew.android.joda.JodaTimeAndroid

class OpenSenseApplication: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(this)
        Mapbox.getInstance(applicationContext, BuildConfig.MAPBOX_API_TOKEN)
    }
}