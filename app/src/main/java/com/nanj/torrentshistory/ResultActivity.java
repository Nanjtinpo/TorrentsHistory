package com.nanj.torrentshistory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ResultActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);
    
    // intentで送られたデータを受け取る
    Intent intent = getIntent();
    String temp = "";
    if (Intent.ACTION_SEND.equals(intent.getAction())) {
      String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
      String ipregexp = "((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])";
      Pattern p = Pattern.compile(ipregexp);
      Matcher m = p.matcher(sharedText);
      if (m.find()){
        temp = m.group();
      }
    } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
      String searchText = intent.getData().toString();
      String ipregexp = "((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])";
      Pattern p = Pattern.compile(ipregexp);
      Matcher m = p.matcher(searchText);
      if (m.find()){
        temp = m.group();
      }
    } else {
      temp = intent.getStringExtra("searchip");
    }
    if (temp.isEmpty()) {
      Toast.makeText(this, "IPアドレスが見つかりませんでした", Toast.LENGTH_LONG).show();
      finish();
    }
    final String searchip = temp;
    
    // IPを検索する
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
            Document doc = Jsoup.parse(html);
            Elements tbody = doc.select("tbody");
            final String result = tbody.text();
            final TextView textView = (TextView)findViewById(R.id.resulttext);
            if (result.isEmpty()) {
              textView.setText(searchip + " はTorrentを使用していません");
            } else {
              textView.setText(searchip + " のTorrent使用履歴\n\n" + result);
            }
          }
        });
      }
    });
    
    // TopAppBarのメニューアイコンのListener
    MaterialToolbar materialtoolbar = (MaterialToolbar)findViewById(R.id.topappbar);
    materialtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
        drawer.openDrawer(GravityCompat.START);
      }
    });

    // ナビゲーションドロワーのListener
    NavigationView navigationView = (NavigationView)findViewById(R.id.navigation);
    navigationView.setNavigationItemSelectedListener(
    new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
        drawer.closeDrawer(Gravity.LEFT);
        switch (item.getItemId()) {
          case R.id.menuhome:
            startActivity(new Intent(getApplication(), MainActivity.class));
            finish();
            return true;
          case R.id.menuabout:
            startActivity(new Intent(getApplication(), AboutActivity.class));
            finish();
            return true;
          case R.id.menuupdate:
            new AppUpdater(ResultActivity.this)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("NanJ-Dev", "TorrentsHistory")
                .start();
            return true;
        }
        return false;
      }
    });
  }
  
  // 戻るキーを押すとドロワーが閉じる
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(Gravity.LEFT);
    } else {
      super.onBackPressed();
    }
  }
}
