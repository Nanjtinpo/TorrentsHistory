package com.nanj.torrentshistory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class AboutActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);

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
            // 何もしない
            return true;
          case R.id.update:
            // アップデートを確認する
            new AppUpdater(AboutActivity.this)
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
