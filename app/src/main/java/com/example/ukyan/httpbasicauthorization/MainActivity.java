package com.example.ukyan.httpbasicauthorization;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView textView,textView2;
    String card_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);

        // 接続先のURLを指定してHTTP GET実行
        URL url = null;
        card_id = "ghthetfe";
        try {
            url = new URL("http://test-ukyankyan.c9users.io/api/app_token/new");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        new HttpGetTask().execute(url);
    }

    // AsyncTaskのサブクラスとして、バックグラウンドでHTTP GETしてTextViewに表示するタスクを定義
    class HttpGetTask extends AsyncTask<URL, Void, String> {
        // HttpURLConnectionを使ったデータ取得 (バックグラウンド)
        @Override
        protected String doInBackground(URL... url) {
            String result = "";
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url[0].openConnection();
                urlConnection.setRequestMethod("GET");
                final String password = "testtest";
                final String userPassword = card_id + ":" + password;
                final String encodeAuthorization = Base64.encodeToString(userPassword.getBytes(), Base64.NO_WRAP);
                urlConnection.setRequestProperty("Authorization", "Basic " + encodeAuthorization);
                if(urlConnection.getInputStream() != null)
                    result = IOUtils.toString(urlConnection.getInputStream());
                else
                    Log.e("error","result is null");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        // データ取得結果のTextViewへの表示 (UIスレッド)
        @Override
        protected void onPostExecute(String response) {
            if(response != null) {
                MainActivity.this.textView.setText(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String token = jsonObject.getString("token");
                    Log.d("test", token);
                    MainActivity.this.textView2.setText(token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
