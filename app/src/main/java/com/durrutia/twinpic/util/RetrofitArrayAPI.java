package com.durrutia.twinpic.util;

import com.durrutia.twinpic.domain.Pic;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface RetrofitArrayAPI {

    @GET("/pics/obtener/[?id={name}]")
    Call<List<Pic>> getPics();

}
