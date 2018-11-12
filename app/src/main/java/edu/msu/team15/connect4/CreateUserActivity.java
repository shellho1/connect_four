package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateUserActivity extends AppCompatActivity {

    private String user = "";
    private String pass = "";
    private String pass_confirm = "";

    private String getUser() {
        EditText username  = (EditText)findViewById(R.id.userEditText);
        return username.getText().toString();
    }

    private String getPass() {
        EditText password = (EditText)findViewById(R.id.passwordEditText);
        return password.getText().toString();
    }

    private String getPass_confirm() {
        EditText password_confirm = (EditText)findViewById(R.id.password2EditText);
        return password_confirm.getText().toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final Button button = (Button)findViewById(R.id.buttonCreateUser);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateUser(view);
            }

        });
    }

    public void onCreateUser(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        user = getUser();
        pass = getPass();
        pass_confirm = getPass_confirm();

        if (pass.equals(pass_confirm)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Cloud cloud = new Cloud();
                    cloud.createUser(user,pass);
                }
            }).start();
        }
        else {
            Toast.makeText(view.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
    }
}
