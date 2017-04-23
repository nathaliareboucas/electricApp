package br.com.electricapp.electricapp.services;

import br.com.electricapp.electricapp.model.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Nathalia on 20/04/2017.
 */
public interface UsuarioService {

    @POST("usuario/autenticar")
    Call<String> autenticar(@Body Usuario usuario);
}
