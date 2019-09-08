package com.wenchao.passwordinputview;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private PasswordInputView passwordInputView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    passwordInputView = findViewById(R.id.et_password);
    passwordInputView.setOnCompleteListener(new PasswordInputView.OnPasswordCompleteListener() {
      @Override
      public void onComplete(String password) {
        Toast.makeText(MainActivity.this, password, Toast.LENGTH_SHORT).show();
      }
    });
  }
}
