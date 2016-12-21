package com.durrutia.twinpic.util;

import com.durrutia.twinpic.domain.Pic;
import com.durrutia.twinpic.domain.Twin;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServerService {

    //Método que permite obtener un Pic según su id.
    @GET("/json/obtener/pic/{id}")
    Call<Pic> getPic(@Path("id") long id);

    //Método que permite obtener un Twin según su id.
    @GET("/json/obtener/twin/{id}")
    Call<Pic> getTwin(@Path("id") long id);

    //Método que permite obtener todas las Pics.
    @GET("json/obtener/piclist")
    Call<List<Pic>> getPicList();

    //Método que permite obtener todas las Twins de un dispositivo.
    @GET("json/obtener/twin/twinlist/{devid}")
    Call<List<Twin>> getTwinList(@Path("devid") String devId);

    //Método que permite subir una pic.
    @POST("/json/subir/pic")
    Call<Pic> subirPic(@Body Pic pic);

    //Método que permite subir una Twin.
    @POST("/json/subir/twin")
    Call<Twin> subirTwin(@Body Twin twin);
}
