package com.nanj.iknowtorrents;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setContentView(R.layout.activity_result);

    new FullScreenDialogFragment.Builder(MainActivity.this)
                    .setTitle(R.string.app_name)
                    .setOnDiscardListener(onDiscardListener)
                    .setContent(ContentFragment.class, argumentsBundle)
                    .build();
    
    Intent intent = getIntent();
    String searchip = intent.getStringExtra("searchip");
    Toast.makeText(this, searchip, Toast.LENGTH_LONG).show();
  }
}
