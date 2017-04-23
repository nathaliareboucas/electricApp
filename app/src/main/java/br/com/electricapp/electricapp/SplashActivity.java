package br.com.electricapp.electricapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

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
                    Intent i = new Intent(SplashActivity.this, ConsumoActivity.class);
                    i.putExtra("base_url", base_url);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);

                    // Fecha esta activity
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
}
