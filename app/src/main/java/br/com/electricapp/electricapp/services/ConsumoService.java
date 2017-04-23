package br.com.electricapp.electricapp.services;

import java.math.BigDecimal;

import br.com.electricapp.electricapp.model.Consumo;
import br.com.electricapp.electricapp.model.Leitura;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Nathalia on 15/04/2017.
 */
public interface ConsumoService {

    @GET("consumo")
    Call<BigDecimal> getConsumoTotal();

    @GET("consumo/{id}")
    Call<Consumo> getConsumoId(@Path("id") Long id);

    @POST("consumo/mensal")
    Call<BigDecimal> consumoMes(@Body Leitura leitura);


}
