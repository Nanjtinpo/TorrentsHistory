package com.nanj.iknowtorrents;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;

import android.widget.Toast;

public class ResultActivity extends AppCompatActivity implements FullScreenDialogFragment.OnDiscardListener {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setContentView(R.layout.activity_result);

    new FullScreenDialogFragment.Builder(this)
                    .setTitle(R.string.app_name)
                    .setOnDiscardListener(onDiscardListener)
                    .build();
    
    Intent intent = getIntent();
    String searchip = intent.getStringExtra("searchip");
    Toast.makeText(this, searchip, Toast.LENGTH_LONG).show();
  }
}
