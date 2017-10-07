package de.codefor.karlsruhe.opensense.data.boxes

import de.codefor.karlsruhe.opensense.data.boxes.model.SenseBox
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BoxesApi {
    @GET("boxes/{boxId}")
    fun getBox(@Path("boxId") boxId: String) : Single<SenseBox>

    @GET("boxes")
    fun getAllBoxes(): Single<List<SenseBox>>
}