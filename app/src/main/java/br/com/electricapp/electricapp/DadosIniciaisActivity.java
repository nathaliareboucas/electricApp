package br.com.electricapp.electricapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.math.BigDecimal;
import java.util.List;
import br.com.electricapp.electricapp.model.Consumo;
import br.com.electricapp.electricapp.model.Leitura;
import br.com.electricapp.electricapp.services.ConsumoService;
import br.com.electricapp.electricapp.services.LeituraService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DadosIniciaisActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private EditText edtDataUltimaLeitura;
    private EditText edtValorUltimaLeitura;
    private EditText edtValorMedidor;
    Intent it;
    public static String base_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dados_iniciais_act);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent itn = getIntent();
        Bundle params = itn.getExtras();
        base_url = params.getString("base_url");

        edtDataUltimaLeitura = (EditText)findViewById(R.id.dataUltimaLeitura);
        edtValorUltimaLeitura = (EditText)findViewById(R.id.valorUltimaLeitura);
        edtValorMedidor = (EditText)findViewById(R.id.valorMedidor);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificaCampos()) {
                    String mensagem = "Todos os campos devem ser preenchidos";
                    Toast.makeText(getBaseContext(), mensagem, Toast.LENGTH_LONG).show();
                } else {
                    dialog.setMessage("Carregando...");
                    dialog.setCancelable(false);
                    dialog.show();
                    salvaUltimaLeitura();
                    salvaConsumoAcumulado();
                    salvaConsumoDiferencaDias();

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                        Intent it = new Intent(getBaseContext(), ConsumoActivity.class);
                        it.putExtra("base_url", base_url);
                        startActivity(it);
                        finish();
                }
            }
        });
    }

    public void salvaUltimaLeitura() {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Leitura leit = new Leitura();
        leit.setUltimaLeitura(edtDataUltimaLeitura.getText().toString());
        leit.setValorUltimaLeitura(new BigDecimal(edtValorUltimaLeitura.getText().toString()));

        LeituraService leituraService = retrofit.create(LeituraService.class);
        Call<Void> salvarLeitura = leituraService.salvarLeitura(leit);

        salvarLeitura.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(getBaseContext(), "Erro ao salvar última leitura", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();

                System.out.println(t.getMessage());
                Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void salvaConsumoAcumulado() {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Consumo consumoAcumulado = new Consumo();
        consumoAcumulado.setValor(new BigDecimal(edtValorUltimaLeitura.getText().toString()));

        ConsumoService consumoService = retrofit.create(ConsumoService.class);
        Call<Void> salvarConsumoAcumulado = consumoService.salvaConsumo(consumoAcumulado);

        salvarConsumoAcumulado.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(getBaseContext(), "Erro ao salvar valor de consumo acumulado", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                System.out.println(t.getMessage());
                Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void salvaConsumoDiferencaDias() {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Consumo consumoDiferencaDias = new Consumo();
        BigDecimal valorDoMedidor = new BigDecimal(edtValorMedidor.getText().toString());
        consumoDiferencaDias.setValor(valorDoMedidor.subtract(
                new BigDecimal(edtValorUltimaLeitura.getText().toString())));

        ConsumoService consumoService = retrofit.create(ConsumoService.class);
        Call<Void> salvarConsumoDiferencaDias = consumoService.salvaConsumo(consumoDiferencaDias);

        salvarConsumoDiferencaDias.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(getBaseContext(), "Erro ao salvar o valor do consumo da diferença dos dias",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                System.out.println(t.getMessage());
                Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean verificaCampos() {
        boolean campos = false;
        if (edtDataUltimaLeitura.getText().toString().trim().equals("")
                || edtDataUltimaLeitura.getText().toString().length() == 0) {
            campos = true;
        }
        if (edtValorUltimaLeitura.getText().toString().trim().equals("")
                || edtValorUltimaLeitura.getText().toString().length() == 0) {
            campos = true;
        }
        if (edtValorMedidor.getText().toString().trim().equals("")
                || edtValorMedidor.getText().toString().length() == 0) {
            campos = true;
        }
        return campos;
    }
}
