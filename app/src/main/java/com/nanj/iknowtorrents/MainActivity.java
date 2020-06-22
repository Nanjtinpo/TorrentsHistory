package com.nanj.iknowtorrents;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import javax.security.auth.callback.Callback;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
  String myip;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // IPを取得してTextFieldにセット
    try {
      urlGet("https://api.ipify.org");
    } catch (IOException e) {
    }
    TextInputLayout textField = (TextInputLayout)findViewById(R.id.searchip);
    textField.getEditText().setText(myip);
    Toast.makeText(this, myip, Toast.LENGTH_LONG).show();

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
        }
        return false;
      }
    });
  }

  // 指定したURLにGET
  void urlGet(String url) throws IOException{
    Request request = new Request.Builder()
            .url(url)
            .build();
    client.newCall(request)
            .enqueue(new Callback() {
      private void onFailure(final Call call, IOException e) {
        // Error
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            // For the example, you can show an error dialog or a toast
            // on the main UI thread
          }
        });
      }
      private void onResponse(Call call, final Response response) throws IOException {
        myip = response.body().string();
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
