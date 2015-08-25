package br.com.fiap.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    private ProgressDialog dialog;
    private final String URL_SERVICE = "http://developers.agenciaideias.com.br/cotacoes/json";

    private ServiceTask serviceTask;

    private Button btAtualizar;

    private TextView tvIndiceBovespa;
    private TextView tvVariacaoBovespa;
    private TextView tvCotacaoDolar;
    private TextView tvVariacaoDolar;
    private TextView tvCotacaoEuro;
    private TextView tvVariacaoEuro;

    private TextView tvAtualizacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAtualizar = (Button) findViewById(R.id.btAtualizar);

        tvIndiceBovespa = (TextView) findViewById(R.id.tvIndiceBovespa);
        tvVariacaoBovespa = (TextView) findViewById(R.id.tvVariacaoBovespa);
        tvCotacaoDolar = (TextView) findViewById(R.id.tvCotacaoDolar);
        tvVariacaoDolar = (TextView) findViewById(R.id.tvVariacaoDolar);
        tvCotacaoEuro = (TextView) findViewById(R.id.tvCotacaoEuro);
        tvVariacaoEuro = (TextView) findViewById(R.id.tvVariacaoEuro);
        tvAtualizacao = (TextView) findViewById(R.id.tvAtualizacao);

        btAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(MainActivity.this, "Cotação Bovespa", "Atualizando Contções");
                serviceTask = new ServiceTask();
                serviceTask.execute(URL_SERVICE);
            }
        });

    }


    private class ServiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return cotacoes(params[0]);
            } catch (IOException ie) {
                ie.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            dialog.dismiss();

            if (json != null) {
                try {
                    String cotacaoStr = getString(R.string.lblCotacao);
                    String variacaoStr = getString(R.string.lblVariacao);

                    JSONObject valores = new JSONObject(json);
                    JSONObject bovespa = valores.getJSONObject("bovespa");
                    JSONObject dolar = valores.getJSONObject("dolar");
                    JSONObject euro = valores.getJSONObject("euro");
                    String atualizacao = valores.getString("atualizacao");

                    String bovespaCotacao = bovespa.getString("cotacao");
                    String bovespaVariacao = bovespa.getString("variacao");
                    tvIndiceBovespa.setText(cotacaoStr + ": " + bovespaCotacao);
                    tvVariacaoBovespa.setText(variacaoStr + ": " + bovespaVariacao);

                    String dolarCotacao = dolar.getString("cotacao");
                    String dolarVariacao = dolar.getString("variacao");
                    tvCotacaoDolar.setText(cotacaoStr + ": " + dolarCotacao);
                    tvVariacaoDolar.setText(variacaoStr + ": " + dolarVariacao);

                    String euroCotacao = euro.getString("cotacao");
                    String euroVariacao = euro.getString("variacao");
                    tvCotacaoEuro.setText(cotacaoStr + ": " + euroCotacao);
                    tvVariacaoEuro.setText(variacaoStr + ": " + euroVariacao);

                    tvAtualizacao.setText(getString(R.string.lblAtualizacao) + ": " + atualizacao);


                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }

        }
    }

    private String cotacoes(String url) throws IOException {
        String content = "";
        try {
            URL urlService = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlService.openConnection();

            connection.setDoInput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String linha = null;
            while ((linha = reader.readLine()) != null) {
                sb.append(linha + "\n");

                content = sb.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return content;
    }

}
