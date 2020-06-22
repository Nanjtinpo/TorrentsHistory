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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import javax.security.auth.callback.Callback;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
  String myip;
  String URL = "https://api.ipify.org";
  interface Listener {
    void onSuccess(Event event);
    void onFailure();
  }
  private Listener mListener;
  private OkHttpClient mOkHttpClient;
  
  
  RequestClient(Context context, Listener listener) {
    this.mListener = listener;
    this.mOkHttpClient = new OkHttpClient();
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    urlGet("https://api.ipify.org");
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
  private void getOkHttp(final Listener listener) {
    final okhttp3.Request request = new okhttp3.Request.Builder().url(URL).get().build();
    mOkHttpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        listener.onFailure();
      }

      @Override
      public void onResponse(Call call, okhttp3.Response response) throws IOException {
        Event event = new Gson().fromJson(response.body().string(), Event.class);
        listener.onSuccess(event);
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
