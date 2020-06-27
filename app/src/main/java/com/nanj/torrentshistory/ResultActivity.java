package com.nanj.torrentshistory;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
    if (Intent.ACTION_SEND.equals(intent.getAction())) {
      final String searchIP = getIp(intent.getStringExtra(Intent.EXTRA_TEXT));
    } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
      final String searchIP = getIp(intent.getData().toString());
    } else {
      final String searchIP = intent.getStringExtra("searchIP");
    }
    if (searchIP.isEmpty()) {
      toastMake("IPアドレスが見つかりませんでした");
      finish();
    }

    // IPを検索する
    final String searchurl = "https://iknowwhatyoudownload.com/en/peer/?ip=" + searchip;
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(searchurl)
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
            Elements tbody = doc.select("tbody > tr > td");
            if (tbody.text().isEmpty()) {
              toastMake(searchip + " はTorrentを使用していません");
	      finish();
            } else {
	      String h = "";
	      for (Element headline : tbody) {
		h = h + headline.text() + "\n";
              }
            }
          }
        });
      }
    });

    // FABのListener
    ExtendedFloatingActionButton urlcopyfab = findViewById(R.id.urlcopy);
    urlcopyfab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        copyToClipboard(searchurl);
	toastMake("URLをコピーしました");
      }
    });
    ExtendedFloatingActionButton allcopyfab = findViewById(R.id.allcopy);
    allcopyfab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        copyToClipboard("all");
        toastMake("全てコピーしました");
      }
    });

    // TopAppBarのメニューアイコンのListener
    MaterialToolbar materialtoolbar = (MaterialToolbar)findViewById(R.id.materialtoolbar);
    materialtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawerlayout);
        drawer.openDrawer(GravityCompat.START);
      }
    });

    // ナビゲーションドロワーのListener
    NavigationView navigationView = (NavigationView)findViewById(R.id.navigationview);
    navigationView.setNavigationItemSelectedListener(
    new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawerlayout);
        drawer.closeDrawer(Gravity.LEFT);
        switch (item.getItemId()) {
          case R.id.home:
            startActivity(new Intent(getApplication(), MainActivity.class));
            finish();
            return true;
          case R.id.about:
            startActivity(new Intent(getApplication(), AboutActivity.class));
            finish();
            return true;
          case R.id.update:
            new AppUpdater(ResultActivity.this)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("NanJ-Dev", "TorrentsHistory")
		.showAppUpdated(true)
              	.setTitleOnUpdateAvailable("アップデートがあります")
	        .setContentOnUpdateAvailable("「アップデートする」を押すとGitHubに飛びます")
	        .setTitleOnUpdateNotAvailable("アップデートはありません")
		.setContentOnUpdateNotAvailable("最新バージョンです")
	        .setButtonUpdate("アップデートする")
	        .setButtonDismiss("無視する")
	        .setButtonDoNotShowAgain("二度と表示しない")
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
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(Gravity.LEFT);
    } else {
      super.onBackPressed();
    }
  }

  public String getIp(String searchText) {
    String ipRegExp = "((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])";
    Pattern pattern = Pattern.compile(ipRegExp);
    Matcher matcher = pattern.matcher(searchText);
    if (matcher.find()){
      return matcher.group();
    } else {
      return "";
    }
  }

  // クリップボードにコピー
  public void copyToClipboard(String copytext) {
    ClipboardManager clipboardManager =
                (ClipboardManager) ResultActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
      if (null == clipboardManager) {
        return;
      }
    clipboardManager.setPrimaryClip(ClipData.newPlainText("", copytext));
  }

  // トーストを出す
  public void toastMake(String toasttext) {
    Toast.makeText(ResultActivity.this, toasttext, Toast.LENGTH_LONG).show();
  }
}
