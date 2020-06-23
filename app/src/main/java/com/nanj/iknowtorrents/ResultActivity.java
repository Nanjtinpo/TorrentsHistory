package com.nanj.iknowtorrents;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    Intent intent = getIntent();
    String searchip = intent.getStringExtra("searchip");
    
    TextView textView = (TextView)findViewById(R.id.resulttext);
    textView.setText(searchip);
  }
}
