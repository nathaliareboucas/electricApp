package br.com.electricapp.electricapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import br.com.electricapp.electricapp.model.Leitura;
import br.com.electricapp.electricapp.services.LeituraService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {
    // Timer da splash screen
    private static int SPLASH_TIME_OUT = 2000;
    public static String base_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_act);

        new Handler().postDelayed(new Runnable() {
            /*
             * Exibindo splash com um timer.
             */
            @Override
            public void run() {
                // Esse método será executado sempre que o timer acabar
                // E inicia a activity principal
                if (verificaLogin()) {
                    getLeituras();
                } else {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    public boolean verificaLogin() {
        SharedPreferences prefs = getSharedPreferences("configuracoes", MODE_PRIVATE);
        base_url = prefs.getString("login", null);
        System.out.println(base_url);

        String senha = prefs.getString("senha", null);
        System.out.println(senha);

        if (base_url == null && senha == null) {
            return false;
        } else if (base_url.equals("") && senha.equals("")) {
            return false;
        }else {
            return true;
        }
    }

    public void getLeituras() {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        LeituraService leituraService = retrofit.create(LeituraService.class);
        Call<List<Leitura>> listarLeituras = leituraService.listar();

        listarLeituras.enqueue(new Callback<List<Leitura>>() {
            @Override
            public void onResponse(Call<List<Leitura>> call, Response<List<Leitura>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Erro na resposta", Toast.LENGTH_LONG).show();
                } else {
                    List<Leitura> leituras = response.body();
                    if (!leituras.isEmpty()) {
                        Intent it = new Intent(SplashActivity.this, ConsumoActivity.class);
                        it.putExtra("base_url", base_url);
                        startActivity(it);
                        finish();
                    } else {
                        Intent i = new Intent(SplashActivity.this, DadosIniciaisActivity.class);
                        i.putExtra("base_url", base_url);
                        startActivity(i);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Leitura>> call, Throwable t) {
                System.out.println(t.getMessage());
                Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
            }
        });
    }
}
