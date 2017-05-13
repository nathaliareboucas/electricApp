package br.com.electricapp.electricapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import br.com.electricapp.electricapp.model.Lampada;
import br.com.electricapp.electricapp.services.LampadaService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AmbientesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog dialog;
    private ImageButton btnEdt1;
    private ImageButton btnEdt2;
    private ImageButton btnEdt3;
    private ImageButton btnEdt4;
    private ImageButton btnEdt5;
    private ImageButton btnEdt6;
    private ImageButton lampada1;
    private ImageButton lampada2;
    private ImageButton lampada3;
    private ImageButton lampada4;
    private ImageButton lampada5;
    private ImageButton lampada6;

    public static String base_url;
    private List<Lampada> lampadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ambientes_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnEdt1 = (ImageButton)findViewById(R.id.btnEdt1);
        btnEdt2 = (ImageButton)findViewById(R.id.btnEdt2);
        btnEdt3 = (ImageButton)findViewById(R.id.btnEdt3);
        btnEdt4 = (ImageButton)findViewById(R.id.btnEdt4);
        btnEdt5 = (ImageButton)findViewById(R.id.btnEdt5);
        btnEdt6 = (ImageButton)findViewById(R.id.btnEdt6);

        lampada1 = (ImageButton)findViewById(R.id.lampada1);
        lampada2 = (ImageButton)findViewById(R.id.lampada2);
        lampada3 = (ImageButton)findViewById(R.id.lampada3);
        lampada4 = (ImageButton)findViewById(R.id.lampada4);
        lampada5 = (ImageButton)findViewById(R.id.lampada5);
        lampada6 = (ImageButton)findViewById(R.id.lampada6);

        dialog = new ProgressDialog(AmbientesActivity.this);

        Intent it = getIntent();
        Bundle params = it.getExtras();
        base_url = params.getString("base_url");

        lampada1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alteraEstado(lampadas.get(0), lampada1);
            }
        });

        lampada2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alteraEstado(lampadas.get(1), lampada2);
            }
        });

        lampada3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alteraEstado(lampadas.get(2), lampada3);
            }
        });

        lampada4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alteraEstado(lampadas.get(3), lampada4);
            }
        });

        lampada5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alteraEstado(lampadas.get(4), lampada5);
            }
        });

        lampada6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alteraEstado(lampadas.get(5), lampada6);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.show();

        exibeEstado();
    }

    public void exibeEstado() {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        LampadaService lampadaService = retrofit.create(LampadaService.class);
        Call<List<Lampada>> getLampadas = lampadaService.getLampadas();
        getLampadas.enqueue(new Callback<List<Lampada>>() {
            @Override
            public void onResponse(Call<List<Lampada>> call, Response<List<Lampada>> response) {
                if (!response.isSuccessful()) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(getBaseContext(), "Erro na resposta", Toast.LENGTH_LONG).show();
                } else {
                    lampadas = response.body();
                    int cont = 1;

                    for (Lampada l : lampadas) {
                        if (cont == 1) {
                            if (l.getValor().equals("1ligada")) {
                                lampada1.setBackgroundResource(R.color.colorPrimary);
                            } else {
                                lampada1.setBackgroundResource(R.color.botao);
                            }
                        } else if (cont == 2) {
                            if (l.getValor().equals("2ligada")) {
                                lampada2.setBackgroundResource(R.color.colorPrimary);
                            }else {
                                lampada2.setBackgroundResource(R.color.botao);
                                }
                        } else if (cont == 3) {
                            if (l.getValor().equals("3ligada")){
                                lampada3.setBackgroundResource(R.color.colorPrimary);
                            }else {
                                lampada3.setBackgroundResource(R.color.botao);
                            }
                        } else if (cont == 4) {
                            if (l.getValor().equals("4ligada")){
                                lampada4.setBackgroundResource(R.color.colorPrimary);
                            }else {
                                lampada4.setBackgroundResource(R.color.botao);
                            }
                        } else if (cont == 5) {
                            if (l.getValor().equals("5ligada")){
                                lampada5.setBackgroundResource(R.color.colorPrimary);
                            }else {
                                lampada5.setBackgroundResource(R.color.botao);
                            }
                        } else if (cont == 6) {
                            if (l.getValor().equals("6ligada")){
                                lampada6.setBackgroundResource(R.color.colorPrimary);
                            }else {
                                lampada6.setBackgroundResource(R.color.botao);
                            }
                        }
                        cont++;
                    }
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Lampada>> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();

                System.out.println(t.getMessage());
                Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void alteraEstado(final Lampada lampada, final ImageButton imgButton) {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.show();

        LampadaService lampadaService = retrofit.create(LampadaService.class);
//        Call<List<Lampada>> getLampadas = lampadaService.getLampadas();
//        if (lampada.getValor().equals("desligada")) {
//            lampada.setValor("ligada");
//        } else {
//            lampada.setValor("desligada");
//        }

        Call<Lampada> alteraEstadoLamp = lampadaService.alterarEstado(lampada.getId(), lampada);
        alteraEstadoLamp.enqueue(new Callback<Lampada>() {
            @Override
            public void onResponse(Call<Lampada> call, Response<Lampada> response) {
                if (!response.isSuccessful()) {
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(getBaseContext(), "Erro na resposta", Toast.LENGTH_LONG).show();
                }else {
                    Lampada lamp = response.body();

                    String valorLampada = lamp.getValor().substring(1);

                    if (valorLampada.equals("ligada")) {
                        imgButton.setBackgroundColor(Color.parseColor("#AAAAAA"));
                    } else if (valorLampada.equals("desligada")) {
                        imgButton.setBackgroundColor(Color.parseColor("#61BA47"));
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<Lampada> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.consumo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            onStart();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_consumo) {
            Intent it = new Intent(this, ConsumoActivity.class);
            it.putExtra("base_url", base_url);
            startActivity(it);
            finish();
        } else if (id == R.id.nav_historico) {
            Intent it = new Intent(this, HistoricoActivity.class);
            it.putExtra("base_url", base_url);
            startActivity(it);
            finish();
        }else if (id == R.id.nav_bandeiras) {

        } else if (id == R.id.nav_consumo_aparelho) {

        } else if (id == R.id.nav_alerta) {

        }else if (id == R.id.nav_sair) {
            SharedPreferences prefs = getSharedPreferences("configuracoes", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("login", "");
            editor.putString("senha", "");
            editor.commit();

            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
