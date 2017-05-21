package br.com.electricapp.electricapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.electricapp.electricapp.model.Consumo;
import br.com.electricapp.electricapp.model.Leitura;
import br.com.electricapp.electricapp.services.ConsumoService;
import br.com.electricapp.electricapp.util.DateConverter;
import br.com.electricapp.electricapp.util.FormataData;
import br.com.electricapp.electricapp.util.MyXAxisValueFormatter;
import br.com.electricapp.electricapp.util.MyYAxisValueFormatter;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsumoInstantaneo extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ProgressDialog dialog;
    private RadioGroup radGroup;
    private RadioButton radioButPorPeriodo;
    private RadioButton radioButTempoReal;
    private ImageButton setaJanela;
    private EditText edtTxtDataInicio;
    private EditText edtTxtDataFim;
    private EditText edtTxtHorarioInicio;
    private EditText edtTxtHorarioFim;
    private Button btnOk;
    private Button btnStar;
    private Button btnStop;
    private LineChart chartLine;
    private TextView consumoInstantaneoAndamento;

    private boolean visivel;
    private boolean visivel2;
    private static Handler handler;
    private Consumo consumoUltimo;
    private List<Consumo> consumos;

    public static String base_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumo_instataneo_act);
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

        dialog = new ProgressDialog(ConsumoInstantaneo.this);
        radGroup = (RadioGroup)findViewById(R.id.radGroup);
//        radioButPorPeriodo = (RadioButton)findViewById(R.id.radioButPorPeriodo);
        radioButTempoReal = (RadioButton)findViewById(R.id.radioButTempoReal);
        setaJanela = (ImageButton)findViewById(R.id.setaJanela);
//        edtTxtDataInicio = (EditText)findViewById(R.id.edtTxtDataInicio);
//        edtTxtDataFim = (EditText)findViewById(R.id.edtTxtDataFim);
//        edtTxtHorarioInicio = (EditText)findViewById(R.id.edtTxtHorarioInicio);
//        edtTxtHorarioFim = (EditText)findViewById(R.id.edtTxtHorarioFim);
//        btnOk = (Button)findViewById(R.id.btnOk);
        btnStar = (Button)findViewById(R.id.btnStart);
        btnStop = (Button)findViewById(R.id.btnStop);
        chartLine = (LineChart)findViewById(R.id.chartLine);
        visivel = false;
        visivel2 = false;
        handler = new Handler();
        consumos = new ArrayList<>();
        consumoInstantaneoAndamento = (TextView)findViewById(R.id.txtConsumoInstantaneoAndamento);

        radGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        setaJanela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visivel || visivel2) {
                    escondeCampos();
                    escondeCampos2();
                    setaJanela.setImageResource(R.drawable.seta_baixo);
                    setaJanela.setBackgroundColor(getResources().getColor(R.color.transparente));
                } else {
//                    if (radioButPorPeriodo.isChecked()) {
//                        exibeCampos();
//                        setaJanela.setImageResource(R.drawable.seta_cima);
//                        setaJanela.setBackgroundColor(getResources().getColor(R.color.transparente));
//                    }
                    if (radioButTempoReal.isChecked()) {
                        exibeCampos2();
                        setaJanela.setImageResource(R.drawable.seta_cima);
                        setaJanela.setBackgroundColor(getResources().getColor(R.color.transparente));
                    }
                }
            }
        });

        btnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Carregando...");
                dialog.setCancelable(false);
                dialog.show();
                consumoInstantaneoAndamento.setVisibility(View.VISIBLE);

                handler.post(new UpdateData());
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                consumoInstantaneoAndamento.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "Consumo instantâneo finalizado", Toast.LENGTH_LONG).show();
            }
        });

    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
