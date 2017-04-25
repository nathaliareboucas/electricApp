package br.com.electricapp.electricapp.services;

import java.util.List;

import br.com.electricapp.electricapp.model.Leitura;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Nathalia on 15/04/2017.
 */
public interface LeituraService {

    @GET("leitura/ultima")
    Call<Leitura> getUltimaLeitura();

    @GET("leitura/medicao")
    Call<Leitura> getMedicao();

    @GET("leitura")
    Call<List<Leitura>> listar();

    @POST("leitura")
    Call<Void> salvarLeitura(@Body Leitura leitura);
}
