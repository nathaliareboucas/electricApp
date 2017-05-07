package br.com.electricapp.electricapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import br.com.electricapp.electricapp.model.Leitura;
import br.com.electricapp.electricapp.services.ConsumoService;
import br.com.electricapp.electricapp.util.DateConverter;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistoricoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txtMedia;
    private ProgressDialog dialog;
    private List<Leitura> leituras;
    private ArrayList<String> consumoMes;
    private ArrayList<String> valorConsumo;

    BarChart chart;

    public static String base_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historico_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent it = getIntent();
        Bundle params = it.getExtras();
        base_url = params.getString("base_url");

        txtMedia = (TextView)findViewById(R.id.txtMedia);
        dialog = new ProgressDialog(HistoricoActivity.this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.show();

        getLeituras();
        chart = (BarChart) findViewById(R.id.chart);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void getLeituras() {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ConsumoService consumoService = retrofit.create(ConsumoService.class);
        Call<List<Leitura>> listarLeiturasConsumoPorMes = consumoService.consumoLeiturasPorMes();

        listarLeiturasConsumoPorMes.enqueue(new Callback<List<Leitura>>() {
            @Override
            public void onResponse(Call<List<Leitura>> call, Response<List<Leitura>> response) {
                if (!response.isSuccessful()) {
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(getBaseContext(), "Erro na resposta", Toast.LENGTH_LONG).show();
                } else {
                    leituras = response.body();
                    mesConsumo();
                }
            }

            @Override
            public void onFailure(Call<List<Leitura>> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();
                System.out.println(t.getMessage());
                Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void mesConsumo() {
        final int tempoDeEspera = 3000;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
            SystemClock.sleep(tempoDeEspera);

                consumoMes = new ArrayList<String>();
                valorConsumo = new ArrayList<String>();

                String valorCons;
                DateConverter dataConvertida = new DateConverter();

                for (int i = 0; i < leituras.size(); i++) {
                    Calendar mesData = Calendar.getInstance();
                    mesData.setTime(dataConvertida.stringToDate(leituras.get(i).getUltimaLeitura()));

                    consumoMes.add(dataConvertida.nomeMes(mesData));

                    valorCons = leituras.get(i).getValorUltimaLeitura().toString();
                    int pos = valorCons.indexOf(".");
                //Substring iniciando em 0 até posição do caracter especial
                    valorConsumo.add(valorCons.substring(0,pos));
                }
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                BarData data = new BarData(consumoMes, getDataSet());
                chart.setData(data);
                chart.setDescription("kWh");
                chart.animateXY(2000, 2000);
                chart.setTouchEnabled(true);
                chart.setScaleEnabled(true);
                chart.setVisibility(View.VISIBLE);
                chart.invalidate();

                media();
            }
        });
    }

    public void media() {
        BigDecimal media = new BigDecimal(0);

        for (String v : valorConsumo) {
            media = media.add(new BigDecimal(v));
        }
        media = media.divide(BigDecimal.valueOf(valorConsumo.size()));
        txtMedia.setText(media.toString());
    }

    public BarDataSet getDataSet() {
        List<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < valorConsumo.size(); i++) {
            barEntries.add(new BarEntry(Float.parseFloat(valorConsumo.get(i)),i));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries,"Consumo");
        barDataSet.setColor(Color.rgb(97, 186, 71));

        return barDataSet;
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
            onStart();
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
