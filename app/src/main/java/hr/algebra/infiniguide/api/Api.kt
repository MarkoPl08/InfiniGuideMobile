package hr.algebra.infiniguide.api

import android.text.Editable
import hr.algebra.infiniguide.model.Account
import hr.algebra.infiniguide.model.Monument
import hr.algebra.infiniguide.model.Route
import hr.algebra.infiniguide.model.RouteMonument
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


const val BASE_URL = "https://infiniguide.azurewebsites.net/"

interface Api {

    @GET("api/kotlinroutes/getkotlinroutes")
    fun getRoutes(): Call<List<Route>>

    @GET("api/kotlinroutes/getkotlinroutes/{id}")
    fun getSavedRoutes(@Query("id") id: String?): Call<List<Route>>

    @GET("api/kotlinmonuments/get/{id}")
    fun getMonument(@Query("id") id: Int?): Call<Monument>

    @GET("api/routemonuments/get/{id}")
    fun getRouteMonuments(@Query("id") id: String?): Call<List<RouteMonument>>

    @GET("api/accounts/validlogin")
    fun validLogin(@Query("username") username: String, @Query("password") password: String): Call<Account>

    @GET("api/accounts/changepassword")
    fun changePassword(@Query("id") id: String, @Query("password") password: String): Call<Account>

    @POST("api/routes/saveroute")
    fun saveRoute(@Query("id") id: String, @Query("email") email: String): Call<Account>

    companion object {
        operator fun invoke(): Api {
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(Api::class.java)
        }
    }
}