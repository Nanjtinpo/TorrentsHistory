package com.nanj.iknowtorrents;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    Intent intent = getIntent();
    String searchip = intent.getStringExtra(MainActivity.com.nanj.iknowtorrents.DATA, 0);
    Toast.makeText(this, searchip, Toast.LENGTH_LONG).show();
  }
}
