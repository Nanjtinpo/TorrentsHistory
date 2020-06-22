package com.nanj.iknowtorrents;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    String myip = getIPAddress(true);
    TextInputLayout textField = (TextInputLayout)findViewById(R.id.searchip);
    textField.setText(myip, TextView.BufferType.NORMAL);

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

  public static String getIPAddress(boolean useIPv4) {
    try {
      List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
      for (NetworkInterface intf : interfaces) {
        List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
        for (InetAddress addr : addrs) {
          if (!addr.isLoopbackAddress()) {
            String sAddr = addr.getHostAddress();
            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
            boolean isIPv4 = sAddr.indexOf(':')<0;

            if (useIPv4) {
              if (isIPv4)
                return sAddr;
            } else {
              if (!isIPv4) {
                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
              }
            }
          }
        }
      }
    } catch (Exception ignored) { } // for now eat exceptions
    return "";
  }
}
