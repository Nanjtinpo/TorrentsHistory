package com.nanj.torrentshistory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
  // フィールド変数
  boolean ipSearch = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // ラジオボタンのListener
    RadioGroup radioGroup = findViewById(R.id.radiogroup);        
    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
	TextInputLayout textInputLayout = findViewById(R.id.textinputlayout);
        switch (checkedId) {
          case R.id.selectip:
	    // 検索する対象をIPにする
	    ipSearch = true;
            textInputLayout.setHint("検索するIP");
            break;
          case R.id.selecthostname:
	    // 検索する対象をホスト名にする
            ipSearch = false;
            textInputLayout.setHint("検索するホスト名");
            break;
        }
      }
    });

    // ボタンのListener
    Button searchStart = findViewById(R.id.searchstart);
    // 「検索」ボタンをおすとTextInputLayoutに入れたIPと一緒にResultActivityに飛ぶ
    searchStart.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        TextInputLayout textInputLayout = findViewById(R.id.textinputlayout);
        if (ipSearch) {
          String searchIP = textInputLayout.getEditText().getText().toString();
	  Intent intent = new Intent(getApplication(), ResultActivity.class);
          intent.putExtra("searchIP", searchIP);
          startActivity(intent);
        } else {
	  String searchHostName = textInputLayout.getEditText().getText().toString();
	  String searchHost = searchHostName.replaceAll("https?://", "");
	  OkHttpClient client = new OkHttpClient();
          Request request = new Request.Builder()
            .url("https://cloudflare-dns.com/dns-query?name=" + searchHost + "&type=A")
	    .addHeader("accept", "application/dns-json")
            .build();
          client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}
            @Override
            public void onResponse(Call call, Response response) throws IOException {
              if(!response.isSuccessful()){
                throw new IOException("Error : " + response);
              }
              String responseJSON = response.body().string();
	      ObjectMapper objectMapper = new ObjectMapper();
	      JsonNode jsonNode = objectMapper.readTree(responseJSON);
	      final String responseIP = jsonNode.get("Answer").get(0).get("data").asText();
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
		  Intent intent = new Intent(getApplication(), ResultActivity.class);
                  intent.putExtra("searchIP", responseIP);
                  startActivity(intent);
                }
              });
            }
          });
        }
      }
    });
    Button searchStartMyIP = findViewById(R.id.searchstartmyip);
    // 「自分のIPを検索」ボタンをおすとAPIから取得したパブリックIPと一緒にResultActivityに飛ぶ
    searchStartMyIP.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url("https://api.ipify.org")
            .build();
        client.newCall(request).enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {}
          @Override
          public void onResponse(Call call, Response response) throws IOException {
            if(!response.isSuccessful()){
              throw new IOException("Error : " + response);
            }
            final String myPublicIP = response.body().string();
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Intent intent = new Intent(getApplication(), ResultActivity.class);
                intent.putExtra("searchIP", myPublicIP);
                startActivity(intent);
              }
            });
          }
        });
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
	    // 何もしない
            return true;
          case R.id.about:
	    // AboutActivityに飛ぶ
            startActivity(new Intent(getApplication(), AboutActivity.class));
            finish();
            return true;
          case R.id.update:
	    // アップデートを確認する
            new AppUpdater(MainActivity.this)
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
    DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      opencloseDrawer(false);
    } else {
      super.onBackPressed();
    }
  }

  // ドロワーを開けたり閉じたりする
  public void opencloseDrawer(boolean openclose) {
    DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
    if (openclose) {
      drawerLayout.openDrawer(GravityCompat.START);
    } else {
      drawerLayout.closeDrawer(Gravity.LEFT);
    }
  }
}
