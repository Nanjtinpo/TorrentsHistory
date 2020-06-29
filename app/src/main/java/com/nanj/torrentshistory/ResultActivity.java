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
import android.widget.ProgressBar;
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
  // フィールド変数
  DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    // Intentで送られたデータを受け取りIPを抽出する
    Intent intent = getIntent();
    String temp = "";
    if (Intent.ACTION_SEND.equals(intent.getAction())) {
      temp = getIp(intent.getStringExtra(Intent.EXTRA_TEXT));
    } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
      temp = getIp(intent.getData().toString());
    } else {
      temp = intent.getStringExtra("searchIP");
    }
    final String searchIP = temp;
    // IPが見つからなかったら終了する
    if (searchIP.isEmpty()) {
      toastMake("IPアドレスが見つかりませんでした");
      finish();
    }

    // IPからTorrentの履歴を検索する
    final String searchURL = "https://iknowwhatyoudownload.com/en/peer/?ip=" + searchIP;
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(searchURL)
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
	    // 取得したHTMLから要素を抽出する
            Document document = Jsoup.parse(html);
            Elements elements = document.select("tbody > tr > td");
	    // 抽出できなければ終了する
            if (elements.text().isEmpty()) {
              toastMake(searchIP + " はTorrentを使用していません");
	      finish();
            } else {
	      String temp2 = "";
	      for (Element element : elements) {
		// 要改善
		temp2 = temp2 + element.text() + "\n";
	        ProgressBar progressBar = findViewById(R.id.progressbar);
	        progressBar.setVisibility(View.GONE);
              }
            }
          }
        });
      }
    });

    // FABのListener
    ExtendedFloatingActionButton copyURL = findViewById(R.id.copyurl);
    // FABを押すとURLをコピーする
    copyURL.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        copyToClipboard(searchURL);
	toastMake("URLをコピーしました");
      }
    });
    ExtendedFloatingActionButton copyALL = findViewById(R.id.copyall);
    // FABを押すと全てコピーする
    copyALL.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
	// 要改善
        copyToClipboard("all");
        toastMake("全てコピーしました");
      }
    });

    // TopAppBarのナビゲーションアイコンのListener
    MaterialToolbar materialToolBar = findViewById(R.id.materialtoolbar);
    // ナビゲーションアイコンをクリックするとドロワーを開く
    materialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        opencloseDrawer(true);
      }
    });

    // ナビゲーションドロワーのListener
    NavigationView navigationView = findViewById(R.id.navigationview);
    // ドロワーの中の項目をクリックすると処理を実行する
    navigationView.setNavigationItemSelectedListener(
    new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(MenuItem item) {
	// ドロワーを閉じる
        opencloseDrawer(false);
        switch (item.getItemId()) {
          case R.id.home:
	    // MainActivityに飛ぶ
            startActivity(new Intent(getApplication(), MainActivity.class));
            finish();
            return true;
          case R.id.about:
	    // AboutActivityに飛ぶ
            startActivity(new Intent(getApplication(), AboutActivity.class));
            finish();
            return true;
          case R.id.update:
	     // アップデートを確認する
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

  // クリップボードにコピーする
  public void copyToClipboard(String copyText) {
    ClipboardManager clipboardManager =
                (ClipboardManager) ResultActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
      if (null == clipboardManager) {
        return;
      }
    clipboardManager.setPrimaryClip(ClipData.newPlainText("", copyText));
  }

  // 文字からIPを抽出する
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

  // 戻るキーを押すとドロワーが閉じる
  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      opencloseDrawer(false);
    } else {
      super.onBackPressed();
    }
  }

  // ドロワーを開けたり閉じたりする
  public void opencloseDrawer(boolean openclose) {
    if (openclose) {
      drawerLayout.openDrawer(GravityCompat.START);
    } else {
      drawerLayout.closeDrawer(Gravity.LEFT);
    }
  }

  // トーストを出す
  public void toastMake(String toastText) {
    Toast.makeText(ResultActivity.this, toastText, Toast.LENGTH_LONG).show();
  }
}
