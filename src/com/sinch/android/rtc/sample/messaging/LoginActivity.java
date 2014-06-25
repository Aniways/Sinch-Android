package com.sinch.android.rtc.sample.messaging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText txtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        txtUsername = (EditText) findViewById(R.id.loginName);

        View btnLogin = findViewById(R.id.loginButton);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {

        String userName = txtUsername.getText().toString();

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, MessageService.class);
        intent.putExtra(MessageService.INTENT_EXTRA_USERNAME, userName);
        startService(intent);
    }
}
