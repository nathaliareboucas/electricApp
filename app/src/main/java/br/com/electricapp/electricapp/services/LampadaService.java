package br.com.electricapp.electricapp.services;

import java.util.List;

import br.com.electricapp.electricapp.model.Lampada;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Nathalia on 20/04/2017.
 */
public interface LampadaService {

    @GET("lampada")
    Call<List<Lampada>> getLampadas();

    @PUT("lampada/{id}")
    Call<Lampada> alterarEstado(@Path("id") Long id, @Body Lampada lampada);
}