//                case R.id.radioButPorPeriodo:
//                    exibeCampos();
//                    escondeCampos2();
//                    setaJanela.setImageResource(R.drawable.seta_cima);
//                    setaJanela.setBackgroundColor(getResources().getColor(R.color.transparente));
//                    break;
                case R.id.radioButTempoReal:
                    exibeCampos2();
                    escondeCampos();
                    setaJanela.setImageResource(R.drawable.seta_cima);
                    setaJanela.setBackgroundColor(getResources().getColor(R.color.transparente));
                    break;
            }
        }
    };

    public void exibeCampos() {
//        edtTxtDataInicio.setVisibility(View.VISIBLE);
//        edtTxtDataFim.setVisibility(View.VISIBLE);
//        edtTxtHorarioInicio.setVisibility(View.VISIBLE);
//        edtTxtHorarioFim.setVisibility(View.VISIBLE);
//        btnOk.setVisibility(View.VISIBLE);
        visivel = true;
    }

    public void escondeCampos() {
//        edtTxtDataInicio.setVisibility(View.GONE);
//        edtTxtDataFim.setVisibility(View.GONE);
//        edtTxtHorarioInicio.setVisibility(View.GONE);
//        edtTxtHorarioFim.setVisibility(View.GONE);
//        btnOk.setVisibility(View.GONE);
        visivel = false;
    }

    public void exibeCampos2() {
        btnStar.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.VISIBLE);
        visivel2 = true;
    }

    public void escondeCampos2() {
        btnStar.setVisibility(View.GONE);
        btnStop.setVisibility(View.GONE);
        visivel2 = false;
    }

    public void geraGrafico() {
        chartLine.clear();
        List<Entry> entries = new ArrayList<>();

        //preenche array com valores do gráfico no eixo y
        for (int i = 0; i < consumos.size(); i++) {
            entries.add(new Entry(i, consumos.get(i).getValor().floatValue()));
        }

        //insere os valores do eixo y e coloca style no gráfico
        LineDataSet lineDataSet = new LineDataSet(entries,"Consumo");
        lineDataSet.setColor(Color.rgb(97, 186, 71));
        lineDataSet.setCircleColor(Color.rgb(97, 186, 71));
        YAxis yAxis = chartLine.getAxis(YAxis.AxisDependency.LEFT);
        yAxis.setValueFormatter(new MyYAxisValueFormatter());
        yAxis.setAxisMinimum(0f);
        YAxis yAxis1 = chartLine.getAxis(YAxis.AxisDependency.RIGHT);
        yAxis1.setValueFormatter(new MyYAxisValueFormatter());
        yAxis1.setAxisMinimum(0f);

        //insere o eixo Y no gráfico, coloca animção e zoom
        LineData lineData = new LineData(lineDataSet);
        chartLine.setData(lineData);
        chartLine.setTouchEnabled(true);
        chartLine.setScaleEnabled(true);

        //insere os valores do eixo x
        XAxis xAxis = chartLine.getXAxis();
        xAxis.setEnabled(false);

        //personaliza a descrição do gráfico
        Description desc = new Description();
        desc.setText("kW");
        chartLine.setDescription(desc);

        //desabilita a legenda do gráfico e atualiza exibindo o gráfico
        Legend legend = chartLine.getLegend();
        legend.setEnabled(false);
        chartLine.setVisibility(View.VISIBLE);
        chartLine.invalidate(); // refresh
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
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
        }
//        else if (id == R.id.nav_bandeiras) {
//
//        } else if (id == R.id.nav_consumo_aparelho) {
//
//        } else if (id == R.id.nav_alerta) {
//
//        }
        else if (id == R.id.nav_sair) {
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

    private final class UpdateData implements Runnable{
        @Override
        public void run() {
            //Faça aqui a requisição via Retrofit
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss")
                    .setLenient().create();
            OkHttpClient client = new OkHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            ConsumoService consumoService = retrofit.create(ConsumoService.class);
            Call<Consumo> ultimoConsumo = consumoService.ultimoConsumo();

            ultimoConsumo.enqueue(new Callback<Consumo>() {
                @Override
                public void onResponse(Call<Consumo> call, Response<Consumo> response) {
                    if (!response.isSuccessful()) {
                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Toast.makeText(getBaseContext(), "Erro na resposta", Toast.LENGTH_LONG).show();
                    } else {
                        consumoUltimo = response.body();
                        consumos.add(consumoUltimo);
                        geraGrafico();

                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Consumo> call, Throwable t) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    System.out.println(t.getMessage());
                    Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
                }
            });


            //agenda nova requisição daqui a 5 minutos
            handler.postDelayed(this, 60*1000);
        }
    }
}
