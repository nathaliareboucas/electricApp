package br.com.electricapp.electricapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import br.com.electricapp.electricapp.model.Usuario;
import br.com.electricapp.electricapp.services.UsuarioService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText edtSSID;
    private EditText edtSenha;
    private Button btnLogin;
    private CheckBox checkLogin;
    private ProgressDialog dialog;

    public static String base_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);

        edtSSID = (EditText)findViewById(R.id.edtSSID);
        edtSenha = (EditText)findViewById(R.id.edtSenha);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        checkLogin = (CheckBox)findViewById(R.id.checkLogin);
        dialog = new ProgressDialog(MainActivity.this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                base_url = edtSSID.getText().toString();
                autenticar(edtSenha.getText().toString());
            }
        });

    }

    public void autenticar(final String senha) {
        Usuario usu = new Usuario();
        usu.setId(new Long(1));
        usu.setEndpoint(base_url);
        usu.setSenha(senha);

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

        UsuarioService usuarioService = retrofit.create(UsuarioService.class);
        Call<String> autenticar = usuarioService.autenticar(usu);
        autenticar.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Erro na resposta", Toast.LENGTH_LONG).show();
                }else {
                    String resposta = response.body();
                    if (resposta.equals("Autenticado")) {
                        if (verificaCheck(checkLogin)) {
                            salvaUser(base_url, senha);
                        }
                        Intent it = new Intent(MainActivity.this, ConsumoActivity.class);
                        it.putExtra("base_url",base_url);
                        startActivity(it);
                    }else {
                        Toast.makeText(getBaseContext(), "SSID ou Senha inválidos", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();

                System.out.println(t.getMessage());
                Toast.makeText(getBaseContext(), "Erro na conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean verificaCheck(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            return true;
        }
        return false;
    }

    public void salvaUser(String base_url, String senha) {
        SharedPreferences prefs = getSharedPreferences("configuracoes", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("login", base_url);
        editor.putString("senha", senha);
        editor.commit();
    }
}
