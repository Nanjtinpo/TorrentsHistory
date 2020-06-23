package com.nanj.iknowtorrents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;

public class ResultActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    Intent intent = getIntent();
    String searchip = intent.getStringExtra("searchip");
    
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url("https://iknowwhatyoudownload.com/en/peer/?ip=" + searchip)
        .build();
    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {}
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if(!response.isSuccessful()){
          throw new IOException("Error : " + response);
        }
        final String html = response.body().string();
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            String parsehtml = "";
            Document doc = Jsoup.parse(html);
            Element architectsHeadline = doc.select("tbody").first();
            Element architectsList = architectsHeadline.parent().nextElementSibling();
            for(Element element : architectsList.select("tr > td")){
              parsehtml = parsehtml + "\n" + element.text();
            }
            TextView textView = (TextView)findViewById(R.id.resulttext);
            textView.setText(parsehtml);
          }
        });
      }
    });
  }
}
