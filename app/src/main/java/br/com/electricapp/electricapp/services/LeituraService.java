package br.com.electricapp.electricapp.services;

import br.com.electricapp.electricapp.model.Leitura;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by Nathalia on 15/04/2017.
 */
public interface LeituraService {

    @GET("leitura")
    Call<Leitura> getUltimaLeitura();

}
