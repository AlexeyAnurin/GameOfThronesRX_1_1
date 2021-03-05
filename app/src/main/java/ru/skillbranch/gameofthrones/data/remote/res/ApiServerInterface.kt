package ru.skillbranch.gameofthrones.data.remote.res

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServerInterface {

    //AA  ретрофитный GET-запрос для базового адреса. Также можно указать параметры в скобках.
    //AA   @Query parameter appended to the URL. Задаёт имя ключа запроса со значением параметра.
    @GET("houses")
    fun getHouses(@Query("page") page: Int, @Query("pageSize") pageSize: Int): Single<List<HouseRes>>

    @GET("characters/{id}")
    fun getCharacter(@Path("id") id: String): Single<CharacterRes>

}