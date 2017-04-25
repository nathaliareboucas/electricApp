package br.com.electricapp.electricapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;

import br.com.electricapp.electricapp.model.Leitura;
import br.com.electricapp.electricapp.services.ConsumoService;
import br.com.electricapp.electricapp.services.LeituraService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsumoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ProgressDialog dialog;
    private TextView txtUltimaLeitura;
    private TextView txtProximaLeitura;
    private TextView txtConsumoTotal;
    private TextView txtValorTarifa;
    private TextView txtConsumMes;
    private TextView txtValorAPagar;
    private String dataUltimaLeitura;

    public static String base_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumo_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getBaseContext(), AmbientesActivity.class);
                it.putExtra("base_url", base_url);
                startActivity(it);
            }
        });

        txtUltimaLeitura = (TextView)findViewById(R.id.txtUltimaLeitura);
        txtProximaLeitura = (TextView)findViewById(R.id.txtProximaLeitura);
        txtConsumoTotal = (TextView)findViewById(R.id.txtConsumoTotal);
        txtValorTarifa = (TextView)findViewById(R.id.txtValorTarifa);
        txtConsumMes = (TextView)findViewById(R.id.txtConsumoMes);
        txtValorAPagar = (TextView)findViewById(R.id.txtValorAPagar);

        Intent it = getIntent();
        Bundle params = it.getExtras();
        base_url = params.getString("base_url");
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog = new ProgressDialog(ConsumoActivity.this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.show();

        ultimaLeitura();
        consumoTotal();
        cosumoMes();
        dialog.dismiss();
    }

    public void ultimaLeitura() {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        LeituraService leituraService = retrofit.create(LeituraService.class);
        Call<Leitura> getUltimaLeitura = leituraService.getUltimaLeitura();

        getUltimaLeitura.enqueue(new Callback<Leitura>() {
            @Override
            public void onResponse(Call<Leitura> call, Response<Leitura> response) {
                if (!response.isSuccessful()) {
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(getBaseContext(), "Erro na resposta - Ultima Leitura", Toast.LENGTH_LONG).show();
                }else {
                    Leitura ultimaLeitura = response.body();
                    txtUltimaLeitura.setText(ultimaLeitura.getValorUltimaLeitura().toString());
                    txtProximaLeitura.setText(ultimaLeitura.getProximaLeitura());
                    dataUltimaLeitura = ultimaLeitura.getUltimaLeitura();
                }
            }

            @Override
            public void onFailure(Call<Leitura> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();

                System.out.println(t.getMessage());
                Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void consumoTotal() {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ConsumoService consumoService = retrofit.create(ConsumoService.class);
        Call<BigDecimal> getConsumoTotal = consumoService.getConsumoTotal();

        getConsumoTotal.enqueue(new Callback<BigDecimal>() {
            @Override
            public void onResponse(Call<BigDecimal> call, Response<BigDecimal> response) {
                if (!response.isSuccessful()) {
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(getBaseContext(), "Erro na resposta - Valor Atual", Toast.LENGTH_LONG).show();
                }else {
                    BigDecimal consumoTotal = response.body();
                    txtConsumoTotal.setText(consumoTotal.toString());
                }
            }

            @Override
            public void onFailure(Call<BigDecimal> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();

                System.out.println(t.getMessage());
                Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cosumoMes() {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Leitura leitura = new Leitura();
        leitura.setUltimaLeitura(dataUltimaLeitura);
        leitura.setProximaLeitura(txtProximaLeitura.getText().toString());

        ConsumoService consumoService = retrofit.create(ConsumoService.class);
        Call<BigDecimal> consumoMes = consumoService.consumoMes(leitura);

        consumoMes.enqueue(new Callback<BigDecimal>() {
            @Override
            public void onResponse(Call<BigDecimal> call, Response<BigDecimal> response) {
                if (!response.isSuccessful()) {
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(getBaseContext(), "Erro na resposta - Consumo", Toast.LENGTH_LONG).show();
                }else {
                    BigDecimal consumoMes = response.body();
                    txtConsumMes.setText(consumoMes.toString());
                    BigDecimal valorTarifa = new BigDecimal(txtValorTarifa.getText().toString());
                    BigDecimal valorAPagar = valorTarifa.multiply(consumoMes);
                    txtValorAPagar.setText(valorAPagar.toString());
                }
            }

            @Override
            public void onFailure(Call<BigDecimal> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();

                System.out.println(t.getMessage());
                Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
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
            onStart();
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
