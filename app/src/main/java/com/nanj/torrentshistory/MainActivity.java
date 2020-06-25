package com.nanj.torrentshistory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // ボタンのListener
    Button searchstartbutton = findViewById(R.id.searchstart);
    searchstartbutton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        TextInputLayout textField = (TextInputLayout)findViewById(R.id.searchip);
        String searchip = textField.getEditText().getText().toString();
        Intent intent = new Intent(getApplication(), ResultActivity.class);
        intent.putExtra("searchip", searchip);
        startActivity(intent);
      }
    });
    Button searchmyipbutton = findViewById(R.id.searchmyip);
    searchmyipbutton.setOnClickListener(new View.OnClickListener() {
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
            final String myip = response.body().string();
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Intent intent = new Intent(getApplication(), ResultActivity.class);
                intent.putExtra("searchip", myip);
                startActivity(intent);
              }
            });
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
            return true;
          case R.id.menuabout:
            startActivity(new Intent(getApplication(), AboutActivity.class));
            finish();
            return true;
          case R.id.menuupdate:
            AppUpdater appUpdater  = new AppUpdater(MainActivity.this);
            appUpdater.setDisplay(Display.DIALOG);
            appUpdater.setUpdateFrom(UpdateFrom.GITHUB);
            appUpdater.setGitHubUserAndRepo("NanJ-Dev", "TorrentsHistory");
            appUpdater.setTitleOnUpdateAvailable("アップデートがあります");
            appUpdater.setContentOnUpdateAvailable("「アップデートする」を押すとGitHubに飛びます");
	    appUpdater.setTitleOnUpdateNotAvailable("アップデートはありません");
	    appUpdater.setButtonUpdate("アップデートする");
	    appUpdater.setButtonDismiss("無視する");
	    appUpdater.setButtonDoNotShowAgain("二度と表示しない");
            appUpdater.start();
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
